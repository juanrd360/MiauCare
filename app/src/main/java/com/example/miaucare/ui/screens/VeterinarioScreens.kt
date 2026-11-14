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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.R
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.components.*
import com.example.miaucare.ui.theme.*

// =========================================================================
// 1. REGISTRO / SOLICITUD VETERINARIO (`Registro/solicitud`)
// =========================================================================
@Composable
fun VetSolicitudScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var cedulaNumber by remember { mutableStateOf("") }
    var photoAttached by remember { mutableStateOf(false) }
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
            color = McBackground.copy(alpha = 0.90f),
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
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Text(text = "Crear Solicitud", style = McTitle28)
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
                    label = "Especialidad Clínica",
                    value = specialty,
                    onValueChange = { specialty = it },
                    placeholder = "Ej. Cirugía",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Número de cédula",
                    value = cedulaNumber,
                    onValueChange = { cedulaNumber = it },
                    placeholder = "Ej. 1234567890",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Cédula Profesional", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = McGlassInput,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, McCardWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .clickable { photoAttached = !photoAttached }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (photoAttached) Icons.Default.CheckCircle else Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = McTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (photoAttached) "cedula_profesional.jpg adjuntada" else "Sube una foto de la cédula",
                            style = McLabel11Regular
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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

                Spacer(modifier = Modifier.height(18.dp))

                McCoralButton(
                    text = "Enviar Solicitud",
                    darkText = true,
                    onClick = {
                        MiauCareRepository.currentRole = MiauRole.VETERINARIO
                        MiauCareRepository.showToast("Solicitud veterinaria enviada")
                        onSubmitted()
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
// 2. DASHBOARD CLÍNICO (`Dashboard clínico`)
// =========================================================================
@Composable
fun VetDashboardScreen(
    onOpenClinicalRecord: (String) -> Unit,
    onOpenMedicalAlerts: () -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    var showDelegateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 110.dp)
    ) {
        McRolePrototypeSwitcher(onRoleSelected = onRoleSwitch)
        Spacer(modifier = Modifier.height(14.dp))

        // Header: "Control clínico · MiauCare" / "Hola, Dra. Sofía" + Avatar with green dot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Control clínico · MiauCare", style = McLabel11Regular)
                Text(text = "Hola, Dra. Sofía", style = McTitle24)
            }
            Box(modifier = Modifier.size(48.dp)) {
                McCatImage(
                    resId = R.drawable.img_doc_sofia,
                    contentDescription = "Dra. Sofía",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF28A745))
                        .border(2.dp, McBackground, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3 KPI Cards (`3 Cirugías CES hoy`, `2 Alertas críticas`, `8 Pacientes asignados`)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VetKpiCard(
                number = "3",
                label = "Cirugías CES\nhoy",
                isAlert = false,
                modifier = Modifier.weight(1f)
            )
            VetKpiCard(
                number = "2",
                label = "Alertas\ncríticas",
                isAlert = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenMedicalAlerts() }
            )
            VetKpiCard(
                number = "8",
                label = "Pacientes\nasignados",
                isAlert = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: "Prioridad médica" | "2 críticas"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Prioridad médica", style = McTitle18)
            Text(
                text = "2 críticas",
                style = McLabel12Bold.copy(color = McCoralDark),
                modifier = Modifier.clickable { onOpenMedicalAlerts() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Priority Alert Card (`CRÍTICA · HACE 8 MIN`, `Luna`, `Dificultad respiratoria`)
        Surface(
            color = McCoralSoft,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, McCoralBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(McCoralDark)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CRÍTICA · HACE 8 MIN",
                        style = McLabel11Regular.copy(
                            color = McCoralDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    McCatImage(
                        resId = R.drawable.img_cat_luna,
                        contentDescription = "Luna",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Luna", style = McTitle18)
                        Text(
                            text = "Dificultad respiratoria",
                            style = McBody14Bold.copy(color = McCoralDark)
                        )
                        Text(
                            text = "Reportó Ana · Colonia Centro",
                            style = McLabel11Regular
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            MiauCareRepository.showToast("Caso aceptado: Luna")
                            onOpenClinicalRecord("cat-luna")
                        },
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = McBlack,
                            contentColor = McCardWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text(text = "Aceptar caso", style = McLabel12Bold.copy(color = McCardWhite))
                    }
                    Surface(
                        color = McCardWhite,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { showDelegateDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Delegar", style = McLabel12Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: "Tus pacientes" | "Ver todos"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tus pacientes", style = McTitle18)
            Text(
                text = "Ver todos",
                style = McLabel12Bold.copy(color = McTextSecondary),
                modifier = Modifier.clickable { onOpenMedicalAlerts() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Patient 1: Manchas (`MC-024 · Revisión a las 09:00`, `Hospitalizado`)
        VetPatientRowCard(
            name = "Manchas",
            subtitle = "MC-024 · Revisión a las 09:00",
            badgeText = "Hospitalizado",
            badgeIsCoral = true,
            imageRes = R.drawable.img_cat_manchas,
            onClick = { onOpenClinicalRecord("cat-manchas") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Patient 2: Benito (`MC-031 · Cirugía a las 10:00`, `CES hoy`)
        VetPatientRowCard(
            name = "Benito",
            subtitle = "MC-031 · Cirugía a las 10:00",
            badgeText = "CES hoy",
            badgeIsCoral = false,
            imageRes = R.drawable.img_cat_benito,
            onClick = { onOpenClinicalRecord("cat-benito") }
        )
    }

    if (showDelegateDialog) {
        VetDelegateModal(
            catName = "Manchas",
            onDismiss = { showDelegateDialog = false },
            onConfirm = { docName ->
                showDelegateDialog = false
                MiauCareRepository.showToast("Caso delegado a $docName")
            }
        )
    }
}

@Composable
private fun VetKpiCard(
    number: String,
    label: String,
    isAlert: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isAlert) McCoralSoft else McCardWhite,
        shape = RoundedCornerShape(20.dp),
        border = if (isAlert) BorderStroke(1.dp, McCoralBorder) else null,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = number,
                style = McTitle24.copy(
                    color = if (isAlert) McCoralDark else McTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = McLabel11Regular.copy(
                    color = if (isAlert) McCoralDark else McTextSecondary,
                    lineHeight = 14.sp
                )
            )
        }
    }
}

@Composable
private fun VetPatientRowCard(
    name: String,
    subtitle: String,
    badgeText: String,
    badgeIsCoral: Boolean,
    imageRes: Int,
    onClick: () -> Unit
) {
    Surface(
        color = McCardWhite,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            McCatImage(
                resId = imageRes,
                contentDescription = name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = McTitle16)
                Text(text = subtitle, style = McLabel11Regular)
            }
            Surface(
                color = if (badgeIsCoral) McCoralSoft else McSoftNeutral,
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = badgeText,
                    style = McLabel11Regular.copy(
                        color = if (badgeIsCoral) McCoralDark else McTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// =========================================================================
// 3. FICHA CLÍNICA + MODAL REGISTRAR ATENCIÓN + ACCESO DENEGADO (`Ficha clínica`)
// =========================================================================
@Composable
fun VetClinicalRecordScreen(
    catId: String,
    onBack: () -> Unit,
    onOpenDirectory: () -> Unit
) {
    val cat = remember(catId) {
        if (catId.isBlank()) MiauCareRepository.getCatById("cat-manchas")
        else MiauCareRepository.getCatById(catId)
    }
    var showRegistrarSheet by remember { mutableStateOf(false) }
    var simulateDeniedAccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Header with toggle to preview `Acceso denegado` state from Figma
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                McBackCircleButton(onClick = onBack)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Ficha clínica", style = McTitle18)
                    Text(text = "Paciente ${cat.code}", style = McLabel11Regular)
                }
            }
            Surface(
                color = if (simulateDeniedAccess) McCoralSoft else McSoftNeutral,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.clickable { simulateDeniedAccess = !simulateDeniedAccess }
            ) {
                Text(
                    text = if (simulateDeniedAccess) "Ver con permiso" else "Simular sin permiso",
                    style = McLabel11Regular.copy(
                        color = if (simulateDeniedAccess) McCoralDark else McTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cat Summary Card
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                McCatImage(
                    resId = cat.imageRes,
                    contentDescription = cat.name,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = cat.name, style = McTitle18)
                    Text(
                        text = "${cat.code} · ${cat.sex} · ${cat.age}",
                        style = McLabel11Regular
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = McTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = cat.colony, style = McLabel11Regular)
                    }
                }
                if (!simulateDeniedAccess) {
                    McStatusBadge(label = cat.statusTag)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (simulateDeniedAccess) {
            // Exact match to `Veterinario · Acceso denegado` (`step_72_shot_6_veterinario_Validaciones_y_Toasts.png`)
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
                        color = McCoralSoft,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = McCoralDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Sin permisos sobre este gato",
                        style = McTitle18,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Este paciente está asignado al Dr. Luis Pérez. Solicita una delegación o consulta el directorio médico para ver su historial clínico.",
                        style = McBody14Regular,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = McSoftNeutral,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = McTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Veterinario responsable: Dr. Luis Pérez",
                                style = McLabel12Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    McCoralButton(
                        text = "Ir al directorio médico",
                        onClick = onOpenDirectory
                    )
                }
            }
        } else {
            // Weight & Sterilization Card
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "PESO", style = McLabel11Regular.copy(fontSize = 10.sp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "${cat.weightKg} kg", style = McTitle16)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "ESTERILIZACIÓN", style = McLabel11Regular.copy(fontSize = 10.sp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = cat.sterilizationStatus, style = McTitle16)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Allergy warning banner (`Alerta: reacción a penicilina`)
            Surface(
                color = McCoralSoft,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, McCoralBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = McCoralDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cat.allergyAlert ?: "Alerta: reacción a penicilina",
                        style = McLabel12Bold.copy(color = McCoralDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Medical History section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Historial médico", style = McTitle18)
                Text(text = "2 registros", style = McLabel11Regular)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Record 1: DIAGNÓSTICO ACTIVO
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DIAGNÓSTICO ACTIVO",
                            style = McLabel11Regular.copy(
                                color = McCoralDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                        Text(text = "12 nov", style = McLabel11Regular)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Deshidratación moderada", style = McTitle16)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fluidoterapia subcutánea cada 8 h. En observación.",
                        style = McBody14Regular
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Dra. Sofía García", style = McLabel11Regular)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Record 2: PROCEDIMIENTO
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PROCEDIMIENTO",
                            style = McLabel11Regular.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                        Text(text = "10 nov", style = McLabel11Regular)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Ingreso y evaluación general", style = McTitle16)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Peso 3.8 kg, mucosas pálidas, sin fiebre.",
                        style = McBody14Regular
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Dra. Sofía García", style = McLabel11Regular)
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            McCoralButton(
                text = "+ Nuevo Diagnóstico / Procedimiento",
                onClick = { showRegistrarSheet = true }
            )
        }
    }

    if (showRegistrarSheet) {
        VetRegistrarAtencionDialog(
            catNameAndCode = "${cat.name} · ${cat.code}",
            onDismiss = { showRegistrarSheet = false },
            onSaved = {
                showRegistrarSheet = false
                MiauCareRepository.showToast("Atención clínica registrada")
            }
        )
    }
}

@Composable
private fun VetRegistrarAtencionDialog(
    catNameAndCode: String,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    var attentionType by remember { mutableStateOf("Urgencia") }
    var diagnosis by remember { mutableStateOf("Deshidratación moderada") }
    var severity by remember { mutableStateOf("Moderada") }
    var notes by remember {
        mutableStateOf("Fluidoterapia subcutánea cada 8 h. Paciente estable y bajo observación.")
    }
    var resolution by remember { mutableStateOf("Dejar hospitalizado") }
    var showValidationError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Registrar atención", style = McTitle18)
                        Text(text = catNameAndCode, style = McLabel11Regular)
                    }
                    Surface(
                        color = McSoftNeutral,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (showValidationError) {
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
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Revisa los campos obligatorios",
                                    style = McLabel12Bold.copy(color = McCoralDark)
                                )
                                Text(
                                    text = "Completa el diagnóstico, las notas clínicas y la resolución antes de guardar.",
                                    style = McLabel11Regular.copy(color = McCoralDark)
                                )
                            }
                        }
                    }
                }

                Text(text = "Tipo de atención", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CES", "Urgencia", "Chequeo").forEach { item ->
                        val selected = attentionType == item
                        Surface(
                            color = if (selected) McBlack else McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable { attentionType = item }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item,
                                    style = McLabel12Bold.copy(
                                        color = if (selected) McCardWhite else McTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                McInputField(
                    label = "Diagnóstico",
                    value = diagnosis,
                    onValueChange = {
                        diagnosis = it
                        showValidationError = false
                    },
                    placeholder = "Selecciona un diagnóstico del catálogo"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Nivel de gravedad", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Leve", "Moderada", "Crítica").forEach { item ->
                        val selected = severity == item
                        Surface(
                            color = if (selected) McCoralSoft else McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable { severity = item }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item,
                                    style = McLabel12Bold.copy(
                                        color = if (selected) McCoralDark else McTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                McInputField(
                    label = "Notas de la atención",
                    value = notes,
                    onValueChange = {
                        notes = it
                        showValidationError = false
                    },
                    placeholder = "Describe los hallazgos, medicación y plan de seguimiento...",
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Resolución del caso", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Dar de alta", "Dejar hospitalizado").forEach { item ->
                        val selected = resolution == item
                        Surface(
                            color = if (selected) McCoralSoft else McSoftNeutral,
                            shape = RoundedCornerShape(14.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { resolution = item }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item,
                                    style = McLabel11Regular.copy(
                                        color = if (selected) McCoralDark else McTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                McCoralButton(
                    text = "Guardar atención",
                    onClick = {
                        if (diagnosis.isBlank() || notes.isBlank()) {
                            showValidationError = true
                        } else {
                            onSaved()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Probar estado de validación vacía",
                    style = McLabel11Regular.copy(color = McCoralDark),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            diagnosis = ""
                            notes = ""
                            showValidationError = true
                        }
                )
            }
        }
    }
}

// =========================================================================
// 4. ALERTAS MÉDICAS + MODALES CERRAR Y DELEGAR (`Alertas médicas`)
// =========================================================================
@Composable
fun VetMedicalAlertsScreen(
    onBack: () -> Unit,
    onOpenClinicalRecord: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Nuevas") }
    var closingAlertCat by remember { mutableStateOf<String?>(null) }
    var delegatingAlertCat by remember { mutableStateOf<String?>(null) }

    val filteredAlerts = MiauCareRepository.medicalAlerts.filter { it.tabStatus == selectedTab }

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
                Text(text = "Alertas médicas", style = McTitle18)
                Text(text = "Triaje y seguimiento", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Segmented tabs (`Nuevas`, `En atención`, `Cerradas`)
        Surface(
            color = McSoftNeutral,
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("Nuevas", "En atención", "Cerradas").forEach { tab ->
                    val selected = selectedTab == tab
                    Surface(
                        color = if (selected) McBlack else Color.Transparent,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = tab }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = tab,
                                style = McLabel12Bold.copy(
                                    color = if (selected) McCardWhite else McTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredAlerts.forEach { alert ->
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        McCatImage(
                            resId = alert.imageRes,
                            contentDescription = alert.catName,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .clickable { onOpenClinicalRecord(alert.catId) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${alert.catName} · ${alert.timeAgo}", style = McBody14Bold)
                                Surface(
                                    color = if (alert.urgencyBadge == "Crítica") McCoralSoft else McSoftNeutral,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = alert.urgencyBadge,
                                        style = McLabel11Regular.copy(
                                            color = if (alert.urgencyBadge == "Crítica") McCoralDark else McTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(text = alert.symptomTitle, style = McTitle16)
                            Text(text = alert.locationAndReporter, style = McLabel11Regular)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedTab == "Nuevas") {
                        McCoralButton(
                            text = "Pasar a en atención",
                            height = 42.dp,
                            onClick = {
                                alert.tabStatus = "En atención"
                                selectedTab = "En atención"
                                MiauCareRepository.showToast("${alert.catName} en atención")
                            }
                        )
                    } else if (selectedTab == "En atención") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { closingAlertCat = alert.catName },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = McCoral),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                            ) {
                                Text(text = "Cerrar alerta", style = McLabel12Bold.copy(color = McCardWhite))
                            }
                            Surface(
                                color = McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clickable { delegatingAlertCat = alert.catName }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "Delegar", style = McLabel12Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (closingAlertCat != null) {
        VetCloseAlertModal(
            catName = closingAlertCat!!,
            onDismiss = { closingAlertCat = null },
            onConfirm = {
                MiauCareRepository.medicalAlerts.find { it.catName == closingAlertCat }?.tabStatus = "Cerradas"
                closingAlertCat = null
                MiauCareRepository.showToast("Alerta médica cerrada")
            }
        )
    }

    if (delegatingAlertCat != null) {
        VetDelegateModal(
            catName = delegatingAlertCat!!,
            onDismiss = { delegatingAlertCat = null },
            onConfirm = { doc ->
                delegatingAlertCat = null
                MiauCareRepository.showToast("Caso delegado a $doc")
            }
        )
    }
}

@Composable
fun VetCloseAlertModal(
    catName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var summary by remember {
        mutableStateOf("Se completó la fluidoterapia. Mucosas rosadas y apetito recuperado.")
    }
    var finalState by remember { mutableStateOf("Dado de alta") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Cerrar alerta - $catName", style = McTitle18)
                        Text(text = "Registra la resolución antes de cerrar", style = McLabel11Regular)
                    }
                    Surface(
                        color = McSoftNeutral,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                McInputField(
                    label = "Resumen de la atención",
                    value = summary,
                    onValueChange = { summary = it },
                    singleLine = false,
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Estado final del gato", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Dado de alta", "Permanece hospitalizado").forEach { st ->
                        val selected = finalState == st
                        Surface(
                            color = if (selected) McCoralSoft else McSoftNeutral,
                            shape = RoundedCornerShape(14.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { finalState = st }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = st,
                                    style = McLabel11Regular.copy(
                                        color = if (selected) McCoralDark else McTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                McCoralButton(text = "Cerrar alerta", onClick = onConfirm)
            }
        }
    }
}

@Composable
fun VetDelegateModal(
    catName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedDoc by remember { mutableStateOf("Dr. Luis Pérez") }
    val docs = listOf(
        Triple("Dr. Luis Pérez", "Cirugía CES · 2 pacientes activos", R.drawable.img_doc_luis),
        Triple("Dra. Elena Ruíz", "Medicina felina · 1 paciente activo", R.drawable.img_doc_irene)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Delegar - $catName", style = McTitle18)
                        Text(text = "Selecciona un veterinario disponible", style = McLabel11Regular)
                    }
                    Surface(
                        color = McSoftNeutral,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                docs.forEach { (name, desc, img) ->
                    val isSelected = selectedDoc == name
                    Surface(
                        color = if (isSelected) McCoralSoft else McBackground,
                        shape = RoundedCornerShape(18.dp),
                        border = if (isSelected) BorderStroke(1.dp, McCoralBorder) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clickable { selectedDoc = name }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            McCatImage(
                                resId = img,
                                contentDescription = name,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = name, style = McBody14Bold)
                                Text(text = desc, style = McLabel11Regular)
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(McCoral),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = McCardWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                McCoralButton(
                    text = "Confirmar delegación",
                    onClick = { onConfirm(selectedDoc) }
                )
            }
        }
    }
}

// =========================================================================
// 5. CENSO Y DIRECTORIO MÉDICO (`Censo y directorio`)
// =========================================================================
@Composable
fun VetDirectoryScreen(
    onOpenClinicalRecord: (String) -> Unit
) {
    var activeSegment by remember { mutableStateOf("Directorio médico") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf("Todas") }

    val externalDoctors = MiauCareRepository.doctors.filter { !it.isInternal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        Text(
            text = if (activeSegment == "Directorio médico") "Directorio médico" else "Censo felino",
            style = McTitle24
        )
        Text(
            text = if (activeSegment == "Directorio médico") "Apoyo clínico y colegas" else "Pacientes registrados en MiauCare",
            style = McBody14Regular
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented toggle (`Censo felino` | `Directorio médico`)
        Surface(
            color = McSoftNeutral,
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("Censo felino", "Directorio médico").forEach { seg ->
                    val selected = activeSegment == seg
                    Surface(
                        color = if (selected) McBlack else Color.Transparent,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeSegment = seg }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = seg,
                                style = McLabel12Bold.copy(
                                    color = if (selected) McCardWhite else McTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = if (activeSegment == "Directorio médico") "Buscar por nombre o especialidad..." else "Buscar paciente..."
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (activeSegment == "Directorio médico") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todas", "Cirugía", "Medicina interna").forEach { spec ->
                    McFilterPill(
                        label = spec,
                        selected = selectedSpecialty == spec,
                        onClick = { selectedSpecialty = spec },
                        unselectedBg = McSoftNeutral
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Colegas disponibles", style = McTitle18)
                Text(text = "2 en línea", style = McLabel11Regular)
            }

            Spacer(modifier = Modifier.height(12.dp))

            externalDoctors.forEach { doc ->
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        McCatImage(
                            resId = doc.imageRes,
                            contentDescription = doc.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = doc.name, style = McTitle16)
                            Text(text = doc.specialty, style = McLabel11Regular)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF28A745))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = doc.distanceText,
                                    style = McLabel11Regular.copy(color = Color(0xFF236E3D))
                                )
                            }
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier.clickable {
                                MiauCareRepository.showToast("Solicitud enviada a ${doc.name}")
                            }
                        ) {
                            Text(
                                text = "Solicitar apoyo",
                                style = McLabel11Regular.copy(
                                    color = McTextPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            MiauCareRepository.cats.chunked(2).forEach { rowCats ->
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
                            onClick = { onOpenClinicalRecord(cat.id) }
                        )
                    }
                    if (rowCats.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// =========================================================================
// 6. AGENDA CLÍNICA (`Agenda clínica`)
// =========================================================================
@Composable
fun VetAgendaScreen(
    onOpenClinicalRecord: (String) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(12) }
    val days = listOf(
        "L" to 9,
        "M" to 10,
        "M" to 11,
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
        Text(text = "Agenda clínica", style = McTitle24)
        Text(text = "Citas y cirugías CES", style = McBody14Regular)

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Card (`Noviembre 2026`, `J 12` in solid black circle)
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedDay = num }
                        ) {
                            Text(text = dow, style = McLabel11Regular)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = if (isSelected) McBlack else Color.Transparent,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = num.toString(),
                                        style = McBody14Bold.copy(
                                            color = if (isSelected) McCardWhite else McTextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Jueves, $selectedDay de noviembre", style = McTitle16)
        Text(text = "3 cirugías CES · 1 ronda obligatoria", style = McLabel11Regular)

        Spacer(modifier = Modifier.height(14.dp))

        // 09:00 Mandatory Round (`OBLIGATORIA`)
        VetTimelineRow(
            time = "09:00",
            badgeText = "OBLIGATORIA",
            title = "Ronda de revisión - Manchas",
            subtitle = "Hospitalización · Control de hidratación",
            isMandatory = true,
            onClick = { onOpenClinicalRecord("cat-manchas") }
        )

        VetTimelineRow(
            time = "10:00",
            badgeText = "Programada",
            title = "CES · Benito",
            subtitle = "MC-031 · Macho · Colonia Norte",
            isMandatory = false,
            onClick = { onOpenClinicalRecord("cat-benito") }
        )

        VetTimelineRow(
            time = "11:00",
            badgeText = "Programada",
            title = "CES · Oliva",
            subtitle = "MC-018 · Hembra · Colonia Centro",
            isMandatory = false,
            onClick = { onOpenClinicalRecord("cat-luna") }
        )

        VetTimelineRow(
            time = "12:00",
            badgeText = "Programada",
            title = "CES · Milo",
            subtitle = "MC-042 · Macho · Parque Sur",
            isMandatory = false,
            onClick = { onOpenClinicalRecord("cat-gary") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = McSoftNeutral,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = McTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Horarios protegidos · Sin cruces", style = McLabel12Bold)
            }
        }
    }
}

@Composable
private fun VetTimelineRow(
    time: String,
    badgeText: String,
    title: String,
    subtitle: String,
    isMandatory: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = time,
            style = McLabel12Bold.copy(color = McTextSecondary),
            modifier = Modifier
                .width(46.dp)
                .padding(top = 14.dp)
        )
        Surface(
            color = if (isMandatory) McCoralSoft else McCardWhite,
            shape = RoundedCornerShape(18.dp),
            border = if (isMandatory) BorderStroke(1.dp, McCoralBorder) else null,
            modifier = Modifier.weight(1f)
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                if (isMandatory) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .background(McCoral)
                    )
                }
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = badgeText,
                        style = McLabel11Regular.copy(
                            color = if (isMandatory) McCoralDark else McTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = title, style = McTitle16)
                    Text(text = subtitle, style = McLabel11Regular)
                }
            }
        }
    }
}

// =========================================================================
// 7. PERFIL Y DISPONIBILIDAD (`Perfil y disponibilidad`)
// =========================================================================
@Composable
fun VetProfileScreen(
    onLogout: () -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    var emergencyAvailable by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        Text(text = "Mi perfil", style = McTitle24)
        Text(text = "Credenciales y disponibilidad", style = McBody14Regular)

        Spacer(modifier = Modifier.height(16.dp))

        // Doctor Profile Card
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    McCatImage(
                        resId = R.drawable.img_doc_sofia,
                        contentDescription = "Dra. Sofía García",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = "Dra. Sofía García", style = McTitle18)
                        Text(text = "Médica veterinaria", style = McLabel11Regular)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = StatusSanoBg,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = "✓ Cédula 8742916 · Verificada",
                                style = McLabel11Regular.copy(
                                    color = StatusSanoText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Especialidades", style = McLabel11Regular)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Cirugía CES", "Medicina felina").forEach { sp ->
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = sp,
                                style = McLabel12Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Emergency Availability Card
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Disponible para Emergencias", style = McTitle16)
                    Text(
                        text = "Recibir alertas críticas y casos urgentes",
                        style = McLabel11Regular
                    )
                }
                Switch(
                    checked = emergencyAvailable,
                    onCheckedChange = {
                        emergencyAvailable = it
                        MiauCareRepository.showToast(
                            if (it) "Disponible para emergencias" else "Modo guardia pausado"
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = McCardWhite,
                        checkedTrackColor = McCoral
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Info note card (`¿Necesitas desconectarte?`)
        Surface(
            color = McSoftNeutral,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = McTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "¿Necesitas desconectarte?", style = McBody14Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Con 2 alertas críticas activas en tu zona, al menos un veterinario debe permanecer disponible.",
                        style = McLabel11Regular
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        McCoralButton(
            text = "Cerrar sesión",
            onClick = onLogout
        )

        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Cambiar vista de rol (Prototipo):", style = McLabel11Regular)
        Spacer(modifier = Modifier.height(6.dp))
        McRolePrototypeSwitcher(onRoleSelected = onRoleSwitch)
    }
}
