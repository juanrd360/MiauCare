package com.example.miaucare.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.R
import com.example.miaucare.data.ApiResult
import com.example.miaucare.data.CatProfile
import com.example.miaucare.data.MiauCareApiClient
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.components.*
import com.example.miaucare.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// =========================================================================
// 1. SPLASH INICIO (`Splash Inicio` - Pantalla de carga Figma)
// =========================================================================
@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        MiauCareRepository.initSession(context)
        MiauCareRepository.fetchZonasResumen()
        MiauCareRepository.fetchCatalogosMaestros()
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .clickable { onFinished() }
    ) {
        McCatImage(
            resId = R.drawable.img_logo_miaucare,
            contentDescription = "MiauCare Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .width(215.dp)
                .height(225.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
                .width(124.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0x1A000000))
        )
    }
}

// =========================================================================
// 2. INICIO DE SESIÓN LITERAL A FIGMA (`Inicio de sesion` + Modal `¿Eres voluntario o veterinario?`)
// =========================================================================
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (MiauRole) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterVoluntarioClick: () -> Unit,
    onRegisterVeterinarioClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var backendError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showRoleModal by remember { mutableStateOf(false) }

    val glassShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F1C18))
    ) {
        // Fotografía completa de gato como en Figma `Inicio de sesion`
        McCatImage(
            resId = R.drawable.img_cat_hero,
            contentDescription = "Gato MiauCare",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Sutil degradado inferior para legibilidad sobre el vidrio translúcido
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0x26000000),
                            Color(0x55EAE5DD)
                        )
                    )
                )
        )

        // Botón superior izquierdo `<` estilo Figma
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 16.dp)
        ) {
            McBackCircleButton(onClick = onBack, isTranslucentOnPhoto = true)
        }

        // Tarjeta glass translúcida inferior exacta a Figma (`Inicio de sesion`)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(glassShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x4DFFFFFF),
                            Color(0x88F6F4F1),
                            Color(0xCCF6F4F1)
                        )
                    )
                )
                .border(1.dp, Color(0xCCFFFFFF), glassShape)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Text(
                    text = "Inicia Sesión",
                    style = McTitle28.copy(
                        color = McCardWhite,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color(0x55000000),
                            offset = Offset(0f, 2f),
                            blurRadius = 8f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Error de autenticación (401 / 403 Veterinario pendiente o rechazado)
                if (backendError != null) {
                    Surface(
                        color = McCoralSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, McCoralBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = McCoralDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = backendError!!,
                                style = McLabel12Bold.copy(color = McCoralDark)
                            )
                        }
                    }
                }

                McInputField(
                    label = "Correo Electronico",
                    value = email,
                    onValueChange = {
                        email = it
                        backendError = null
                    },
                    placeholder = "helloworld@gmail.com",
                    isGlassStyle = true,
                    showCheckEndIcon = email.contains("@") && email.contains(".")
                )
                Spacer(modifier = Modifier.height(12.dp))

                McInputField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = {
                        password = it
                        backendError = null
                    },
                    placeholder = "••••••••",
                    isPassword = true,
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Olvido la contraseña?",
                    style = McLabel12Bold.copy(
                        color = McTextPrimary.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable { onForgotPasswordClick() }
                )

                Spacer(modifier = Modifier.height(18.dp))

                McCoralButton(
                    text = if (isSubmitting) "Autenticando..." else "Iniciar Sesión",
                    darkText = true,
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            backendError = "Ingresa tu correo electrónico y contraseña."
                            return@McCoralButton
                        }
                        if (!isSubmitting) {
                            isSubmitting = true
                            backendError = null
                            scope.launch {
                                val res = MiauCareRepository.login(context, email, password)
                                isSubmitting = false
                                when (res) {
                                    is ApiResult.Success -> onLoginSuccess(res.data)
                                    is ApiResult.Error -> backendError = res.message
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRoleModal = true }
                ) {
                    Text(
                        text = "No tienes ninguna cuenta?",
                        style = McBody14Regular.copy(
                            color = McTextPrimary.copy(alpha = 0.78f),
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "Registrate",
                        style = McBody14Bold.copy(
                            color = McTextPrimary,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

    if (showRoleModal) {
        Dialog(onDismissRequest = { showRoleModal = false }) {
            Surface(
                color = McBackground,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(22.dp)
                ) {
                    Surface(
                        color = McSoftNeutral,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.size(165.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            McCatImage(
                                resId = R.drawable.img_cat_role_modal,
                                contentDescription = "Rol",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "¿Eres voluntario o\nveterinario?",
                        style = McTitle24,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Si eres veterinario registra tu solicitud",
                        style = McBody14Regular,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    McCoralButton(
                        text = "Voluntario",
                        onClick = {
                            showRoleModal = false
                            onRegisterVoluntarioClick()
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    McCoralButton(
                        text = "Veterinario",
                        onClick = {
                            showRoleModal = false
                            onRegisterVeterinarioClick()
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. REGISTRO DE CUENTA VOLUNTARIO (`POST /api/voluntarios/registro` + GPS RN-07)
// =========================================================================
@Composable
fun RegisterVoluntarioScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val initialCoords = remember { MiauCareApiClient.obtainDeviceCoordinates(context) }
    var latitudBase by remember { mutableDoubleStateOf(initialCoords.first) }
    var longitudBase by remember { mutableDoubleStateOf(initialCoords.second) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
            latitudBase = lat
            longitudBase = lon
            MiauCareRepository.showToast("Coordenadas GPS en Colombia: $latitudBase, $longitudBase")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
    ) {
        McCatImage(
            resId = R.drawable.img_cat_hero,
            contentDescription = "Gato MiauCare",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 14.dp)
        ) {
            McBackCircleButton(onClick = onBack, isTranslucentOnPhoto = true)
        }

        Surface(
            color = McBackground.copy(alpha = 0.92f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            border = BorderStroke(1.dp, McCardWhite),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.86f)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(text = "Crear Cuenta", style = McTitle28)
                Spacer(modifier = Modifier.height(10.dp))

                // GPS Coordinates Card (RN-07)
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Ubicación GPS Base (RN-07)", style = McLabel12Bold)
                            Text(
                                text = "Lat: ${String.format(java.util.Locale.US, "%.4f", latitudBase)} · Lon: ${String.format(java.util.Locale.US, "%.4f", longitudBase)}",
                                style = McLabel11Regular
                            )
                        }
                        Surface(
                            color = McCoralSoft,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier.clickable {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = null,
                                    tint = McCoralDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Usar mi GPS",
                                    style = McLabel11Regular.copy(color = McCoralDark, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Nombre completo",
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Tu nombre y apellido",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Número de cédula",
                    value = cedula,
                    onValueChange = { cedula = it },
                    placeholder = "Ej. 1234567890",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Correo Electrónico",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "ejemplo@correo.com",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Teléfono",
                    value = telefono,
                    onValueChange = { telefono = it },
                    placeholder = "Ej. 3001234567",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = {
                        password = it
                        formError = null
                    },
                    placeholder = "Debe contener mínimo 6 caracteres",
                    isPassword = true,
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Confirma la contraseña",
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        formError = null
                    },
                    placeholder = "Repite la contraseña",
                    isPassword = true,
                    isGlassStyle = true
                )
                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Las contraseñas no coinciden",
                        style = McLabel12Bold.copy(color = McCoralDark)
                    )
                } else if (confirmPassword.isNotEmpty() && password == confirmPassword && password.length >= 6) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Las contraseñas coinciden",
                        style = McLabel11Regular.copy(color = StatusSanoText, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (formError != null) {
                    Surface(
                        color = McCoralSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, McCoralBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = formError!!,
                            style = McLabel12Bold.copy(color = McCoralDark),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { termsAccepted = !termsAccepted }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (termsAccepted) McBlack else McCardWhite)
                            .border(1.dp, McBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (termsAccepted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = McCardWhite,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Acepto los términos y la política de privacidad",
                        style = McLabel11Regular.copy(color = McTextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                McCoralButton(
                    text = if (isSubmitting) "Registrando..." else "Crear cuenta",
                    darkText = true,
                    onClick = {
                        when {
                            fullName.isBlank() || cedula.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                val msg = "Completa todos los campos obligatorios y confirma tu contraseña."
                                formError = msg
                                MiauCareRepository.showToast(msg, isError = true)
                                return@McCoralButton
                            }
                            !email.contains("@") -> {
                                val msg = "Ingresa un correo electrónico válido."
                                formError = msg
                                MiauCareRepository.showToast(msg, isError = true)
                                return@McCoralButton
                            }
                            password.length < 6 -> {
                                val msg = "La contraseña debe tener al menos 6 caracteres."
                                formError = msg
                                MiauCareRepository.showToast(msg, isError = true)
                                return@McCoralButton
                            }
                            password != confirmPassword -> {
                                val msg = "Las contraseñas no coinciden. Verifica que ambas sean iguales."
                                formError = msg
                                MiauCareRepository.showToast(msg, isError = true)
                                return@McCoralButton
                            }
                            !termsAccepted -> {
                                val msg = "Debes aceptar los términos y la política de privacidad."
                                formError = msg
                                MiauCareRepository.showToast(msg, isError = true)
                                return@McCoralButton
                            }
                        }
                        if (!isSubmitting) {
                            isSubmitting = true
                            formError = null
                            scope.launch {
                                val regRes = MiauCareRepository.registrarVoluntario(
                                    nombreCompleto = fullName,
                                    numeroCedula = cedula,
                                    correo = email,
                                    contrasena = password,
                                    telefono = telefono,
                                    disponibilidad = "Mañanas",
                                    latitudBase = latitudBase,
                                    longitudBase = longitudBase
                                )
                                if (regRes is ApiResult.Success) {
                                    MiauCareRepository.login(context, email, password)
                                    isSubmitting = false
                                    onRegistered()
                                } else {
                                    if (regRes is ApiResult.Error) formError = regRes.message
                                    isSubmitting = false
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Ya tienes cuenta? ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = McTextPrimary)) {
                            append("Inicia sesión")
                        }
                    },
                    style = McBody14Regular,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLoginClick() }
                )
            }
        }
    }
}

// =========================================================================
// 5. OLVIDÓ LA CONTRASEÑA (`POST /api/auth/restablecer-contrasena`)
// =========================================================================
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onResetComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var correo by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        McBackCircleButton(onClick = onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Olvido la contraseña?", style = McTitle28)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No te preocupes! Puedes volver a crear una aqui",
            style = McBody14Regular
        )
        Spacer(modifier = Modifier.height(24.dp))

        McInputField(
            label = "Correo electrónico registrado",
            value = correo,
            onValueChange = { correo = it },
            placeholder = "ejemplo@correo.com"
        )
        Spacer(modifier = Modifier.height(14.dp))

        McInputField(
            label = "Nueva contraseña",
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = "Debe contener al menos 6 caracteres",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(14.dp))

        McInputField(
            label = "Confirma la contraseña",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Repite la contraseña",
            isPassword = true
        )
        if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Las contraseñas no coinciden",
                style = McLabel12Bold.copy(color = McCoralDark)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        McCoralButton(
            text = if (isSubmitting) "Actualizando..." else "Restablecer contraseña",
            darkText = true,
            onClick = {
                when {
                    correo.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                        MiauCareRepository.showToast("Completa correo y ambas contraseñas", isError = true)
                        return@McCoralButton
                    }
                    newPassword.length < 6 -> {
                        MiauCareRepository.showToast("La contraseña debe tener al menos 6 caracteres", isError = true)
                        return@McCoralButton
                    }
                    newPassword != confirmPassword -> {
                        MiauCareRepository.showToast("Las contraseñas no coinciden", isError = true)
                        return@McCoralButton
                    }
                }
                if (!isSubmitting) {
                    isSubmitting = true
                    scope.launch {
                        val res = MiauCareRepository.restablecerContrasena(correo, newPassword)
                        isSubmitting = false
                        if (res is ApiResult.Success) {
                            onResetComplete()
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = buildAnnotatedString {
                append("Recuerdas tu contraseña? ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = McTextPrimary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Inicia Sesion")
                }
            },
            style = McBody14Regular,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .clickable { onBack() }
        )
    }
}

// =========================================================================
// 6. VOLUNTARIO · INICIO (`GET /api/zonas/ruta`, `PATCH /api/voluntarios/{id}/ubicar-gps`, `GET /api/gatos`)
// =========================================================================
@Composable
fun VoluntarioHomeScreen(
    onOpenSightingHistory: () -> Unit,
    onOpenNotifications: () -> Unit,
    onReportUnregisteredCat: () -> Unit,
    onOpenRoundForm: (String) -> Unit,
    onOpenCatDetail: (String) -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf("Todos") }

    val gpsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
            scope.launch {
                MiauCareRepository.actualizarUbicacionGpsVoluntario(lat, lon)
            }
        }
    }

    LaunchedEffect(Unit) {
        val (lat, lon) = MiauCareApiClient.obtainDeviceCoordinates(context)
        MiauCareRepository.fetchVoluntarioPerfil()
        MiauCareRepository.fetchRutaVoluntario(lat, lon)
        MiauCareRepository.fetchGatos(zonaId = MiauCareRepository.volunteerAssignedZoneId)
        if (MiauCareRepository.cats.isEmpty()) {
            MiauCareRepository.fetchGatos()
        }
    }

    val allCats = MiauCareRepository.cats
    val filteredCats = when (selectedFilter) {
        "Mi Zona" -> {
            val zId = MiauCareRepository.volunteerAssignedZoneId
            if (zId != null) allCats.filter { it.zoneId == zId } else allCats
        }
        "Hospitalizados" -> allCats.filter { it.estadoInternacion.equals("HOSPITALIZADO", true) }
        "Sin gatos" -> emptyList()
        else -> allCats
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 110.dp)
    ) {
        // Header Row: Volunteer Name + History & Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hola, Voluntario! · ${MiauCareRepository.volunteerAssignedZoneName}",
                    style = McLabel12Bold.copy(color = McTextSecondary)
                )
                Text(
                    text = MiauCareRepository.sessionUserName.ifBlank { "Voluntario MiauCare" },
                    style = McTitle24
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = McCardWhite,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onOpenSightingHistory() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "Historial de avistamientos",
                            tint = McTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Surface(
                    color = McCardWhite,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onOpenNotifications() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = McTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Colony Street Map Card consuming GET /api/zonas/ruta + PATCH /ubicar-gps
        McColonyMapCard(
            onAddClick = onReportUnregisteredCat,
            onColonyPinClick = {
                val selId = MiauCareRepository.selectedColonyId
                val pickedCol = MiauCareRepository.routeColonies.firstOrNull { it.id == selId }
                    ?: MiauCareRepository.colonies.firstOrNull { it.id == selId }
                    ?: MiauCareRepository.routeColonies.firstOrNull()
                if (pickedCol != null) {
                    MiauCareRepository.showToast("${pickedCol.name} (${pickedCol.address}) · ${pickedCol.catsCountText}")
                }
            },
            onUseGpsClick = {
                gpsPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Gatos en esta zona", style = McTitle18)
            Text(text = "${filteredCats.size} gatos", style = McLabel12Bold.copy(color = McTextSecondary))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Mi Zona", "Hospitalizados", "Sin gatos").forEach { filter ->
                McFilterPill(
                    label = filter,
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    unselectedBg = McSoftNeutral
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredCats.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Surface(
                        color = McSoftNeutral,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.size(160.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            McCatImage(
                                resId = R.drawable.img_cat_role_modal,
                                contentDescription = "Sin gatos",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay gatos por aquí",
                        style = McTitle18,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No encontramos gatos registrados con este filtro en tu zona.",
                        style = McBody14Regular,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    McCoralButton(
                        text = "Reportar gato no censado",
                        onClick = onReportUnregisteredCat
                    )
                }
            }
        } else {
            filteredCats.chunked(2).forEach { rowCats ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCats.forEach { cat ->
                        VoluntarioZoneCatCard(
                            cat = cat,
                            modifier = Modifier.weight(1f),
                            onCardClick = { onOpenCatDetail(cat.id) },
                            onReportSightingClick = { onOpenRoundForm(cat.id) }
                        )
                    }
                    if (rowCats.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun VoluntarioZoneCatCard(
    cat: CatProfile,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onReportSightingClick: () -> Unit
) {
    Surface(
        color = McCardWhite,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.clickable { onCardClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            McCatImage(
                resId = cat.imageRes,
                imageUrl = cat.fotoUrl,
                contentDescription = cat.name,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = cat.name, style = McTitle16, maxLines = 1)
            Text(
                text = "${cat.sex} · ${cat.age}",
                style = McLabel11Regular,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))

            val isCritical = cat.isFallecido || cat.statusTag.contains("Hospitalizado", ignoreCase = true)
            Surface(
                color = if (isCritical) McCoralSoft else McSoftNeutral,
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = cat.statusTag,
                    style = McLabel11Regular.copy(
                        color = if (isCritical) McCoralDark else McTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = McSoftNeutral,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReportSightingClick() }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    Text(
                        text = "Reportar avistamiento",
                        style = McLabel11Regular.copy(
                            color = McTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// =========================================================================
// 7. FORMULARIO DE RONDA (`POST /api/avistamientos/normal` & `POST /api/solicitudes-gato`)
// =========================================================================
@Composable
fun RoundFormScreen(
    catId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var cat by remember { mutableStateOf(MiauCareRepository.getCatById(catId)) }
    var healthStatus by remember { mutableStateOf("Sano") }
    var ateInRound by remember { mutableStateOf("Sí") }
    var notes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(catId) {
        val loaded = MiauCareRepository.fetchGatoDetalleYHistorial(catId)
        if (loaded != null) cat = loaded
    }

    val currentCat = cat

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Reporte de Avistamiento", style = McTitle18)
                Text(
                    text = "Actualizando estado temporal del gato",
                    style = McLabel11Regular
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (currentCat != null) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    McCatImage(
                        resId = currentCat.imageRes,
                        imageUrl = currentCat.fotoUrl,
                        contentDescription = currentCat.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${currentCat.name} · ${currentCat.code}", style = McTitle16)
                        Text(text = currentCat.colony, style = McLabel11Regular)
                    }
                    McStatusBadge(label = currentCat.statusTag)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "1. ¿Cómo se ve el gato hoy?", style = McBody14Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Sano", "Enfermo", "Herido").forEach { option ->
                val isSelected = healthStatus == option
                Surface(
                    color = if (isSelected) McCoral else McCardWhite,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { healthStatus = option }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = option,
                            style = McLabel12Bold.copy(
                                color = if (isSelected) McCardWhite else McTextSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "2. ¿El gato comió en esta ronda?", style = McBody14Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Sí", "No").forEach { option ->
                val isSelected = ateInRound == option
                Surface(
                    color = if (isSelected) Color(0xFFB5B0A8) else McCardWhite,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { ateInRound = option }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = option,
                            style = McLabel12Bold.copy(
                                color = if (isSelected) McCardWhite else McTextSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        McInputField(
            label = "3. Observaciones rápidas (Opcional)",
            value = notes,
            onValueChange = { notes = it },
            placeholder = "Ej. Tiene una pequeña herida en la oreja derecha, pero está activo...",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Critical Attention Box (`POST /api/solicitudes-gato`)
        Surface(
            color = McCoralSoft,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, McCoralBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = McCoralDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Atención Crítica",
                        style = McBody14Bold.copy(color = McCoralDark)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Usa estas opciones solo si el gato requiere intervención veterinaria urgente o ha fallecido.",
                    style = McLabel11Regular.copy(color = McCoralDark.copy(alpha = 0.85f))
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, McCoralBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val targetId = currentCat?.numericId ?: MiauCareRepository.cats.firstOrNull()?.numericId ?: 1L
                            scope.launch {
                                MiauCareRepository.crearSolicitudGato(targetId, "INGRESO_HOSPITALIZACION")
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            tint = McCoralDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Solicitar ingreso a Hospitalización",
                            style = McLabel12Bold.copy(color = McCoralDark)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, McCoralBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val targetId = currentCat?.numericId ?: MiauCareRepository.cats.firstOrNull()?.numericId ?: 1L
                            scope.launch {
                                MiauCareRepository.crearSolicitudGato(targetId, "FALLECIMIENTO")
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeartBroken,
                            contentDescription = null,
                            tint = McCoralDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reportar Fallecimiento",
                            style = McLabel12Bold.copy(color = McCoralDark)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        McCoralButton(
            text = if (isSaving) "Guardando..." else "Guardar Avistamiento",
            onClick = {
                val targetId = currentCat?.numericId ?: MiauCareRepository.cats.firstOrNull()?.numericId
                if (targetId == null) {
                    MiauCareRepository.showToast("No hay gato seleccionado", isError = true)
                    return@McCoralButton
                }
                if (!isSaving) {
                    isSaving = true
                    scope.launch {
                        val res = MiauCareRepository.registrarAvistamientoNormal(
                            gatoNumericId = targetId,
                            estadoAparenteUi = healthStatus,
                            comio = ateInRound == "Sí",
                            observaciones = notes
                        )
                        isSaving = false
                        if (res is ApiResult.Success) {
                            onSaved()
                        }
                    }
                }
            }
        )
    }
}

// =========================================================================
// 8. CALENDARIO DE RONDAS (`GET /api/rondas` & `PATCH /api/rondas/{id}/asistir`)
// =========================================================================
@Composable
fun VoluntarioCalendarScreen(
    onOpenNotifications: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedDay by remember { mutableIntStateOf(14) }
    val days = listOf(
        "L" to 9,
        "M" to 10,
        "X" to 11,
        "J" to 12,
        "V" to 13,
        "S" to 14,
        "D" to 15
    )

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchRondas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Rondas de Revisión", style = McTitle24)
                Text(text = "Organiza tus visitas", style = McBody14Regular)
            }
            Surface(
                color = McCardWhite,
                shape = CircleShape,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onOpenNotifications() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = McTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Noviembre 2026", style = McTitle16)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = McTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = McTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEach { (dow, num) ->
                        val isSelected = selectedDay == num
                        Surface(
                            color = if (isSelected) McCoral else Color.Transparent,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .width(40.dp)
                                .clickable { selectedDay = num }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = dow,
                                    style = McLabel11Regular.copy(
                                        color = if (isSelected) McCardWhite else McTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = num.toString(),
                                    style = McBody14Bold.copy(
                                        color = if (isSelected) McCardWhite else McTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) McCardWhite else Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Próximas Rondas", style = McTitle18)
            Text(
                text = "${MiauCareRepository.rounds.size} programadas",
                style = McLabel12Bold.copy(color = McTextSecondary)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (MiauCareRepository.rounds.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(text = "No hay rondas programadas", style = McTitle16)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cuando un administrador programe una ronda en tu zona aparecerá aquí.",
                        style = McLabel11Regular,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        MiauCareRepository.rounds.forEach { round ->
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(McCoral)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = McTextPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "${round.fecha} · ${round.timeRange}", style = McBody14Bold)
                            }
                            Surface(
                                color = McCoralSoft,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = round.estadoBackend,
                                    style = McLabel11Regular.copy(
                                        color = McCoralDark,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = round.zoneTitle, style = McTitle16)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = round.description, style = McLabel11Regular)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                round.initials.take(3).forEachIndexed { idx, init ->
                                    Surface(
                                        color = if (idx % 2 == 0) McSoftNeutral else McCoralSoft,
                                        shape = CircleShape,
                                        border = BorderStroke(1.5.dp, McCardWhite),
                                        modifier = Modifier
                                            .offset(x = (-6 * idx).dp)
                                            .size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = init,
                                                style = McLabel11Regular.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = McTextPrimary
                                                )
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = round.peopleCountText,
                                    style = McLabel11Regular
                                )
                            }

                            if (!round.isJoined && round.estadoBackend == "PROGRAMADA") {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            MiauCareRepository.asistirARonda(round.numericId)
                                        }
                                    },
                                    shape = RoundedCornerShape(999.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = McCoral),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = "Asistir",
                                        style = McLabel12Bold.copy(color = McTextPrimary)
                                    )
                                }
                            } else {
                                Surface(
                                    color = McSoftNeutral,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = if (round.isJoined) "Inscrito" else round.estadoBackend,
                                        style = McLabel12Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 9. LISTA DE GATOS / CENSO (`GET /api/gatos`)
// =========================================================================
@Composable
fun VoluntarioCensusScreen(
    onOpenSightingHistory: () -> Unit,
    onReportUnregisteredCat: () -> Unit,
    onOpenCatDetail: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    LaunchedEffect(searchQuery) {
        MiauCareRepository.fetchGatos(nombre = searchQuery.takeIf { it.isNotBlank() })
    }

    val filteredCats = MiauCareRepository.cats.filter { cat ->
        val matchesTab = when (selectedFilter) {
            "Mi Zona" -> MiauCareRepository.volunteerAssignedZoneId == null ||
                cat.zoneId == MiauCareRepository.volunteerAssignedZoneId
            "En Calle" -> cat.estadoInternacion.equals("EN_CALLE", ignoreCase = true)
            "Hospitalizado" -> cat.estadoInternacion.equals("HOSPITALIZADO", ignoreCase = true)
            else -> true
        }
        matchesTab
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Censo de gatos", style = McTitle24)
                Text(text = "${filteredCats.size} gatos", style = McBody14Regular)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = McCardWhite,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onOpenSightingHistory() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "Historial",
                            tint = McTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Surface(
                    color = McCoral,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onReportUnregisteredCat() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir",
                            tint = McCardWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar gato por nombre..."
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Mi Zona", "En Calle", "Hospitalizado").forEach { tab ->
                McFilterPill(
                    label = tab,
                    selected = selectedFilter == tab,
                    onClick = { selectedFilter = tab },
                    unselectedBg = McSoftNeutral
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredCats.chunked(2).forEach { rowCats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCats.forEach { cat ->
                    CensusGridCatCard(
                        cat = cat,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenCatDetail(cat.id) }
                    )
                }
                if (rowCats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CensusGridCatCard(
    cat: CatProfile,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Surface(
        color = McCardWhite,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                McCatImage(
                    resId = cat.imageRes,
                    imageUrl = cat.fotoUrl,
                    contentDescription = cat.name,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = McCardWhite,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = McCoral,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = cat.name,
                style = McTitle16,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "${cat.code} · ${cat.breed}",
                style = McLabel11Regular,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = if (cat.isFallecido) McCoralSoft else McSoftNeutral,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cat.statusTag,
                        style = McLabel11Regular.copy(
                            color = if (cat.isFallecido) McCoralDark else McTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
                Surface(
                    color = McSoftNeutral,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cat.age,
                        style = McLabel11Regular.copy(
                            color = McTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }
            if (onDeleteClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = McCoralSoft,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, McCoralBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeleteClick() }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Eliminar",
                            tint = McCoralDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Eliminar gato",
                            style = McLabel11Regular.copy(
                                color = McCoralDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 10. INFORMACIÓN DEL GATO (`GET /api/gatos/{id}` + `GET /api/historial-clinico/gato/{id}`)
// =========================================================================
@Composable
fun CatDetailScreen(
    catId: String,
    onBack: () -> Unit,
    onReportSightingClick: (String) -> Unit
) {
    var cat by remember { mutableStateOf(MiauCareRepository.getCatById(catId)) }

    LaunchedEffect(catId) {
        val loaded = MiauCareRepository.fetchGatoDetalleYHistorial(catId)
        if (loaded != null) cat = loaded
    }

    val currentCat = cat

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
    ) {
        McCatImage(
            resId = currentCat?.imageRes ?: R.drawable.img_cat_samantha,
            imageUrl = currentCat?.fotoUrl,
            contentDescription = currentCat?.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 14.dp)
        ) {
            McBackCircleButton(onClick = onBack)
        }

        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 265.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(McBorder)
                )
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = currentCat?.name ?: "Cargando...", style = McTitle24)
                        Text(
                            text = "${currentCat?.breed ?: "Mestizo"} · ${currentCat?.code ?: ""}",
                            style = McBody14Regular
                        )
                    }
                    McStatusBadge(label = currentCat?.statusTag ?: "En calle", showRedDot = true)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = McCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentCat?.colony ?: "Colonia MiauCare",
                        style = McLabel12Bold.copy(color = McTextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CatStatBox(value = currentCat?.sex ?: "-", label = "Sexo", modifier = Modifier.weight(1f))
                    CatStatBox(value = currentCat?.age ?: "-", label = "Edad aprox.", modifier = Modifier.weight(1f))
                    CatStatBox(value = currentCat?.sterilizationStatus ?: "-", label = "Estado", modifier = Modifier.weight(1.2f))
                    CatStatBox(value = "${currentCat?.weightKg ?: "-"} kg", label = "Peso", modifier = Modifier.weight(0.9f))
                }

                if (!currentCat?.condicionesEspeciales.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = McCoralSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, McCoralBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Outlined.WarningAmber, contentDescription = null, tint = McCoralDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentCat?.condicionesEspeciales ?: "",
                                style = McLabel12Bold.copy(color = McCoralDark)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Historia Clínica", style = McTitle18)
                    Surface(
                        color = McSoftNeutral,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Solo lectura",
                            style = McLabel11Regular.copy(fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (MiauCareRepository.currentCatHistory.isEmpty()) {
                    Surface(
                        color = McBackground,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sin entradas clínicas registradas para este gato.",
                            style = McLabel11Regular,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    MiauCareRepository.currentCatHistory.forEach { entry ->
                        val isCrit = entry.nivelGravedad.equals("CRITICA", true)
                        Surface(
                            color = if (isCrit) McCoralSoft else McBackground,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                if (isCrit) {
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .fillMaxHeight()
                                            .background(McCoral)
                                    )
                                }
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = entry.diagnosticoNombre.ifBlank { entry.descripcion.ifBlank { entry.tipo } },
                                        style = McBody14Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${entry.fecha} · ${entry.notas.ifBlank { entry.descripcion }} (${entry.veterinarioNombre})",
                                        style = McLabel11Regular
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (currentCat?.isFallecido == true) {
                    Surface(
                        color = McCoralSoft,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Este gato se encuentra registrado como FALLECIDO.",
                            style = McLabel12Bold.copy(color = McCoralDark),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    McCoralButton(
                        text = "Reportar avistamiento en ronda",
                        onClick = { onReportSightingClick(currentCat?.id ?: catId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CatStatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = McBackground,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = value,
                style = McLabel12Bold.copy(fontSize = 11.5.sp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = McLabel11Regular.copy(fontSize = 10.sp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// =========================================================================
// 11. REGISTRAR / REPORTAR GATO EN VOLUNTARIO (`POST /api/archivos/subir`, `POST /api/gatos`, `POST /api/avistamientos/no-censado`)
// =========================================================================
@Composable
fun ReportUnregisteredCatScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var catName by remember { mutableStateOf("") }
    var catCode by remember { mutableStateOf("MC-BOG-${MiauCareRepository.cats.size + 25}") }
    var catBreed by remember { mutableStateOf("Mestizo") }
    var catSex by remember { mutableStateOf("Macho") }
    var catAge by remember { mutableStateOf("1 año") }
    var catWeight by remember { mutableStateOf("3.5") }
    var catSterilized by remember { mutableStateOf(false) }
    var apparentState by remember { mutableStateOf("Sano") }
    var selectedZoneIndex by remember { mutableIntStateOf(0) }
    var observations by remember { mutableStateOf("") }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (MiauCareRepository.colonies.isEmpty()) {
            MiauCareRepository.fetchZonasResumen()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageBytes = MiauCareApiClient.readUriBytes(context, uri)
            selectedFileName = "gato_bogota_${System.currentTimeMillis()}.jpg"
        }
    }

    val zones = MiauCareRepository.colonies
    val currentZone = zones.getOrNull(selectedZoneIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Registrar gato y reporte", style = McTitle18)
                Text(text = "Datos del gato, colonia en Bogotá y evidencia fotográfica", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Photo Upload Box (opens Gallery picker or quick sample fallback)
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, McBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clickable { imagePickerLauncher.launch("image/*") }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(14.dp)
            ) {
                Surface(
                    color = if (selectedImageBytes != null) StatusSanoBg else McSoftNeutral,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (selectedImageBytes != null) Icons.Default.Check else Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = if (selectedImageBytes != null) StatusSanoText else McTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedFileName ?: "Añadir foto del gato (Galería)",
                    style = McBody14Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "O tocar aquí para usar foto de muestra",
                    style = McLabel11Regular.copy(color = McCoralDark, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        selectedImageBytes = MiauCareApiClient.drawableToJpegBytes(context, R.drawable.img_cat_samantha)
                        selectedFileName = "gato_muestra_bogota.jpg"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Datos completos del gato
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Datos del gato", style = McTitle16)
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    McInputField(
                        label = "Nombre del gato",
                        value = catName,
                        onValueChange = { catName = it },
                        placeholder = "Ej. Michi, Simba...",
                        modifier = Modifier.weight(1.2f)
                    )
                    McInputField(
                        label = "Código",
                        value = catCode,
                        onValueChange = { catCode = it },
                        placeholder = "MC-BOG-25",
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    McInputField(
                        label = "Raza / Pelaje",
                        value = catBreed,
                        onValueChange = { catBreed = it },
                        placeholder = "Mestizo, Calicó...",
                        modifier = Modifier.weight(1f)
                    )
                    McInputField(
                        label = "Edad aprox.",
                        value = catAge,
                        onValueChange = { catAge = it },
                        placeholder = "Ej. 1 año, 6 meses",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
                    McInputField(
                        label = "Peso aprox. (kg)",
                        value = catWeight,
                        onValueChange = { catWeight = it },
                        placeholder = "3.5",
                        modifier = Modifier.weight(1f)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Sexo", style = McLabel12Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Macho", "Hembra").forEach { sx ->
                                McFilterPill(
                                    label = sx,
                                    selected = catSex == sx,
                                    onClick = { catSex = sx },
                                    unselectedBg = McSoftNeutral
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "¿Está esterilizado?", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(false to "No esterilizado", true to "Esterilizado (CES)").forEach { (valBool, label) ->
                        McFilterPill(
                            label = label,
                            selected = catSterilized == valBool,
                            onClick = { catSterilized = valBool },
                            unselectedBg = McSoftNeutral
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Colonia en Bogotá", style = McLabel12Bold)
        Spacer(modifier = Modifier.height(8.dp))
        if (zones.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                zones.forEachIndexed { idx, zn ->
                    McFilterPill(
                        label = zn.name,
                        selected = selectedZoneIndex == idx,
                        onClick = { selectedZoneIndex = idx },
                        unselectedBg = McCardWhite
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = McCoral,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentZone?.let { "${it.name} · ${it.address}" } ?: "Colonia Norte · Parque El Virrey, Bogotá",
                    style = McBody14Regular.copy(color = McTextPrimary),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Estado aparente de salud", style = McLabel12Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val states = listOf(
                Triple("Sano", StatusSanoBg, StatusSanoText),
                Triple("Enfermo", StatusEnfermoBg, StatusEnfermoText),
                Triple("Herido", StatusHeridoBg, StatusHeridoText)
            )
            states.forEach { (label, bg, fg) ->
                val selected = apparentState == label
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(999.dp),
                    border = if (selected) BorderStroke(1.5.dp, fg) else null,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable { apparentState = label }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = McLabel12Bold.copy(color = fg)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        McInputField(
            label = "Observaciones y condiciones especiales",
            value = observations,
            onValueChange = { observations = it },
            placeholder = "Color del pelaje, comportamiento, señas particulares...",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(20.dp))

        McCoralButton(
            text = if (isUploading) "Guardando gato y reporte..." else "Registrar gato y enviar reporte",
            onClick = {
                if (!isUploading) {
                    isUploading = true
                    val bytesToUpload = selectedImageBytes
                        ?: MiauCareApiClient.drawableToJpegBytes(context, R.drawable.img_cat_samantha)
                    scope.launch {
                        val res = MiauCareRepository.registrarAvistamientoNoCensado(
                            zonaId = currentZone?.numericId,
                            estadoAparenteUi = apparentState,
                            observaciones = observations.ifBlank { "Gato registrado en Bogotá (${apparentState})" },
                            imageBytes = bytesToUpload,
                            nombreGato = catName.ifBlank { "Michi Bogotá" },
                            codigoGato = catCode,
                            razaGato = catBreed,
                            sexoGatoUi = catSex,
                            edadEstimadaGato = catAge,
                            pesoGatoKg = catWeight.toDoubleOrNull() ?: 3.5,
                            esterilizadoGato = catSterilized
                        )
                        isUploading = false
                        if (res is ApiResult.Success) {
                            onSubmitted()
                        }
                    }
                }
            }
        )
    }
}

// =========================================================================
// 12. HISTORIAL DE AVISTAMIENTOS (`GET /api/avistamientos/zona/{zonaId}`)
// =========================================================================
@Composable
fun SightingHistoryScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos los gatos") }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchAvistamientosPorGatoOZona(zonaId = MiauCareRepository.volunteerAssignedZoneId)
    }

    val filtered = MiauCareRepository.sightingHistory.filter { item ->
        val matchesQuery = item.catName.contains(searchQuery, ignoreCase = true) ||
            item.observaciones.contains(searchQuery, ignoreCase = true)
        val matchesStatus = selectedStatus == "Todos los gatos" || item.healthStatus == selectedStatus
        matchesQuery && matchesStatus
    }

    val grouped = filtered.groupBy { it.dateGroup }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Historial de avistamientos", style = McTitle18)
                Text(text = "Rondas registradas en el backend", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre de gato..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos los gatos", "Sano", "Herido", "Enfermo").forEach { tab ->
                McFilterPill(
                    label = tab,
                    selected = selectedStatus == tab,
                    onClick = { selectedStatus = tab },
                    unselectedBg = McSoftNeutral
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (grouped.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aún no hay avistamientos registrados en esta zona.",
                    style = McBody14Regular,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }

        grouped.forEach { (groupTitle, items) ->
            Text(
                text = groupTitle,
                style = McLabel12Bold.copy(color = McTextSecondary),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            items.forEach { item ->
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        McCatImage(
                            resId = item.imageRes,
                            imageUrl = item.fotoUrl,
                            contentDescription = item.catName,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.catName, style = McTitle16)
                            Text(text = item.dateTime, style = McLabel11Regular)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                McStatusBadge(label = item.healthStatus)
                                if (item.ateFood) {
                                    Surface(
                                        color = McSoftNeutral,
                                        shape = RoundedCornerShape(999.dp)
                                    ) {
                                        Text(
                                            text = "Comió",
                                            style = McLabel11Regular.copy(
                                                color = McTextPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                            if (item.observaciones.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = item.observaciones, style = McLabel11Regular)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.reportedBy, style = McLabel11Regular)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

// =========================================================================
// 13. NOTIFICACIONES (`GET /api/notificaciones/mias`)
// =========================================================================
@Composable
fun VoluntarioNotificationsScreen(
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        MiauCareRepository.fetchMisNotificaciones()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                McBackCircleButton(onClick = onBack)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Notificaciones", style = McTitle24)
                    Text(text = "Tus notificaciones y alertas", style = McBody14Regular)
                }
            }
            Surface(
                color = McCardWhite,
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = McTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (MiauCareRepository.notifications.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No tienes notificaciones pendientes en este momento.",
                    style = McBody14Regular,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            MiauCareRepository.notifications.forEach { notif ->
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = McSoftNeutral,
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = McTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = notif.titulo, style = McBody14Bold)
                            Text(text = notif.mensaje, style = McLabel11Regular)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${notif.fechaCreacion} · Para: ${notif.destinatarios}",
                                style = McLabel11Regular.copy(fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 14. PERFIL VOLUNTARIO (`GET / PUT /api/voluntarios/{id}` + `PATCH /ubicar-gps`)
// =========================================================================
@Composable
fun VoluntarioProfileScreen(
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(MiauCareRepository.sessionUserName) }
    var email by remember { mutableStateOf(MiauCareRepository.sessionUserEmail) }
    var phone by remember { mutableStateOf(MiauCareRepository.volunteerPhone) }
    var disponibilidad by remember { mutableStateOf(MiauCareRepository.volunteerDisponibilidad) }
    var selectedZoneId by remember { mutableStateOf(MiauCareRepository.volunteerAssignedZoneId) }

    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
            scope.launch {
                MiauCareRepository.actualizarUbicacionGpsVoluntario(lat, lon)
                selectedZoneId = MiauCareRepository.volunteerAssignedZoneId
            }
        }
    }

    LaunchedEffect(Unit) {
        if (MiauCareRepository.colonies.isEmpty()) {
            MiauCareRepository.fetchZonasResumen()
        }
        MiauCareRepository.fetchVoluntarioPerfil()
        fullName = MiauCareRepository.sessionUserName
        email = MiauCareRepository.sessionUserEmail
        phone = MiauCareRepository.volunteerPhone
        disponibilidad = MiauCareRepository.volunteerDisponibilidad
        selectedZoneId = MiauCareRepository.volunteerAssignedZoneId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Mi Perfil", style = McTitle24)
                Text(text = "Información personal y zona asignada", style = McBody14Regular)
            }
            Surface(
                color = McCardWhite,
                shape = CircleShape,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onOpenNotifications() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = McTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.size(92.dp)) {
                Surface(
                    color = McSoftNeutral,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initials = fullName.split(" ").take(2).joinToString("") { it.take(1).uppercase() }.ifBlank { "VO" }
                        Text(text = initials, style = McTitle24.copy(color = McTextSecondary))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = fullName.ifBlank { "Voluntario" }, style = McTitle18)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = McCoral,
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = "Voluntario de Campo",
                    style = McLabel11Regular.copy(
                        color = McTextPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Active Zone Card + "Usar mi ubicación GPS" (RN-07)
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = McSoftNeutral,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = McCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zona Activa: ${MiauCareRepository.volunteerAssignedZoneName}",
                            style = McBody14Bold
                        )
                        Text(
                            text = "Asignación automática por cercanía GPS (RN-07)",
                            style = McLabel11Regular
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                McSoftButton(
                    text = "Usar mi ubicación GPS",
                    leadingIcon = Icons.Default.MyLocation,
                    onClick = {
                        gpsLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        McInputField(
            label = "Nombre completo",
            value = fullName,
            onValueChange = { fullName = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        McInputField(
            label = "Correo electrónico",
            value = email,
            onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        McInputField(
            label = "Teléfono",
            value = phone,
            onValueChange = { phone = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Disponibilidad de horario", style = McLabel12Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Mañanas", "Tardes", "Noches", "Fines de semana").forEach { disp ->
                McFilterPill(
                    label = disp,
                    selected = disponibilidad.equals(disp, ignoreCase = true),
                    onClick = { disponibilidad = disp },
                    unselectedBg = McCardWhite
                )
            }
        }

        if (MiauCareRepository.colonies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "Colonia asignada en Bogotá", style = McLabel12Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiauCareRepository.colonies.forEach { zn ->
                    McFilterPill(
                        label = zn.name,
                        selected = selectedZoneId == zn.numericId,
                        onClick = {
                            selectedZoneId = zn.numericId
                            scope.launch {
                                MiauCareRepository.asignarZonaManualVoluntario(zn.numericId)
                            }
                        },
                        unselectedBg = McCardWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        McCoralButton(
            text = "Guardar cambios",
            darkText = true,
            onClick = {
                scope.launch {
                    MiauCareRepository.actualizarPerfilVoluntario(
                        nombre = fullName,
                        correo = email,
                        telefono = phone,
                        disponibilidad = disponibilidad,
                        zonaId = selectedZoneId
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                MiauCareRepository.logout(context)
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = McCoralSoft,
                contentColor = McCoralDark
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cerrar Sesión", style = McBody14Bold.copy(color = McCoralDark))
        }
    }
}
