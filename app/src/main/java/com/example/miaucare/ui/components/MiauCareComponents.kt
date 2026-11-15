package com.example.miaucare.ui.components

import android.content.Context
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.data.ColonyItem
import com.example.miaucare.data.MiauCareApiClient
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.hypot


@Composable
fun McRolePrototypeSwitcher(
    onRoleSelected: (MiauRole) -> Unit,
    onOpenAdminTools: (() -> Unit)? = null
) {
    val currentRole = MiauCareRepository.currentRole
    Surface(
        color = McSoftNeutral.copy(alpha = 0.85f),
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiauRole.entries.forEach { role ->
                val selected = currentRole == role
                Surface(
                    color = if (selected) McBlack else Color.Transparent,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            MiauCareRepository.currentRole = role
                            onRoleSelected(role)
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = role.label,
                            style = McLabel12Bold.copy(
                                fontSize = 11.sp,
                                color = if (selected) McCardWhite else McTextSecondary
                            )
                        )
                    }
                }
            }
            if (onOpenAdminTools != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = McCoral,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onOpenAdminTools() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Herramientas",
                            tint = McCardWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. FLOATING BOTTOM NAVIGATION BAR (Voluntario / Veterinario / Administrador)
// Exact match to Figma: rounded cream capsule with solid black active tab
// =========================================================================
data class McNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun McFloatingBottomBar(
    role: MiauRole,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = when (role) {
        MiauRole.VOLUNTARIO -> listOf(
            McNavItem("vol_home", "Inicio", Icons.Outlined.Home),
            McNavItem("vol_calendar", "Calendario", Icons.Outlined.CalendarToday),
            McNavItem("vol_census", "Censo", Icons.AutoMirrored.Outlined.Assignment),
            McNavItem("vol_profile", "Perfil", Icons.Outlined.PersonOutline)
        )
        MiauRole.VETERINARIO -> listOf(
            McNavItem("vet_dashboard", "Inicio", Icons.Outlined.Home),
            McNavItem("vet_directory", "Censo", Icons.AutoMirrored.Outlined.Assignment),
            McNavItem("vet_agenda", "Agenda", Icons.Outlined.CalendarToday),
            McNavItem("vet_profile", "Perfil", Icons.Outlined.PersonOutline)
        )
        MiauRole.ADMINISTRADOR -> listOf(
            McNavItem("admin_home", "Inicio", Icons.Outlined.Home),
            McNavItem("admin_census", "Censo", Icons.AutoMirrored.Outlined.Assignment),
            McNavItem("admin_users", "Usuarios", Icons.Outlined.Group),
            McNavItem("admin_broadcast", "Avisos", Icons.Outlined.NotificationsNone)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = McNavContainer,
            shape = RoundedCornerShape(34.dp),
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, McCardWhite.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    Surface(
                        color = if (isSelected) McBlack else Color.Transparent,
                        shape = RoundedCornerShape(26.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clickable { onNavigate(item.route) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) McCardWhite else McTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                style = McLabel11Regular.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
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

// =========================================================================
// 3. BUTTONS, PILLS & BADGES
// =========================================================================
@Composable
fun McCoralButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    darkText: Boolean = false,
    leadingIcon: ImageVector? = null,
    height: Dp = 52.dp,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = McCoral,
            contentColor = if (darkText) McTextPrimary else McCardWhite,
            disabledContainerColor = McSoftNeutral,
            disabledContentColor = McTextSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = McBody14Bold.copy(
                color = if (darkText) McTextPrimary else McCardWhite,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun McSoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    bgColor: Color = McSoftNeutral,
    textColor: Color = McTextPrimary,
    leadingIcon: ImageVector? = null,
    height: Dp = 46.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = McBody14Bold.copy(color = textColor)
        )
    }
}

@Composable
fun McFilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    unselectedBg: Color = Color.Transparent
) {
    Surface(
        color = if (selected) McBlack else unselectedBg,
        shape = RoundedCornerShape(999.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = McLabel12Bold.copy(
                    color = if (selected) McCardWhite else McTextPrimary
                )
            )
        }
    }
}

@Composable
fun McStatusBadge(
    label: String,
    showRedDot: Boolean = false
) {
    val (bg, fg) = when (label.lowercase()) {
        "sano", "activo" -> StatusSanoBg to StatusSanoText
        "herido", "crítica", "cuenta activa" -> StatusHeridoBg to StatusHeridoText
        "enfermo", "moderada" -> StatusEnfermoBg to StatusEnfermoText
        else -> StatusEnCalleBg to StatusEnCalleText
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            if (showRedDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(McCoral)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                style = McLabel11Regular.copy(
                    color = fg,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun McBackCircleButton(
    onClick: () -> Unit,
    isTranslucentOnPhoto: Boolean = false
) {
    if (isTranslucentOnPhoto) {
        Surface(
            color = Color(0x33FFFFFF),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0x88FFFFFF)),
            modifier = Modifier
                .size(40.dp)
                .clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    tint = McCardWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    } else {
        Surface(
            color = McCardWhite,
            shape = CircleShape,
            modifier = Modifier
                .size(40.dp)
                .clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    tint = McTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// =========================================================================
// 4. TEXT INPUTS & SEARCH BARS
// =========================================================================
@Composable
fun McInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    isGlassStyle: Boolean = false,
    showCheckEndIcon: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    rightLabelText: String? = null,
    rightLabelColor: Color = McTextSecondary,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = McLabel12Bold.copy(
                        color = McTextPrimary
                    )
                )
                if (rightLabelText != null) {
                    Text(
                        text = rightLabelText,
                        style = McLabel11Regular.copy(color = rightLabelColor)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = McBody14Regular.copy(color = McTextSecondary)
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = when {
                isPassword -> {
                    {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = McTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                showCheckEndIcon -> {
                    {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(McBlack),
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
                else -> null
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isGlassStyle) McGlassInput else McCardWhite,
                unfocusedContainerColor = if (isGlassStyle) McGlassInput else McCardWhite,
                focusedBorderColor = if (isGlassStyle) Color(0x88FFFFFF) else McBorder,
                unfocusedBorderColor = if (isGlassStyle) Color(0x66FFFFFF) else McBorder,
                focusedTextColor = McTextPrimary,
                unfocusedTextColor = McTextPrimary
            ),
            textStyle = McBody14Regular.copy(color = McTextPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
        )
    }
}

@Composable
fun McSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Buscar gato por nombre..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(text = placeholder, style = McBody14Regular)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = McTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(999.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = McCardWhite,
            unfocusedContainerColor = McCardWhite,
            focusedBorderColor = McBorder,
            unfocusedBorderColor = Color.Transparent
        ),
        textStyle = McBody14Regular.copy(color = McTextPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

// =========================================================================
// 5. MAPA INTERACTIVO REAL DE BOGOTÁ, D.C. CON GPS EN VIVO (`Voluntario · Inicio` + GET /api/zonas/ruta)
// =========================================================================
private const val BOGOTA_MIN_LAT = 4.550
private const val BOGOTA_MAX_LAT = 4.700
private const val BOGOTA_MIN_LON = -74.122
private const val BOGOTA_MAX_LON = -74.032

private fun projectBogotaToCanvas(
    lat: Double,
    lon: Double,
    width: Float,
    height: Float,
    zoom: Float,
    pan: Offset
): Offset {
    val normX = ((lon - BOGOTA_MIN_LON) / (BOGOTA_MAX_LON - BOGOTA_MIN_LON)).toFloat().coerceIn(-0.25f, 1.25f)
    val normY = (1.0 - (lat - BOGOTA_MIN_LAT) / (BOGOTA_MAX_LAT - BOGOTA_MIN_LAT)).toFloat().coerceIn(-0.25f, 1.25f)
    val baseX = normX * width
    val baseY = normY * height
    val cx = width / 2f
    val cy = height / 2f
    return Offset(
        x = cx + (baseX - cx) * zoom + pan.x,
        y = cy + (baseY - cy) * zoom + pan.y
    )
}

private fun unprojectCanvasToBogota(
    tap: Offset,
    width: Float,
    height: Float,
    zoom: Float,
    pan: Offset
): Pair<Double, Double> {
    val cx = width / 2f
    val cy = height / 2f
    val baseX = ((tap.x - pan.x - cx) / zoom) + cx
    val baseY = ((tap.y - pan.y - cy) / zoom) + cy
    val normX = (baseX / width).coerceIn(0.02f, 0.98f)
    val normY = (baseY / height).coerceIn(0.02f, 0.98f)
    val lon = BOGOTA_MIN_LON + normX * (BOGOTA_MAX_LON - BOGOTA_MIN_LON)
    val lat = BOGOTA_MAX_LAT - normY * (BOGOTA_MAX_LAT - BOGOTA_MIN_LAT)
    return lat to lon
}

@Composable
fun McColonyMapCard(
    onAddClick: () -> Unit,
    onColonyPinClick: () -> Unit,
    onUseGpsClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val defaultBogotaColonies = remember {
        listOf(
            ColonyItem(
                numericId = 2L,
                id = "2",
                name = "Colonia Norte",
                zoneName = "Colonia Norte",
                address = "Parque El Virrey, Cl 88 #15, Bogotá",
                feedingTime = "07:30",
                latitud = 4.6725,
                longitud = -74.0545,
                cantidadGatos = 6,
                catsCountText = "6 gatos",
                ordenParada = 1
            ),
            ColonyItem(
                numericId = 1L,
                id = "1",
                name = "Colonia Centro",
                zoneName = "Colonia Centro",
                address = "Parque Nacional, Cra 7 #36, Bogotá",
                feedingTime = "08:00",
                latitud = 4.6230,
                longitud = -74.0660,
                cantidadGatos = 8,
                catsCountText = "8 gatos",
                ordenParada = 2
            ),
            ColonyItem(
                numericId = 3L,
                id = "3",
                name = "Colonia Sur",
                zoneName = "Colonia Sur",
                address = "Parque Ciudad Jardín, Cl 17 Sur, Bogotá",
                feedingTime = "08:30",
                latitud = 4.5780,
                longitud = -74.0940,
                cantidadGatos = 5,
                catsCountText = "5 gatos",
                ordenParada = 3
            )
        )
    }

    val routeList = MiauCareRepository.routeColonies
        .ifEmpty { MiauCareRepository.colonies }
        .ifEmpty { defaultBogotaColonies }

    val userLat = MiauCareRepository.lastDeviceLat.takeIf {
        it in 4.45..4.85
    } ?: 4.6350
    val userLon = MiauCareRepository.lastDeviceLon.takeIf {
        it in -74.25..-73.95
    } ?: -74.0680

    var zoomLevel by remember { mutableFloatStateOf(1.15f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize(1, 1)) }
    var selectedColony by remember { mutableStateOf<ColonyItem?>(null) }

    val countText = "${routeList.size} colonias en Bogotá, D.C."

    // Solicita ubicación GPS real apenas se muestra el mapa
    LaunchedEffect(Unit) {
        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
            val bogotaLat = if (lat in 4.45..4.85) lat else 4.6350
            val bogotaLon = if (lon in -74.25..-73.95) lon else -74.0680
            MiauCareRepository.lastDeviceLat = bogotaLat
            MiauCareRepository.lastDeviceLon = bogotaLon
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF3EFE6))
                .border(1.dp, McBorder, RoundedCornerShape(24.dp))
                .onSizeChanged { canvasSize = it }
                .pointerInput(routeList, zoomLevel, panOffset) {
                    detectTapGestures { tapOffset ->
                        val w = canvasSize.width.toFloat().coerceAtLeast(1f)
                        val h = canvasSize.height.toFloat().coerceAtLeast(1f)

                        // 1. Verificar si tocó el pin de alguna colonia en Bogotá
                        val hitColony = routeList.firstOrNull { col ->
                            val cLat = col.latitud ?: 4.6230
                            val cLon = col.longitud ?: -74.0660
                            val pos = projectBogotaToCanvas(cLat, cLon, w, h, zoomLevel, panOffset)
                            hypot((tapOffset.x - pos.x).toDouble(), (tapOffset.y - pos.y).toDouble()) <= 44.0
                        }

                        if (hitColony != null) {
                            selectedColony = hitColony
                            MiauCareRepository.selectedColonyId = hitColony.id
                            onColonyPinClick()
                        } else {
                            // 2. Actualizar ubicación GPS en Bogotá al punto tocado y recalcular ruta en el backend
                            val (newLat, newLon) = unprojectCanvasToBogota(tapOffset, w, h, zoomLevel, panOffset)
                            MiauCareRepository.lastDeviceLat = newLat
                            MiauCareRepository.lastDeviceLon = newLon
                            scope.launch {
                                if (MiauCareRepository.sessionUserId > 0L &&
                                    MiauCareRepository.currentRole == MiauRole.VOLUNTARIO
                                ) {
                                    MiauCareRepository.actualizarUbicacionGpsVoluntario(newLat, newLon)
                                } else {
                                    MiauCareRepository.fetchRutaVoluntario(newLat, newLon)
                                }
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val maxPanX = (canvasSize.width * 0.45f * zoomLevel)
                        val maxPanY = (canvasSize.height * 0.45f * zoomLevel)
                        panOffset = Offset(
                            x = (panOffset.x + dragAmount.x).coerceIn(-maxPanX, maxPanX),
                            y = (panOffset.y + dragAmount.y).coerceIn(-maxPanY, maxPanY)
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                fun pt(lat: Double, lon: Double): Offset =
                    projectBogotaToCanvas(lat, lon, w, h, zoomLevel, panOffset)

                // 1. Fondo cartográfico urbano de Bogotá
                drawRect(color = Color(0xFFF3EFE6))

                // 2. Cuadrícula de manzanas urbanas de Bogotá (Carreras y Calles secundarias)
                val gridColor = Color(0xFFE5DFD2)
                for (latStep in 0..15) {
                    val lat = BOGOTA_MIN_LAT + latStep * 0.010
                    val p1 = pt(lat, BOGOTA_MIN_LON)
                    val p2 = pt(lat, -74.046)
                    drawLine(color = gridColor, start = p1, end = p2, strokeWidth = 1.4f * zoomLevel)
                }
                for (lonStep in 0..10) {
                    val lon = BOGOTA_MIN_LON + lonStep * 0.008
                    val p1 = pt(BOGOTA_MAX_LAT, lon)
                    val p2 = pt(BOGOTA_MIN_LAT, lon)
                    drawLine(color = gridColor, start = p1, end = p2, strokeWidth = 1.4f * zoomLevel)
                }

                // 3. Cerros Orientales de Bogotá (franja oriental Monserrate / Guadalupe / Usaquén)
                val cerrosOuterPath = Path().apply {
                    val c1 = pt(4.705, -74.044)
                    val c2 = pt(4.675, -74.046)
                    val c3 = pt(4.645, -74.052)
                    val c4 = pt(4.618, -74.056)
                    val c5 = pt(4.585, -74.062)
                    val c6 = pt(4.545, -74.066)
                    val c7 = pt(4.545, -74.020)
                    val c8 = pt(4.705, -74.020)
                    moveTo(c1.x, c1.y)
                    lineTo(c2.x, c2.y)
                    lineTo(c3.x, c3.y)
                    lineTo(c4.x, c4.y)
                    lineTo(c5.x, c5.y)
                    lineTo(c6.x, c6.y)
                    lineTo(c7.x, c7.y)
                    lineTo(c8.x, c8.y)
                    close()
                }
                drawPath(path = cerrosOuterPath, color = Color(0xFFD2E5CB))

                val cerrosHighPath = Path().apply {
                    val c1 = pt(4.705, -74.036)
                    val c2 = pt(4.660, -74.040)
                    val c3 = pt(4.615, -74.046)
                    val c4 = pt(4.545, -74.054)
                    val c5 = pt(4.545, -74.020)
                    val c6 = pt(4.705, -74.020)
                    moveTo(c1.x, c1.y)
                    lineTo(c2.x, c2.y)
                    lineTo(c3.x, c3.y)
                    lineTo(c4.x, c4.y)
                    lineTo(c5.x, c5.y)
                    lineTo(c6.x, c6.y)
                    close()
                }
                drawPath(path = cerrosHighPath, color = Color(0xFFBDD8B3))

                // 4. Parques emblemáticos de Bogotá (El Virrey, Simón Bolívar, Parque Nacional, Ciudad Jardín)
                data class BogotaPark(val name: String, val lat: Double, val lon: Double, val wDeg: Double, val hDeg: Double)
                val parks = listOf(
                    BogotaPark("Parque El Virrey", 4.6725, -74.0545, 0.018, 0.008),
                    BogotaPark("Parque Simón Bolívar", 4.6580, -74.0935, 0.022, 0.016),
                    BogotaPark("Parque Nacional", 4.6230, -74.0660, 0.018, 0.012),
                    BogotaPark("Parque Ciudad Jardín", 4.5780, -74.0940, 0.018, 0.011)
                )
                parks.forEach { park ->
                    val topLeft = pt(park.lat + park.hDeg / 2.0, park.lon - park.wDeg / 2.0)
                    val bottomRight = pt(park.lat - park.hDeg / 2.0, park.lon + park.wDeg / 2.0)
                    drawRoundRect(
                        color = Color(0xFFC8E3BC),
                        topLeft = topLeft,
                        size = Size(
                            width = (bottomRight.x - topLeft.x).coerceAtLeast(14f),
                            height = (bottomRight.y - topLeft.y).coerceAtLeast(10f)
                        ),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }

                // 5. Canales hídricos de Bogotá (Canal El Virrey, Río Arzobispo y Lago Simón Bolívar)
                val waterColor = Color(0xFFA2D2E6)
                drawLine(
                    color = waterColor,
                    start = pt(4.6728, -74.042),
                    end = pt(4.6735, -74.095),
                    strokeWidth = 3.5f * zoomLevel,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = waterColor,
                    start = pt(4.6240, -74.052),
                    end = pt(4.6360, -74.088),
                    strokeWidth = 3.2f * zoomLevel,
                    cap = StrokeCap.Round
                )
                val lakeCenter = pt(4.6575, -74.0930)
                drawCircle(color = waterColor, radius = 9f * zoomLevel, center = lakeCenter)

                // 6. Vías y Avenidas Principales de Bogotá (Carrera 7, Autonorte/Caracas, NQS, Boyacá, Calles 100, 85, 72, 26, 1 Mayo)
                fun drawBogotaAvenue(points: List<Pair<Double, Double>>, isPrimary: Boolean = true) {
                    if (points.size < 2) return
                    val path = Path()
                    points.forEachIndexed { idx, (lat, lon) ->
                        val p = pt(lat, lon)
                        if (idx == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = (if (isPrimary) 7.5f else 5.2f) * zoomLevel,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    drawPath(
                        path = path,
                        color = if (isPrimary) Color(0xFFFAD89D) else Color(0xFFFDFBF7),
                        style = Stroke(
                            width = (if (isPrimary) 4.8f else 3.2f) * zoomLevel,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Carrera 7 (Séptima)
                drawBogotaAvenue(
                    listOf(
                        4.700 to -74.042,
                        4.672 to -74.048,
                        4.645 to -74.058,
                        4.623 to -74.066,
                        4.598 to -74.074,
                        4.555 to -74.082
                    ),
                    isPrimary = true
                )
                // Autopista Norte / Av. Caracas (Cra 14 / Cra 15)
                drawBogotaAvenue(
                    listOf(
                        4.700 to -74.054,
                        4.672 to -74.057,
                        4.645 to -74.065,
                        4.623 to -74.072,
                        4.590 to -74.084,
                        4.555 to -74.092
                    ),
                    isPrimary = true
                )
                // Av. NQS (Carrera 30)
                drawBogotaAvenue(
                    listOf(
                        4.700 to -74.064,
                        4.665 to -74.076,
                        4.635 to -74.081,
                        4.605 to -74.090,
                        4.578 to -74.095,
                        4.555 to -74.104
                    ),
                    isPrimary = true
                )
                // Av. Boyacá / Av. 68
                drawBogotaAvenue(
                    listOf(
                        4.700 to -74.092,
                        4.658 to -74.102,
                        4.615 to -74.110,
                        4.555 to -74.115
                    ),
                    isPrimary = false
                )
                // Calles principales Este-Oeste en Bogotá
                drawBogotaAvenue(listOf(4.686 to -74.042, 4.686 to -74.115), isPrimary = false) // Calle 100
                drawBogotaAvenue(listOf(4.672 to -74.046, 4.672 to -74.105), isPrimary = false) // Calle 85 / El Virrey
                drawBogotaAvenue(listOf(4.656 to -74.052, 4.656 to -74.112), isPrimary = false) // Calle 72
                drawBogotaAvenue(listOf(4.620 to -74.062, 4.642 to -74.118), isPrimary = true)  // Calle 26 (Av. El Dorado)
                drawBogotaAvenue(listOf(4.579 to -74.076, 4.585 to -74.118), isPrimary = false) // Av. Primero de Mayo (Sur)

                // 7. Etiquetas cartográficas nativas (Localidades, Parques y Avenidas de Bogotá)
                val nativeCanvas = drawContext.canvas.nativeCanvas
                val localityPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(120, 113, 102)
                    textSize = 21f * zoomLevel.coerceAtMost(1.45f)
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                    letterSpacing = 0.08f
                }
                val parkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(47, 102, 52)
                    textSize = 20f * zoomLevel.coerceAtMost(1.4f)
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }
                val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(112, 92, 58)
                    textSize = 18f * zoomLevel.coerceAtMost(1.35f)
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }

                // Localidades de Bogotá
                listOf(
                    Triple("USAQUÉN", 4.691, -74.050),
                    Triple("CHAPINERO", 4.652, -74.058),
                    Triple("TEUSAQUILLO", 4.642, -74.086),
                    Triple("SANTA FE · CENTRO", 4.608, -74.070),
                    Triple("ANTONIO NARIÑO · SUR", 4.566, -74.095),
                    Triple("CERROS ORIENTALES", 4.638, -74.039)
                ).forEach { (label, lat, lon) ->
                    val p = pt(lat, lon)
                    nativeCanvas.drawText(label, p.x, p.y, localityPaint)
                }

                // Nombres de vías reales de Bogotá
                listOf(
                    Triple("Cra. 7", 4.636, -74.060),
                    Triple("Autonorte / Caracas", 4.663, -74.063),
                    Triple("Av. NQS (Cra 30)", 4.618, -74.086),
                    Triple("Av. El Dorado (Cl 26)", 4.632, -74.100),
                    Triple("Calle 85", 4.675, -74.076),
                    Triple("Av. 1 de Mayo", 4.585, -74.104)
                ).forEach { (road, lat, lon) ->
                    val p = pt(lat, lon)
                    nativeCanvas.drawText(road, p.x, p.y, roadPaint)
                }

                // Nombres de parques
                parks.forEach { park ->
                    val p = pt(park.lat - 0.006, park.lon)
                    nativeCanvas.drawText(park.name, p.x, p.y, parkPaint)
                }

                // 8. Línea de Ruta Activa en Bogotá (conecta GPS del voluntario con las colonias en orden de parada)
                val userCanvasPt = pt(userLat, userLon)
                val orderedRoutePts = mutableListOf(userCanvasPt)
                routeList.forEach { col ->
                    val cLat = col.latitud ?: 4.6230
                    val cLon = col.longitud ?: -74.0660
                    orderedRoutePts.add(pt(cLat, cLon))
                }
                if (orderedRoutePts.size >= 2) {
                    val routePath = Path().apply {
                        orderedRoutePts.forEachIndexed { idx, p ->
                            if (idx == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                        }
                    }
                    // Borde blanco de la ruta
                    drawPath(
                        path = routePath,
                        color = Color.White.copy(alpha = 0.9f),
                        style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Línea coral punteada de la ruta en Bogotá
                    drawPath(
                        path = routePath,
                        color = McCoral,
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                        )
                    )
                }

                // 9. Marcadores numerados de las Colonias en Bogotá
                val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                    textSize = 26f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }
                val labelBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb(235, 255, 255, 255)
                    style = Paint.Style.FILL
                }
                val labelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(26, 26, 26)
                    textSize = 21f
                    textAlign = Paint.Align.LEFT
                    isFakeBoldText = true
                }

                routeList.forEachIndexed { idx, col ->
                    val cLat = col.latitud ?: (4.6725 - idx * 0.045)
                    val cLon = col.longitud ?: (-74.0545 - idx * 0.015)
                    val pos = pt(cLat, cLon)
                    val stopNum = col.ordenParada ?: (idx + 1)
                    val isSelected = selectedColony?.id == col.id

                    // Halo de colonia
                    drawCircle(
                        color = McCoral.copy(alpha = if (isSelected) 0.38f else 0.22f),
                        radius = if (isSelected) 34f else 26f,
                        center = pos
                    )
                    // Círculo exterior coral
                    drawCircle(
                        color = McCoral,
                        radius = 20f,
                        center = pos
                    )
                    // Círculo interior oscuro
                    drawCircle(
                        color = McBlack,
                        radius = 15f,
                        center = pos
                    )
                    val textY = pos.y - (badgeTextPaint.descent() + badgeTextPaint.ascent()) / 2f
                    nativeCanvas.drawText(stopNum.toString(), pos.x, textY, badgeTextPaint)

                    // Etiqueta flotante al lado del pin con nombre de la colonia y cantidad de gatos
                    val tagText = "${col.name} (${col.catsCountText})"
                    val textWidth = labelTextPaint.measureText(tagText)
                    val tagLeft = (pos.x + 24f).coerceAtMost(w - textWidth - 24f)
                    val tagTop = pos.y - 16f
                    nativeCanvas.drawRoundRect(
                        tagLeft,
                        tagTop,
                        tagLeft + textWidth + 18f,
                        tagTop + 32f,
                        14f,
                        14f,
                        labelBgPaint
                    )
                    nativeCanvas.drawText(tagText, tagLeft + 9f, tagTop + 23f, labelTextPaint)
                }

                // 10. Marcador de Ubicación GPS Real del Usuario en Bogotá ("Estás aquí")
                drawCircle(
                    color = McCoral.copy(alpha = 0.28f),
                    radius = 28f,
                    center = userCanvasPt
                )
                drawCircle(
                    color = Color.White,
                    radius = 14f,
                    center = userCanvasPt
                )
                drawCircle(
                    color = McCoralDark,
                    radius = 9.5f,
                    center = userCanvasPt
                )
            }

            // Píldora superior izquierda: Colonias activas en Bogotá, D.C.
            Surface(
                color = McCardWhite.copy(alpha = 0.95f),
                shape = RoundedCornerShape(999.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = McCoralDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = countText,
                        style = McLabel11Regular.copy(
                            color = McTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Botón superior derecho: "Usar mi ubicación GPS" (RN-07) y centrar en Bogotá
            Surface(
                color = McBlack,
                shape = RoundedCornerShape(999.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp)
                    .clickable {
                        panOffset = Offset.Zero
                        zoomLevel = 1.25f
                        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
                            val bogotaLat = if (lat in 4.45..4.85) lat else 4.6350
                            val bogotaLon = if (lon in -74.25..-73.95) lon else -74.0680
                            MiauCareRepository.lastDeviceLat = bogotaLat
                            MiauCareRepository.lastDeviceLon = bogotaLon
                        }
                        onUseGpsClick?.invoke()
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "GPS",
                        tint = McCoral,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Usar mi ubicación GPS",
                        style = McLabel11Regular.copy(
                            color = McCardWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Controles de Zoom (+ / -) en la esquina inferior izquierda
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp)
            ) {
                Surface(
                    color = McCardWhite.copy(alpha = 0.95f),
                    shape = CircleShape,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            zoomLevel = (zoomLevel + 0.25f).coerceAtMost(2.8f)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Acercar", tint = McTextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
                Surface(
                    color = McCardWhite.copy(alpha = 0.95f),
                    shape = CircleShape,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(1.0f)
                            if (zoomLevel <= 1.05f) panOffset = Offset.Zero
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Alejar", tint = McTextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Etiqueta inferior central con coordenadas GPS activas en Bogotá
            Surface(
                color = McCardWhite.copy(alpha = 0.94f),
                shape = RoundedCornerShape(999.dp),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "GPS Bogotá: %.4f, %.4f · Toca para ubicar", userLat, userLon),
                    style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 9.5.sp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Botón flotante '+' para registrar/reportar gato en Bogotá
            Surface(
                color = McCoral,
                shape = CircleShape,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 14.dp, bottom = 14.dp)
                    .size(44.dp)
                    .clickable { onAddClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Registrar gato",
                        tint = McTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Tarjetas interactivas de paradas de la ruta en Bogotá (al tocar una colonia, centra en ella y abre su detalle)
        if (routeList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                routeList.take(3).forEachIndexed { idx, stop ->
                    Surface(
                        color = McCardWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (selectedColony?.id == stop.id) McCoral else Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedColony = stop
                                MiauCareRepository.selectedColonyId = stop.id
                                onColonyPinClick()
                            }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Text(
                                text = "${stop.ordenParada ?: (idx + 1)}. ${stop.name}",
                                style = McLabel11Regular.copy(
                                    color = McTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                            val distTxt = if (stop.distanciaKm != null) "${stop.distanciaKm} km" else stop.feedingTime
                            Text(
                                text = "$distTxt · ${stop.catsCountText}",
                                style = McLabel11Regular.copy(fontSize = 9.5.sp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun McPawPin(modifier: Modifier = Modifier) {
    Surface(
        color = McCardWhite,
        shape = CircleShape,
        shadowElevation = 4.dp,
        modifier = modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = "Colonia",
                tint = McBlack,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

// =========================================================================
// 6. IMAGE HELPER (Supports Supabase Storage `fotoUrl` + local drawable fallback)
// =========================================================================
@Composable
fun McCatImage(
    @DrawableRes resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    imageUrl: String? = null
) {
    var remoteBitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        if (!imageUrl.isNullOrBlank() && imageUrl.startsWith("http")) {
            remoteBitmap = MiauCareApiClient.fetchRemoteBitmap(imageUrl)
        } else {
            remoteBitmap = null
        }
    }

    val loaded = remoteBitmap
    if (loaded != null) {
        Image(
            bitmap = loaded,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

// =========================================================================
// 7. GLOBAL MODALS & TOASTS FOR BACKEND BUSINESS RULES (403, 409, Server Config)
// =========================================================================
@Composable
fun McFloatingToastHost() {
    val toast = MiauCareRepository.toastMessage
    if (toast != null) {
        val (title, isError) = toast
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, if (isError) McCoralBorder else McBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isError) McCoralSoft else McSoftNeutral),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isError) McCoralDark else McTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = McTitle16)
                        Text(
                            text = if (isError) "Verifica la regla de negocio o tus permisos." else "Sincronizado con el backend MiauCare.",
                            style = McLabel11Regular
                        )
                    }
                    IconButton(onClick = { MiauCareRepository.toastMessage = null }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = McTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // 403 Forbidden Modal: "Sin permisos sobre este gato"
    val sinPermisosMsg = MiauCareRepository.sinPermisosModalMessage
    if (sinPermisosMsg != null) {
        Dialog(onDismissRequest = { MiauCareRepository.sinPermisosModalMessage = null }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(28.dp),
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
                        text = sinPermisosMsg,
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
                                text = "Código 403 Forbidden · Solo el veterinario responsable o delegado autorizado puede gestionar este paciente.",
                                style = McLabel11Regular.copy(color = McTextPrimary)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    McCoralButton(
                        text = "Entendido",
                        onClick = { MiauCareRepository.sinPermisosModalMessage = null }
                    )
                }
            }
        }
    }

    // 409 Conflict Modal (RN-01): "Cruce de horario detectado" with `rondaExistente`
    val conflicto = MiauCareRepository.conflictoHorarioModal
    if (conflicto != null) {
        Dialog(onDismissRequest = { MiauCareRepository.conflictoHorarioModal = null }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = McCoralSoft,
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.EventBusy,
                                    contentDescription = null,
                                    tint = McCoralDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Cruce de horario detectado", style = McTitle18)
                            Text(text = "Regla RN-01 · 409 Conflict", style = McLabel11Regular.copy(color = McCoralDark))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = conflicto.errorMessage,
                        style = McBody14Regular
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = McCoralSoft,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, McCoralBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "RONDA EXISTENTE EN CONFLICTO",
                                style = McLabel11Regular.copy(
                                    color = McCoralDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = conflicto.zonaNombre, style = McTitle16)
                            Text(
                                text = "Fecha: ${conflicto.fecha} · Horario: ${conflicto.horaInicio} - ${conflicto.horaFin}",
                                style = McBody14Bold
                            )
                            Text(
                                text = "Veterinario: ${conflicto.veterinarioNombre} · Estado: ${conflicto.estado}",
                                style = McLabel11Regular
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    McCoralButton(
                        text = "Cambiar horario",
                        onClick = { MiauCareRepository.conflictoHorarioModal = null }
                    )
                }
            }
        }
    }
}

@Composable
fun McServerConnectionChip(
    @Suppress("UNUSED_PARAMETER") context: android.content.Context
) {
    // Conexión transparente y automática al servidor en producción sin mostrar etiquetas técnicas en la interfaz
}

