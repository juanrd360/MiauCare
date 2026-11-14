package com.example.miaucare.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.R
import com.example.miaucare.data.ColonyItem
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.components.*
import com.example.miaucare.ui.theme.*

// =========================================================================
// 1. ADMINISTRADOR · INICIO + MODAL HERRAMIENTAS (`Inicio`)
// =========================================================================
@Composable
fun AdminHomeScreen(
    onNavigate: (String) -> Unit,
    onRoleSwitch: (MiauRole) -> Unit
) {
    var showToolsModal by remember { mutableStateOf(false) }
    var lifeStatusConfirmed by remember { mutableStateOf(false) }
    val pendingCount = MiauCareRepository.pendingApprovals.count { !it.isReviewed }

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

        // Header: "MiauCare · Administración" / "Hola, Admin" / "Viernes, 13 de noviembre" + "Mi perfil"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "MiauCare · Administración", style = McLabel11Regular)
                Text(text = "Hola, Admin", style = McTitle24)
                Text(text = "Viernes, 13 de noviembre", style = McLabel11Regular)
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

        // 3 KPI Boxes (`10 Gatos censados`, `4 Usuarios`, `3 Por aprobar`)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminKpiBox(
                number = "10",
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
                number = "$pendingCount",
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
                subtitle = "10 fichas",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("admin_census") }
            )
            AdminQuickActionCard(
                icon = Icons.Outlined.Group,
                title = "Usuarios",
                subtitle = "4 cuentas",
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
                subtitle = "$pendingCount nuevos",
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

        // Section: "Bandeja de entrada" | "3 pendientes"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bandeja de entrada", style = McTitle18)
            Text(
                text = if (lifeStatusConfirmed) "2 pendientes" else "3 pendientes",
                style = McLabel12Bold.copy(color = McCoralDark)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Inbox Item 1: "Estado de vida por confirmar" (`Manchas · MC-024`)
        if (!lifeStatusConfirmed) {
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
                                text = "Estado de vida por confirmar",
                                style = McLabel11Regular.copy(
                                    color = McCoralDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(text = "Manchas · MC-024", style = McTitle16)
                            Text(
                                text = "Voluntario reportó fallecimiento en Colonia Norte.",
                                style = McLabel11Regular
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                lifeStatusConfirmed = true
                                MiauCareRepository.showToast(
                                    title = if (MiauCareRepository.prototypeSimulateError) "No se pudo procesar" else "Cambio confirmado",
                                    isError = MiauCareRepository.prototypeSimulateError
                                )
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
                                    lifeStatusConfirmed = true
                                    MiauCareRepository.showToast("Reporte rechazado")
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

        // Inbox Item 2: "Reasignación automática" (`Colonia Norte · 4 gatos`)
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
                        text = "Reasignación automática",
                        style = McLabel11Regular.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(text = "Colonia Norte · 4 gatos", style = McTitle16)
                    Text(
                        text = "Dr. Luis Pérez pasó a no disponible. Reasignados a Dra. Sofía García.",
                        style = McLabel11Regular
                    )
                }
            }
        }
    }

    // Modal "Herramientas" matching `step_80_shot_6_admin_Parametros_y_Modales.png`
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
    val simulateError = MiauCareRepository.prototypeSimulateError

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

                // Simulación del prototipo (`Flujo normal` | `Simular error`)
                Surface(
                    color = McBackground,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Simulación del prototipo", style = McLabel12Bold)
                        Text(
                            text = "Alterna el resultado de las acciones para probar estados.",
                            style = McLabel11Regular
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = if (!simulateError) McBlack else McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clickable { MiauCareRepository.prototypeSimulateError = false }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Flujo normal",
                                        style = McLabel11Regular.copy(
                                            color = if (!simulateError) McCardWhite else McTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Surface(
                                color = if (simulateError) McBlack else McSoftNeutral,
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clickable { MiauCareRepository.prototypeSimulateError = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Simular error",
                                        style = McLabel11Regular.copy(
                                            color = if (simulateError) McCardWhite else McTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
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

    val censusOrderIds = listOf("cat-gary", "cat-peach", "cat-whitney", "cat-buggy", "cat-manchas", "cat-samantha", "cat-benito", "cat-luna")
    val orderedCats = censusOrderIds.mapNotNull { id -> MiauCareRepository.cats.find { it.id == id } }

    val filteredCats = orderedCats.filter { cat ->
        val matchesQuery = cat.name.contains(searchQuery, ignoreCase = true) ||
            cat.code.contains(searchQuery, ignoreCase = true)
        val matchesZone = selectedZone == "Todas las zonas" || cat.colony.contains(selectedZone.removePrefix("Colonia "), ignoreCase = true)
        matchesQuery && matchesZone
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
                Text(text = "Censo felino", style = McTitle24)
                Text(text = "10 gatos registrados", style = McBody14Regular)
            }
            Surface(
                color = McCoral,
                shape = CircleShape,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onOpenEditCat("cat-manchas") }
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = McSoftNeutral,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        selectedZone = if (selectedZone == "Todas las zonas") "Colonia Norte" else "Todas las zonas"
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedZone, style = McLabel12Bold)
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
            Surface(
                color = McSoftNeutral,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Todos los estados", style = McLabel12Bold)
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
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
// 3. ADMINISTRADOR · EDITAR FICHA (`Editar ficha` - Manchas · MC-024)
// =========================================================================
@Composable
fun AdminEditCatScreen(
    catId: String,
    onBack: () -> Unit
) {
    val cat = remember(catId) {
        if (catId.isBlank()) MiauCareRepository.getCatById("cat-manchas")
        else MiauCareRepository.getCatById(catId)
    }
    var name by remember { mutableStateOf(cat.name) }
    var code by remember { mutableStateOf(cat.code) }
    var sex by remember { mutableStateOf(cat.sex) }
    var age by remember { mutableStateOf(cat.age) }
    var zone by remember { mutableStateOf(cat.colony) }
    var lifeStatus by remember { mutableStateOf(cat.lifeStatus) }

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
                Text(text = "Editar ficha", style = McTitle18)
                Text(text = "${cat.name} · ${cat.code}", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Photo + Cambiar foto
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
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = name, style = McTitle18)
                    Text(text = "${cat.breed} · ${cat.sterilizationStatus}", style = McLabel11Regular)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = McSoftNeutral,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.clickable {
                            MiauCareRepository.showToast("Selector de foto abierto")
                        }
                    ) {
                        Text(
                            text = "Cambiar foto",
                            style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
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
                        onValueChange = { name = it },
                        modifier = Modifier.weight(1f)
                    )
                    McInputField(
                        label = "Código",
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    McInputField(
                        label = "Sexo",
                        value = sex,
                        onValueChange = { sex = it },
                        modifier = Modifier.weight(1f)
                    )
                    McInputField(
                        label = "Edad",
                        value = age,
                        onValueChange = { age = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                McInputField(
                    label = "Zona / colonia",
                    value = zone,
                    onValueChange = { zone = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Estado de vida (`Activo` | `Fallecido`)
        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Estado de vida", style = McTitle16)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Activo", "Fallecido").forEach { st ->
                        val selected = lifeStatus == st
                        Surface(
                            color = if (selected) McBlack else McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { lifeStatus = st }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = st,
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

        // Historial médico
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
                    Text(text = "2 entradas", style = McLabel11Regular)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = McBackground,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "12 nov 2026 · Esterilización", style = McBody14Bold)
                            Text(text = "Dra. Sofía García · Sin complicaciones", style = McLabel11Regular)
                        }
                        Text(text = "Editar", style = McLabel11Regular)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = McBackground,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "03 oct 2026 · Vacunación antirrábica", style = McBody14Bold)
                            Text(text = "Dr. Luis Pérez · Dosis anual", style = McLabel11Regular)
                        }
                        Text(text = "Editar", style = McLabel11Regular)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = McSoftNeutral,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { MiauCareRepository.showToast("Entrada médica añadida") }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(text = "+ Añadir entrada médica", style = McLabel12Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        McCoralButton(
            text = "Guardar cambios",
            onClick = {
                cat.name = name
                cat.code = code
                cat.sex = sex
                cat.age = age
                cat.colony = zone
                cat.lifeStatus = lifeStatus
                MiauCareRepository.showToast(
                    title = if (MiauCareRepository.prototypeSimulateError) "No se pudo procesar" else "Cambios guardados",
                    isError = MiauCareRepository.prototypeSimulateError
                )
                onBack()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                MiauCareRepository.showToast("Ficha eliminada del sistema")
                onBack()
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

// =========================================================================
// 4. ADMINISTRADOR · APROBAR REGISTROS (`Aprobar registros` - Cake, Kiwi, Willie)
// =========================================================================
@Composable
fun AdminApproveRecordsScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Pendientes") }
    val pendingList = MiauCareRepository.pendingApprovals.filter {
        if (selectedTab == "Pendientes") !it.isReviewed else it.isReviewed
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
                Text(text = "Aprobar registros", style = McTitle18)
                Text(text = "Enviados por veterinarios", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val count = MiauCareRepository.pendingApprovals.count { !it.isReviewed }
            McFilterPill(
                label = "Pendientes · $count",
                selected = selectedTab == "Pendientes",
                onClick = { selectedTab = "Pendientes" },
                unselectedBg = McSoftNeutral
            )
            McFilterPill(
                label = "Revisados",
                selected = selectedTab == "Revisados",
                onClick = { selectedTab = "Revisados" },
                unselectedBg = McSoftNeutral
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        pendingList.forEach { item ->
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

                    if (!item.isReviewed) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    item.isReviewed = true
                                    MiauCareRepository.showToast("Ficha aprobada: ${item.nameAndCode}")
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
                                        item.isReviewed = true
                                        MiauCareRepository.showToast("Ficha rechazada")
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
// 5. ADMINISTRADOR · GESTIÓN DE USUARIOS (`Gestión de usuarios`)
// =========================================================================
@Composable
fun AdminUsersScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredUsers = MiauCareRepository.teamUsers.filter { u ->
        val matchesQuery = u.name.contains(searchQuery, ignoreCase = true) ||
            u.roleLabel.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedFilter) {
            "Veterinarios" -> u.roleLabel.startsWith("Veterinari")
            "Voluntarios" -> u.roleLabel.startsWith("Voluntari")
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
        Text(text = "4 miembros del equipo", style = McBody14Regular)

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
            var activeState by remember(user.id) { mutableStateOf(user.isActive) }
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
                        onCheckedChange = {
                            activeState = it
                            user.isActive = it
                            MiauCareRepository.showToast(
                                "${user.name}: ${if (it) "Activo" else "Inactivo"}"
                            )
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
// 6. ADMINISTRADOR · NOTIFICACIÓN GLOBAL (`Notificación global` / `Avisos`)
// =========================================================================
@Composable
fun AdminBroadcastScreen() {
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
                        text = "El aviso llegará al instante a las cuentas seleccionadas.",
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
            text = "Enviar notificación",
            onClick = {
                MiauCareRepository.showToast("Notificación global enviada a $targetGroup")
            }
        )
    }
}

// =========================================================================
// 7. ADMINISTRADOR · INDICADORES POR ZONA (`Indicadores por zona`)
// =========================================================================
@Composable
fun AdminZoneIndicatorsScreen(
    onBack: () -> Unit
) {
    val zones = listOf(
        Triple("Colonia Norte", "3 esterilizados · 1 en tratamiento", 0.75f),
        Triple("Colonia Centro", "3 esterilizados · 0 en tratamiento", 0.75f),
        Triple("Colonia Sur", "1 esterilizado · 1 en tratamiento", 0.50f)
    )

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
                Text(text = "Indicadores por zona", style = McTitle18)
                Text(text = "Cobertura y estado del censo", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminKpiBox(
                number = "10",
                label = "Gatos censados",
                isCoral = false,
                modifier = Modifier.weight(1f)
            )
            AdminKpiBox(
                number = "70%",
                label = "Esterilizados",
                isCoral = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        zones.forEachIndexed { idx, (name, details, pct) ->
            val catsCount = if (idx == 2) "2 gatos" else "4 gatos"
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
                                Text(text = name, style = McTitle16)
                                Text(text = details, style = McLabel11Regular)
                            }
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = catsCount,
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
}

// =========================================================================
// 8. ADMINISTRADOR · AUDITORÍA (`Auditoría`)
// =========================================================================
@Composable
fun AdminAuditScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }

    val grouped = MiauCareRepository.auditLogs
        .filter { it.title.contains(searchQuery, ignoreCase = true) }
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
// 9. ADMINISTRADOR · PROGRAMAR RONDA (`Programar ronda`)
// =========================================================================
@Composable
fun AdminScheduleRoundScreen(
    onBack: () -> Unit
) {
    var selectedResponsible by remember { mutableStateOf("Sofía Álvarez") }
    var notes by remember {
        mutableStateOf("Revisión general y control de puntos de alimentación.")
    }

    val members = listOf(
        "Sofía Álvarez" to "Voluntaria · Colonia Norte",
        "Dra. Sofía García" to "Veterinaria · Cirugía CES",
        "Dr. Luis Pérez" to "Veterinario · Medicina general"
    )

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
                Text(text = "Asignar visita a una colonia", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = McCardWhite,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Colonia", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = McBackground,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Parque Norte · Colonia Norte", style = McBody14Bold)
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Fecha", style = McLabel12Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = McBackground,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Sábado, 14 nov",
                                style = McBody14Bold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Hora", style = McLabel12Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = McBackground,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "09:00",
                                style = McBody14Bold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Responsable", style = McLabel12Bold)
                Spacer(modifier = Modifier.height(8.dp))

                members.forEach { (name, role) ->
                    val selected = selectedResponsible == name
                    Surface(
                        color = if (selected) McCoralSoft else McBackground,
                        shape = RoundedCornerShape(16.dp),
                        border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { selectedResponsible = name }
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
                                Text(text = name, style = McBody14Bold)
                                Text(text = role, style = McLabel11Regular)
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

                Spacer(modifier = Modifier.height(8.dp))
                McInputField(
                    label = "Notas",
                    value = notes,
                    onValueChange = { notes = it },
                    singleLine = false,
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        McCoralButton(
            text = "Guardar ronda",
            onClick = {
                MiauCareRepository.showToast("Ronda programada para $selectedResponsible")
                onBack()
            }
        )
    }
}

// =========================================================================
// 10. ADMINISTRADOR · COLONIAS Y ZONAS + MODAL AÑADIR COLONIA (`Colonias y zonas`)
// =========================================================================
@Composable
fun AdminColoniesScreen(
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Todas") }
    var showAddColonyModal by remember { mutableStateOf(false) }

    val filtered = MiauCareRepository.colonies.filter {
        selectedFilter == "Todas" || it.zoneName == selectedFilter
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
                    Text(text = "Puntos de alimentación y rondas", style = McLabel11Regular)
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

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todas", "Colonia Norte", "Colonia Centro").forEach { tab ->
                McFilterPill(
                    label = tab,
                    selected = selectedFilter == tab,
                    onClick = { selectedFilter = tab },
                    unselectedBg = McSoftNeutral
                )
            }
        }

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
                                Text(text = "${col.zoneName} · ${col.address}", style = McLabel11Regular)
                                Text(text = "Alimentación: ${col.feedingTime}", style = McLabel11Regular)
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
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable { showAddColonyModal = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "Editar", style = McLabel12Bold)
                            }
                        }
                        Surface(
                            color = McCoralSoft,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable {
                                    MiauCareRepository.colonies.remove(col)
                                    MiauCareRepository.showToast("Colonia eliminada")
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "Eliminar", style = McLabel12Bold.copy(color = McCoralDark))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddColonyModal) {
        var colName by remember { mutableStateOf("Mirador del Río") }
        var colZone by remember { mutableStateOf("Colonia Norte") }
        var colAddress by remember { mutableStateOf("Av. del Río, 24") }
        var colTime by remember { mutableStateOf("08:30") }

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
                            Text(text = "Añadir colonia", style = McTitle18)
                            Text(text = "Nuevo punto de alimentación", style = McLabel11Regular)
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
                    McInputField(label = "Zona", value = colZone, onValueChange = { colZone = it })
                    Spacer(modifier = Modifier.height(10.dp))
                    McInputField(label = "Dirección / referencia", value = colAddress, onValueChange = { colAddress = it })
                    Spacer(modifier = Modifier.height(10.dp))
                    McInputField(label = "Horario de alimentación", value = colTime, onValueChange = { colTime = it })
                    Spacer(modifier = Modifier.height(18.dp))
                    McCoralButton(
                        text = "Guardar colonia",
                        onClick = {
                            MiauCareRepository.colonies.add(
                                ColonyItem(
                                    id = "col-${System.currentTimeMillis()}",
                                    name = colName,
                                    zoneName = colZone,
                                    address = colAddress,
                                    feedingTime = colTime,
                                    catsCountText = "0 gatos"
                                )
                            )
                            showAddColonyModal = false
                            MiauCareRepository.showToast("Colonia guardada")
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
    var activeSegment by remember { mutableStateOf("Zonas") }
    val items = when (activeSegment) {
        "Estados" -> listOf(
            "En calle" to "Gato comunitario monitoreado en colonia",
            "Hospitalizado" to "En observación clínica veterinaria",
            "Adoptado" to "Integrado en hogar definitivo"
        )
        "Diagnósticos" -> listOf(
            "Deshidratación moderada" to "Requiere fluidoterapia subcutánea",
            "FIV+ (Inmunodeficiencia)" to "Monitoreo periódico de peso y mucosas",
            "Valoración prequirúrgica CES" to "Apto para protocolo de esterilización"
        )
        else -> listOf(
            "Colonia Norte" to "4 gatos · Parque Norte",
            "Colonia Centro" to "4 gatos · Plaza Mayor",
            "Colonia Sur" to "2 gatos · Jardín del Sur"
        )
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
            listOf("Zonas", "Estados", "Diagnósticos").forEach { seg ->
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
            text = "+ Añadir elemento",
            onClick = { MiauCareRepository.showToast("Nuevo elemento añadido a $activeSegment") }
        )

        Spacer(modifier = Modifier.height(14.dp))

        items.forEach { (title, desc) ->
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
                        color = McSoftNeutral,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { MiauCareRepository.showToast("Editar $title") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = McCoralSoft,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { MiauCareRepository.showToast("Elemento eliminado") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = null, tint = McCoralDark, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 12. ADMINISTRADOR · DIRECTORIO Y REASIGNACIÓN (`Directorio y reasignación`)
// =========================================================================
@Composable
fun AdminMedicalDirectoryScreen(
    onBack: () -> Unit
) {
    var selectedSegment by remember { mutableStateOf("Internos") }
    var showReassignModal by remember { mutableStateOf(false) }

    val docs = MiauCareRepository.doctors.filter {
        if (selectedSegment == "Internos") it.isInternal else !it.isInternal
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
                Text(text = "Internos y colaboradores externos", style = McLabel11Regular)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            McFilterPill(
                label = "Internos · 2",
                selected = selectedSegment == "Internos",
                onClick = { selectedSegment = "Internos" },
                unselectedBg = McSoftNeutral
            )
            McFilterPill(
                label = "Externos · 2",
                selected = selectedSegment == "Externos",
                onClick = { selectedSegment = "Externos" },
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
                                McStatusBadge(label = "Activo")
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
                            .clickable { showReassignModal = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Reasignar pacientes", style = McLabel12Bold)
                        }
                    }
                }
            }
        }
    }

    if (showReassignModal) {
        var targetVet by remember { mutableStateOf("Dr. Luis Pérez") }
        Dialog(onDismissRequest = { showReassignModal = false }) {
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
                            Text(text = "Dra. Sofía García · 8 pacientes", style = McLabel11Regular)
                        }
                        Surface(
                            color = McSoftNeutral,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { showReassignModal = false }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Selecciona el veterinario receptor", style = McLabel12Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Dr. Luis Pérez" to "Interno · 2 pacientes a cargo",
                        "Dr. Mateo Ruiz" to "Colaborador externo · Cirugía"
                    ).forEach { (name, sub) ->
                        val selected = targetVet == name
                        Surface(
                            color = if (selected) McCoralSoft else McBackground,
                            shape = RoundedCornerShape(16.dp),
                            border = if (selected) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { targetVet = name }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, style = McBody14Bold)
                                    Text(text = sub, style = McLabel11Regular)
                                }
                                if (selected) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = McCoral)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    McCoralButton(
                        text = "Confirmar reasignación",
                        onClick = {
                            showReassignModal = false
                            MiauCareRepository.showToast("Pacientes reasignados a $targetVet")
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// 13. ADMINISTRADOR · PARÁMETROS DEL SISTEMA (`Parámetros del sistema`)
// =========================================================================
@Composable
fun AdminSystemParamsScreen(
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            McBackCircleButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Parámetros del sistema", style = McTitle18)
                Text(text = "Reglas y tiempos de alerta", style = McLabel11Regular)
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
                        text = "Estos valores definen cuándo el sistema genera alertas o reasigna casos.",
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
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        McCoralButton(
            text = "Guardar parámetros",
            onClick = {
                MiauCareRepository.showToast("Parámetros del sistema actualizados")
                onBack()
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
    onIncrement: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = unit, style = McLabel11Regular)
            }
        }
    }
}

// =========================================================================
// 14. ADMINISTRADOR · MI PERFIL (`Mi perfil`)
// =========================================================================
@Composable
fun AdminProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var name by remember { mutableStateOf("Ana") }
    var email by remember { mutableStateOf("admin@miaucare.org") }

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

        // Admin identity card (`AN`, `Ana`, `Administradora`, `Cuenta activa`)
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
                            text = "AN",
                            style = McTitle18.copy(color = McCoralDark)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = name, style = McTitle18)
                    Text(text = "Administradora", style = McLabel11Regular)
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
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Rol", style = McLabel12Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Administrador general",
                                style = McLabel11Regular.copy(color = McTextPrimary),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Estado de cuenta", style = McLabel12Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = McSoftNeutral,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Activa",
                                style = McLabel11Regular.copy(color = McTextPrimary),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        McCoralButton(
            text = "Guardar cambios",
            onClick = { MiauCareRepository.showToast("Perfil actualizado") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        McSoftButton(
            text = "Cerrar sesión",
            onClick = onLogout
        )
    }
}
