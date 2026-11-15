package com.example.miaucare.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.miaucare.R
import org.json.JSONArray
import org.json.JSONObject

enum class MiauRole(val label: String, val backendRole: String) {
    VOLUNTARIO("Voluntario", "VOLUNTARIO"),
    VETERINARIO("Veterinario", "VETERINARIO"),
    ADMINISTRADOR("Administrador", "ADMINISTRADOR");

    companion object {
        fun fromBackend(role: String): MiauRole {
            return entries.find { it.backendRole.equals(role.removePrefix("ROLE_"), ignoreCase = true) }
                ?: VOLUNTARIO
        }
    }
}

data class CatProfile(
    val numericId: Long,
    val id: String,
    var code: String,
    var name: String,
    var breed: String,
    var sex: String,               // "Macho" / "Hembra" (backend: "MACHO" / "HEMBRA")
    var age: String,
    var weightKg: String,
    var sterilizationStatus: String,
    var isSterilized: Boolean,
    var colony: String,
    var zoneId: Long?,
    var statusTag: String,         // "En calle", "Hospitalizado"
    var estadoInternacion: String, // "EN_CALLE", "HOSPITALIZADO"
    var lifeStatus: String = "Activo", // "Activo", "Fallecido", "Adoptado", "Trasladado"
    var estadoVidaBackend: String = "ACTIVO", // "ACTIVO", "FALLECIDO", "ADOPTADO", "TRASLADADO"
    var estadoAprobacion: String = "APROBADO",
    var condicionesEspeciales: String = "",
    var fotoUrl: String? = null,
    var veterinarioResponsableId: Long? = null,
    var veterinarioResponsableNombre: String? = null,
    @param:DrawableRes val imageRes: Int = R.drawable.img_cat_samantha
) {
    val isFallecido: Boolean
        get() = estadoVidaBackend.equals("FALLECIDO", ignoreCase = true) ||
            lifeStatus.equals("Fallecido", ignoreCase = true)
}

data class ClinicalHistoryEntry(
    val id: Long,
    val tipo: String,              // "DIAGNOSTICO" o "PROCEDIMIENTO"
    val diagnosticoId: Long?,
    val diagnosticoNombre: String,
    val nivelGravedad: String,     // "LEVE", "MODERADA", "CRITICA"
    val descripcion: String,
    val notas: String,
    val pesoRegistrado: Double?,
    val dejarHospitalizado: Boolean?,
    val veterinarioNombre: String,
    val fecha: String
)

data class SightingRecord(
    val id: String,
    val catName: String,
    val dateGroup: String,
    val dateTime: String,
    val healthStatus: String, // "Sano", "Herido", "Enfermo"
    val ateFood: Boolean,
    val reportedBy: String,
    val observaciones: String = "",
    val fotoUrl: String? = null,
    val esNoCensado: Boolean = false,
    @param:DrawableRes val imageRes: Int = R.drawable.img_cat_benito
)

data class RoundScheduleItem(
    val numericId: Long,
    val id: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val timeRange: String,
    val roundType: String,
    val zoneId: Long?,
    val zoneTitle: String,
    val description: String,
    val veterinarioId: Long?,
    val veterinarioNombre: String,
    val initials: List<String>,
    val peopleCountText: String,
    val estadoBackend: String, // "PROGRAMADA", "EN_CURSO", "COMPLETADA", "CANCELADA"
    var isJoined: Boolean
)

data class ConflictoHorarioInfo(
    val errorMessage: String,
    val rondaId: Long?,
    val zonaNombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val veterinarioNombre: String,
    val estado: String
)

data class MedicalAlertItem(
    val numericId: Long,
    val id: String,
    val catNumericId: Long?,
    val catId: String,
    val catName: String,
    val timeAgo: String,
    val urgencyBadge: String,
    val symptomTitle: String,
    val locationAndReporter: String,
    var tabStatus: String,     // "Nuevas", "En atención", "Cerradas"
    val estadoBackend: String, // "NUEVA", "EN_ATENCION", "CERRADA"
    val veterinarioResponsableId: Long?,
    val veterinarioResponsableNombre: String?,
    val delegadosIds: List<Long> = emptyList(),
    val resolucion: String? = null,
    val observacionCierre: String? = null,
    val fotoUrl: String? = null,
    @param:DrawableRes val imageRes: Int = R.drawable.img_cat_luna
)

data class DoctorItem(
    val numericId: Long,
    val id: String,
    val name: String,
    val correo: String,
    val numeroCedula: String,
    val specialtyId: Long?,
    val specialty: String,
    val distanceText: String,
    val patientsLoadText: String,
    val estadoBackend: String, // "PENDIENTE", "ACTIVO", "INACTIVO", "RECHAZADO"
    val disponibleEmergencias: Boolean,
    val isInternal: Boolean,
    val fotoCedulaUrl: String? = null,
    @param:DrawableRes val imageRes: Int = R.drawable.img_doc_sofia
)

data class PendingCatRecord(
    val numericId: Long,
    val id: String,
    val nameAndCode: String,
    val breedAgeSex: String,
    val zone: String,
    val submittedByAndTime: String,
    var isReviewed: Boolean = false,
    val isVeterinarioApplication: Boolean = false,
    val fotoUrl: String? = null,
    @param:DrawableRes val imageRes: Int = R.drawable.img_cat_cake
)

data class SolicitudGatoItem(
    val id: Long,
    val gatoId: Long,
    val gatoNombre: String,
    val gatoCodigo: String,
    val zonaNombre: String,
    val voluntarioNombre: String,
    val tipo: String,   // "INGRESO_HOSPITALIZACION" o "FALLECIMIENTO"
    val estado: String, // "PENDIENTE", "ACEPTADA", "RECHAZADA"
    val fecha: String
)

data class ColonyItem(
    val numericId: Long,
    val id: String,
    var name: String,
    var zoneName: String,
    var address: String,
    var feedingTime: String,
    val latitud: Double?,
    val longitud: Double?,
    val cantidadGatos: Int,
    val catsCountText: String,
    val ordenParada: Int? = null,
    val distanciaKm: Double? = null
)

data class TeamUserItem(
    val numericId: Long,
    val id: String,
    val name: String,
    val correo: String,
    val roleLabel: String,
    val isVeterinario: Boolean,
    val estadoBackend: String,
    var isActive: Boolean
)

data class AuditLogItem(
    val id: String,
    val dateGroup: String,
    val title: String,
    val subtitle: String,
    val iconType: String
)

data class CatalogItemDto(
    val id: Long,
    val nombre: String,
    val descripcion: String = ""
)

data class ZoneIndicatorDto(
    val zonaId: Long,
    val nombreZona: String,
    val zonaMacro: String,
    val gatosCensados: Int,
    val gatosEsterilizados: Int,
    val gatosHospitalizados: Int,
    val alertasActivas: Int,
    val rondasCompletadas: Int
)

data class NotificacionGlobalDto(
    val id: Long,
    val titulo: String,
    val mensaje: String,
    val destinatarios: String,
    val creadoPor: String,
    val fechaCreacion: String
)

data class CirugiaCesDto(
    val id: Long,
    val gatoId: Long,
    val gatoNombre: String,
    val gatoCodigo: String,
    val gatoSexo: String,
    val coloniaNombre: String,
    val veterinarioId: Long?,
    val veterinarioNombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val quirofano: String,
    val estado: String, // "PROGRAMADA", "EN_CURSO", "REALIZADA", "CANCELADA"
    val resultado: String?
)

object MiauCareRepository {
    private const val PREFS_NAME = "miaucare_jwt_session"

    // =========================================================================
    // SESSION & AUTH STATE (JWT)
    // =========================================================================
    var jwtToken by mutableStateOf("")
    var sessionUserId by mutableLongStateOf(0L)
    var sessionUserName by mutableStateOf("")
    var sessionUserEmail by mutableStateOf("")
    var debeCambiarContrasena by mutableStateOf(false)
    var currentRole by mutableStateOf(MiauRole.VOLUNTARIO)
    var isLoading by mutableStateOf(false)

    // Current volunteer's assigned zone & GPS
    var volunteerAssignedZoneId by mutableStateOf<Long?>(null)
    var volunteerAssignedZoneName by mutableStateOf("Sin zona asignada")
    var volunteerPhone by mutableStateOf("")
    var volunteerDisponibilidad by mutableStateOf("Mañanas")
    var lastDeviceLat by mutableDoubleStateOf(4.6533)
    var lastDeviceLon by mutableDoubleStateOf(-74.0836)

    // Current veterinarian's profile state
    var vetCedula by mutableStateOf("")
    var vetSpecialtyName by mutableStateOf("Cirugía CES")
    var vetSpecialtyId by mutableStateOf<Long?>(1L)
    var vetDisponibleEmergencias by mutableStateOf(true)
    var vetEstadoCuenta by mutableStateOf("ACTIVO")

    // Dashboard summary counters from GET /api/dashboard/resumen
    var dashUrgenciasHoy by mutableIntStateOf(0)
    var dashCasosActivos by mutableIntStateOf(0)
    var dashPacientesInternados by mutableIntStateOf(0)
    var dashGatosCensadosTotales by mutableIntStateOf(0)
    var dashVeterinariosActivos by mutableIntStateOf(0)
    var dashSolicitudesPendientes by mutableIntStateOf(0)

    // System parameters (`GET / PUT /api/parametros`)
    var daysWithoutSightingX by mutableStateOf("7")
    var consecutiveCriticalN by mutableStateOf("2")
    var maxResponseHoursT by mutableStateOf("24")

    // Global UI Modals & Toasts for Business Rules
    var prototypeSimulateError by mutableStateOf(false)
    var toastMessage by mutableStateOf<Pair<String, Boolean>?>(null)
    var sinPermisosModalMessage by mutableStateOf<String?>(null)
    var conflictoHorarioModal by mutableStateOf<ConflictoHorarioInfo?>(null)
    var exportedCsvPreview by mutableStateOf<String?>(null)

    // =========================================================================
    // LIVE BACKEND COLLECTIONS (No hardcoded/mock items in memory)
    // =========================================================================
    val cats = mutableStateListOf<CatProfile>()
    val vetAssignedCats = mutableStateListOf<CatProfile>()
    val currentCatHistory = mutableStateListOf<ClinicalHistoryEntry>()
    val sightingHistory = mutableStateListOf<SightingRecord>()
    val rounds = mutableStateListOf<RoundScheduleItem>()
    val medicalAlerts = mutableStateListOf<MedicalAlertItem>()
    val doctors = mutableStateListOf<DoctorItem>()
    val pendingApprovals = mutableStateListOf<PendingCatRecord>()
    val pendingVetApprovals = mutableStateListOf<DoctorItem>()
    val solicitudesGatoPendientes = mutableStateListOf<SolicitudGatoItem>()
    val colonies = mutableStateListOf<ColonyItem>()
    val routeColonies = mutableStateListOf<ColonyItem>()
    val teamUsers = mutableStateListOf<TeamUserItem>()
    val auditLogs = mutableStateListOf<AuditLogItem>()
    val notifications = mutableStateListOf<NotificacionGlobalDto>()
    val zoneIndicators = mutableStateListOf<ZoneIndicatorDto>()
    val diagnosticosCatalog = mutableStateListOf<CatalogItemDto>()
    val especialidadesCatalog = mutableStateListOf<CatalogItemDto>()
    val cirugiasCes = mutableStateListOf<CirugiaCesDto>()

    private val catDrawables = listOf(
        R.drawable.img_cat_benito,
        R.drawable.img_cat_luna,
        R.drawable.img_cat_gary,
        R.drawable.img_cat_peach,
        R.drawable.img_cat_whitney,
        R.drawable.img_cat_buggy,
        R.drawable.img_cat_samantha,
        R.drawable.img_cat_manchas,
        R.drawable.img_cat_cake,
        R.drawable.img_cat_kiwi,
        R.drawable.img_cat_willie
    )

    private val docDrawables = listOf(
        R.drawable.img_doc_sofia,
        R.drawable.img_doc_luis,
        R.drawable.img_doc_mateo,
        R.drawable.img_doc_irene
    )

    fun pickCatDrawable(id: Long): Int {
        val idx = (id.coerceAtLeast(0L) % catDrawables.size).toInt()
        return catDrawables[idx]
    }

    fun pickDocDrawable(id: Long): Int {
        val idx = (id.coerceAtLeast(0L) % docDrawables.size).toInt()
        return docDrawables[idx]
    }

    // =========================================================================
    // SESSION PERSISTENCE
    // =========================================================================
    fun initSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        jwtToken = prefs.getString("token", "") ?: ""
        sessionUserId = prefs.getLong("userId", 0L)
        sessionUserName = prefs.getString("userName", "") ?: ""
        sessionUserEmail = prefs.getString("userEmail", "") ?: ""
        val roleStr = prefs.getString("role", "VOLUNTARIO") ?: "VOLUNTARIO"
        currentRole = MiauRole.fromBackend(roleStr)
        debeCambiarContrasena = prefs.getBoolean("debeCambiar", false)
        // Siempre usar Render Cloud (https://miaucare.onrender.com) por defecto
        MiauCareApiClient.baseUrl = MiauCareApiClient.RENDER_CLOUD_URL
        prefs.edit().putString("baseUrl", MiauCareApiClient.RENDER_CLOUD_URL).apply()
    }

    fun saveSession(
        context: Context,
        token: String,
        id: Long,
        nombre: String,
        correo: String,
        rol: String,
        debeCambiar: Boolean
    ) {
        jwtToken = token
        sessionUserId = id
        sessionUserName = nombre
        sessionUserEmail = correo
        currentRole = MiauRole.fromBackend(rol)
        debeCambiarContrasena = debeCambiar

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("token", token)
            .putLong("userId", id)
            .putString("userName", nombre)
            .putString("userEmail", correo)
            .putString("role", currentRole.backendRole)
            .putBoolean("debeCambiar", debeCambiar)
            .putString("baseUrl", MiauCareApiClient.baseUrl)
            .apply()
    }

    fun saveBaseUrl(context: Context, newBaseUrl: String) {
        MiauCareApiClient.baseUrl = newBaseUrl.trim().trimEnd('/')
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("baseUrl", MiauCareApiClient.baseUrl)
            .apply()
    }

    fun logout(context: Context? = null) {
        jwtToken = ""
        sessionUserId = 0L
        sessionUserName = ""
        sessionUserEmail = ""
        debeCambiarContrasena = false
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
    }

    fun getCatById(catId: String): CatProfile? {
        val numeric = catId.removePrefix("cat-").toLongOrNull()
        return cats.find { it.id == catId || it.code == catId || (numeric != null && it.numericId == numeric) }
            ?: vetAssignedCats.find { it.id == catId || it.code == catId || (numeric != null && it.numericId == numeric) }
    }

    fun showToast(title: String, isError: Boolean = false) {
        toastMessage = title to isError
    }

    // =========================================================================
    // 1. AUTENTICACIÓN (`/api/auth`)
    // =========================================================================
    suspend fun login(context: Context, correo: String, contrasena: String): ApiResult<MiauRole> {
        isLoading = true
        val payload = JSONObject().apply {
            put("correo", correo.trim())
            put("contrasena", contrasena)
        }
        val res = MiauCareApiClient.request("POST", "/api/auth/login", payload.toString())
        isLoading = false
        return when (res) {
            is ApiResult.Success -> {
                val json = JSONObject(res.data)
                val token = json.optString("token")
                val id = json.optLong("id", 0L)
                val nombre = json.optString("nombreCompleto", correo)
                val rol = json.optString("rol", "VOLUNTARIO")
                val debeCambiar = json.optBoolean("debeCambiarContrasena", false)
                saveSession(context, token, id, nombre, correo.trim(), rol, debeCambiar)
                showToast("Bienvenido, $nombre")
                ApiResult.Success(currentRole)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun restablecerContrasena(correo: String, nuevaContrasena: String): ApiResult<Unit> {
        isLoading = true
        val payload = JSONObject().apply {
            put("correo", correo.trim())
            put("nuevaContrasena", nuevaContrasena)
        }
        val res = MiauCareApiClient.request("POST", "/api/auth/restablecer-contrasena", payload.toString())
        isLoading = false
        return when (res) {
            is ApiResult.Success -> {
                showToast("Contraseña restablecida correctamente")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    // =========================================================================
    // 2. CATÁLOGOS PÚBLICOS Y MAPA / RUTA GPS (`/api/zonas`, `/api/diagnosticos`, `/api/especialidades`)
    // =========================================================================
    suspend fun fetchZonasResumen() {
        val res = MiauCareApiClient.request("GET", "/api/zonas/resumen")
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            colonies.clear()
            colonies.addAll(arr.map { parseColony(it) })
        } else {
            val resFallback = MiauCareApiClient.request("GET", "/api/zonas")
            if (resFallback is ApiResult.Success) {
                val arr = JSONArray(resFallback.data).toJSONObjectList()
                colonies.clear()
                colonies.addAll(arr.map { parseColony(it) })
            }
        }
    }

    suspend fun fetchRutaVoluntario(latitud: Double, longitud: Double, zonaMacro: String? = null) {
        lastDeviceLat = latitud
        lastDeviceLon = longitud
        val macroParam = if (!zonaMacro.isNullOrBlank()) "&zonaMacro=$zonaMacro" else ""
        val res = MiauCareApiClient.request("GET", "/api/zonas/ruta?latitud=$latitud&longitud=$longitud$macroParam")
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            val parsed = arr.map { parseColony(it) }
            routeColonies.clear()
            routeColonies.addAll(parsed)
            if (colonies.isEmpty()) {
                colonies.addAll(parsed)
            }
        }
    }

    suspend fun fetchCatalogosMaestros() {
        val diagRes = MiauCareApiClient.request("GET", "/api/diagnosticos")
        if (diagRes is ApiResult.Success) {
            val arr = JSONArray(diagRes.data).toJSONObjectList()
            diagnosticosCatalog.clear()
            diagnosticosCatalog.addAll(
                arr.map {
                    CatalogItemDto(
                        id = it.optLong("id"),
                        nombre = it.optStringOrEmpty("nombre"),
                        descripcion = "Diagnóstico clínico registrado en catálogo"
                    )
                }
            )
        }
        val espRes = MiauCareApiClient.request("GET", "/api/especialidades")
        if (espRes is ApiResult.Success) {
            val arr = JSONArray(espRes.data).toJSONObjectList()
            especialidadesCatalog.clear()
            especialidadesCatalog.addAll(
                arr.map {
                    CatalogItemDto(
                        id = it.optLong("id"),
                        nombre = it.optStringOrEmpty("nombre"),
                        descripcion = "Especialidad médica veterinaria"
                    )
                }
            )
        }
    }

    // =========================================================================
    // 3. FLUJO VOLUNTARIO (`/api/voluntarios`, `/api/gatos`, `/api/avistamientos`, `/api/solicitudes-gato`, `/api/rondas`)
    // =========================================================================
    suspend fun registrarVoluntario(
        nombreCompleto: String,
        numeroCedula: String,
        correo: String,
        contrasena: String,
        telefono: String,
        disponibilidad: String,
        latitudBase: Double,
        longitudBase: Double
    ): ApiResult<Unit> {
        isLoading = true
        val body = JSONObject().apply {
            put("nombreCompleto", nombreCompleto.trim())
            put("numeroCedula", numeroCedula.trim())
            put("correo", correo.trim())
            put("contrasena", contrasena)
            put("telefono", telefono.trim().ifBlank { "3000000000" })
            put("disponibilidad", disponibilidad.ifBlank { "Mañanas" })
            put("latitudBase", latitudBase)
            put("longitudBase", longitudBase)
        }
        val res = MiauCareApiClient.request("POST", "/api/voluntarios/registro", body.toString())
        isLoading = false
        return when (res) {
            is ApiResult.Success -> {
                val json = JSONObject(res.data)
                val zonaObj = json.optJSONObject("zona")
                val zonaNombre = zonaObj?.optStringOrEmpty("nombre")?.takeIf { it.isNotBlank() } ?: "Colonia más cercana"
                showToast("Cuenta creada · Asignada a $zonaNombre")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchVoluntarioPerfil() {
        if (sessionUserId <= 0L) return
        val res = MiauCareApiClient.request("GET", "/api/voluntarios/$sessionUserId")
        if (res is ApiResult.Success) {
            val json = JSONObject(res.data)
            sessionUserName = json.optString("nombreCompleto", sessionUserName)
            sessionUserEmail = json.optString("correo", sessionUserEmail)
            volunteerPhone = json.optStringOrEmpty("telefono")
            volunteerDisponibilidad = json.optStringOrEmpty("disponibilidad").ifBlank { "Mañanas" }
            val zonaObj = json.optJSONObject("zona")
            volunteerAssignedZoneId = zonaObj?.optLongOrNull("id")
            volunteerAssignedZoneName = zonaObj?.optStringOrEmpty("nombre")?.ifBlank { "Sin zona asignada" }
                ?: "Sin zona asignada"
        }
    }

    suspend fun actualizarUbicacionGpsVoluntario(latitud: Double, longitud: Double): ApiResult<String> {
        if (sessionUserId <= 0L) return ApiResult.Error(400, "Inicia sesión como voluntario primero.")
        val body = JSONObject().apply {
            put("latitud", latitud)
            put("longitud", longitud)
        }
        // Try /ubicar-gps (backend controller) and fallback to /ubicacion-gps (guide alias)
        var res = MiauCareApiClient.request("PATCH", "/api/voluntarios/$sessionUserId/ubicar-gps", body.toString())
        if (res is ApiResult.Error && res.statusCode == 404) {
            res = MiauCareApiClient.request("PATCH", "/api/voluntarios/$sessionUserId/ubicacion-gps", body.toString())
        }
        return when (res) {
            is ApiResult.Success -> {
                val json = JSONObject(res.data)
                val zonaObj = json.optJSONObject("zona")
                volunteerAssignedZoneId = zonaObj?.optLongOrNull("id")
                volunteerAssignedZoneName = zonaObj?.optStringOrEmpty("nombre")?.ifBlank { "Colonia asignada" }
                    ?: "Colonia asignada"
                fetchRutaVoluntario(latitud, longitud)
                fetchGatos(zonaId = volunteerAssignedZoneId)
                showToast("GPS actualizado · Zona: $volunteerAssignedZoneName")
                ApiResult.Success(volunteerAssignedZoneName)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun asignarZonaManualVoluntario(zonaId: Long): ApiResult<Unit> {
        if (sessionUserId <= 0L) return ApiResult.Error(400, "Sesión no válida")
        val body = JSONObject().apply { put("zonaId", zonaId) }
        val res = MiauCareApiClient.request("PATCH", "/api/voluntarios/$sessionUserId/ubicar-zona-manual", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                val json = JSONObject(res.data)
                val zonaObj = json.optJSONObject("zona")
                volunteerAssignedZoneId = zonaObj?.optLongOrNull("id")
                volunteerAssignedZoneName = zonaObj?.optStringOrEmpty("nombre") ?: "Zona actualizada"
                showToast("Zona asignada: $volunteerAssignedZoneName")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun actualizarPerfilVoluntario(nombre: String, correo: String, telefono: String): ApiResult<Unit> {
        if (sessionUserId <= 0L) return ApiResult.Error(400, "Sesión no válida")
        val body = JSONObject().apply {
            put("nombreCompleto", nombre.trim())
            put("correo", correo.trim())
            put("telefono", telefono.trim())
            put("disponibilidad", volunteerDisponibilidad)
            put("recibiendoAlertas", true)
        }
        val res = MiauCareApiClient.request("PUT", "/api/voluntarios/$sessionUserId", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                sessionUserName = nombre.trim()
                sessionUserEmail = correo.trim()
                volunteerPhone = telefono.trim()
                showToast("Perfil actualizado correctamente")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchGatos(nombre: String? = null, zonaId: Long? = null) {
        val params = buildList {
            if (!nombre.isNullOrBlank()) add("nombre=${nombre.trim()}")
            if (zonaId != null) add("zonaId=$zonaId")
        }.joinToString("&")
        val path = if (params.isNotEmpty()) "/api/gatos?$params" else "/api/gatos"
        val res = MiauCareApiClient.request("GET", path)
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            cats.clear()
            cats.addAll(arr.map { parseCat(it) })
        }
    }

    suspend fun fetchGatoDetalleYHistorial(catId: String): CatProfile? {
        val numericId = catId.removePrefix("cat-").toLongOrNull()
        val endpoint = if (numericId != null) "/api/gatos/$numericId" else "/api/gatos/codigo/$catId"
        val res = MiauCareApiClient.request("GET", endpoint)
        var loadedCat: CatProfile? = null
        if (res is ApiResult.Success) {
            val parsed = parseCat(JSONObject(res.data))
            loadedCat = parsed
            val existingIdx = cats.indexOfFirst { it.numericId == parsed.numericId }
            if (existingIdx >= 0) cats[existingIdx] = parsed else cats.add(parsed)
        }
        val targetId = loadedCat?.numericId ?: numericId
        if (targetId != null) {
            val histRes = MiauCareApiClient.request("GET", "/api/historial-clinico/gato/$targetId")
            if (histRes is ApiResult.Success) {
                val arr = JSONArray(histRes.data).toJSONObjectList()
                currentCatHistory.clear()
                currentCatHistory.addAll(arr.map { parseClinicalHistory(it) })
            }
        }
        return loadedCat ?: getCatById(catId)
    }

    suspend fun registrarAvistamientoNormal(
        gatoNumericId: Long,
        estadoAparenteUi: String,
        comio: Boolean,
        observaciones: String
    ): ApiResult<Unit> {
        val estadoBackend = when (estadoAparenteUi.uppercase()) {
            "ENFERMO" -> "ENFERMO"
            "HERIDO" -> "HERIDO"
            else -> "SANO"
        }
        val body = JSONObject().apply {
            put("gato", JSONObject().put("id", gatoNumericId))
            if (sessionUserId > 0L) {
                put("voluntario", JSONObject().put("id", sessionUserId))
            }
            put("estadoAparente", estadoBackend)
            put("comio", comio)
            put("observaciones", observaciones.trim())
        }
        val res = MiauCareApiClient.request("POST", "/api/avistamientos/normal", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Avistamiento registrado en el servidor")
                fetchAvistamientosPorGatoOZona(gatoId = gatoNumericId, zonaId = volunteerAssignedZoneId)
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun registrarAvistamientoNoCensado(
        zonaId: Long?,
        estadoAparenteUi: String,
        observaciones: String,
        imageBytes: ByteArray?
    ): ApiResult<Unit> {
        var uploadedUrl: String? = null
        if (imageBytes != null) {
            when (val up = MiauCareApiClient.subirArchivo(
                bytes = imageBytes,
                fileName = "gato_no_censado_${System.currentTimeMillis()}.jpg",
                carpeta = "gatos"
            )) {
                is ApiResult.Success -> uploadedUrl = up.data
                is ApiResult.Error -> {
                    showToast(up.message, isError = true)
                    return up
                }
            }
        }

        val estadoBackend = when (estadoAparenteUi.uppercase()) {
            "ENFERMO" -> "ENFERMO"
            "HERIDO" -> "HERIDO"
            else -> "SANO"
        }
        val targetZonaId = zonaId ?: volunteerAssignedZoneId ?: colonies.firstOrNull()?.numericId ?: 1L
        val body = JSONObject().apply {
            if (sessionUserId > 0L) {
                put("voluntario", JSONObject().put("id", sessionUserId))
            }
            put("zona", JSONObject().put("id", targetZonaId))
            put("estadoAparente", estadoBackend)
            put("observaciones", observaciones.trim())
            if (!uploadedUrl.isNullOrBlank()) {
                put("fotoUrl", uploadedUrl)
            }
        }
        val res = MiauCareApiClient.request("POST", "/api/avistamientos/no-censado", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Alerta de gato no censado enviada")
                fetchAvistamientosPorGatoOZona(zonaId = targetZonaId)
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun crearSolicitudGato(gatoNumericId: Long, tipoBackend: String): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("gatoId", gatoNumericId)
            put("voluntarioId", if (sessionUserId > 0L) sessionUserId else 1L)
            put("tipo", tipoBackend) // "INGRESO_HOSPITALIZACION" o "FALLECIMIENTO"
        }
        val res = MiauCareApiClient.request("POST", "/api/solicitudes-gato", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                val label = if (tipoBackend == "FALLECIMIENTO") "Reporte de fallecimiento enviado"
                else "Solicitud de hospitalización enviada"
                showToast(label)
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchAvistamientosPorGatoOZona(gatoId: Long? = null, zonaId: Long? = null) {
        val results = mutableListOf<SightingRecord>()
        if (zonaId != null) {
            val res = MiauCareApiClient.request("GET", "/api/avistamientos/zona/$zonaId")
            if (res is ApiResult.Success) {
                val arr = JSONArray(res.data).toJSONObjectList()
                results.addAll(arr.map { parseSighting(it) })
            }
        } else if (gatoId != null) {
            val res = MiauCareApiClient.request("GET", "/api/avistamientos/gato/$gatoId")
            if (res is ApiResult.Success) {
                val arr = JSONArray(res.data).toJSONObjectList()
                results.addAll(arr.map { parseSighting(it) })
            }
        } else {
            val zoneIds = colonies.map { it.numericId }.ifEmpty { listOf(1L, 2L, 3L) }
            for (zId in zoneIds) {
                val res = MiauCareApiClient.request("GET", "/api/avistamientos/zona/$zId")
                if (res is ApiResult.Success) {
                    val arr = JSONArray(res.data).toJSONObjectList()
                    results.addAll(arr.map { parseSighting(it) })
                }
            }
        }
        sightingHistory.clear()
        sightingHistory.addAll(results.distinctBy { it.id })
    }

    suspend fun fetchRondas(fecha: String? = null) {
        val path = if (!fecha.isNullOrBlank()) "/api/rondas?fecha=$fecha" else "/api/rondas"
        val res = MiauCareApiClient.request("GET", path)
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            rounds.clear()
            rounds.addAll(arr.map { parseRound(it) })
        }
    }

    suspend fun asistirARonda(rondaNumericId: Long): ApiResult<Unit> {
        val res = MiauCareApiClient.request("PATCH", "/api/rondas/$rondaNumericId/asistir")
        return when (res) {
            is ApiResult.Success -> {
                showToast("Te inscribiste a la ronda exitosamente")
                fetchRondas()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchMisNotificaciones() {
        val res = MiauCareApiClient.request("GET", "/api/notificaciones/mias")
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            notifications.clear()
            notifications.addAll(arr.map { parseNotificacion(it) })
        }
    }

    // =========================================================================
    // 4. FLUJO VETERINARIO (`/api/veterinarios`, `/api/alertas-medicas`, `/api/historial-clinico`, `/api/cirugias-ces`)
    // =========================================================================
    suspend fun enviarSolicitudVeterinario(
        nombreCompleto: String,
        correo: String,
        contrasena: String,
        numeroCedula: String,
        especialidadId: Long,
        cedulaImageBytes: ByteArray?
    ): ApiResult<Unit> {
        isLoading = true
        var fotoCedulaUrl = "https://gnzssoqdufdexeixtwdj.supabase.co/storage/v1/object/public/miaucare-archivos/cedulas/cedula_pendiente.jpg"
        if (cedulaImageBytes != null) {
            when (val up = MiauCareApiClient.subirArchivo(
                bytes = cedulaImageBytes,
                fileName = "cedula_${numeroCedula.trim()}_${System.currentTimeMillis()}.jpg",
                carpeta = "cedulas"
            )) {
                is ApiResult.Success -> fotoCedulaUrl = up.data
                is ApiResult.Error -> {
                    isLoading = false
                    showToast(up.message, isError = true)
                    return up
                }
            }
        }

        val body = JSONObject().apply {
            put("nombreCompleto", nombreCompleto.trim())
            put("correo", correo.trim())
            put("contrasena", contrasena)
            put("numeroCedula", numeroCedula.trim())
            put("fotoCedulaUrl", fotoCedulaUrl)
            put("especialidad", JSONObject().put("id", especialidadId))
        }
        val res = MiauCareApiClient.request("POST", "/api/veterinarios/solicitud", body.toString())
        isLoading = false
        return when (res) {
            is ApiResult.Success -> {
                showToast("Solicitud enviada · Pendiente de aprobación por Admin")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchVetDashboardData() {
        // 1. Resumen KPIs
        val dashRes = MiauCareApiClient.request("GET", "/api/dashboard/resumen")
        if (dashRes is ApiResult.Success) {
            parseDashboardResumen(JSONObject(dashRes.data))
        }
        // 2. Alertas médicas nuevas + asignadas al veterinario
        fetchAlertasMedicasVet()
        // 3. Pacientes asignados al veterinario (`GET /api/gatos/veterinario/{id}`)
        if (sessionUserId > 0L) {
            val pacRes = MiauCareApiClient.request("GET", "/api/gatos/veterinario/$sessionUserId")
            if (pacRes is ApiResult.Success) {
                val arr = JSONArray(pacRes.data).toJSONObjectList()
                vetAssignedCats.clear()
                vetAssignedCats.addAll(arr.map { parseCat(it) })
            }
            // 4. Perfil del veterinario
            val profRes = MiauCareApiClient.request("GET", "/api/veterinarios/$sessionUserId")
            if (profRes is ApiResult.Success) {
                val json = JSONObject(profRes.data)
                sessionUserName = json.optString("nombreCompleto", sessionUserName)
                vetCedula = json.optStringOrEmpty("numeroCedula")
                vetDisponibleEmergencias = json.optBoolean("disponibleEmergencias", true)
                vetEstadoCuenta = json.optString("estado", "ACTIVO")
                val espObj = json.optJSONObject("especialidad")
                vetSpecialtyId = espObj?.optLongOrNull("id")
                vetSpecialtyName = espObj?.optStringOrEmpty("nombre")?.ifBlank { "Cirugía CES" } ?: "Cirugía CES"
            }
        }
        // 5. Cirugías CES del veterinario
        fetchCirugiasCesMias()
        // 6. Colegas disponibles
        fetchVeterinariosDirectorio()
        // 7. Censo general
        fetchGatos()
    }

    suspend fun fetchAlertasMedicasVet() {
        val combined = mutableListOf<MedicalAlertItem>()
        val nuevasRes = MiauCareApiClient.request("GET", "/api/alertas-medicas/nuevas")
        if (nuevasRes is ApiResult.Success) {
            val arr = JSONArray(nuevasRes.data).toJSONObjectList()
            combined.addAll(arr.map { parseMedicalAlert(it) })
        }
        if (sessionUserId > 0L) {
            val miasRes = MiauCareApiClient.request("GET", "/api/alertas-medicas/veterinario/$sessionUserId")
            if (miasRes is ApiResult.Success) {
                val arr = JSONArray(miasRes.data).toJSONObjectList()
                combined.addAll(arr.map { parseMedicalAlert(it) })
            }
        }
        medicalAlerts.clear()
        medicalAlerts.addAll(combined.distinctBy { it.numericId })
    }

    suspend fun aceptarAlertaMedica(alertaNumericId: Long): ApiResult<Unit> {
        val body = JSONObject().put("veterinarioId", sessionUserId)
        val res = MiauCareApiClient.request("PATCH", "/api/alertas-medicas/$alertaNumericId/aceptar", body.toString())
        return handleVetProtectedResult(res, "Caso aceptado y vinculado a tu cargo") {
            // Also move to EN_ATENCION automatically if desired
            MiauCareApiClient.request("PATCH", "/api/alertas-medicas/$alertaNumericId/en-atencion", body.toString())
            fetchVetDashboardData()
        }
    }

    suspend fun pasarAlertaEnAtencion(alertaNumericId: Long): ApiResult<Unit> {
        val body = JSONObject().put("veterinarioId", sessionUserId)
        val res = MiauCareApiClient.request("PATCH", "/api/alertas-medicas/$alertaNumericId/en-atencion", body.toString())
        return handleVetProtectedResult(res, "Alerta en atención") {
            fetchAlertasMedicasVet()
        }
    }

    suspend fun delegarAlertaMedica(alertaNumericId: Long, destinoVetId: Long): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("veterinarioId", sessionUserId)
            put("destinoId", destinoVetId)
        }
        val res = MiauCareApiClient.request("PATCH", "/api/alertas-medicas/$alertaNumericId/delegar", body.toString())
        return handleVetProtectedResult(res, "Caso delegado exitosamente") {
            fetchAlertasMedicasVet()
        }
    }

    suspend fun cerrarAlertaMedica(
        alertaNumericId: Long,
        resolucion: String,
        observacion: String
    ): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("veterinarioId", sessionUserId)
            put("resolucion", resolucion.trim())
            put("observacion", observacion.trim())
        }
        val res = MiauCareApiClient.request("PATCH", "/api/alertas-medicas/$alertaNumericId/cerrar", body.toString())
        return handleVetProtectedResult(res, "Alerta médica cerrada") {
            fetchAlertasMedicasVet()
        }
    }

    /**
     * POST /api/historial-clinico (solo Veterinario):
     * envía gato: {"id"}, tipo ("DIAGNOSTICO" o "PROCEDIMIENTO"), diagnostico: {"id"},
     * nivelGravedad ("LEVE", "MODERADA", "CRITICA"), descripcion, notas, pesoRegistrado
     * y dejarHospitalizado (true/false).
     */
    suspend fun registrarHistorialClinico(
        gatoNumericId: Long,
        tipoBackend: String,
        diagnosticoId: Long?,
        nivelGravedadBackend: String,
        descripcion: String,
        notas: String,
        pesoRegistrado: Double?,
        dejarHospitalizado: Boolean
    ): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("gato", JSONObject().put("id", gatoNumericId))
            put("tipo", tipoBackend) // "DIAGNOSTICO" o "PROCEDIMIENTO"
            if (diagnosticoId != null) {
                put("diagnostico", JSONObject().put("id", diagnosticoId))
            }
            put("nivelGravedad", nivelGravedadBackend) // "LEVE", "MODERADA", "CRITICA"
            put("descripcion", descripcion.trim())
            put("notas", notas.trim())
            if (pesoRegistrado != null && pesoRegistrado > 0.0) {
                put("pesoRegistrado", pesoRegistrado)
            }
            put("dejarHospitalizado", dejarHospitalizado)
        }
        val res = MiauCareApiClient.request("POST", "/api/historial-clinico", body.toString())
        return handleVetProtectedResult(res, "Atención clínica registrada") {
            fetchGatoDetalleYHistorial("cat-$gatoNumericId")
            fetchVetDashboardData()
        }
    }

    suspend fun fetchCirugiasCesMias(fecha: String? = null) {
        val path = if (!fecha.isNullOrBlank()) "/api/cirugias-ces/mias?fecha=$fecha" else "/api/cirugias-ces/mias"
        val res = MiauCareApiClient.request("GET", path)
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            cirugiasCes.clear()
            cirugiasCes.addAll(arr.map { parseCirugiaCes(it) })
        } else {
            val allRes = MiauCareApiClient.request("GET", "/api/cirugias-ces")
            if (allRes is ApiResult.Success) {
                val arr = JSONArray(allRes.data).toJSONObjectList()
                cirugiasCes.clear()
                cirugiasCes.addAll(arr.map { parseCirugiaCes(it) })
            }
        }
    }

    suspend fun programarCirugiaCes(
        gatoNumericId: Long,
        fechaIso: String,
        horaInicioIso: String,
        horaFinIso: String,
        quirofano: String
    ): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("gato", JSONObject().put("id", gatoNumericId))
            put("fecha", fechaIso)
            put("horaInicio", if (horaInicioIso.length == 5) "$horaInicioIso:00" else horaInicioIso)
            put("horaFin", if (horaFinIso.length == 5) "$horaFinIso:00" else horaFinIso)
            put("quirofano", quirofano.trim())
        }
        val res = MiauCareApiClient.request("POST", "/api/cirugias-ces", body.toString())
        return handleVetProtectedResult(res, "Cirugía CES programada") {
            fetchCirugiasCesMias()
        }
    }

    suspend fun iniciarCirugiaCes(cirugiaId: Long): ApiResult<Unit> {
        val res = MiauCareApiClient.request("PATCH", "/api/cirugias-ces/$cirugiaId/iniciar")
        return handleVetProtectedResult(res, "Cirugía CES en curso") {
            fetchCirugiasCesMias()
        }
    }

    suspend fun completarCirugiaCes(cirugiaId: Long, resultado: String, darDeAlta: Boolean): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("resultado", resultado.trim())
            put("darDeAlta", darDeAlta)
        }
        val res = MiauCareApiClient.request("PATCH", "/api/cirugias-ces/$cirugiaId/completar", body.toString())
        return handleVetProtectedResult(res, "Cirugía CES completada · Gato esterilizado") {
            fetchCirugiasCesMias()
            fetchGatos()
        }
    }

    suspend fun fetchVeterinariosDirectorio(especialidadId: Long? = null) {
        val path = if (especialidadId != null) "/api/veterinarios?especialidadId=$especialidadId" else "/api/veterinarios"
        val res = MiauCareApiClient.request("GET", path)
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            doctors.clear()
            doctors.addAll(arr.map { parseDoctor(it) })
        }
    }

    suspend fun cambiarDisponibilidadEmergencias(disponible: Boolean): ApiResult<Unit> {
        if (sessionUserId <= 0L) return ApiResult.Error(400, "Sesión no válida")
        val res = MiauCareApiClient.request(
            "PATCH",
            "/api/veterinarios/$sessionUserId/disponibilidad?disponible=$disponible"
        )
        return when (res) {
            is ApiResult.Success -> {
                vetDisponibleEmergencias = disponible
                showToast(if (disponible) "Disponible para emergencias" else "Disponibilidad desactivada")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    private suspend fun handleVetProtectedResult(
        res: ApiResult<String>,
        successToast: String,
        onSuccessRefresh: suspend () -> Unit
    ): ApiResult<Unit> {
        return when (res) {
            is ApiResult.Success -> {
                showToast(successToast)
                onSuccessRefresh()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                if (res.statusCode == 403) {
                    // Regla: "En alertas médicas y cirugías CES, si un veterinario no es el responsable ni delegado
                    // autorizado del gato y el backend responde 403, muestra la pantalla/modal 'Sin permisos sobre este gato'."
                    sinPermisosModalMessage = res.message
                } else {
                    showToast(res.message, isError = true)
                }
                res
            }
        }
    }

    // =========================================================================
    // 5. FLUJO ADMINISTRADOR (`/api/dashboard/resumen`, `/api/gatos`, `/api/solicitudes-gato`, `/api/rondas`, `/api/indicadores`, `/api/auditoria`, `/api/parametros`)
    // =========================================================================
    suspend fun fetchAdminDashboardData() {
        val dashRes = MiauCareApiClient.request("GET", "/api/dashboard/resumen")
        if (dashRes is ApiResult.Success) {
            parseDashboardResumen(JSONObject(dashRes.data))
        }
        fetchGatos()
        fetchZonasResumen()
        fetchCatalogosMaestros()
        fetchSolicitudesGatoPendientes()
        fetchRegistrosPendientesAprobacion()
        fetchUsuariosEquipo()
    }

    suspend fun fetchSolicitudesGatoPendientes() {
        val res = MiauCareApiClient.request("GET", "/api/solicitudes-gato/pendientes")
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            solicitudesGatoPendientes.clear()
            solicitudesGatoPendientes.addAll(arr.map { parseSolicitudGato(it) })
        }
    }

    suspend fun resolverSolicitudGato(id: Long, aceptar: Boolean): ApiResult<Unit> {
        val action = if (aceptar) "aceptar" else "rechazar"
        val res = MiauCareApiClient.request("PATCH", "/api/solicitudes-gato/$id/$action")
        return when (res) {
            is ApiResult.Success -> {
                showToast(if (aceptar) "Solicitud aceptada y aplicada al gato" else "Solicitud rechazada")
                fetchSolicitudesGatoPendientes()
                fetchGatos()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchRegistrosPendientesAprobacion() {
        val gatosRes = MiauCareApiClient.request("GET", "/api/gatos/pendientes")
        if (gatosRes is ApiResult.Success) {
            val arr = JSONArray(gatosRes.data).toJSONObjectList()
            pendingApprovals.clear()
            pendingApprovals.addAll(arr.map { parsePendingCat(it) })
        }
        val vetsRes = MiauCareApiClient.request("GET", "/api/veterinarios/pendientes")
        if (vetsRes is ApiResult.Success) {
            val arr = JSONArray(vetsRes.data).toJSONObjectList()
            pendingVetApprovals.clear()
            pendingVetApprovals.addAll(arr.map { parseDoctor(it) })
        }
    }

    suspend fun aprobarORechazarGato(gatoNumericId: Long, aprobar: Boolean): ApiResult<Unit> {
        val action = if (aprobar) "aprobar" else "rechazar"
        val res = MiauCareApiClient.request("PATCH", "/api/gatos/$gatoNumericId/$action")
        return when (res) {
            is ApiResult.Success -> {
                showToast(if (aprobar) "Registro de gato aprobado" else "Registro de gato rechazado")
                fetchRegistrosPendientesAprobacion()
                fetchGatos()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun aprobarORechazarVeterinario(vetNumericId: Long, aprobar: Boolean): ApiResult<Unit> {
        val action = if (aprobar) "aprobar" else "rechazar"
        val res = MiauCareApiClient.request("PATCH", "/api/veterinarios/$vetNumericId/$action")
        return when (res) {
            is ApiResult.Success -> {
                showToast(if (aprobar) "Solicitud de veterinario aprobada" else "Solicitud de veterinario rechazada")
                fetchRegistrosPendientesAprobacion()
                fetchUsuariosEquipo()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun crearOActualizarGato(
        existingNumericId: Long?,
        codigo: String,
        nombre: String,
        sexoUi: String,
        edadEstimada: String,
        raza: String,
        peso: Double,
        condicionesEspeciales: String,
        zonaId: Long,
        estadoInternacionBackend: String,
        esterilizado: Boolean,
        currentFotoUrl: String?,
        newPhotoBytes: ByteArray?
    ): ApiResult<CatProfile> {
        var finalFotoUrl = currentFotoUrl
        if (newPhotoBytes != null) {
            when (val up = MiauCareApiClient.subirArchivo(
                bytes = newPhotoBytes,
                fileName = "gato_${codigo.trim()}_${System.currentTimeMillis()}.jpg",
                carpeta = "gatos"
            )) {
                is ApiResult.Success -> finalFotoUrl = up.data
                is ApiResult.Error -> {
                    showToast(up.message, isError = true)
                    return up
                }
            }
        }

        val sexoBackend = if (sexoUi.uppercase().startsWith("H")) "HEMBRA" else "MACHO"
        val body = JSONObject().apply {
            put("codigo", codigo.trim())
            put("nombre", nombre.trim())
            put("sexo", sexoBackend)
            put("edadEstimada", edadEstimada.trim())
            put("raza", raza.trim())
            put("peso", peso)
            put("condicionesEspeciales", condicionesEspeciales.trim())
            put("zona", JSONObject().put("id", zonaId))
            put("esterilizado", esterilizado)
            put("estadoVida", "ACTIVO")
            put("estadoInternacion", estadoInternacionBackend)
            if (!finalFotoUrl.isNullOrBlank()) {
                put("fotoUrl", finalFotoUrl)
            }
        }

        val res = if (existingNumericId == null || existingNumericId <= 0L) {
            MiauCareApiClient.request("POST", "/api/gatos", body.toString())
        } else {
            MiauCareApiClient.request("PUT", "/api/gatos/$existingNumericId", body.toString())
        }

        return when (res) {
            is ApiResult.Success -> {
                val parsed = parseCat(JSONObject(res.data))
                fetchGatos()
                showToast(if (existingNumericId == null) "Gato registrado exitosamente" else "Ficha actualizada")
                ApiResult.Success(parsed)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    /**
     * Regla Gato FALLECIDO:
     * PATCH /api/gatos/{id}/estado-vida con {"estadoVida": "ACTIVO" | "FALLECIDO" | "ADOPTADO" | "TRASLADADO"}
     * Una vez marcado como FALLECIDO, cualquier intento de editarlo, agregarle historial o volverlo a ACTIVO
     * devuelve 409 Conflict.
     */
    suspend fun cambiarEstadoVidaGato(gatoNumericId: Long, nuevoEstadoBackend: String): ApiResult<CatProfile> {
        val body = JSONObject().put("estadoVida", nuevoEstadoBackend.uppercase())
        val res = MiauCareApiClient.request("PATCH", "/api/gatos/$gatoNumericId/estado-vida", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                val parsed = parseCat(JSONObject(res.data))
                fetchGatos()
                showToast("Estado de vida actualizado a ${parsed.lifeStatus}")
                ApiResult.Success(parsed)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun eliminarGato(gatoNumericId: Long): ApiResult<Unit> {
        val res = MiauCareApiClient.request("DELETE", "/api/gatos/$gatoNumericId")
        return when (res) {
            is ApiResult.Success -> {
                cats.removeAll { it.numericId == gatoNumericId }
                showToast("Ficha eliminada del sistema")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchUsuariosEquipo() {
        val combined = mutableListOf<TeamUserItem>()
        val vetsRes = MiauCareApiClient.request("GET", "/api/veterinarios")
        if (vetsRes is ApiResult.Success) {
            val arr = JSONArray(vetsRes.data).toJSONObjectList()
            doctors.clear()
            doctors.addAll(arr.map { parseDoctor(it) })
            combined.addAll(
                arr.map {
                    val id = it.optLong("id")
                    val estado = it.optString("estado", "ACTIVO")
                    TeamUserItem(
                        numericId = id,
                        id = "vet-$id",
                        name = it.optStringOrEmpty("nombreCompleto"),
                        correo = it.optStringOrEmpty("correo"),
                        roleLabel = "Veterinario",
                        isVeterinario = true,
                        estadoBackend = estado,
                        isActive = estado == "ACTIVO"
                    )
                }
            )
        }
        val volsRes = MiauCareApiClient.request("GET", "/api/voluntarios")
        if (volsRes is ApiResult.Success) {
            val arr = JSONArray(volsRes.data).toJSONObjectList()
            combined.addAll(
                arr.map {
                    val id = it.optLong("id")
                    val estado = it.optString("estado", "ACTIVO")
                    TeamUserItem(
                        numericId = id,
                        id = "vol-$id",
                        name = it.optStringOrEmpty("nombreCompleto"),
                        correo = it.optStringOrEmpty("correo"),
                        roleLabel = "Voluntario",
                        isVeterinario = false,
                        estadoBackend = estado,
                        isActive = estado == "ACTIVO"
                    )
                }
            )
        }
        teamUsers.clear()
        teamUsers.addAll(combined)
    }

    suspend fun cambiarEstadoUsuario(user: TeamUserItem, activo: Boolean): ApiResult<Unit> {
        val prefix = if (user.isVeterinario) "/api/veterinarios" else "/api/voluntarios"
        val res = MiauCareApiClient.request("PATCH", "$prefix/${user.numericId}/estado?activo=$activo")
        return when (res) {
            is ApiResult.Success -> {
                user.isActive = activo
                showToast("${user.name}: ${if (activo) "Activo" else "Inactivo"}")
                fetchUsuariosEquipo()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun enviarNotificacionGlobal(
        titulo: String,
        mensaje: String,
        destinatariosUi: String
    ): ApiResult<Unit> {
        val destBackend = when (destinatariosUi.uppercase()) {
            "VETERINARIOS" -> "VETERINARIOS"
            "VOLUNTARIOS" -> "VOLUNTARIOS"
            else -> "AMBOS"
        }
        val body = JSONObject().apply {
            put("titulo", titulo.trim())
            put("mensaje", mensaje.trim())
            put("destinatarios", destBackend)
        }
        val res = MiauCareApiClient.request("POST", "/api/notificaciones", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Notificación global enviada a $destinatariosUi")
                fetchNotificacionesGlobales()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchNotificacionesGlobales() {
        val res = MiauCareApiClient.request("GET", "/api/notificaciones")
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            notifications.clear()
            notifications.addAll(arr.map { parseNotificacion(it) })
        }
    }

    /**
     * Regla RN-01: Cruce de horario en Rondas:
     * Si POST /api/rondas o PUT /api/rondas/{id} devuelve 409 Conflict,
     * lee `error` y el objeto `rondaExistente` del JSON de respuesta para mostrar
     * el modal "Cruce de horario detectado" con los datos de la ronda en conflicto.
     */
    suspend fun programarRondaAdmin(
        zonaId: Long,
        fechaIso: String,
        horaInicioIso: String,
        horaFinIso: String,
        veterinarioId: Long
    ): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("zona", JSONObject().put("id", zonaId))
            put("fecha", fechaIso.trim())
            put("horaInicio", if (horaInicioIso.length == 5) "$horaInicioIso:00" else horaInicioIso)
            put("horaFin", if (horaFinIso.length == 5) "$horaFinIso:00" else horaFinIso)
            put("veterinario", JSONObject().put("id", veterinarioId))
        }
        val res = MiauCareApiClient.request("POST", "/api/rondas", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Ronda programada exitosamente")
                fetchRondas()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                if (res.statusCode == 409 && res.errorJson?.has("rondaExistente") == true && !res.errorJson.isNull("rondaExistente")) {
                    val rObj = res.errorJson.optJSONObject("rondaExistente")
                    val zonaObj = rObj?.optJSONObject("zona")
                    val vetObj = rObj?.optJSONObject("veterinario")
                    conflictoHorarioModal = ConflictoHorarioInfo(
                        errorMessage = res.message,
                        rondaId = rObj?.optLongOrNull("id"),
                        zonaNombre = zonaObj?.optStringOrEmpty("nombre")?.ifBlank { "Zona asignada" } ?: "Zona asignada",
                        fecha = rObj?.optStringOrEmpty("fecha") ?: fechaIso,
                        horaInicio = rObj?.optStringOrEmpty("horaInicio")?.take(5) ?: horaInicioIso,
                        horaFin = rObj?.optStringOrEmpty("horaFin")?.take(5) ?: horaFinIso,
                        veterinarioNombre = vetObj?.optStringOrEmpty("nombreCompleto")?.ifBlank { "Veterinario asignado" } ?: "Veterinario asignado",
                        estado = rObj?.optStringOrEmpty("estado") ?: "PROGRAMADA"
                    )
                } else {
                    showToast(res.message, isError = true)
                }
                res
            }
        }
    }

    suspend fun fetchIndicadoresZonas(zonaId: Long? = null) {
        val path = if (zonaId != null) "/api/indicadores?zonaId=$zonaId" else "/api/indicadores"
        val res = MiauCareApiClient.request("GET", path)
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            zoneIndicators.clear()
            zoneIndicators.addAll(
                arr.map {
                    ZoneIndicatorDto(
                        zonaId = it.optLong("zonaId"),
                        nombreZona = it.optStringOrEmpty("nombreZona"),
                        zonaMacro = it.optStringOrEmpty("zonaMacro").ifBlank { "Colonia" },
                        gatosCensados = it.optInt("gatosCensados", 0),
                        gatosEsterilizados = it.optInt("gatosEsterilizados", 0),
                        gatosHospitalizados = it.optInt("gatosHospitalizados", 0),
                        alertasActivas = it.optInt("alertasActivas", 0),
                        rondasCompletadas = it.optInt("rondasCompletadas", 0)
                    )
                }
            )
        }
    }

    suspend fun exportarIndicadoresCsv(zonaId: Long? = null): ApiResult<String> {
        val path = if (zonaId != null) "/api/indicadores/exportar-csv?zonaId=$zonaId" else "/api/indicadores/exportar-csv"
        val res = MiauCareApiClient.request("GET", path)
        return when (res) {
            is ApiResult.Success -> {
                exportedCsvPreview = res.data
                showToast("CSV exportado exitosamente")
                res
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchAuditoria(tipoAccion: String? = null, buscar: String? = null) {
        val params = buildList {
            if (!tipoAccion.isNullOrBlank() && tipoAccion != "Todas") add("tipoAccion=$tipoAccion")
            if (!buscar.isNullOrBlank()) add("buscar=${buscar.trim()}")
        }.joinToString("&")
        val path = if (params.isNotEmpty()) "/api/auditoria?$params" else "/api/auditoria"
        val res = MiauCareApiClient.request("GET", path)
        if (res is ApiResult.Success) {
            val arr = JSONArray(res.data).toJSONObjectList()
            auditLogs.clear()
            auditLogs.addAll(arr.map { parseAuditLog(it) })
        }
    }

    suspend fun crearOActualizarZona(
        id: Long?,
        nombre: String,
        zonaMacro: String,
        puntoAlimentacion: String,
        horaAlimentacion: String,
        latitud: Double = 4.6533,
        longitud: Double = -74.0836
    ): ApiResult<Unit> {
        val horaIso = if (horaAlimentacion.length == 5) "$horaAlimentacion:00" else horaAlimentacion
        val body = JSONObject().apply {
            put("nombre", nombre.trim())
            put("zonaMacro", zonaMacro.trim())
            put("puntoAlimentacion", puntoAlimentacion.trim())
            put("horaAlimentacion", horaIso)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        val res = if (id == null) {
            MiauCareApiClient.request("POST", "/api/zonas", body.toString())
        } else {
            MiauCareApiClient.request("PUT", "/api/zonas/$id", body.toString())
        }
        return when (res) {
            is ApiResult.Success -> {
                showToast(if (id == null) "Colonia creada" else "Colonia actualizada")
                fetchZonasResumen()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun eliminarZona(zonaId: Long): ApiResult<Unit> {
        val res = MiauCareApiClient.request("DELETE", "/api/zonas/$zonaId")
        return when (res) {
            is ApiResult.Success -> {
                colonies.removeAll { it.numericId == zonaId }
                showToast("Colonia eliminada")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun crearItemCatalogo(segmento: String, nombre: String): ApiResult<Unit> {
        val endpoint = when (segmento) {
            "Diagnósticos" -> "/api/diagnosticos"
            "Especialidades", "Estados" -> "/api/especialidades"
            else -> "/api/zonas"
        }
        val body = if (endpoint == "/api/zonas") {
            JSONObject().apply {
                put("nombre", nombre.trim())
                put("zonaMacro", nombre.trim())
                put("puntoAlimentacion", "Punto central")
                put("horaAlimentacion", "09:00:00")
                put("latitud", 4.65)
                put("longitud", -74.08)
            }
        } else {
            JSONObject().put("nombre", nombre.trim())
        }
        val res = MiauCareApiClient.request("POST", endpoint, body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Elemento añadido a $segmento")
                fetchCatalogosMaestros()
                fetchZonasResumen()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun eliminarItemCatalogo(segmento: String, id: Long): ApiResult<Unit> {
        val endpoint = when (segmento) {
            "Diagnósticos" -> "/api/diagnosticos/$id"
            "Especialidades", "Estados" -> "/api/especialidades/$id"
            else -> "/api/zonas/$id"
        }
        val res = MiauCareApiClient.request("DELETE", endpoint)
        return when (res) {
            is ApiResult.Success -> {
                showToast("Elemento eliminado")
                fetchCatalogosMaestros()
                fetchZonasResumen()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun reasignarPacientesMasivo(origenVetId: Long, destinoVetId: Long): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("veterinarioOrigenId", origenVetId)
            put("veterinarioDestinoId", destinoVetId)
        }
        val res = MiauCareApiClient.request("POST", "/api/gatos/reasignar-masivo", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Pacientes reasignados exitosamente")
                fetchGatos()
                fetchUsuariosEquipo()
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun fetchParametrosSistema() {
        val res = MiauCareApiClient.request("GET", "/api/parametros")
        if (res is ApiResult.Success) {
            val json = JSONObject(res.data)
            daysWithoutSightingX = json.optInt("diasAusenciaAlerta", 7).toString()
            consecutiveCriticalN = json.optInt("reportesCriticosConsecutivos", 2).toString()
            maxResponseHoursT = json.optInt("tiempoMaximoRespuestaHoras", 24).toString()
        }
    }

    suspend fun guardarParametrosSistema(): ApiResult<Unit> {
        val body = JSONObject().apply {
            put("diasAusenciaAlerta", daysWithoutSightingX.toIntOrNull() ?: 7)
            put("reportesCriticosConsecutivos", consecutiveCriticalN.toIntOrNull() ?: 2)
            put("tiempoMaximoRespuestaHoras", maxResponseHoursT.toIntOrNull() ?: 24)
        }
        val res = MiauCareApiClient.request("PUT", "/api/parametros", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                showToast("Parámetros del sistema actualizados")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    suspend fun actualizarPerfilAdmin(nombre: String, correo: String): ApiResult<Unit> {
        if (sessionUserId <= 0L) return ApiResult.Error(400, "Sesión no válida")
        val body = JSONObject().apply {
            put("nombreCompleto", nombre.trim())
            put("correo", correo.trim())
        }
        val res = MiauCareApiClient.request("PUT", "/api/administradores/$sessionUserId", body.toString())
        return when (res) {
            is ApiResult.Success -> {
                sessionUserName = nombre.trim()
                sessionUserEmail = correo.trim()
                showToast("Perfil de administrador actualizado")
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> {
                showToast(res.message, isError = true)
                res
            }
        }
    }

    // =========================================================================
    // JSON PARSERS
    // =========================================================================
    private fun parseDashboardResumen(json: JSONObject) {
        dashUrgenciasHoy = json.optInt("urgenciasHoy", 0)
        dashCasosActivos = json.optInt("casosActivos", 0)
        dashPacientesInternados = json.optInt("pacientesInternados", 0)
        dashGatosCensadosTotales = json.optInt("gatosCensadosTotales", 0)
        dashVeterinariosActivos = json.optInt("veterinariosActivos", 0)
        dashSolicitudesPendientes = json.optInt("solicitudesPendientes", 0)
    }

    private fun parseCat(json: JSONObject): CatProfile {
        val numId = json.optLong("id", 0L)
        val sexoRaw = json.optString("sexo", "MACHO")
        val sexoUi = if (sexoRaw.equals("HEMBRA", ignoreCase = true)) "Hembra" else "Macho"
        val esterilizado = json.optBoolean("esterilizado", false)
        val internacionRaw = json.optString("estadoInternacion", "EN_CALLE")
        val vidaRaw = json.optString("estadoVida", "ACTIVO")
        val statusUi = when {
            vidaRaw.equals("FALLECIDO", ignoreCase = true) -> "Fallecido"
            internacionRaw.equals("HOSPITALIZADO", ignoreCase = true) -> "Hospitalizado"
            else -> "En calle"
        }
        val lifeUi = when (vidaRaw.uppercase()) {
            "FALLECIDO" -> "Fallecido"
            "ADOPTADO" -> "Adoptado"
            "TRASLADADO" -> "Trasladado"
            else -> "Activo"
        }
        val zonaObj = json.optJSONObject("zona")
        val vetObj = json.optJSONObject("veterinarioResponsable")
        val pesoVal = json.optDoubleOrNull("peso") ?: 3.8

        return CatProfile(
            numericId = numId,
            id = "cat-$numId",
            code = json.optStringOrEmpty("codigo").ifBlank { "MC-00$numId" },
            name = json.optStringOrEmpty("nombre").ifBlank { "Gato #$numId" },
            breed = json.optStringOrEmpty("raza").ifBlank { "Mestizo" },
            sex = sexoUi,
            age = json.optStringOrEmpty("edadEstimada").ifBlank { "2 años" },
            weightKg = String.format(java.util.Locale.US, "%.1f", pesoVal),
            sterilizationStatus = if (esterilizado) "Esterilizado" else "Pendiente CES",
            isSterilized = esterilizado,
            colony = zonaObj?.optStringOrEmpty("nombre")?.ifBlank { "Colonia Centro" } ?: "Colonia Centro",
            zoneId = zonaObj?.optLongOrNull("id"),
            statusTag = statusUi,
            estadoInternacion = internacionRaw,
            lifeStatus = lifeUi,
            estadoVidaBackend = vidaRaw,
            estadoAprobacion = json.optString("estadoAprobacion", "APROBADO"),
            condicionesEspeciales = json.optStringOrEmpty("condicionesEspeciales"),
            fotoUrl = json.optStringOrEmpty("fotoUrl").takeIf { it.startsWith("http") },
            veterinarioResponsableId = vetObj?.optLongOrNull("id"),
            veterinarioResponsableNombre = vetObj?.optStringOrEmpty("nombreCompleto"),
            imageRes = pickCatDrawable(numId)
        )
    }

    private fun parsePendingCat(json: JSONObject): PendingCatRecord {
        val numId = json.optLong("id", 0L)
        val nombre = json.optStringOrEmpty("nombre").ifBlank { "Gato" }
        val codigo = json.optStringOrEmpty("codigo").ifBlank { "MC-$numId" }
        val raza = json.optStringOrEmpty("raza").ifBlank { "Mestizo" }
        val edad = json.optStringOrEmpty("edadEstimada").ifBlank { "1 año" }
        val sexo = if (json.optString("sexo").equals("HEMBRA", true)) "Hembra" else "Macho"
        val zonaObj = json.optJSONObject("zona")
        val vetObj = json.optJSONObject("veterinarioResponsable")
        return PendingCatRecord(
            numericId = numId,
            id = "pend-$numId",
            nameAndCode = "$nombre · $codigo",
            breedAgeSex = "$raza · $edad · $sexo",
            zone = zonaObj?.optStringOrEmpty("nombre")?.ifBlank { "Colonia" } ?: "Colonia",
            submittedByAndTime = vetObj?.optStringOrEmpty("nombreCompleto")?.ifBlank { "Pendiente de revisión" }
                ?: "Enviado para aprobación",
            isReviewed = json.optString("estadoAprobacion", "PENDIENTE") != "PENDIENTE",
            fotoUrl = json.optStringOrEmpty("fotoUrl").takeIf { it.startsWith("http") },
            imageRes = pickCatDrawable(numId)
        )
    }

    private fun parseColony(json: JSONObject): ColonyItem {
        val numId = json.optLong("id", 0L)
        val count = json.optInt("cantidadGatos", 0)
        val horaRaw = json.optStringOrEmpty("horaAlimentacion").take(5).ifBlank { "09:00" }
        return ColonyItem(
            numericId = numId,
            id = "col-$numId",
            name = json.optStringOrEmpty("nombre").ifBlank { "Colonia #$numId" },
            zoneName = json.optStringOrEmpty("zonaMacro").ifBlank { json.optStringOrEmpty("nombre") },
            address = json.optStringOrEmpty("puntoAlimentacion").ifBlank { "Punto de alimentación" },
            feedingTime = horaRaw,
            latitud = json.optDoubleOrNull("latitud"),
            longitud = json.optDoubleOrNull("longitud"),
            cantidadGatos = count,
            catsCountText = "$count gatos",
            ordenParada = if (json.has("ordenParada") && !json.isNull("ordenParada")) json.optInt("ordenParada") else null,
            distanciaKm = json.optDoubleOrNull("distanciaDesdePuntoAnteriorKm")
        )
    }

    private fun parseClinicalHistory(json: JSONObject): ClinicalHistoryEntry {
        val diagObj = json.optJSONObject("diagnostico")
        val vetObj = json.optJSONObject("veterinario")
        return ClinicalHistoryEntry(
            id = json.optLong("id", 0L),
            tipo = json.optString("tipo", "DIAGNOSTICO"),
            diagnosticoId = diagObj?.optLongOrNull("id"),
            diagnosticoNombre = diagObj?.optStringOrEmpty("nombre") ?: "",
            nivelGravedad = json.optString("nivelGravedad", "MODERADA"),
            descripcion = json.optStringOrEmpty("descripcion"),
            notas = json.optStringOrEmpty("notas"),
            pesoRegistrado = json.optDoubleOrNull("pesoRegistrado"),
            dejarHospitalizado = if (json.has("dejarHospitalizado") && !json.isNull("dejarHospitalizado")) {
                json.optBoolean("dejarHospitalizado")
            } else null,
            veterinarioNombre = vetObj?.optStringOrEmpty("nombreCompleto")?.ifBlank { "Veterinario MiauCare" }
                ?: "Veterinario MiauCare",
            fecha = json.optStringOrEmpty("fecha")
        )
    }

    private fun parseSighting(json: JSONObject): SightingRecord {
        val id = json.optLong("id", 0L)
        val gatoObj = json.optJSONObject("gato")
        val volObj = json.optJSONObject("voluntario")
        val esNoCensado = json.optBoolean("esGatoNoCensado", gatoObj == null)
        val catName = if (esNoCensado) "Gato no censado" else (gatoObj?.optStringOrEmpty("nombre") ?: "Gato")
        val estadoRaw = json.optString("estadoAparente", "SANO")
        val healthUi = when (estadoRaw.uppercase()) {
            "ENFERMO" -> "Enfermo"
            "HERIDO" -> "Herido"
            else -> "Sano"
        }
        val fechaRaw = json.optStringOrEmpty("fecha")
        val datePart = fechaRaw.take(10).ifBlank { "Reciente" }
        val timePart = if (fechaRaw.length >= 16) fechaRaw.substring(11, 16) else ""
        return SightingRecord(
            id = "sig-$id",
            catName = catName,
            dateGroup = datePart,
            dateTime = if (timePart.isNotEmpty()) "$datePart · $timePart" else datePart,
            healthStatus = healthUi,
            ateFood = json.optBoolean("comio", false),
            reportedBy = "Reportado por ${volObj?.optStringOrEmpty("nombreCompleto")?.ifBlank { "Voluntario" } ?: "Voluntario"}",
            observaciones = json.optStringOrEmpty("observaciones"),
            fotoUrl = json.optStringOrEmpty("fotoUrl").takeIf { it.startsWith("http") },
            esNoCensado = esNoCensado,
            imageRes = pickCatDrawable(gatoObj?.optLong("id") ?: id)
        )
    }

    private fun parseRound(json: JSONObject): RoundScheduleItem {
        val numId = json.optLong("id", 0L)
        val zonaObj = json.optJSONObject("zona")
        val vetObj = json.optJSONObject("veterinario")
        val volsArr = json.optJSONArray("voluntarios")?.toJSONObjectList().orEmpty()
        val hi = json.optStringOrEmpty("horaInicio").take(5).ifBlank { "09:00" }
        val hf = json.optStringOrEmpty("horaFin").take(5).ifBlank { "11:00" }
        val initials = volsArr.map { v ->
            val parts = v.optStringOrEmpty("nombreCompleto").trim().split(" ")
            parts.take(2).joinToString("") { it.take(1).uppercase() }.ifBlank { "VO" }
        }.ifEmpty { listOf("MC") }
        val joined = volsArr.any {
            it.optLong("id") == sessionUserId ||
                it.optStringOrEmpty("correo").equals(sessionUserEmail, ignoreCase = true)
        }
        return RoundScheduleItem(
            numericId = numId,
            id = "rnd-$numId",
            fecha = json.optStringOrEmpty("fecha"),
            horaInicio = hi,
            horaFin = hf,
            timeRange = "$hi - $hf",
            roundType = if (vetObj != null) "Veterinaria" else "General",
            zoneId = zonaObj?.optLongOrNull("id"),
            zoneTitle = zonaObj?.optStringOrEmpty("nombre")?.ifBlank { "Zona MiauCare" } ?: "Zona MiauCare",
            description = "Responsable: ${vetObj?.optStringOrEmpty("nombreCompleto")?.ifBlank { "Equipo MiauCare" } ?: "Equipo MiauCare"}",
            veterinarioId = vetObj?.optLongOrNull("id"),
            veterinarioNombre = vetObj?.optStringOrEmpty("nombreCompleto") ?: "",
            initials = initials,
            peopleCountText = "${volsArr.size} voluntarios",
            estadoBackend = json.optString("estado", "PROGRAMADA"),
            isJoined = joined
        )
    }

    private fun parseMedicalAlert(json: JSONObject): MedicalAlertItem {
        val numId = json.optLong("id", 0L)
        val gatoObj = json.optJSONObject("gato")
        val vetObj = json.optJSONObject("veterinarioResponsable")
        val delegadosArr = json.optJSONArray("delegados")?.toJSONObjectList().orEmpty()
        val estadoRaw = json.optString("estado", "NUEVA")
        val tabUi = when (estadoRaw.uppercase()) {
            "EN_ATENCION" -> "En atención"
            "CERRADA" -> "Cerradas"
            else -> "Nuevas"
        }
        val catNumId = gatoObj?.optLongOrNull("id")
        val fechaRaw = json.optStringOrEmpty("fechaCreacion")
        val timeDisplay = if (fechaRaw.length >= 16) fechaRaw.substring(11, 16) else "Reciente"
        return MedicalAlertItem(
            numericId = numId,
            id = "al-$numId",
            catNumericId = catNumId,
            catId = if (catNumId != null) "cat-$catNumId" else "cat-0",
            catName = gatoObj?.optStringOrEmpty("nombre")?.ifBlank { "Gato en alerta" } ?: "Gato en alerta",
            timeAgo = timeDisplay,
            urgencyBadge = if (estadoRaw == "NUEVA") "Crítica" else estadoRaw,
            symptomTitle = json.optStringOrEmpty("motivo").ifBlank { "Alerta médica" },
            locationAndReporter = "${json.optStringOrEmpty("zonaDescripcion").ifBlank { "Colonia" }} · Reportó ${json.optStringOrEmpty("reportadoPor").ifBlank { "Voluntario" }}",
            tabStatus = tabUi,
            estadoBackend = estadoRaw,
            veterinarioResponsableId = vetObj?.optLongOrNull("id"),
            veterinarioResponsableNombre = vetObj?.optStringOrEmpty("nombreCompleto"),
            delegadosIds = delegadosArr.mapNotNull { it.optLongOrNull("id") },
            resolucion = json.optStringOrEmpty("resolucion"),
            observacionCierre = json.optStringOrEmpty("observacionCierre"),
            fotoUrl = gatoObj?.optStringOrEmpty("fotoUrl")?.takeIf { it.startsWith("http") },
            imageRes = pickCatDrawable(catNumId ?: numId)
        )
    }

    private fun parseDoctor(json: JSONObject): DoctorItem {
        val numId = json.optLong("id", 0L)
        val espObj = json.optJSONObject("especialidad")
        val disponible = json.optBoolean("disponibleEmergencias", true)
        val estado = json.optString("estado", "ACTIVO")
        return DoctorItem(
            numericId = numId,
            id = "doc-$numId",
            name = json.optStringOrEmpty("nombreCompleto").ifBlank { "Veterinario #$numId" },
            correo = json.optStringOrEmpty("correo"),
            numeroCedula = json.optStringOrEmpty("numeroCedula"),
            specialtyId = espObj?.optLongOrNull("id"),
            specialty = espObj?.optStringOrEmpty("nombre")?.ifBlank { "Medicina felina" } ?: "Medicina felina",
            distanceText = if (disponible) "Disponible para emergencias" else "No disponible",
            patientsLoadText = "Cédula ${json.optStringOrEmpty("numeroCedula")} · $estado",
            estadoBackend = estado,
            disponibleEmergencias = disponible,
            isInternal = true,
            fotoCedulaUrl = json.optStringOrEmpty("fotoCedulaUrl").takeIf { it.startsWith("http") },
            imageRes = pickDocDrawable(numId)
        )
    }

    private fun parseSolicitudGato(json: JSONObject): SolicitudGatoItem {
        val id = json.optLong("id", 0L)
        val gatoObj = json.optJSONObject("gato")
        val zonaObj = gatoObj?.optJSONObject("zona")
        val volObj = json.optJSONObject("voluntario")
        return SolicitudGatoItem(
            id = id,
            gatoId = gatoObj?.optLong("id", 0L) ?: 0L,
            gatoNombre = gatoObj?.optStringOrEmpty("nombre") ?: "Gato",
            gatoCodigo = gatoObj?.optStringOrEmpty("codigo") ?: "",
            zonaNombre = zonaObj?.optStringOrEmpty("nombre") ?: "Colonia",
            voluntarioNombre = volObj?.optStringOrEmpty("nombreCompleto") ?: "Voluntario",
            tipo = json.optString("tipo", "FALLECIMIENTO"),
            estado = json.optString("estado", "PENDIENTE"),
            fecha = json.optStringOrEmpty("fecha").take(16).replace("T", " ")
        )
    }

    private fun parseAuditLog(json: JSONObject): AuditLogItem {
        val id = json.optLong("id", 0L)
        val tipo = json.optStringOrEmpty("tipoAccion")
        val desc = json.optStringOrEmpty("descripcion")
        val user = json.optStringOrEmpty("usuarioResponsable")
        val fechaHora = json.optStringOrEmpty("fechaHora")
        val datePart = fechaHora.take(10).ifBlank { "Reciente" }
        val timePart = if (fechaHora.length >= 16) fechaHora.substring(11, 16) else ""
        val icon = when {
            tipo.contains("ELIMINAR", true) -> "delete"
            tipo.contains("CREAR", true) || tipo.contains("APROBAR", true) -> "add"
            tipo.contains("USUARIO", true) || tipo.contains("VOLUNTARIO", true) || tipo.contains("VETERINARIO", true) -> "user"
            else -> "settings"
        }
        return AuditLogItem(
            id = "aud-$id",
            dateGroup = datePart,
            title = desc.ifBlank { tipo },
            subtitle = "$user · $datePart $timePart",
            iconType = icon
        )
    }

    private fun parseNotificacion(json: JSONObject): NotificacionGlobalDto {
        return NotificacionGlobalDto(
            id = json.optLong("id", 0L),
            titulo = json.optStringOrEmpty("titulo"),
            mensaje = json.optStringOrEmpty("mensaje"),
            destinatarios = json.optString("destinatarios", "AMBOS"),
            creadoPor = json.optStringOrEmpty("creadoPor"),
            fechaCreacion = json.optStringOrEmpty("fechaCreacion").take(16).replace("T", " ")
        )
    }

    private fun parseCirugiaCes(json: JSONObject): CirugiaCesDto {
        val id = json.optLong("id", 0L)
        val gatoObj = json.optJSONObject("gato")
        val zonaObj = gatoObj?.optJSONObject("zona")
        val vetObj = json.optJSONObject("veterinario")
        return CirugiaCesDto(
            id = id,
            gatoId = gatoObj?.optLong("id", 0L) ?: 0L,
            gatoNombre = gatoObj?.optStringOrEmpty("nombre") ?: "Gato",
            gatoCodigo = gatoObj?.optStringOrEmpty("codigo") ?: "",
            gatoSexo = if (gatoObj?.optString("sexo").equals("HEMBRA", true)) "Hembra" else "Macho",
            coloniaNombre = zonaObj?.optStringOrEmpty("nombre") ?: "Colonia",
            veterinarioId = vetObj?.optLongOrNull("id"),
            veterinarioNombre = vetObj?.optStringOrEmpty("nombreCompleto") ?: "",
            fecha = json.optStringOrEmpty("fecha"),
            horaInicio = json.optStringOrEmpty("horaInicio").take(5),
            horaFin = json.optStringOrEmpty("horaFin").take(5),
            quirofano = json.optStringOrEmpty("quirofano"),
            estado = json.optString("estado", "PROGRAMADA"),
            resultado = json.optStringOrEmpty("resultado")
        )
    }
}
