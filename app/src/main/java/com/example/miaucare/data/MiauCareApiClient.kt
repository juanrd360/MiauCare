package com.example.miaucare.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

sealed class ApiResult<out T> {
    data class Success<T>(val data: T, val statusCode: Int = 200) : ApiResult<T>()
    data class Error(
        val statusCode: Int,
        val message: String,
        val errorJson: JSONObject? = null
    ) : ApiResult<Nothing>()
}

object MiauCareApiClient {
    const val RENDER_CLOUD_URL = "https://miaucare.onrender.com"
    const val DEFAULT_EMULATOR_URL = "https://miaucare.onrender.com"

    // Conectado directamente al backend en producción de Render (https://miaucare.onrender.com)
    var baseUrl: String = RENDER_CLOUD_URL
    var autoFallbackToCloud: Boolean = true

    private val imageCache = ConcurrentHashMap<String, ImageBitmap>()

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        val token = MiauCareRepository.jwtToken
        if (token.isNotBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        if (original.header("Content-Type") == null && original.body !is MultipartBody) {
            builder.header("Content-Type", "application/json")
        }
        builder.header("Accept", "application/json")
        chain.proceed(builder.build())
    }

    val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun request(
        method: String,
        path: String,
        jsonBody: String? = null
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val primaryUrl = "${baseUrl.trimEnd('/')}$normalizedPath"
        executeWithRetry(method, primaryUrl, jsonBody)
    }

    private suspend fun executeWithRetry(
        method: String,
        fullUrl: String,
        jsonBody: String?
    ): ApiResult<String> {
        var lastError: ApiResult.Error? = null
        val mediaType = "application/json; charset=utf-8".toMediaType()

        for (attempt in 1..3) {
            try {
                val reqBody = when (method.uppercase()) {
                    "POST", "PUT", "PATCH" -> (jsonBody ?: "{}").toRequestBody(mediaType)
                    else -> null
                }
                val requestBuilder = Request.Builder().url(fullUrl)
                when (method.uppercase()) {
                    "GET" -> requestBuilder.get()
                    "DELETE" -> requestBuilder.delete()
                    "POST" -> requestBuilder.post(reqBody!!)
                    "PUT" -> requestBuilder.put(reqBody!!)
                    "PATCH" -> requestBuilder.patch(reqBody!!)
                }

                httpClient.newCall(requestBuilder.build()).execute().use { response ->
                    val code = response.code
                    val rawBody = response.body?.string().orEmpty()

                    // Handle transient Supabase PgBouncer prepared statement collision automatically
                    if ((code == 404 || code == 500) &&
                        (rawBody.contains("prepared statement", ignoreCase = true) ||
                            rawBody.contains("already exists", ignoreCase = true)) &&
                        attempt < 3
                    ) {
                        delay(180L * attempt)
                        continue
                    }

                    if (code in 200..299) {
                        return ApiResult.Success(rawBody, code)
                    } else {
                        val parsedJson = try {
                            if (rawBody.trim().startsWith("{")) JSONObject(rawBody) else null
                        } catch (_: Exception) {
                            null
                        }
                        val errorMsg = parsedJson?.optString("error")?.takeIf { it.isNotBlank() }
                            ?: when (code) {
                                400 -> "Datos inválidos (400 Bad Request)."
                                401 -> "Correo o contraseña incorrectos."
                                403 -> "No tienes permisos para realizar esta acción (403 Forbidden)."
                                404 -> "Recurso no encontrado (404)."
                                409 -> "Conflicto de regla de negocio (409 Conflict)."
                                else -> "Error del servidor ($code)."
                            }
                        return ApiResult.Error(code, errorMsg, parsedJson)
                    }
                }
            } catch (e: IOException) {
                lastError = ApiResult.Error(
                    statusCode = 0,
                    message = "Error de conexión con el servidor (${e.localizedMessage ?: "sin red"})."
                )
                if (attempt < 2) delay(150L)
            } catch (e: Exception) {
                return ApiResult.Error(
                    statusCode = -1,
                    message = e.localizedMessage ?: "Error inesperado."
                )
            }
        }
        return lastError ?: ApiResult.Error(0, "No se pudo conectar al servidor.")
    }

    /**
     * Sube una imagen a Supabase Storage mediante POST /api/archivos/subir
     * (multipart/form-data con campos "archivo" y "carpeta" = "gatos" | "cedulas")
     * y retorna el campo "urlPublica".
     */
    suspend fun subirArchivo(
        bytes: ByteArray,
        fileName: String,
        mimeType: String = "image/jpeg",
        carpeta: String = "gatos"
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        val targetUrls = buildList {
            add("${baseUrl.trimEnd('/')}/api/archivos/subir?carpeta=$carpeta")
            if (autoFallbackToCloud && baseUrl != RENDER_CLOUD_URL) {
                add("$RENDER_CLOUD_URL/api/archivos/subir?carpeta=$carpeta")
            }
        }

        for (url in targetUrls) {
            try {
                val fileBody = bytes.toRequestBody(mimeType.toMediaType())
                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("carpeta", carpeta)
                    .addFormDataPart("archivo", fileName, fileBody)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(multipartBody)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val code = response.code
                    val raw = response.body?.string().orEmpty()
                    if (code in 200..299) {
                        val json = JSONObject(raw)
                        val urlPublica = json.optString("urlPublica")
                        if (url.startsWith(RENDER_CLOUD_URL)) {
                            baseUrl = RENDER_CLOUD_URL
                        }
                        return@withContext ApiResult.Success(urlPublica, code)
                    } else {
                        val errJson = try { JSONObject(raw) } catch (_: Exception) { null }
                        val msg = errJson?.optString("error")?.takeIf { it.isNotBlank() }
                            ?: "No se pudo subir la imagen ($code)."
                        return@withContext ApiResult.Error(code, msg, errJson)
                    }
                }
            } catch (_: IOException) {
                // Try fallback URL if available
            } catch (e: Exception) {
                return@withContext ApiResult.Error(-1, e.localizedMessage ?: "Error al subir imagen")
            }
        }
        ApiResult.Error(0, "No se pudo conectar al servidor para subir la imagen.")
    }

    /**
     * Descarga y cachea una imagen remota (ej. urlPublica de Supabase Storage).
     */
    suspend fun fetchRemoteBitmap(url: String?): ImageBitmap? = withContext(Dispatchers.IO) {
        if (url.isNullOrBlank() || !url.startsWith("http")) return@withContext null
        imageCache[url]?.let { return@withContext it }
        try {
            val req = Request.Builder().url(url).get().build()
            httpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                val bytes = resp.body?.bytes() ?: return@withContext null
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext null
                val imgBmp = bmp.asImageBitmap()
                imageCache[url] = imgBmp
                imgBmp
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Lee los bytes de un Uri seleccionado de la galería o genera un JPEG comprimido desde un drawable de respaldo.
     */
    fun readUriBytes(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Exception) {
            null
        }
    }

    fun drawableToJpegBytes(context: Context, drawableRes: Int): ByteArray {
        val bmp = BitmapFactory.decodeResource(context.resources, drawableRes)
        val out = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
        return out.toByteArray()
    }

    /**
     * Obtiene la latitud y longitud reales del dispositivo (GPS / Red),
     * con fallback a coordenadas base en Bogotá si el emulador aún no reporta fix GPS.
     */
    @SuppressLint("MissingPermission")
    fun obtainDeviceCoordinates(context: Context): Pair<Double, Double> {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )
            for (provider in providers) {
                val loc = try { lm?.getLastKnownLocation(provider) } catch (_: SecurityException) { null }
                if (loc != null && (loc.latitude != 0.0 || loc.longitude != 0.0)) {
                    return loc.latitude to loc.longitude
                }
            }
            4.6533 to -74.0836
        } catch (_: Exception) {
            4.6533 to -74.0836
        }
    }
}

fun JSONObject.optLongOrNull(key: String): Long? {
    return if (has(key) && !isNull(key)) optLong(key) else null
}

fun JSONObject.optDoubleOrNull(key: String): Double? {
    return if (has(key) && !isNull(key)) {
        val d = optDouble(key)
        if (d.isNaN()) null else d
    } else null
}

fun JSONObject.optStringOrEmpty(key: String): String {
    return if (has(key) && !isNull(key)) optString(key, "") else ""
}

fun JSONArray.toJSONObjectList(): List<JSONObject> {
    val list = ArrayList<JSONObject>(length())
    for (i in 0 until length()) {
        optJSONObject(i)?.let { list.add(it) }
    }
    return list
}
