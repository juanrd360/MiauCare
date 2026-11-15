package com.example.miaucare.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.R
import com.example.miaucare.data.ApiResult
import com.example.miaucare.data.CatProfile
import com.example.miaucare.data.CirugiaCesDto
import com.example.miaucare.data.MedicalAlertItem
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.components.*
import com.example.miaucare.ui.theme.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

// =========================================================================
// 1. REGISTRO / SOLICITUD VETERINARIO (`POST /api/archivos/subir?carpeta=cedulas` + `POST /api/veterinarios/solicitud`)
// =========================================================================
@Composable
fun VetSolicitudScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedEspecialidadId by remember { mutableLongStateOf(1L) }
    var specialtyName by remember { mutableStateOf("Cirugía") }
    var cedulaNumber by remember { mutableStateOf("") }
    var cedulaBytes by remember { mutableStateOf<ByteArray?>(null) }
    var cedulaFileName by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }
    var errorBanner by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchCatalogosMaestros()
        MiauCareRepository.especialidadesCatalog.firstOrNull()?.let {
            selectedEspecialidadId = it.id
            specialtyName = it.nombre
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    cedulaBytes = bytes
                    cedulaFileName = "cedula_${System.currentTimeMillis()}.jpg"
                    MiauCareRepository.showToast("Foto de cédula cargada (${bytes.size / 1024} KB)")
                }
            } catch (_: Exception) {
                MiauCareRepository.showToast("No se pudo leer la imagen seleccionada", isError = true)
            }
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
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Text(text = "Crear Solicitud", style = McTitle28)
                Spacer(modifier = Modifier.height(10.dp))

                if (errorBanner != null) {
                    Surface(
                        color = McCoralSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, McCoralBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = errorBanner!!,
                            style = McLabel12Bold.copy(color = McCoralDark),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                McInputField(
                    label = "Nombre completo",
                    value = fullName,
                    onValueChange = { fullName = it; errorBanner = null },
                    placeholder = "Tu nombre y apellido",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Correo Electrónico",
                    value = email,
                    onValueChange = { email = it; errorBanner = null },
                    placeholder = "ejemplo@correo.com",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = { password = it; errorBanner = null },
                    placeholder = "Mínimo 8 caracteres",
                    isPassword = true,
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Especialidad Clínica", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val especialidades = MiauCareRepository.especialidadesCatalog
                if (especialidades.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        especialidades.forEach { esp ->
                            val selected = selectedEspecialidadId == esp.id
                            McFilterPill(
                                label = esp.nombre,
                                selected = selected,
                                onClick = {
                                    selectedEspecialidadId = esp.id
                                    specialtyName = esp.nombre
                                },
                                unselectedBg = McCardWhite
                            )
                        }
                    }
                } else {
                    McInputField(
                        label = "",
                        value = specialtyName,
                        onValueChange = { specialtyName = it },
                        placeholder = "Ej. Cirugía",
                        isGlassStyle = true
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Número de cédula",
                    value = cedulaNumber,
                    onValueChange = { cedulaNumber = it; errorBanner = null },
                    placeholder = "Ej. 1234567890",
                    isGlassStyle = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Cédula Profesional (Supabase Storage - carpeta cedulas)", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = McGlassInput,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, McCardWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .clickable {
                            galleryLauncher.launch("image/*")
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (cedulaBytes != null) Icons.Default.CheckCircle else Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = if (cedulaBytes != null) StatusSanoText else McTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (cedulaBytes != null) "$cedulaFileName lista para subir" else "Toca para elegir foto de tu galería",
                            style = McLabel11Regular
                        )
                        if (cedulaBytes == null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "O toca aquí para adjuntar cédula de prueba",
                                style = McLabel11Regular.copy(color = McCoralDark, fontWeight = FontWeight.Bold),
                                modifier = Modifier.clickable {
                                    val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.img_doc_sofia)
                                    val bos = ByteArrayOutputStream()
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 85, bos)
                                    cedulaBytes = bos.toByteArray()
                                    cedulaFileName = "cedula_profesional.jpg"
                                    MiauCareRepository.showToast("Imagen de cédula preparada")
                                }
                            )
                        }
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
                    text = if (MiauCareRepository.isLoading) "Subiendo cédula y enviando..." else "Enviar Solicitud",
                    darkText = true,
                    enabled = !MiauCareRepository.isLoading,
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || password.length < 8 || cedulaNumber.isBlank()) {
                            errorBanner = "Completa todos los campos y usa una contraseña de al menos 8 caracteres."
                            return@McCoralButton
                        }
                        scope.launch {
                            val bytesToUpload = cedulaBytes ?: run {
                                val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.img_doc_sofia)
                                val bos = ByteArrayOutputStream()
                                bmp.compress(Bitmap.CompressFormat.JPEG, 85, bos)
                                bos.toByteArray()
                            }
                            val res = MiauCareRepository.enviarSolicitudVeterinario(
                                nombreCompleto = fullName.trim(),
                                correo = email.trim(),
                                contrasena = password,
                                numeroCedula = cedulaNumber.trim(),
                                especialidadId = selectedEspecialidadId,
                                cedulaImageBytes = bytesToUpload
                            )
                            when (res) {
                                is ApiResult.Success -> onLoginClick()
                                is ApiResult.Error -> errorBanner = res.message
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
// 2. DASHBOARD CLÍNICO (`Dashboard clínico`)
// =========================================================================
@Composable
fun VetDashboardScreen(
    onOpenClinicalRecord: (String) -> Unit,
    onOpenMedicalAlerts: () -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var delegatingAlert by remember { mutableStateOf<MedicalAlertItem?>(null) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchVetDashboardData()
        MiauCareRepository.fetchVeterinariosDirectorio()
    }

    val priorityAlert = MiauCareRepository.medicalAlerts.firstOrNull { it.tabStatus == "Nuevas" }
        ?: MiauCareRepository.medicalAlerts.firstOrNull()
    val assignedCats = if (MiauCareRepository.vetAssignedCats.isNotEmpty()) {
        MiauCareRepository.vetAssignedCats
    } else {
        MiauCareRepository.cats.take(4)
    }
    val cesCount = MiauCareRepository.cirugiasCes.count { it.estado != "REALIZADA" && it.estado != "COMPLETADA" }
    val criticalAlertsCount = MiauCareRepository.medicalAlerts.count { it.tabStatus != "Cerradas" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 110.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            McServerConnectionChip(context = context)
            IconButton(onClick = { scope.launch { MiauCareRepository.fetchVetDashboardData() } }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Actualizar", tint = McTextPrimary)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        McRolePrototypeSwitcher(onRoleSelected = onRoleSwitch)
        Spacer(modifier = Modifier.height(14.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Control clínico · MiauCare", style = McLabel11Regular)
                Text(
                    text = "Hola, ${MiauCareRepository.sessionUserName.ifBlank { "Veterinario" }}",
                    style = McTitle24
                )
            }
            Box(modifier = Modifier.size(48.dp)) {
                McCatImage(
                    resId = R.drawable.img_doc_sofia,
                    contentDescription = MiauCareRepository.sessionUserName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (MiauCareRepository.vetDisponibleEmergencias) Color(0xFF28A745) else McTextSecondary)
                        .border(2.dp, McBackground, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3 KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VetKpiCard(
                number = cesCount.toString(),
                label = "Cirugías CES\npendientes",
                isAlert = false,
                modifier = Modifier.weight(1f)
            )
            VetKpiCard(
                number = criticalAlertsCount.toString(),
                label = "Alertas\nactivas",
                isAlert = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenMedicalAlerts() }
            )
            VetKpiCard(
                number = assignedCats.size.toString(),
                label = "Pacientes\nasignados",
                isAlert = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: "Prioridad médica"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Prioridad médica", style = McTitle18)
            Text(
                text = "$criticalAlertsCount activas",
                style = McLabel12Bold.copy(color = McCoralDark),
                modifier = Modifier.clickable { onOpenMedicalAlerts() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (priorityAlert != null) {
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
                            text = "${priorityAlert.urgencyBadge.uppercase()} · ${priorityAlert.timeAgo.uppercase()}",
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
                            resId = priorityAlert.imageRes,
                            contentDescription = priorityAlert.catName,
                            imageUrl = priorityAlert.fotoUrl,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = priorityAlert.catName, style = McTitle18)
                            Text(
                                text = priorityAlert.symptomTitle,
                                style = McBody14Bold.copy(color = McCoralDark)
                            )
                            Text(
                                text = priorityAlert.locationAndReporter,
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
                                scope.launch {
                                    val res = MiauCareRepository.aceptarAlertaMedica(priorityAlert.numericId)
                                    if (res is ApiResult.Success && priorityAlert.catId.isNotBlank()) {
                                        onOpenClinicalRecord(priorityAlert.catId)
                                    }
                                }
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
                                .clickable { delegatingAlert = priorityAlert }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "Delegar", style = McLabel12Bold)
                            }
                        }
                    }
                }
            }
        } else {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No hay alertas médicas críticas pendientes en este momento.",
                    style = McBody14Regular,
                    modifier = Modifier.padding(18.dp)
                )
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
                text = "Ver alertas",
                style = McLabel12Bold.copy(color = McTextSecondary),
                modifier = Modifier.clickable { onOpenMedicalAlerts() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (assignedCats.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aún no tienes pacientes asignados en el servidor.",
                    style = McBody14Regular,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            assignedCats.forEach { cat ->
                val isHosp = cat.estadoInternacion.equals("HOSPITALIZADO", ignoreCase = true)
                VetPatientRowCard(
                    name = cat.name,
                    subtitle = "${cat.code} · ${cat.colony}",
                    badgeText = if (isHosp) "Hospitalizado" else cat.statusTag,
                    badgeIsCoral = isHosp || cat.statusTag == "Enfermo" || cat.statusTag == "Crítico",
                    imageRes = cat.imageRes,
                    imageUrl = cat.fotoUrl,
                    onClick = { onOpenClinicalRecord(cat.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    if (delegatingAlert != null) {
        val alertToDelegate = delegatingAlert!!
        VetDelegateModal(
            catName = alertToDelegate.catName,
            onDismiss = { delegatingAlert = null },
            onConfirm = { docNumericId, _ ->
                delegatingAlert = null
                scope.launch {
                    MiauCareRepository.delegarAlertaMedica(alertToDelegate.numericId, docNumericId)
                }
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
    imageUrl: String? = null,
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
                imageUrl = imageUrl,
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
// 3. FICHA CLÍNICA + MODAL REGISTRAR ATENCIÓN (`POST /api/historial-clinico`, 403 & 409)
// =========================================================================
@Composable
fun VetClinicalRecordScreen(
    catId: String,
    onBack: () -> Unit,
    onOpenDirectory: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showRegistrarSheet by remember { mutableStateOf(false) }
    var simulateDeniedAccess by remember { mutableStateOf(false) }
    var conflict409Banner by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(catId) {
        MiauCareRepository.fetchGatoDetalleYHistorial(catId)
        MiauCareRepository.fetchCatalogosMaestros()
    }

    val cat = MiauCareRepository.getCatById(catId)
        ?: MiauCareRepository.cats.firstOrNull()
        ?: CatProfile(
            numericId = 1L,
            id = "cat-1",
            code = "MC-001",
            name = "Paciente",
            breed = "Mestizo",
            sex = "Macho",
            age = "2 años",
            weightKg = "3.8",
            sterilizationStatus = "Pendiente CES",
            isSterilized = false,
            colony = "Colonia Centro",
            zoneId = 1L,
            statusTag = "En calle",
            estadoInternacion = "EN_CALLE"
        )

    val currentUserId = MiauCareRepository.sessionUserId
    val isAssignedOrOpen = cat.veterinarioResponsableId == null ||
            cat.veterinarioResponsableId == 0L ||
            cat.veterinarioResponsableId == currentUserId ||
            MiauCareRepository.currentRole == MiauRole.ADMINISTRADOR
    val showDeniedView = simulateDeniedAccess || !isAssignedOrOpen

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
                    Text(text = "Ficha clínica", style = McTitle18)
                    Text(text = "Paciente ${cat.code}", style = McLabel11Regular)
                }
            }
            Surface(
                color = if (showDeniedView) McCoralSoft else McSoftNeutral,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.clickable { simulateDeniedAccess = !simulateDeniedAccess }
            ) {
                Text(
                    text = if (showDeniedView) "Ver ficha" else "Vista 403",
                    style = McLabel11Regular.copy(
                        color = if (showDeniedView) McCoralDark else McTextPrimary,
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
                    imageUrl = cat.fotoUrl,
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
                if (!showDeniedView) {
                    McStatusBadge(label = if (cat.isFallecido) "Fallecido" else cat.statusTag)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (showDeniedView) {
            // Pantalla/modal "Sin permisos sobre este gato" (403 Forbidden)
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
                        text = "Este paciente está asignado a ${cat.veterinarioResponsableNombre ?: "otro veterinario responsable"}. Solicita una delegación o consulta el directorio médico para intervenir en su historial clínico.",
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
                                text = "Veterinario responsable: ${cat.veterinarioResponsableNombre ?: "Asignado en otra zona"}",
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
            // Gato FALLECIDO (409 Conflict rule)
            if (cat.isFallecido) {
                Surface(
                    color = McCoralSoft,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, McCoralBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = McCoralDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Paciente FALLECIDO (Edición bloqueada - RN 409)",
                                style = McLabel12Bold.copy(color = McCoralDark)
                            )
                            Text(
                                text = "No es posible editar ni agregar nuevos registros clínicos a un gato marcado como FALLECIDO.",
                                style = McLabel11Regular.copy(color = McCoralDark)
                            )
                        }
                    }
                }
            }

            if (conflict409Banner != null) {
                Surface(
                    color = McCoralSoft,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, McCoralBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "409 Conflict: $conflict409Banner",
                        style = McLabel12Bold.copy(color = McCoralDark),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

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

            // Allergy / Hospitalization warning banner
            val isHosp = cat.estadoInternacion.equals("HOSPITALIZADO", ignoreCase = true)
            if (cat.condicionesEspeciales.isNotBlank() || isHosp) {
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
                            text = cat.condicionesEspeciales.ifBlank { "Paciente actualmente hospitalizado" },
                            style = McLabel12Bold.copy(color = McCoralDark)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Medical History section
            val records = MiauCareRepository.currentCatHistory
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Historial médico", style = McTitle18)
                Text(text = "${records.size} registros", style = McLabel11Regular)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (records.isEmpty()) {
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No hay registros clínicos previos para este paciente.",
                        style = McBody14Regular,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                records.forEach { rec ->
                    val isDiag = rec.tipo.uppercase().contains("DIAGNOSTICO")
                    Surface(
                        color = McCardWhite,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${rec.tipo.uppercase()} · ${rec.nivelGravedad.uppercase()}",
                                    style = McLabel11Regular.copy(
                                        color = if (isDiag) McCoralDark else McTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                                Text(text = rec.fecha, style = McLabel11Regular)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rec.diagnosticoNombre.ifBlank { rec.descripcion },
                                style = McTitle16
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rec.notas.ifBlank { rec.descripcion },
                                style = McBody14Regular
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${rec.veterinarioNombre} · Peso: ${rec.pesoRegistrado ?: cat.weightKg} kg" +
                                        (if (rec.dejarHospitalizado == true) " · Hospitalizado" else ""),
                                style = McLabel11Regular
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            McCoralButton(
                text = if (cat.isFallecido) "Paciente Fallecido (No editable)" else "+ Nuevo Diagnóstico / Procedimiento",
                enabled = !cat.isFallecido,
                onClick = { showRegistrarSheet = true }
            )
        }
    }

    if (showRegistrarSheet) {
        VetRegistrarAtencionDialog(
            cat = cat,
            onDismiss = { showRegistrarSheet = false },
            onSaved = { tipo, diagId, nivelGravedad, desc, notas, peso, hospitalizar ->
                scope.launch {
                    val res = MiauCareRepository.registrarHistorialClinico(
                        gatoNumericId = cat.numericId,
                        tipoBackend = tipo,
                        diagnosticoId = diagId,
                        nivelGravedadBackend = nivelGravedad,
                        descripcion = desc,
                        notas = notas,
                        pesoRegistrado = peso,
                        dejarHospitalizado = hospitalizar
                    )
                    showRegistrarSheet = false
                    if (res is ApiResult.Error && res.statusCode == 409) {
                        conflict409Banner = res.message
                    }
                }
            }
        )
    }
}

@Composable
private fun VetRegistrarAtencionDialog(
    cat: CatProfile,
    onDismiss: () -> Unit,
    onSaved: (
        tipo: String,
        diagId: Long,
        nivelGravedad: String,
        descripcion: String,
        notas: String,
        pesoRegistrado: Double,
        dejarHospitalizado: Boolean
    ) -> Unit
) {
    val diagCatalog = MiauCareRepository.diagnosticosCatalog
    var attentionType by remember { mutableStateOf("DIAGNOSTICO") }
    var selectedDiagId by remember { mutableLongStateOf(diagCatalog.firstOrNull()?.id ?: 1L) }
    var diagnosisText by remember {
        mutableStateOf(diagCatalog.firstOrNull()?.nombre ?: "Deshidratación moderada")
    }
    var severity by remember { mutableStateOf("MODERADA") }
    var weightInput by remember { mutableStateOf(cat.weightKg.ifBlank { "3.8" }) }
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
                        Text(text = "${cat.name} · ${cat.code}", style = McLabel11Regular)
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
                                    text = "Completa el diagnóstico, el peso y las notas clínicas antes de guardar.",
                                    style = McLabel11Regular.copy(color = McCoralDark)
                                )
                            }
                        }
                    }
                }

                Text(text = "Tipo de registro (Backend Enum)", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("DIAGNOSTICO" to "Diagnóstico", "PROCEDIMIENTO" to "Procedimiento").forEach { (code, label) ->
                        val selected = attentionType == code
                        Surface(
                            color = if (selected) McBlack else McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable { attentionType = code }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = McLabel12Bold.copy(
                                        color = if (selected) McCardWhite else McTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (diagCatalog.isNotEmpty()) {
                    Text(text = "Catálogo de Diagnósticos", style = McLabel12Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        diagCatalog.forEach { d ->
                            val selected = selectedDiagId == d.id
                            McFilterPill(
                                label = d.nombre,
                                selected = selected,
                                onClick = {
                                    selectedDiagId = d.id
                                    diagnosisText = d.nombre
                                    showValidationError = false
                                },
                                unselectedBg = McSoftNeutral
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                McInputField(
                    label = "Descripción clínica",
                    value = diagnosisText,
                    onValueChange = {
                        diagnosisText = it
                        showValidationError = false
                    },
                    placeholder = "Descripción del diagnóstico o procedimiento"
                )

                Spacer(modifier = Modifier.height(10.dp))

                McInputField(
                    label = "Peso registrado (kg)",
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    placeholder = "Ej. 3.8"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Nivel de gravedad", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("LEVE" to "Leve", "MODERADA" to "Moderada", "CRITICA" to "Crítica").forEach { (code, label) ->
                        val selected = severity == code
                        Surface(
                            color = if (selected) McCoralSoft else McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable { severity = code }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
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
                    text = if (MiauCareRepository.isLoading) "Guardando..." else "Guardar atención",
                    enabled = !MiauCareRepository.isLoading,
                    onClick = {
                        if (diagnosisText.isBlank() || notes.isBlank()) {
                            showValidationError = true
                        } else {
                            onSaved(
                                attentionType,
                                selectedDiagId,
                                severity,
                                diagnosisText.trim(),
                                notes.trim(),
                                weightInput.toDoubleOrNull() ?: 3.8,
                                resolution == "Dejar hospitalizado"
                            )
                        }
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
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("Nuevas") }
    var closingAlert by remember { mutableStateOf<MedicalAlertItem?>(null) }
    var delegatingAlert by remember { mutableStateOf<MedicalAlertItem?>(null) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchAlertasMedicasVet()
        MiauCareRepository.fetchVeterinariosDirectorio()
    }

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                McBackCircleButton(onClick = onBack)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Alertas médicas", style = McTitle18)
                    Text(text = "Triaje y seguimiento", style = McLabel11Regular)
                }
            }
            IconButton(onClick = { scope.launch { MiauCareRepository.fetchAlertasMedicasVet() } }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Actualizar", tint = McTextPrimary)
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

        if (filteredAlerts.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No hay alertas en la pestaña \"$selectedTab\".",
                    style = McBody14Regular,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

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
                            imageUrl = alert.fotoUrl,
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
                                scope.launch {
                                    val res = MiauCareRepository.pasarAlertaEnAtencion(alert.numericId)
                                    if (res is ApiResult.Success) selectedTab = "En atención"
                                }
                            }
                        )
                    } else if (selectedTab == "En atención") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { closingAlert = alert },
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
                                    .clickable { delegatingAlert = alert }
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

    if (closingAlert != null) {
        val alertToClose = closingAlert!!
        VetCloseAlertModal(
            catName = alertToClose.catName,
            onDismiss = { closingAlert = null },
            onConfirm = { resolucion, observacion ->
                closingAlert = null
                scope.launch {
                    val res = MiauCareRepository.cerrarAlertaMedica(
                        alertaNumericId = alertToClose.numericId,
                        resolucion = resolucion,
                        observacion = observacion
                    )
                    if (res is ApiResult.Success) selectedTab = "Cerradas"
                }
            }
        )
    }

    if (delegatingAlert != null) {
        val alertToDelegate = delegatingAlert!!
        VetDelegateModal(
            catName = alertToDelegate.catName,
            onDismiss = { delegatingAlert = null },
            onConfirm = { docNumericId, _ ->
                delegatingAlert = null
                scope.launch {
                    MiauCareRepository.delegarAlertaMedica(alertToDelegate.numericId, docNumericId)
                }
            }
        )
    }
}

@Composable
fun VetCloseAlertModal(
    catName: String,
    onDismiss: () -> Unit,
    onConfirm: (resolucion: String, observacion: String) -> Unit
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
                McCoralButton(
                    text = "Cerrar alerta",
                    onClick = { onConfirm(finalState, summary) }
                )
            }
        }
    }
}

@Composable
fun VetDelegateModal(
    catName: String,
    onDismiss: () -> Unit,
    onConfirm: (Long, String) -> Unit
) {
    val availableDoctors = MiauCareRepository.doctors.filter {
        it.numericId != MiauCareRepository.sessionUserId
    }.ifEmpty { MiauCareRepository.doctors }

    var selectedDocId by remember { mutableLongStateOf(availableDoctors.firstOrNull()?.numericId ?: 1L) }
    var selectedDocName by remember { mutableStateOf(availableDoctors.firstOrNull()?.name ?: "Colega Veterinario") }

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
                availableDoctors.forEach { doc ->
                    val isSelected = selectedDocId == doc.numericId
                    Surface(
                        color = if (isSelected) McCoralSoft else McBackground,
                        shape = RoundedCornerShape(18.dp),
                        border = if (isSelected) BorderStroke(1.dp, McCoralBorder) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clickable {
                                selectedDocId = doc.numericId
                                selectedDocName = doc.name
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            McCatImage(
                                resId = doc.imageRes,
                                contentDescription = doc.name,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = doc.name, style = McBody14Bold)
                                Text(text = "${doc.specialty} · ${doc.distanceText}", style = McLabel11Regular)
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
                    onClick = { onConfirm(selectedDocId, selectedDocName) }
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

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchCatalogosMaestros()
        MiauCareRepository.fetchVeterinariosDirectorio()
        MiauCareRepository.fetchGatos()
    }

    val doctors = MiauCareRepository.doctors.filter { doc ->
        val matchesSpec = selectedSpecialty == "Todas" || doc.specialty.contains(selectedSpecialty, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                doc.name.contains(searchQuery, ignoreCase = true) ||
                doc.specialty.contains(searchQuery, ignoreCase = true)
        matchesSpec && matchesSearch
    }

    val filteredCats = MiauCareRepository.cats.filter { cat ->
        searchQuery.isBlank() ||
                cat.name.contains(searchQuery, ignoreCase = true) ||
                cat.code.contains(searchQuery, ignoreCase = true)
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
            val specialtyOptions = buildList {
                add("Todas")
                MiauCareRepository.especialidadesCatalog.forEach { add(it.nombre) }
                if (size == 1) {
                    add("Cirugía")
                    add("Medicina interna")
                }
            }.distinct()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                specialtyOptions.forEach { spec ->
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
                Text(text = "${doctors.size} registrados", style = McLabel11Regular)
            }

            Spacer(modifier = Modifier.height(12.dp))

            doctors.forEach { doc ->
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
                            Text(text = "${doc.specialty} · Céd. ${doc.numeroCedula}", style = McLabel11Regular)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (doc.disponibleEmergencias) Color(0xFF28A745) else McTextSecondary)
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
                                MiauCareRepository.showToast("Contacto: ${doc.correo.ifBlank { doc.name }}")
                            }
                        ) {
                            Text(
                                text = "Contactar",
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
// 6. AGENDA CLÍNICA (`Agenda clínica` - `GET / POST / PATCH /api/cirugias-ces`)
// =========================================================================
@Composable
fun VetAgendaScreen(
    onOpenClinicalRecord: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedDay by remember { mutableIntStateOf(15) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var completingSurgery by remember { mutableStateOf<CirugiaCesDto?>(null) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchCirugiasCesMias()
        MiauCareRepository.fetchGatos()
    }

    val days = listOf(
        "L" to 11,
        "M" to 12,
        "M" to 13,
        "J" to 14,
        "V" to 15,
        "S" to 16,
        "D" to 17
    )

    val surgeries = MiauCareRepository.cirugiasCes

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
                Text(text = "Agenda clínica", style = McTitle24)
                Text(text = "Citas y cirugías CES", style = McBody14Regular)
            }
            Button(
                onClick = { showScheduleDialog = true },
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = McCoral)
            ) {
                Text(text = "+ Programar CES", style = McLabel12Bold.copy(color = McCardWhite))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Card
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

        Text(text = "Cirugías CES registradas", style = McTitle16)
        Text(text = "${surgeries.size} cirugías en tu agenda", style = McLabel11Regular)

        Spacer(modifier = Modifier.height(14.dp))

        if (surgeries.isEmpty()) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No tienes cirugías CES programadas aún. Usa el botón '+ Programar CES' para agendar una.",
                    style = McBody14Regular,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            surgeries.forEach { ces ->
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (ces.gatoId > 0L) onOpenClinicalRecord("cat-${ces.gatoId}")
                                    }
                            ) {
                                Text(
                                    text = "${ces.estado} · ${ces.fecha} (${ces.horaInicio} - ${ces.horaFin})",
                                    style = McLabel11Regular.copy(
                                        color = McCoralDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "CES · ${ces.gatoNombre}", style = McTitle16)
                                Text(
                                    text = "${ces.gatoCodigo} · ${ces.coloniaNombre} · ${ces.quirofano}",
                                    style = McLabel11Regular
                                )
                            }

                            if (ces.estado == "PROGRAMADA") {
                                Button(
                                    onClick = {
                                        scope.launch { MiauCareRepository.iniciarCirugiaCes(ces.id) }
                                    },
                                    shape = RoundedCornerShape(999.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = McBlack)
                                ) {
                                    Text("Iniciar", style = McLabel11Regular.copy(color = McCardWhite))
                                }
                            } else if (ces.estado == "EN_CURSO") {
                                Button(
                                    onClick = { completingSurgery = ces },
                                    shape = RoundedCornerShape(999.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = McCoral)
                                ) {
                                    Text("Completar", style = McLabel11Regular.copy(color = McCardWhite))
                                }
                            }
                        }
                    }
                }
            }
        }

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

    if (showScheduleDialog) {
        val candidateCats = MiauCareRepository.cats.filter {
            !it.isSterilized && !it.isFallecido
        }.ifEmpty { MiauCareRepository.cats }
        var selectedCatId by remember { mutableLongStateOf(candidateCats.firstOrNull()?.numericId ?: 1L) }
        var fechaInput by remember { mutableStateOf("2026-11-20") }
        var horaIniInput by remember { mutableStateOf("10:00") }
        var horaFinInput by remember { mutableStateOf("11:00") }
        var quirofanoInput by remember { mutableStateOf("Quirófano 1") }

        Dialog(onDismissRequest = { showScheduleDialog = false }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Programar Cirugía CES", style = McTitle18)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Selecciona paciente:", style = McLabel12Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        candidateCats.forEach { c ->
                            McFilterPill(
                                label = "${c.name} (${c.code})",
                                selected = selectedCatId == c.numericId,
                                onClick = { selectedCatId = c.numericId },
                                unselectedBg = McSoftNeutral
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    McInputField(
                        label = "Fecha (YYYY-MM-DD)",
                        value = fechaInput,
                        onValueChange = { fechaInput = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        McInputField(
                            label = "Hora inicio",
                            value = horaIniInput,
                            onValueChange = { horaIniInput = it },
                            modifier = Modifier.weight(1f)
                        )
                        McInputField(
                            label = "Hora fin",
                            value = horaFinInput,
                            onValueChange = { horaFinInput = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    McInputField(
                        label = "Quirófano",
                        value = quirofanoInput,
                        onValueChange = { quirofanoInput = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    McCoralButton(
                        text = "Agendar CES",
                        onClick = {
                            scope.launch {
                                val res = MiauCareRepository.programarCirugiaCes(
                                    gatoNumericId = selectedCatId,
                                    fechaIso = fechaInput.trim(),
                                    horaInicioIso = horaIniInput.trim(),
                                    horaFinIso = horaFinInput.trim(),
                                    quirofano = quirofanoInput.trim()
                                )
                                if (res is ApiResult.Success) showScheduleDialog = false
                            }
                        }
                    )
                }
            }
        }
    }

    if (completingSurgery != null) {
        val surgeryToComplete = completingSurgery!!
        var obs by remember { mutableStateOf("Cirugía CES exitosa, recuperación favorable.") }
        var darDeAlta by remember { mutableStateOf(true) }

        Dialog(onDismissRequest = { completingSurgery = null }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Completar CES · ${surgeryToComplete.gatoNombre}", style = McTitle18)
                    Spacer(modifier = Modifier.height(12.dp))
                    McInputField(
                        label = "Resultado / Observaciones postoperatorias",
                        value = obs,
                        onValueChange = { obs = it },
                        singleLine = false,
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { darDeAlta = !darDeAlta }
                    ) {
                        Checkbox(checked = darDeAlta, onCheckedChange = { darDeAlta = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Dar de alta al paciente", style = McBody14Regular)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    McCoralButton(
                        text = "Finalizar y marcar Esterilizado",
                        onClick = {
                            completingSurgery = null
                            scope.launch {
                                MiauCareRepository.completarCirugiaCes(
                                    cirugiaId = surgeryToComplete.id,
                                    resultado = obs,
                                    darDeAlta = darDeAlta
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 7. PERFIL Y DISPONIBILIDAD (`PATCH /api/veterinarios/{id}/disponibilidad`)
// =========================================================================
@Composable
fun VetProfileScreen(
    onLogout: () -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchVetDashboardData()
    }

    val vetName = MiauCareRepository.sessionUserName.ifBlank { "Dra. Sofía García" }
    val vetCedula = MiauCareRepository.vetCedula.ifBlank { "Verificada" }
    val vetSpec = MiauCareRepository.vetSpecialtyName.ifBlank { "Cirugía CES" }
    val emergencyAvailable = MiauCareRepository.vetDisponibleEmergencias

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
                        contentDescription = vetName,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = vetName, style = McTitle18)
                        Text(
                            text = MiauCareRepository.sessionUserEmail.ifBlank { "Médico veterinario" },
                            style = McLabel11Regular
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = StatusSanoBg,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = "✓ Cédula $vetCedula · Verificada",
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
                    listOf(vetSpec, "Medicina felina").distinct().forEach { sp ->
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
                    onCheckedChange = { newVal ->
                        scope.launch {
                            MiauCareRepository.cambiarDisponibilidadEmergencias(newVal)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = McCardWhite,
                        checkedTrackColor = McCoral
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Info note card
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
                        text = "Con alertas críticas activas en tu zona, el backend valida (RN-02) que al menos un veterinario permanezca disponible.",
                        style = McLabel11Regular
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        McCoralButton(
            text = "Cerrar sesión",
            onClick = {
                MiauCareRepository.logout(context)
                onLogout()
            }
        )

        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Cambiar vista de rol:", style = McLabel11Regular)
        Spacer(modifier = Modifier.height(6.dp))
        McRolePrototypeSwitcher(onRoleSelected = onRoleSwitch)
    }
}
