package com.example.miaucare.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.miaucare.data.CatProfile
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.data.SightingRecord
import com.example.miaucare.ui.components.*
import com.example.miaucare.ui.theme.*
import kotlinx.coroutines.delay

// =========================================================================
// 1. SPLASH INICIO (`Splash Inicio`)
// =========================================================================
@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .clickable { onFinished() },
        contentAlignment = Alignment.Center
    ) {
        McCatImage(
            resId = R.drawable.img_logo_miaucare,
            contentDescription = "MiauCare Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(230.dp)
                .height(250.dp)
        )
    }
}

// =========================================================================
// 2. BIENVENIDA (`Bienvenida` + Modal `¿Eres voluntario o veterinario?`)
// =========================================================================
@Composable
fun BienvenidaScreen(
    onLoginClick: () -> Unit,
    onRegisterVoluntarioClick: () -> Unit,
    onRegisterVeterinarioClick: () -> Unit,
    onQuickRoleJump: (MiauRole) -> Unit
) {
    var showRoleModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
    ) {
        // Top full-bleed cat hero image
        McCatImage(
            resId = R.drawable.img_cat_hero,
            contentDescription = "Gato MiauCare",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.TopCenter)
        )

        // Gradient fade into warm background at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            McBackground.copy(alpha = 0.65f),
                            McBackground
                        )
                    )
                )
        )

        // Frosted glass bottom card matching step_46_shot_2_voluntario_Bienvenida.png
        Surface(
            color = McCardWhite.copy(alpha = 0.72f),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, McCardWhite),
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp)
            ) {
                Text(
                    text = "Bienvenido a\nMiauCare",
                    style = McTitle28.copy(fontSize = 28.sp, lineHeight = 34.sp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "La plataforma integral para el bienestar felino y las rondas de revisión.",
                    style = McBody14Regular.copy(color = McTextPrimary.copy(alpha = 0.8f))
                )
                Spacer(modifier = Modifier.height(24.dp))

                McCoralButton(
                    text = "Iniciar Sesión",
                    darkText = true,
                    onClick = onLoginClick
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showRoleModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, McCardWhite),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = McCardWhite.copy(alpha = 0.65f),
                        contentColor = McTextPrimary
                    )
                ) {
                    Text(
                        text = "Crear Cuenta",
                        style = McBody14Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                // Prototype quick jump bar so evaluator can inspect any role immediately
                McRolePrototypeSwitcher(onRoleSelected = onQuickRoleJump)
            }
        }
    }

    // Modal "¿Eres voluntario o veterinario?" (from Figma `Frame 4` / `step_65_shot_1`)
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
// 3. INICIO DE SESIÓN (`Inicio de sesion`)
// =========================================================================
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (MiauRole) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("helloworld@gmail.com") }
    var password by remember { mutableStateOf("1234567890") }

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
                .fillMaxHeight(0.62f)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 16.dp)
        ) {
            McBackCircleButton(onClick = onBack, isTranslucentOnPhoto = true)
        }

        // Frosted glass bottom sheet
        Surface(
            color = McBackground.copy(alpha = 0.88f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            border = BorderStroke(1.dp, McCardWhite),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 26.dp)
            ) {
                Text(
                    text = "Inicia Sesión",
                    style = McTitle28
                )
                Spacer(modifier = Modifier.height(18.dp))

                McInputField(
                    label = "Correo Electronico",
                    value = email,
                    onValueChange = { email = it },
                    isGlassStyle = true,
                    showCheckEndIcon = true
                )
                Spacer(modifier = Modifier.height(14.dp))

                McInputField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true,
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Olvido la contraseña?",
                    style = McLabel12Bold.copy(color = McTextSecondary),
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onForgotPasswordClick() }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Role selector for login
                Text(
                    text = "Entrar con rol:",
                    style = McLabel11Regular,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                McRolePrototypeSwitcher(onRoleSelected = {})

                Spacer(modifier = Modifier.height(18.dp))

                McCoralButton(
                    text = "Iniciar Sesión",
                    darkText = true,
                    onClick = { onLoginSuccess(MiauCareRepository.currentRole) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = buildAnnotatedString {
                        append("No tienes ninguna cuenta? ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = McTextPrimary,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Registrate")
                        }
                    },
                    style = McBody14Regular.copy(color = McTextPrimary),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRegisterClick() }
                )
            }
        }
    }
}

// =========================================================================
// 4. REGISTRO DE CUENTA VOLUNTARIO (`Registro de cuenta`)
// =========================================================================
@Composable
fun RegisterVoluntarioScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }

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
                .fillMaxHeight(0.45f)
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
            color = McBackground.copy(alpha = 0.90f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            border = BorderStroke(1.dp, McCardWhite),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.84f)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Text(text = "Crear Cuenta", style = McTitle28)
                Spacer(modifier = Modifier.height(14.dp))

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
                    label = "Contraseña",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Debe contener 8 caracteres",
                    isPassword = true,
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Confirma la contraseña",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repite la contraseña",
                    isPassword = true,
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { termsAccepted = !termsAccepted }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip( CircleShape)
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

                Spacer(modifier = Modifier.height(18.dp))

                McCoralButton(
                    text = "Crear cuenta",
                    darkText = true,
                    onClick = {
                        MiauCareRepository.currentRole = MiauRole.VOLUNTARIO
                        MiauCareRepository.showToast("Cuenta creada exitosamente")
                        onRegistered()
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
// 5. OLVIDÓ LA CONTRASEÑA (`Olvido la contaseña`)
// =========================================================================
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onResetComplete: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        McBackCircleButton(onClick = onBack)
        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Olvido la contraseña?", style = McTitle28)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No te preocupes! Puedes volver a crear una aqui",
            style = McBody14Regular
        )
        Spacer(modifier = Modifier.height(28.dp))

        McInputField(
            label = "Nueva contraseña",
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = "Debe contener 8 caracteres",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        McInputField(
            label = "Confirma la contraseña",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Repite la contraseña",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(26.dp))

        McCoralButton(
            text = "Restablecer contraseña",
            darkText = true,
            onClick = {
                MiauCareRepository.showToast("Contraseña actualizada")
                onResetComplete()
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
// 6. VOLUNTARIO · INICIO (`Inicio` + `Estado vacío · Sin gatos`)
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
    var selectedFilter by remember { mutableStateOf("Todos") }
    val zoneCats = MiauCareRepository.cats.take(2) // Benito & Luna as in Figma `Inicio`

    val filteredCats = when (selectedFilter) {
        "No vistos hoy" -> zoneCats.filter { it.statusTag.contains("No vista", ignoreCase = true) }
        "Sin gatos" -> emptyList()
        else -> zoneCats
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
        // Role switcher bar for easy prototype testing across all 3 Figma pages
        McRolePrototypeSwitcher(onRoleSelected = onRoleSwitch)
        Spacer(modifier = Modifier.height(14.dp))

        // Header Row: "Hola, Voluntario!" / "Pepe Perez" + Clock & Bell buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Hola, Voluntario!", style = McLabel12Bold.copy(color = McTextSecondary))
                Text(text = "Pepe Perez", style = McTitle24)
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

        // Interactive Colony Street Map Card (`3 colonias cerca`, 3 paw pins, `+` FAB)
        McColonyMapCard(
            onAddClick = onReportUnregisteredCat,
            onColonyPinClick = { MiauCareRepository.showToast("Colonia Norte · 4 gatos registrados") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section Header: "Gatos en esta zona" | "2 gatos"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Gatos en esta zona", style = McTitle18)
            Text(text = "${filteredCats.size} gatos", style = McLabel12Bold.copy(color = McTextSecondary))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter pills (`Todos`, `Frecuentes`, `No vistos hoy`, `Sin gatos`)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Frecuentes", "No vistos hoy", "Sin gatos").forEach { filter ->
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
            // Exact match to `Estado vacío · Sin gatos` (`step_65_shot_1_voluntario_Extra_States.png`)
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
            // 2-Column Cat Cards (`Benito` & `Luna`) matching `step_57_shot_3_voluntario_Inicio.png`
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredCats.forEach { cat ->
                    VoluntarioZoneCatCard(
                        cat = cat,
                        modifier = Modifier.weight(1f),
                        onCardClick = { onOpenCatDetail(cat.id) },
                        onReportSightingClick = { onOpenRoundForm(cat.id) }
                    )
                }
                if (filteredCats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
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
                contentDescription = cat.name,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = cat.name, style = McTitle16)
            Text(
                text = "${cat.sex} · ${cat.age}",
                style = McLabel11Regular
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Status pill (`En calle` / `No vista hoy`)
            val isUnseen = cat.statusTag.contains("No vista", ignoreCase = true)
            Surface(
                color = if (isUnseen) McCoralSoft else McSoftNeutral,
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = cat.statusTag,
                    style = McLabel11Regular.copy(
                        color = if (isUnseen) McCoralDark else McTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "Reportar avistamiento" button
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
// 7. FORMULARIO DE RONDA (`Formulario de ronda` / `Reporte de Avistamiento`)
// =========================================================================
@Composable
fun RoundFormScreen(
    catId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val cat = remember(catId) {
        if (catId == "cat-samantha" || catId.isBlank()) {
            MiauCareRepository.getCatById("cat-samantha")
        } else {
            MiauCareRepository.getCatById(catId)
        }
    }
    var healthStatus by remember { mutableStateOf("Sano") }
    var ateInRound by remember { mutableStateOf("Sí") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Header
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

        // Cat summary row (`Samantha` / `Ficha del censo` / `En Calle`)
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
                    resId = cat.imageRes,
                    contentDescription = cat.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = cat.name, style = McTitle16)
                    Text(text = "Ficha del censo", style = McLabel11Regular)
                }
                McStatusBadge(label = "En Calle")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Question 1: "1. ¿Cómo se ve el gato hoy?"
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

        // Question 2: "2. ¿El gato comió en esta ronda?"
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

        // Question 3: "3. Observaciones rápidas (Opcional)"
        McInputField(
            label = "3. Observaciones rápidas (Opcional)",
            value = notes,
            onValueChange = { notes = it },
            placeholder = "Ej. Tiene una pequeña herida en la oreja derecha, pero está activo...",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Critical Attention Box (`Atención Crítica`) matching `step_58_shot_4_voluntario_Formulario.png`
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
                            MiauCareRepository.showToast("Solicitud de hospitalización enviada")
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
                            MiauCareRepository.showToast("Reporte de fallecimiento enviado a Admin")
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
            text = "Guardar Avistamiento",
            onClick = {
                MiauCareRepository.sightingHistory.add(
                    0,
                    SightingRecord(
                        id = "sig-${System.currentTimeMillis()}",
                        catName = cat.name,
                        dateGroup = "Hoy · 13 nov 2026",
                        dateTime = "13 nov 2026 · Ahora",
                        healthStatus = healthStatus,
                        ateFood = ateInRound == "Sí",
                        reportedBy = "Reportado por Pepe Pérez",
                        imageRes = cat.imageRes
                    )
                )
                MiauCareRepository.showToast("Avistamiento guardado")
                onSaved()
            }
        )
    }
}

// =========================================================================
// 8. CALENDARIO DE RONDAS (`Calendario`)
// =========================================================================
@Composable
fun VoluntarioCalendarScreen(
    onOpenNotifications: () -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(11) }
    val days = listOf(
        "L" to 9,
        "M" to 10,
        "X" to 11,
        "J" to 12,
        "V" to 13,
        "S" to 14,
        "D" to 15
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        // Header
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

        // Calendar Card (`Noviembre 2026`, `L 9` .. `D 15`, `X 11` in coral capsule)
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
            Text(text = "Hoy · $selectedDay nov.", style = McLabel12Bold.copy(color = McTextSecondary))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Round schedule cards with left coral indicator bar
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
                                Text(text = round.timeRange, style = McBody14Bold)
                            }
                            Surface(
                                color = if (round.roundType == "Veterinaria") McCoralSoft else McSoftNeutral,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = round.roundType,
                                    style = McLabel11Regular.copy(
                                        color = if (round.roundType == "Veterinaria") McCoralDark else McTextPrimary,
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
                                round.initials.forEachIndexed { idx, init ->
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

                            if (!round.isJoined) {
                                Button(
                                    onClick = {
                                        round.isJoined = true
                                        MiauCareRepository.showToast("Te uniste a la ronda")
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
                                    shape = RoundedCornerShape(999.dp),
                                    modifier = Modifier.clickable {
                                        MiauCareRepository.showToast("Detalles de ronda: ${round.zoneTitle}")
                                    }
                                ) {
                                    Text(
                                        text = "Ver",
                                        style = McLabel12Bold,
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp)
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
// 9. LISTA DE GATOS / CENSO (`Lista de gatos`)
// =========================================================================
@Composable
fun VoluntarioCensusScreen(
    onOpenSightingHistory: () -> Unit,
    onReportUnregisteredCat: () -> Unit,
    onOpenCatDetail: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Order matching Figma `Lista de gatos`: Gary, Peach, Whitney, Buggy + others
    val censusOrderIds = listOf("cat-gary", "cat-peach", "cat-whitney", "cat-buggy", "cat-samantha", "cat-benito", "cat-luna", "cat-manchas")
    val orderedCats = censusOrderIds.mapNotNull { id -> MiauCareRepository.cats.find { it.id == id } }

    val filteredCats = orderedCats.filter { cat ->
        val matchesQuery = cat.name.contains(searchQuery, ignoreCase = true) ||
            cat.breed.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedFilter) {
            "Mi Zona" -> cat.colony.contains("Norte", ignoreCase = true)
            "En Calle" -> cat.statusTag.contains("calle", ignoreCase = true)
            "Hospitalizado" -> cat.statusTag.contains("Hospitalizado", ignoreCase = true)
            else -> true
        }
        matchesQuery && matchesTab
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
        // Header: "Censo de gatos" / "10 gatos" + History Clock & Coral '+' FAB
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Censo de gatos", style = McTitle24)
                Text(text = "10 gatos", style = McBody14Regular)
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

        // 2x2 Grid matching `step_59_shot_5_voluntario_Calendario_y_Lista.png`
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
    onClick: () -> Unit
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
                    contentDescription = cat.name,
                    modifier = Modifier.fillMaxSize()
                )
                // Top-right white circle with red location pin
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
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = cat.breed,
                style = McLabel11Regular,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = McSoftNeutral,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cat.statusTag,
                        style = McLabel11Regular.copy(
                            color = McTextPrimary,
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
        }
    }
}

// =========================================================================
// 10. INFORMACIÓN DEL GATO (`Información del gato` - Samantha)
// =========================================================================
@Composable
fun CatDetailScreen(
    catId: String,
    onBack: () -> Unit,
    onReportSightingClick: (String) -> Unit
) {
    val cat = remember(catId) {
        if (catId.isBlank()) MiauCareRepository.getCatById("cat-samantha")
        else MiauCareRepository.getCatById(catId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
    ) {
        // Top hero cat image
        McCatImage(
            resId = cat.imageRes,
            contentDescription = cat.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .align(Alignment.TopCenter)
        )

        // Back button on photo
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 14.dp)
        ) {
            McBackCircleButton(onClick = onBack)
        }

        // Bottom rounded sheet overlapping photo
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
                // Drag handle
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
                        Text(text = cat.name, style = McTitle24)
                        Text(text = "${cat.breed} · Ficha del censo", style = McBody14Regular)
                    }
                    McStatusBadge(label = cat.statusTag, showRedDot = true)
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
                        text = cat.colony,
                        style = McLabel12Bold.copy(color = McTextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4 stat boxes (`Macho`, `3 años`, `Esterilizado`, `4.2 kg`)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CatStatBox(value = cat.sex, label = "Sexo", modifier = Modifier.weight(1f))
                    CatStatBox(value = cat.age, label = "Edad aprox.", modifier = Modifier.weight(1f))
                    CatStatBox(value = cat.sterilizationStatus, label = "Estado", modifier = Modifier.weight(1.2f))
                    CatStatBox(value = "${cat.weightKg} kg", label = "Peso", modifier = Modifier.weight(0.9f))
                }

                Spacer(modifier = Modifier.height(22.dp))

                // "Historia Clínica" + "Solo lectura" badge
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

                // Soft red card: FIV+
                Surface(
                    color = McCoralSoft,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .fillMaxHeight()
                                .background(McCoral)
                        )
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddBox,
                                contentDescription = null,
                                tint = McCoral,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cat.fivWarningTitle ?: "FIV+ (Inmunodeficiencia Felina)",
                                    style = McBody14Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = cat.fivWarningDesc ?: "Diagnosticado el 12/Oct/2026. Requiere monitoreo de peso.",
                                    style = McLabel11Regular
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary condition card: Ceguera Parcial
                Surface(
                    color = McBackground,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MedicalServices,
                            contentDescription = null,
                            tint = McTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = cat.secondaryConditionTitle ?: "Ceguera Parcial - Ojo Izquierdo",
                                style = McBody14Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cat.secondaryConditionDesc ?: "Condición estable. No requiere medicación.",
                                style = McLabel11Regular
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                McCoralButton(
                    text = "Reportar avistamiento en ronda",
                    onClick = { onReportSightingClick(cat.id) }
                )
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
// 11. REPORTAR GATO NO CENSADO (`Reportar gato no censado`)
// =========================================================================
@Composable
fun ReportUnregisteredCatScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var apparentState by remember { mutableStateOf("Herido") }
    var location by remember { mutableStateOf("Parque Norte - C. del Olivo") }
    var observations by remember { mutableStateOf("") }
    var photoAttached by remember { mutableStateOf(false) }

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
                Text(text = "Reportar gato no censado", style = McTitle18)
                Text(text = "Registrar nuevo avistamiento", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info alert banner
        Surface(
            color = McSoftNeutral,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = McTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Este gato no está en el censo, se generará una alerta de rescate",
                    style = McLabel12Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Photo Upload Box
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, McBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(155.dp)
                .clickable { photoAttached = !photoAttached }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Surface(
                    color = McSoftNeutral,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (photoAttached) Icons.Default.Check else Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = McTextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (photoAttached) "Foto adjuntada (gato_rescate.jpg)" else "Añadir foto del gato",
                    style = McBody14Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Toca para tomar una foto o subir de la galería",
                    style = McLabel11Regular
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Ubicación
        Text(text = "Ubicación", style = McLabel12Bold)
        Spacer(modifier = Modifier.height(8.dp))
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
                    tint = McTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = location,
                    style = McBody14Regular.copy(color = McTextPrimary),
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = McSoftNeutral,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.clickable {
                        location = if (location.contains("Olivo")) "Plaza Mayor - Centro" else "Parque Norte - C. del Olivo"
                    }
                ) {
                    Text(
                        text = "Cambiar",
                        style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Estado aparente (`Sano`, `Enfermo`, `Herido`)
        Text(text = "Estado aparente", style = McLabel12Bold)
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

        Spacer(modifier = Modifier.height(18.dp))

        McInputField(
            label = "Observaciones",
            value = observations,
            onValueChange = { observations = it },
            placeholder = "Color del pelaje, comportamiento, señas particulares...",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        McCoralButton(
            text = "Enviar alerta de rescate",
            onClick = {
                MiauCareRepository.sightingHistory.add(
                    0,
                    SightingRecord(
                        id = "sig-${System.currentTimeMillis()}",
                        catName = "Gato no censado",
                        dateGroup = "Hoy · 13 nov 2026",
                        dateTime = "13 nov 2026 · Ahora",
                        healthStatus = apparentState,
                        ateFood = false,
                        reportedBy = "Reportado por Pepe Pérez",
                        imageRes = R.drawable.img_cat_samantha
                    )
                )
                MiauCareRepository.showToast("Alerta de rescate enviada")
                onSubmitted()
            }
        )
    }
}

// =========================================================================
// 12. HISTORIAL DE AVISTAMIENTOS (`Historial de avistamientos`)
// =========================================================================
@Composable
fun SightingHistoryScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos los gatos") }

    val filtered = MiauCareRepository.sightingHistory.filter { item ->
        val matchesQuery = item.catName.contains(searchQuery, ignoreCase = true)
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
                Text(text = "Rondas registradas en tu zona", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre de gato..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter row matching `step_60_shot_6_voluntario_Detalle_y_Reportes.png`
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
                            Spacer(modifier = Modifier.height(6.dp))
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
// 13. NOTIFICACIONES (`Notificaciones`)
// =========================================================================
@Composable
fun VoluntarioNotificationsScreen(
    onBack: () -> Unit
) {
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

        Text(text = "TODAY", style = McLabel12Bold)
        Spacer(modifier = Modifier.height(10.dp))
        repeat(3) {
            NotificationItemCard()
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "YESTERDAY", style = McLabel12Bold)
        Spacer(modifier = Modifier.height(10.dp))
        repeat(2) {
            NotificationItemCard()
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun NotificationItemCard() {
    Surface(
        color = McCardWhite,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
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
                Text(text = "Recordatorio de Ronda", style = McBody14Bold)
                Text(
                    text = "Tienes una ronda programada en Zona Norte a las 4:00 PM.",
                    style = McLabel11Regular
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "15m ago.",
                    style = McLabel11Regular.copy(fontSize = 10.sp)
                )
            }
        }
    }
}

// =========================================================================
// 14. PERFIL VOLUNTARIO (`Perfil`)
// =========================================================================
@Composable
fun VoluntarioProfileScreen(
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    var fullName by remember { mutableStateOf("Sofía Álvarez") }
    var email by remember { mutableStateOf("sofia.alvarez@miaucare.org") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Mi Perfil", style = McTitle24)
                Text(text = "Gestiona tu información personal", style = McBody14Regular)
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

        // Avatar + Name + "Voluntaria de Campo" coral pill
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
                        Text(
                            text = "SA",
                            style = McTitle24.copy(color = McTextSecondary)
                        )
                    }
                }
                Surface(
                    color = McCoral,
                    shape = CircleShape,
                    border = BorderStroke(2.dp, McBackground),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = McCardWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = fullName, style = McTitle18)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = McCoral,
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = "Voluntaria de Campo",
                    style = McLabel11Regular.copy(
                        color = McTextPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Active Zone Card
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Text(text = "Zona Activa: Norte - Parque Central", style = McBody14Bold)
                    Text(text = "3 colonias asignadas para rondas", style = McLabel11Regular)
                }
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar",
                    tint = McTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        McInputField(
            label = "Nombre completo",
            value = fullName,
            onValueChange = { fullName = it }
        )
        Spacer(modifier = Modifier.height(14.dp))

        McInputField(
            label = "Correo electrónico",
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        McCoralButton(
            text = "Guardar cambios",
            darkText = true,
            onClick = { MiauCareRepository.showToast("Cambios guardados") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onLogout,
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

        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Cambiar vista de rol (Prototipo):", style = McLabel11Regular)
        Spacer(modifier = Modifier.height(6.dp))
        McRolePrototypeSwitcher(onRoleSelected = onRoleSwitch)
    }
}
