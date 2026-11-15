package com.example.miaucare.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.R
import com.example.miaucare.data.ApiResult
import com.example.miaucare.data.DoctorItem
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.components.*
import com.example.miaucare.ui.theme.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale

// =========================================================================
// 1. ADMINISTRADOR · INICIO + MODAL HERRAMIENTAS (`Inicio`)
// =========================================================================
@Composable
fun AdminHomeScreen(
    onNavigate: (String) -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showToolsModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchAdminDashboardData()
    }

    val pendingCatsCount = MiauCareRepository.pendingApprovals.count { !it.isReviewed }
    val pendingVetsCount = MiauCareRepository.pendingVetApprovals.size
    val totalPending = pendingCatsCount + pendingVetsCount
    val solicitudesInbox = MiauCareRepository.solicitudesGatoPendientes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 110.dp)
    ) {
        McRolePrototypeSwitcher(
            onRoleSelected = onRoleSwitch,
            onOpenAdminTools = { showToolsModal = true }
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "MiauCare · Administración", style = McLabel11Regular)
                Text(
                    text = "Hola, ${MiauCareRepository.sessionUserName.ifBlank { "Admin" }}",
                    style = McTitle24
                )
                Text(
                    text = MiauCareRepository.sessionUserEmail.ifBlank { "adminmiaucare@administrador.com" },
                    style = McLabel11Regular
                )
            }
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.clickable { onNavigate("admin_profile") }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = "Mi perfil",
                        tint = McTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Mi perfil", style = McLabel11Regular.copy(fontSize = 10.sp, color = McTextPrimary))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3 KPI Boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminKpiBox(
                number = "${MiauCareRepository.cats.size}",
                label = "Gatos censados",
                isCoral = false,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate("admin_census") }
            )
            AdminKpiBox(
                number = "${MiauCareRepository.teamUsers.size}",
                label = "Usuarios",
                isCoral = false,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate("admin_users") }
            )
            AdminKpiBox(
                number = "$totalPending",
                label = "Por aprobar",
                isCoral = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate("admin_approve_records") }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: "Gestiona MiauCare" | "+ Más"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Gestiona MiauCare", style = McTitle18)
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.clickable { showToolsModal = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = McTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Más", style = McLabel12Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Action Grid (`Censo felino`, `Usuarios`, `Aprobar registros`, `Crear aviso`)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminQuickActionCard(
                icon = Icons.AutoMirrored.Outlined.Assignment,
                title = "Censo felino",
                subtitle = "${MiauCareRepository.cats.size} fichas",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("admin_census") }
            )
            AdminQuickActionCard(
                icon = Icons.Outlined.Group,
                title = "Usuarios",
                subtitle = "${MiauCareRepository.teamUsers.size} cuentas",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("admin_users") }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminQuickActionCard(
                icon = Icons.Outlined.CheckCircle,
                title = "Aprobar registros",
                subtitle = "$totalPending nuevos",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("admin_approve_records") }
            )
            AdminQuickActionCard(
                icon = Icons.Outlined.NotificationsActive,
                title = "Crear aviso",
                subtitle = "A todo el equipo",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("admin_broadcast") }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: "Bandeja de entrada" (`GET /api/solicitudes-gato/pendientes`)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bandeja de entrada", style = McTitle18)
            Text(
                text = "${solicitudesInbox.size} solicitudes críticas",
                style = McLabel12Bold.copy(color = McCoralDark)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        solicitudesInbox.forEach { sol ->
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Surface(
                            color = McCoralSoft,
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null,
                                    tint = McCoralDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Solicitud: ${sol.tipo}",
                                style = McLabel11Regular.copy(
                                    color = McCoralDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(text = "${sol.gatoNombre} · ${sol.gatoCodigo}", style = McTitle16)
                            Text(
                                text = "${sol.zonaNombre} · Solicitó: ${sol.voluntarioNombre}",
                                style = McLabel11Regular
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    MiauCareRepository.resolverSolicitudGato(sol.id, aceptar = true)
                                }
                            },
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = McCoral),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Text(text = "Aceptar", style = McLabel12Bold.copy(color = McCardWhite))
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clickable {
                                    scope.launch {
                                        MiauCareRepository.resolverSolicitudGato(sol.id, aceptar = false)
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "Rechazar", style = McLabel12Bold)
                            }
                        }
                    }
                }
            }
        }

        // Reasignación masiva shortcut
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate("admin_medical_directory") }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    color = McSoftNeutral,
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Sync,
                            contentDescription = null,
                            tint = McTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Directorio y reasignación masiva",
                        style = McLabel11Regular.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(text = "${MiauCareRepository.doctors.size} veterinarios activos", style = McTitle16)
                    Text(
                        text = "Toca para gestionar carga de pacientes o reasignar masivamente.",
                        style = McLabel11Regular
                    )
                }
            }
        }
    }

    if (showToolsModal) {
        AdminHerramientasModal(
            onDismiss = { showToolsModal = false },
            onSelectRoute = { route ->
                showToolsModal = false
                onNavigate(route)
            }
        )
    }
}

@Composable
private fun AdminKpiBox(
    number: String,
    label: String,
    isCoral: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isCoral) McCoralSoft else McCardWhite,
        shape = RoundedCornerShape(20.dp),
        border = if (isCoral) BorderStroke(1.dp, McCoralBorder) else null,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = number,
                style = McTitle24.copy(
                    color = if (isCoral) McCoralDark else McTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = McLabel11Regular.copy(
                    color = if (isCoral) McCoralDark else McTextSecondary
                )
            )
        }
    }
}

@Composable
private fun AdminQuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = McCardWhite,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = McSoftNeutral,
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = McTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = McTitle16)
            Text(text = subtitle, style = McLabel11Regular)
        }
    }
}

@Composable
fun AdminHerramientasModal(
    onDismiss: () -> Unit,
    onSelectRoute: (String) -> Unit
) {
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
                        Text(text = "Herramientas", style = McTitle18)
                        Text(text = "Más opciones de administración", style = McLabel11Regular)
                    }
                    Surface(
                        color = McSoftNeutral,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Pantallas de gestión", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(10.dp))

                val tools = listOf(
                    Triple("Auditoría", Icons.Outlined.History, "admin_audit"),
                    Triple("Indicadores por zona", Icons.Outlined.BarChart, "admin_zone_indicators"),
                    Triple("Colonias y zonas", Icons.Outlined.LocationOn, "admin_colonies"),
                    Triple("Zonas y catálogos", Icons.Outlined.FolderOpen, "admin_catalogs"),
                    Triple("Directorio médico", Icons.Outlined.MedicalServices, "admin_medical_directory"),
                    Triple("Programar ronda", Icons.Outlined.CalendarToday, "admin_schedule_round"),
                    Triple("Crear cuenta", Icons.Outlined.PersonAdd, "register_voluntario"),
                    Triple("Parámetros del sistema", Icons.Outlined.Tune, "admin_system_params")
                )

                tools.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { (label, icon, route) ->
                            Surface(
                                color = McBackground,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectRoute(route) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = McTextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = label, style = McLabel12Bold)
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
// 2. ADMINISTRADOR · CENSO FELINO (`Censo felino`)
// =========================================================================
@Composable
fun AdminCensusScreen(
    onOpenEditCat: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf("Todas las zonas") }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchGatos()
        MiauCareRepository.fetchZonasResumen()
    }

    val filteredCats = MiauCareRepository.cats.filter { cat ->
        val matchesQuery = searchQuery.isBlank() ||
                cat.name.contains(searchQuery, ignoreCase = true) ||
                cat.code.contains(searchQuery, ignoreCase = true)
        val matchesZone = selectedZone == "Todas las zonas" ||
                cat.colony.contains(selectedZone, ignoreCase = true)
        matchesQuery && matchesZone
    }

    val zoneOptions = buildList {
        add("Todas las zonas")
        MiauCareRepository.colonies.forEach { add(it.name) }
    }.distinct()

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
                Text(text = "Censo felino", style = McTitle24)
                Text(text = "${MiauCareRepository.cats.size} gatos registrados", style = McBody14Regular)
            }
            Surface(
                color = McCoral,
                shape = CircleShape,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onOpenEditCat("new") }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo",
                        tint = McCardWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre o código..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            zoneOptions.forEach { zn ->
                McFilterPill(
                    label = zn,
                    selected = selectedZone == zn,
                    onClick = { selectedZone = zn },
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
                        onClick = { onOpenEditCat(cat.id) }
                    )
                }
                if (rowCats.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// =========================================================================
// 3. ADMINISTRADOR · EDITAR / CREAR FICHA (`POST / PUT / PATCH /api/gatos`, Regla FALLECIDO 409)
// =========================================================================
@Composable
fun AdminEditCatScreen(
    catId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isNewCat = catId == "new" || catId.isBlank()
    val cat = remember(catId, MiauCareRepository.cats.size) {
        if (isNewCat) null else MiauCareRepository.getCatById(catId)
    }

    var name by remember(cat) { mutableStateOf(cat?.name ?: "") }
    var code by remember(cat) { mutableStateOf(cat?.code ?: "MC-0${MiauCareRepository.cats.size + 11}") }
    var sex by remember(cat) { mutableStateOf(cat?.sex ?: "Macho") }
    var age by remember(cat) { mutableStateOf(cat?.age ?: "2 años") }
    var breed by remember(cat) { mutableStateOf(cat?.breed ?: "Mestizo") }
    var weight by remember(cat) { mutableStateOf(cat?.weightKg ?: "3.8") }
    var selectedZoneId by remember(cat) { mutableLongStateOf(cat?.zoneId ?: 1L) }
    var zoneName by remember(cat) { mutableStateOf(cat?.colony ?: "Colonia Centro") }
    var lifeStatus by remember(cat) { mutableStateOf(if (cat?.isFallecido == true) "Fallecido" else "Activo") }
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var conflict409Message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(catId) {
        MiauCareRepository.fetchZonasResumen()
        if (!isNewCat && cat != null) {
            MiauCareRepository.fetchGatoDetalleYHistorial(cat.id)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    photoBytes = bytes
                    MiauCareRepository.showToast("Foto cargada lista para subir a Supabase (gatos)")
                }
            } catch (_: Exception) {
                MiauCareRepository.showToast("No se pudo leer la imagen", isError = true)
            }
        }
    }

    val isCurrentlyFallecido = cat?.isFallecido == true

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
                Text(text = if (isNewCat) "Nuevo gato en censo" else "Editar ficha", style = McTitle18)
                Text(
                    text = if (isNewCat) "Registro directo aprobado" else "${cat?.name ?: ""} · ${cat?.code ?: ""}",
                    style = McLabel11Regular
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Regla Gato FALLECIDO (409 Conflict)
        if (isCurrentlyFallecido) {
            Surface(
                color = McCoralSoft,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, McCoralBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        tint = McCoralDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Gato FALLECIDO · Edición deshabilitada (RN 409)",
                            style = McLabel12Bold.copy(color = McCoralDark)
                        )
                        Text(
                            text = "Una vez marcado como FALLECIDO, el backend bloquea cualquier edición (PUT), nuevo historial o retorno a ACTIVO con 409 Conflict.",
                            style = McLabel11Regular.copy(color = McCoralDark)
                        )
                    }
                }
            }
        }

        if (conflict409Message != null) {
            Surface(
                color = McCoralSoft,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, McCoralBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Text(
                    text = "409 Conflict: $conflict409Message",
                    style = McLabel12Bold.copy(color = McCoralDark),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Photo + Cambiar foto (Supabase Storage carpeta="gatos")
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
                    resId = cat?.imageRes ?: R.drawable.img_cat_manchas,
                    contentDescription = name,
                    imageUrl = cat?.fotoUrl,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = name.ifBlank { "Nuevo paciente" }, style = McTitle18)
                    Text(
                        text = if (photoBytes != null) "Nueva foto lista para Supabase" else "$breed · ${cat?.sterilizationStatus ?: "Pendiente CES"}",
                        style = McLabel11Regular
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (!isCurrentlyFallecido) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                            ) {
                                Text(
                                    text = "Subir de galería",
                                    style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                color = McCoralSoft,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier.clickable {
                                    val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.img_cat_manchas)
                                    val bos = ByteArrayOutputStream()
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 85, bos)
                                    photoBytes = bos.toByteArray()
                                    MiauCareRepository.showToast("Foto de prueba cargada para Supabase")
                                }
                            ) {
                                Text(
                                    text = "Usar foto demo",
                                    style = McLabel11Regular.copy(color = McCoralDark, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Datos maestros
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Datos maestros", style = McTitle16)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    McInputField(
                        label = "Nombre",
                        value = name,
                        onValueChange = { if (!isCurrentlyFallecido) name = it },
                        modifier = Modifier.weight(1f)
                    )
                    McInputField(
                        label = "Código",
                        value = code,
                        onValueChange = { if (!isCurrentlyFallecido) code = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    McInputField(
                        label = "Edad estimada",
                        value = age,
                        onValueChange = { if (!isCurrentlyFallecido) age = it },
                        modifier = Modifier.weight(1f)
                    )
                    McInputField(
                        label = "Peso (kg)",
                        value = weight,
                        onValueChange = { if (!isCurrentlyFallecido) weight = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Sexo", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Macho", "Hembra").forEach { sx ->
                        val selected = sex.equals(sx, ignoreCase = true)
                        McFilterPill(
                            label = sx,
                            selected = selected,
                            onClick = { if (!isCurrentlyFallecido) sex = sx },
                            unselectedBg = McSoftNeutral
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Zona / Colonia", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiauCareRepository.colonies.forEach { zn ->
                        McFilterPill(
                            label = zn.name,
                            selected = selectedZoneId == zn.numericId || zoneName.equals(zn.name, ignoreCase = true),
                            onClick = {
                                if (!isCurrentlyFallecido) {
                                    selectedZoneId = zn.numericId
                                    zoneName = zn.name
                                }
                            },
                            unselectedBg = McSoftNeutral
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Estado de vida (`PATCH /api/gatos/{id}/estado-vida`)
        if (!isNewCat && cat != null) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Estado de vida (PATCH /estado-vida)", style = McTitle16)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Activo" to "ACTIVO", "Fallecido" to "FALLECIDO").forEach { (label, codeVal) ->
                            val selected = lifeStatus == label
                            Surface(
                                color = if (selected) McBlack else McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clickable {
                                        scope.launch {
                                            val res = MiauCareRepository.cambiarEstadoVidaGato(cat.numericId, codeVal)
                                            when (res) {
                                                is ApiResult.Success -> {
                                                    lifeStatus = label
                                                    conflict409Message = null
                                                }
                                                is ApiResult.Error -> {
                                                    conflict409Message = res.message
                                                }
                                            }
                                        }
                                    }
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
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Historial médico real
            val records = MiauCareRepository.currentCatHistory
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Historial médico", style = McTitle16)
                        Text(text = "${records.size} entradas", style = McLabel11Regular)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (records.isEmpty()) {
                        Text(
                            text = "Sin entradas clínicas registradas.",
                            style = McLabel11Regular
                        )
                    } else {
                        records.forEach { rec ->
                            Surface(
                                color = McBackground,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${rec.fecha} · ${rec.diagnosticoNombre.ifBlank { rec.descripcion }}",
                                        style = McBody14Bold
                                    )
                                    Text(
                                        text = "${rec.veterinarioNombre} · ${rec.notas.ifBlank { rec.descripcion }}",
                                        style = McLabel11Regular
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        McCoralButton(
            text = when {
                isCurrentlyFallecido -> "Edición bloqueada (Gato Fallecido)"
                MiauCareRepository.isLoading -> "Guardando..."
                else -> "Guardar cambios"
            },
            enabled = !isCurrentlyFallecido && !MiauCareRepository.isLoading,
            onClick = {
                scope.launch {
                    val res = MiauCareRepository.crearOActualizarGato(
                        existingNumericId = if (isNewCat) null else cat?.numericId,
                        codigo = code.ifBlank { "MC-099" },
                        nombre = name.ifBlank { "Michi" },
                        sexoUi = sex,
                        edadEstimada = age.ifBlank { "2 años" },
                        raza = breed.ifBlank { "Mestizo" },
                        peso = weight.toDoubleOrNull() ?: 3.8,
                        condicionesEspeciales = cat?.condicionesEspeciales ?: "",
                        zonaId = selectedZoneId,
                        estadoInternacionBackend = cat?.estadoInternacion ?: "EN_CALLE",
                        esterilizado = cat?.isSterilized ?: false,
                        currentFotoUrl = cat?.fotoUrl,
                        newPhotoBytes = photoBytes
                    )
                    when (res) {
                        is ApiResult.Success -> onBack()
                        is ApiResult.Error -> conflict409Message = res.message
                    }
                }
            }
        )

        if (!isNewCat && cat != null) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val res = MiauCareRepository.eliminarGato(cat.numericId)
                        if (res is ApiResult.Success) onBack()
                    }
                },
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, McCoralBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = McCoralDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(text = "Eliminar del sistema", style = McLabel12Bold.copy(color = McCoralDark))
            }
        }
    }
}

// =========================================================================
// 4. ADMINISTRADOR · APROBAR REGISTROS (`GET /api/gatos/pendientes` & `GET /api/veterinarios/pendientes`)
// =========================================================================
@Composable
fun AdminApproveRecordsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedSegment by remember { mutableStateOf("Gatos") }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchRegistrosPendientesAprobacion()
    }

    val pendingCats = MiauCareRepository.pendingApprovals.filter { !it.isReviewed }
    val pendingVets = MiauCareRepository.pendingVetApprovals

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
                    Text(text = "Aprobar registros", style = McTitle18)
                    Text(text = "Gatos reportados y solicitudes veterinarias", style = McLabel11Regular)
                }
            }
            IconButton(onClick = { scope.launch { MiauCareRepository.fetchRegistrosPendientesAprobacion() } }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Actualizar", tint = McTextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            McFilterPill(
                label = "Gatos pendientes · ${pendingCats.size}",
                selected = selectedSegment == "Gatos",
                onClick = { selectedSegment = "Gatos" },
                unselectedBg = McSoftNeutral
            )
            McFilterPill(
                label = "Veterinarios · ${pendingVets.size}",
                selected = selectedSegment == "Veterinarios",
                onClick = { selectedSegment = "Veterinarios" },
                unselectedBg = McSoftNeutral
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSegment == "Gatos") {
            if (pendingCats.isEmpty()) {
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No hay gatos pendientes de aprobación en este momento.",
                        style = McBody14Regular,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
            pendingCats.forEach { item ->
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
                                resId = item.imageRes,
                                contentDescription = item.nameAndCode,
                                imageUrl = item.fotoUrl,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = item.nameAndCode, style = McTitle16)
                                    Surface(
                                        color = McSoftNeutral,
                                        shape = RoundedCornerShape(999.dp)
                                    ) {
                                        Text(
                                            text = item.zone,
                                            style = McLabel11Regular.copy(
                                                color = McTextPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(text = item.breedAgeSex, style = McLabel11Regular)
                                Text(text = item.submittedByAndTime, style = McLabel11Regular)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        MiauCareRepository.aprobarORechazarGato(item.numericId, aprobar = true)
                                    }
                                },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = McCoral),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Text(text = "Aprobar", style = McLabel12Bold.copy(color = McCardWhite))
                            }
                            Surface(
                                color = McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clickable {
                                        scope.launch {
                                            MiauCareRepository.aprobarORechazarGato(item.numericId, aprobar = false)
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "Rechazar", style = McLabel12Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (pendingVets.isEmpty()) {
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No hay solicitudes de veterinarios pendientes.",
                        style = McBody14Regular,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
            pendingVets.forEach { vet ->
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
                                resId = R.drawable.img_doc_sofia,
                                contentDescription = vet.name,
                                imageUrl = vet.fotoCedulaUrl,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = vet.name, style = McTitle16)
                                Text(text = "${vet.specialty} · Cédula: ${vet.numeroCedula}", style = McLabel11Regular)
                                Text(text = vet.correo, style = McLabel11Regular)
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        MiauCareRepository.aprobarORechazarVeterinario(vet.numericId, aprobar = true)
                                    }
                                },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = McCoral),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Text(text = "Aprobar", style = McLabel12Bold.copy(color = McCardWhite))
                            }
                            Surface(
                                color = McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clickable {
                                        scope.launch {
                                            MiauCareRepository.aprobarORechazarVeterinario(vet.numericId, aprobar = false)
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "Rechazar", style = McLabel12Bold)
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
// 5. ADMINISTRADOR · GESTIÓN DE USUARIOS (`GET /api/veterinarios` & `GET /api/voluntarios`)
// =========================================================================
@Composable
fun AdminUsersScreen() {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchUsuariosEquipo()
    }

    val filteredUsers = MiauCareRepository.teamUsers.filter { u ->
        val matchesQuery = searchQuery.isBlank() ||
                u.name.contains(searchQuery, ignoreCase = true) ||
                u.roleLabel.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedFilter) {
            "Veterinarios" -> u.isVeterinario
            "Voluntarios" -> !u.isVeterinario
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
        Text(text = "Gestión de usuarios", style = McTitle24)
        Text(text = "${MiauCareRepository.teamUsers.size} miembros del equipo", style = McBody14Regular)

        Spacer(modifier = Modifier.height(14.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por nombre o rol..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Veterinarios", "Voluntarios").forEach { tab ->
                McFilterPill(
                    label = tab,
                    selected = selectedFilter == tab,
                    onClick = { selectedFilter = tab },
                    unselectedBg = McSoftNeutral
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredUsers.forEach { user ->
            var activeState by remember(user.id, user.isActive) { mutableStateOf(user.isActive) }
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
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
                                imageVector = Icons.Outlined.PersonOutline,
                                contentDescription = null,
                                tint = McTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = user.name, style = McTitle16)
                        if (user.correo.isNotBlank()) {
                            Text(text = user.correo, style = McLabel11Regular)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = McSoftNeutral,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = user.roleLabel,
                                    style = McLabel11Regular.copy(color = McTextPrimary, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                color = if (activeState) StatusSanoBg else McSoftNeutral,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = if (activeState) "Activo" else "Inactivo",
                                    style = McLabel11Regular.copy(
                                        color = if (activeState) StatusSanoText else McTextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    Switch(
                        checked = activeState,
                        onCheckedChange = { newVal ->
                            activeState = newVal
                            scope.launch {
                                MiauCareRepository.cambiarEstadoUsuario(user, newVal)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = McCardWhite,
                            checkedTrackColor = McCoral
                        )
                    )
                }
            }
        }
    }
}

// =========================================================================
// 6. ADMINISTRADOR · NOTIFICACIÓN GLOBAL (`POST /api/notificaciones`)
// =========================================================================
@Composable
fun AdminBroadcastScreen() {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("Jornada de revisión felina") }
    var message by remember {
        mutableStateOf("Este sábado a las 9:00 realizaremos la ronda general en Colonia Norte.")
    }
    var targetGroup by remember { mutableStateOf("Ambos") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(McBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 110.dp)
    ) {
        Text(text = "Notificación global", style = McTitle24)
        Text(text = "Enviar aviso al equipo", style = McBody14Regular)

        Spacer(modifier = Modifier.height(16.dp))

        // Banner "Un mensaje para todos"
        Surface(
            color = McCoralSoft,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, McCoralBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = McCardWhite,
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = McCoralDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Un mensaje para todos", style = McBody14Bold)
                    Text(
                        text = "El aviso se guarda en la API y llega al instante a las cuentas seleccionadas.",
                        style = McLabel11Regular
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                McInputField(
                    label = "Título",
                    value = title,
                    onValueChange = { title = it }
                )
                Spacer(modifier = Modifier.height(14.dp))
                McInputField(
                    label = "Mensaje",
                    value = message,
                    onValueChange = { message = it },
                    singleLine = false,
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Destinatarios", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Veterinarios", "Voluntarios", "Ambos").forEach { item ->
                        val selected = targetGroup == item
                        Surface(
                            color = if (selected) McBlack else McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clickable { targetGroup = item }
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
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        McCoralButton(
            text = if (MiauCareRepository.isLoading) "Enviando..." else "Enviar notificación",
            enabled = !MiauCareRepository.isLoading,
            onClick = {
                scope.launch {
                    MiauCareRepository.enviarNotificacionGlobal(
                        titulo = title,
                        mensaje = message,
                        destinatariosUi = targetGroup
                    )
                }
            }
        )
    }
}

// =========================================================================
// 7. ADMINISTRADOR · INDICADORES POR ZONA (`GET /api/indicadores` & `/exportar-csv`)
// =========================================================================
@Composable
fun AdminZoneIndicatorsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchIndicadoresZonas()
    }

    val indicators = MiauCareRepository.zoneIndicators
    val totalCats = indicators.sumOf { it.gatosCensados }
    val totalSterilized = indicators.sumOf { it.gatosEsterilizados }
    val globalPct = if (totalCats > 0) (totalSterilized * 100 / totalCats) else 0
    val csvPreview = MiauCareRepository.exportedCsvPreview

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
                    Text(text = "Indicadores por zona", style = McTitle18)
                    Text(text = "Cobertura y estado del censo", style = McLabel11Regular)
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        MiauCareRepository.exportarIndicadoresCsv()
                    }
                },
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = McBlack)
            ) {
                Text("Exportar CSV", style = McLabel11Regular.copy(color = McCardWhite, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminKpiBox(
                number = "$totalCats",
                label = "Gatos censados",
                isCoral = false,
                modifier = Modifier.weight(1f)
            )
            AdminKpiBox(
                number = "$globalPct%",
                label = "Esterilizados",
                isCoral = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        indicators.forEach { ind ->
            val pct = if (ind.gatosCensados > 0) {
                (ind.gatosEsterilizados.toFloat() / ind.gatosCensados.toFloat()).coerceIn(0f, 1f)
            } else 0f
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = McSoftNeutral,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = ind.nombreZona, style = McTitle16)
                                Text(
                                    text = "${ind.gatosEsterilizados} esterilizados · ${ind.gatosHospitalizados} hospitalizados",
                                    style = McLabel11Regular
                                )
                            }
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = "${ind.gatosCensados} gatos",
                                style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { pct },
                        color = McCoral,
                        trackColor = McSoftNeutral,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(99.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Esterilización", style = McLabel11Regular)
                        Text(text = "${(pct * 100).toInt()}%", style = McLabel12Bold)
                    }
                }
            }
        }
    }

    if (csvPreview != null) {
        Dialog(onDismissRequest = { MiauCareRepository.exportedCsvPreview = null }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Reporte CSV Exportado", style = McTitle16)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = McBackground,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = csvPreview,
                            style = McLabel11Regular.copy(color = McTextPrimary),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    McCoralButton(
                        text = "Cerrar",
                        onClick = { MiauCareRepository.exportedCsvPreview = null }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 8. ADMINISTRADOR · AUDITORÍA (`GET /api/auditoria`)
// =========================================================================
@Composable
fun AdminAuditScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }

    LaunchedEffect(selectedFilter, searchQuery) {
        val catFilter = if (selectedFilter == "Todas") null else selectedFilter.uppercase()
        MiauCareRepository.fetchAuditoria(tipoAccion = catFilter, buscar = searchQuery.ifBlank { null })
    }

    val grouped = MiauCareRepository.auditLogs
        .filter {
            searchQuery.isBlank() ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(searchQuery, ignoreCase = true)
        }
        .groupBy { it.dateGroup }

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
                Text(text = "Auditoría", style = McTitle18)
                Text(text = "Actividad reciente del sistema", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        McSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Buscar por usuario o acción..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todas", "Censo", "Usuarios", "Sistema").forEach { tab ->
                McFilterPill(
                    label = tab,
                    selected = selectedFilter == tab,
                    onClick = { selectedFilter = tab },
                    unselectedBg = McSoftNeutral
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        grouped.forEach { (dateGroup, logs) ->
            Text(
                text = dateGroup,
                style = McLabel12Bold.copy(color = McTextSecondary),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            logs.forEach { log ->
                Surface(
                    color = McCardWhite,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isDelete = log.iconType == "delete"
                        Surface(
                            color = if (isDelete) McCoralSoft else McSoftNeutral,
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (log.iconType) {
                                        "delete" -> Icons.Outlined.DeleteOutline
                                        "add" -> Icons.Default.Add
                                        "user" -> Icons.Outlined.PersonOutline
                                        else -> Icons.Outlined.Settings
                                    },
                                    contentDescription = null,
                                    tint = if (isDelete) McCoralDark else McTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = log.title, style = McBody14Bold)
                            Text(text = log.subtitle, style = McLabel11Regular)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

// =========================================================================
// 9. ADMINISTRADOR · PROGRAMAR RONDA (`POST /api/rondas` + RN-01 409 Conflict Modal)
// =========================================================================
@Composable
fun AdminScheduleRoundScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedZoneId by remember { mutableLongStateOf(1L) }
    var selectedVeterinarioId by remember { mutableLongStateOf(1L) }
    var fechaInput by remember { mutableStateOf("2026-11-21") }
    var horaInicioInput by remember { mutableStateOf("09:00") }
    var horaFinInput by remember { mutableStateOf("11:00") }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchZonasResumen()
        MiauCareRepository.fetchUsuariosEquipo()
        MiauCareRepository.fetchRondas()
        MiauCareRepository.colonies.firstOrNull()?.let { selectedZoneId = it.numericId }
        MiauCareRepository.doctors.firstOrNull()?.let { selectedVeterinarioId = it.numericId }
    }

    val vetMembers = MiauCareRepository.doctors

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
                Text(text = "Programar ronda", style = McTitle18)
                Text(text = "Asignar visita a una colonia (RN-01)", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Colonia / Zona", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
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
                            onClick = { selectedZoneId = zn.numericId },
                            unselectedBg = McSoftNeutral
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                McInputField(
                    label = "Fecha (YYYY-MM-DD)",
                    value = fechaInput,
                    onValueChange = { fechaInput = it }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    McInputField(
                        label = "Hora inicio (HH:mm)",
                        value = horaInicioInput,
                        onValueChange = { horaInicioInput = it },
                        modifier = Modifier.weight(1f)
                    )
                    McInputField(
                        label = "Hora fin (HH:mm)",
                        value = horaFinInput,
                        onValueChange = { horaFinInput = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Veterinario Responsable", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (vetMembers.isEmpty()) {
                    Text(
                        text = "Cargando veterinarios disponibles...",
                        style = McLabel11Regular
                    )
                } else {
                    vetMembers.forEach { doc ->
                        val selected = selectedVeterinarioId == doc.numericId
                        Surface(
                            color = if (selected) McCoralSoft else McBackground,
                            shape = RoundedCornerShape(16.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { selectedVeterinarioId = doc.numericId }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = McCardWhite,
                                    shape = CircleShape,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Outlined.PersonOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = doc.name, style = McBody14Bold)
                                    Text(text = "${doc.specialty} · ${doc.patientsLoadText}", style = McLabel11Regular)
                                }
                                if (selected) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(McCoral),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = McCardWhite,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        McCoralButton(
            text = if (MiauCareRepository.isLoading) "Guardando ronda..." else "Guardar ronda",
            enabled = !MiauCareRepository.isLoading,
            onClick = {
                scope.launch {
                    val res = MiauCareRepository.programarRondaAdmin(
                        zonaId = selectedZoneId,
                        fechaIso = fechaInput.trim(),
                        horaInicioIso = horaInicioInput.trim(),
                        horaFinIso = horaFinInput.trim(),
                        veterinarioId = selectedVeterinarioId
                    )
                    if (res is ApiResult.Success) onBack()
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        McSoftButton(
            text = "Guardar y repetir mismo horario (Probar RN-01 409)",
            onClick = {
                scope.launch {
                    MiauCareRepository.programarRondaAdmin(
                        zonaId = selectedZoneId,
                        fechaIso = fechaInput.trim(),
                        horaInicioIso = horaInicioInput.trim(),
                        horaFinIso = horaFinInput.trim(),
                        veterinarioId = selectedVeterinarioId
                    )
                    MiauCareRepository.programarRondaAdmin(
                        zonaId = selectedZoneId,
                        fechaIso = fechaInput.trim(),
                        horaInicioIso = horaInicioInput.trim(),
                        horaFinIso = horaFinInput.trim(),
                        veterinarioId = selectedVeterinarioId
                    )
                }
            }
        )
    }
}

// =========================================================================
// 10. ADMINISTRADOR · COLONIAS Y ZONAS (`GET / POST / DELETE /api/zonas`)
// =========================================================================
@Composable
fun AdminColoniesScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf("Todas") }
    var showAddColonyModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchZonasResumen()
    }

    val filtered = MiauCareRepository.colonies.filter {
        selectedFilter == "Todas" || it.zoneName.contains(selectedFilter, ignoreCase = true)
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
                    Text(text = "Colonias y zonas", style = McTitle18)
                    Text(text = "Puntos de alimentación y coordenadas GPS", style = McLabel11Regular)
                }
            }
            Surface(
                color = McCoral,
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { showAddColonyModal = true }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir", tint = McCardWhite)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mapa interactivo real de las colonias de Colombia
        McColonyMapCard(
            onAddClick = { showAddColonyModal = true },
            onColonyPinClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        filtered.forEach { col ->
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(22.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = McSoftNeutral,
                                shape = CircleShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = col.name, style = McTitle16)
                                Text(text = col.address, style = McLabel11Regular)
                                val latStr = String.format(Locale.US, "%.4f", col.latitud ?: 4.6097)
                                val lonStr = String.format(Locale.US, "%.4f", col.longitud ?: -74.0817)
                                Text(
                                    text = "Alimentación: ${col.feedingTime} · GPS ($latStr, $lonStr)",
                                    style = McLabel11Regular
                                )
                            }
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = col.catsCountText,
                                style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            color = McCoralSoft,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clickable {
                                    scope.launch {
                                        MiauCareRepository.eliminarZona(col.numericId)
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "Eliminar zona", style = McLabel12Bold.copy(color = McCoralDark))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddColonyModal) {
        var colName by remember { mutableStateOf("Colonia Chapinero") }
        var colZone by remember { mutableStateOf("Zona Norte") }
        var colAddress by remember { mutableStateOf("Parque de los Hippies, Bogotá") }
        var colTime by remember { mutableStateOf("08:30") }
        var colLat by remember { mutableStateOf("4.6425") }
        var colLon by remember { mutableStateOf("-74.0631") }

        Dialog(onDismissRequest = { showAddColonyModal = false }) {
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
                            Text(text = "Añadir colonia / zona", style = McTitle18)
                            Text(text = "Registro de zona con coordenadas GPS en Colombia", style = McLabel11Regular)
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { showAddColonyModal = false }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    McInputField(label = "Nombre de la colonia", value = colName, onValueChange = { colName = it })
                    Spacer(modifier = Modifier.height(10.dp))
                    McInputField(label = "Zona macro", value = colZone, onValueChange = { colZone = it })
                    Spacer(modifier = Modifier.height(10.dp))
                    McInputField(label = "Dirección / punto alimentación", value = colAddress, onValueChange = { colAddress = it })
                    Spacer(modifier = Modifier.height(10.dp))
                    McInputField(label = "Horario de alimentación", value = colTime, onValueChange = { colTime = it })
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        McInputField(label = "Latitud", value = colLat, onValueChange = { colLat = it }, modifier = Modifier.weight(1f))
                        McInputField(label = "Longitud", value = colLon, onValueChange = { colLon = it }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    McCoralButton(
                        text = "Guardar colonia",
                        onClick = {
                            scope.launch {
                                val res = MiauCareRepository.crearOActualizarZona(
                                    id = null,
                                    nombre = colName,
                                    zonaMacro = colZone,
                                    puntoAlimentacion = colAddress,
                                    horaAlimentacion = colTime,
                                    latitud = colLat.toDoubleOrNull() ?: 4.6097,
                                    longitud = colLon.toDoubleOrNull() ?: -74.0817
                                )
                                if (res is ApiResult.Success) showAddColonyModal = false
                            }
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 11. ADMINISTRADOR · ZONAS Y CATÁLOGOS (`Zonas y catálogos`)
// =========================================================================
@Composable
fun AdminCatalogsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var activeSegment by remember { mutableStateOf("Zonas") }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchCatalogosMaestros()
        MiauCareRepository.fetchZonasResumen()
    }

    val items = when (activeSegment) {
        "Especialidades" -> MiauCareRepository.especialidadesCatalog.map { Triple(it.id, it.nombre, "Especialidad médica veterinaria") }
        "Diagnósticos" -> MiauCareRepository.diagnosticosCatalog.map { Triple(it.id, it.nombre, it.descripcion.ifBlank { "Diagnóstico clínico" }) }
        else -> MiauCareRepository.colonies.map { Triple(it.numericId, it.name, "${it.catsCountText} · ${it.address}") }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Zonas y catálogos", style = McTitle18)
                Text(text = "Listas maestras del sistema", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Zonas", "Especialidades", "Diagnósticos").forEach { seg ->
                McFilterPill(
                    label = seg,
                    selected = activeSegment == seg,
                    onClick = { activeSegment = seg },
                    unselectedBg = McSoftNeutral
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        McCoralButton(
            text = "+ Añadir elemento a $activeSegment",
            onClick = { showAddDialog = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        items.forEach { (id, title, desc) ->
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = McTitle16)
                        Text(text = desc, style = McLabel11Regular)
                    }
                    Surface(
                        color = McCoralSoft,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(34.dp)
                            .clickable {
                                scope.launch {
                                    MiauCareRepository.eliminarItemCatalogo(activeSegment, id)
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = null, tint = McCoralDark, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Nuevo en $activeSegment", style = McTitle18)
                    Spacer(modifier = Modifier.height(12.dp))
                    McInputField(label = "Nombre", value = newName, onValueChange = { newName = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    McCoralButton(
                        text = "Guardar",
                        onClick = {
                            scope.launch {
                                val res = MiauCareRepository.crearItemCatalogo(activeSegment, newName)
                                if (res is ApiResult.Success) showAddDialog = false
                            }
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 12. ADMINISTRADOR · DIRECTORIO Y REASIGNACIÓN (`POST /api/gatos/reasignar-masivo`)
// =========================================================================
@Composable
fun AdminMedicalDirectoryScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedSegment by remember { mutableStateOf("Todos") }
    var reassignFromDoc by remember { mutableStateOf<DoctorItem?>(null) }

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchVeterinariosDirectorio()
    }

    val docs = MiauCareRepository.doctors.filter {
        when (selectedSegment) {
            "Disponibles" -> it.disponibleEmergencias
            else -> true
        }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Directorio médico", style = McTitle18)
                Text(text = "Gestión y reasignación masiva de pacientes", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            McFilterPill(
                label = "Todos · ${MiauCareRepository.doctors.size}",
                selected = selectedSegment == "Todos",
                onClick = { selectedSegment = "Todos" },
                unselectedBg = McSoftNeutral
            )
            McFilterPill(
                label = "Disponibles · ${MiauCareRepository.doctors.count { it.disponibleEmergencias }}",
                selected = selectedSegment == "Disponibles",
                onClick = { selectedSegment = "Disponibles" },
                unselectedBg = McSoftNeutral
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        docs.forEach { doc ->
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
                            resId = doc.imageRes,
                            contentDescription = doc.name,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = doc.name, style = McTitle16)
                                McStatusBadge(label = if (doc.disponibleEmergencias) "Activo" else "Pausado")
                            }
                            Text(text = doc.specialty, style = McLabel11Regular)
                            Text(text = doc.patientsLoadText, style = McLabel11Regular)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = McSoftNeutral,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable { reassignFromDoc = doc }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Reasignar pacientes", style = McLabel12Bold)
                        }
                    }
                }
            }
        }
    }

    if (reassignFromDoc != null) {
        val fromDoc = reassignFromDoc!!
        val candidates = MiauCareRepository.doctors.filter { it.numericId != fromDoc.numericId }
            .ifEmpty { MiauCareRepository.doctors }
        var targetVetId by remember { mutableLongStateOf(candidates.firstOrNull()?.numericId ?: 1L) }

        Dialog(onDismissRequest = { reassignFromDoc = null }) {
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
                            Text(text = "Reasignar pacientes", style = McTitle18)
                            Text(text = "Origen: ${fromDoc.name}", style = McLabel11Regular)
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { reassignFromDoc = null }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Selecciona el veterinario receptor", style = McLabel12Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    candidates.forEach { cand ->
                        val selected = targetVetId == cand.numericId
                        Surface(
                            color = if (selected) McCoralSoft else McBackground,
                            shape = RoundedCornerShape(16.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { targetVetId = cand.numericId }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = cand.name, style = McBody14Bold)
                                    Text(text = "${cand.specialty} · ${cand.patientsLoadText}", style = McLabel11Regular)
                                }
                                if (selected) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = McCoral)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    McCoralButton(
                        text = "Confirmar reasignación masiva",
                        onClick = {
                            reassignFromDoc = null
                            scope.launch {
                                MiauCareRepository.reasignarPacientesMasivo(fromDoc.numericId, targetVetId)
                            }
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 13. ADMINISTRADOR · PARÁMETROS DEL SISTEMA (`GET / PUT /api/parametros`)
// =========================================================================
@Composable
fun AdminSystemParamsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        MiauCareRepository.fetchParametrosSistema()
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Parámetros del sistema", style = McTitle18)
                Text(text = "Reglas y tiempos de alerta (RN-03)", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner "Reglas automáticas"
        Surface(
            color = McCoralSoft,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, McCoralBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = McCardWhite,
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Outlined.Tune, contentDescription = null, tint = McCoralDark, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Reglas automáticas", style = McBody14Bold)
                    Text(
                        text = "Estos valores definen cuándo el backend genera alertas o escala prioridad.",
                        style = McLabel11Regular
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        AdminParamCard(
            title = "Días sin avistamiento (X)",
            description = "Días consecutivos sin reporte antes de marcar al gato en alerta.",
            value = MiauCareRepository.daysWithoutSightingX,
            unit = "días",
            onIncrement = {
                val next = (MiauCareRepository.daysWithoutSightingX.toIntOrNull() ?: 7) + 1
                MiauCareRepository.daysWithoutSightingX = next.toString()
            },
            onDecrement = {
                val prev = ((MiauCareRepository.daysWithoutSightingX.toIntOrNull() ?: 7) - 1).coerceAtLeast(1)
                MiauCareRepository.daysWithoutSightingX = prev.toString()
            }
        )

        AdminParamCard(
            title = "Reportes críticos consecutivos (N)",
            description = "Cantidad de reportes graves seguidos para escalar a prioridad médica.",
            value = MiauCareRepository.consecutiveCriticalN,
            unit = "reportes",
            onIncrement = {
                val next = (MiauCareRepository.consecutiveCriticalN.toIntOrNull() ?: 3) + 1
                MiauCareRepository.consecutiveCriticalN = next.toString()
            },
            onDecrement = {
                val prev = ((MiauCareRepository.consecutiveCriticalN.toIntOrNull() ?: 3) - 1).coerceAtLeast(1)
                MiauCareRepository.consecutiveCriticalN = prev.toString()
            }
        )

        AdminParamCard(
            title = "Tiempo máximo de atención (T)",
            description = "Horas límite sin respuesta antes de reasignar el caso automáticamente.",
            value = MiauCareRepository.maxResponseHoursT,
            unit = "horas",
            onIncrement = {
                val next = (MiauCareRepository.maxResponseHoursT.toIntOrNull() ?: 24) + 2
                MiauCareRepository.maxResponseHoursT = next.toString()
            },
            onDecrement = {
                val prev = ((MiauCareRepository.maxResponseHoursT.toIntOrNull() ?: 24) - 2).coerceAtLeast(1)
                MiauCareRepository.maxResponseHoursT = prev.toString()
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        McCoralButton(
            text = if (MiauCareRepository.isLoading) "Guardando..." else "Guardar parámetros",
            enabled = !MiauCareRepository.isLoading,
            onClick = {
                scope.launch {
                    val res = MiauCareRepository.guardarParametrosSistema()
                    if (res is ApiResult.Success) onBack()
                }
            }
        )
    }
}

@Composable
private fun AdminParamCard(
    title: String,
    description: String,
    value: String,
    unit: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        color = McCardWhite,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = McTitle16)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, style = McLabel11Regular)
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = McSoftNeutral,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onDecrement() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("-", style = McTitle16)
                    }
                }
                Surface(
                    color = McBackground,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { onIncrement() }
                ) {
                    Text(
                        text = value,
                        style = McTitle16,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                Surface(
                    color = McSoftNeutral,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onIncrement() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+", style = McTitle16)
                    }
                }
                Text(text = unit, style = McLabel11Regular)
            }
        }
    }
}

// =========================================================================
// 14. ADMINISTRADOR · MI PERFIL (`PUT /api/administradores/{id}`)
// =========================================================================
@Composable
fun AdminProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(MiauCareRepository.sessionUserName.ifBlank { "Administrador MiauCare" }) }
    var email by remember { mutableStateOf(MiauCareRepository.sessionUserEmail.ifBlank { "adminmiaucare@administrador.com" }) }

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
                Text(text = "Mi perfil", style = McTitle18)
                Text(text = "Cuenta de administración", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Admin identity card
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = McCoralSoft,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = name.take(2).uppercase(),
                            style = McTitle18.copy(color = McCoralDark)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = name, style = McTitle18)
                    Text(text = "Administrador", style = McLabel11Regular)
                    Spacer(modifier = Modifier.height(4.dp))
                    McStatusBadge(label = "Cuenta activa")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                McInputField(label = "Nombre", value = name, onValueChange = { name = it })
                Spacer(modifier = Modifier.height(12.dp))
                McInputField(label = "Correo electrónico", value = email, onValueChange = { email = it })
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        McCoralButton(
            text = "Guardar cambios",
            onClick = {
                scope.launch {
                    MiauCareRepository.actualizarPerfilAdmin(
                        nombre = name,
                        correo = email
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        McSoftButton(
            text = "Cerrar sesión",
            onClick = {
                MiauCareRepository.logout(context)
                onLogout()
            }
        )
    }
}
