package com.example.miaucare.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.miaucare.data.ColonyItem
import com.example.miaucare.data.MiauCareApiClient
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.theme.*
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Locale


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
// 5. MAPA REAL DE COLOMBIA CON GPS EN VIVO (`Voluntario · Inicio` + GET /api/zonas/ruta)
// =========================================================================
private fun createUserGpsMarkerDrawable(context: Context): BitmapDrawable {
    val sizePx = (34f * context.resources.displayMetrics.density).toInt().coerceAtLeast(36)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    val cx = sizePx / 2f
    val cy = sizePx / 2f

    val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(75, 241, 120, 101)
        style = Paint.Style.FILL
    }
    val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(241, 120, 101)
        style = Paint.Style.FILL
    }

    canvas.drawCircle(cx, cy, sizePx * 0.46f, haloPaint)
    canvas.drawCircle(cx, cy, sizePx * 0.27f, whitePaint)
    canvas.drawCircle(cx, cy, sizePx * 0.19f, dotPaint)
    return BitmapDrawable(context.resources, bmp)
}

private fun createColonyMarkerDrawable(context: Context, indexNumber: Int): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (38f * density).toInt().coerceAtLeast(40)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    val cx = sizePx / 2f
    val cy = sizePx / 2f

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(241, 120, 101)
        style = Paint.Style.FILL
    }
    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(26, 26, 26)
        style = Paint.Style.FILL
    }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = 13f * density
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    canvas.drawCircle(cx, cy, sizePx * 0.44f, borderPaint)
    canvas.drawCircle(cx, cy, sizePx * 0.35f, innerPaint)
    val textY = cy - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(indexNumber.toString(), cx, textY, textPaint)
    return BitmapDrawable(context.resources, bmp)
}

@Composable
fun McColonyMapCard(
    onAddClick: () -> Unit,
    onColonyPinClick: () -> Unit,
    onUseGpsClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val defaultColombiaColonies = remember {
        listOf(
            ColonyItem(
                numericId = 1L,
                id = "1",
                name = "Colonia Norte",
                zoneName = "Zona Norte",
                address = "Parque del Virrey, Bogotá",
                feedingTime = "07:30",
                latitud = 4.6533,
                longitud = -74.0836,
                cantidadGatos = 6,
                catsCountText = "6 gatos",
                ordenParada = 1
            ),
            ColonyItem(
                numericId = 2L,
                id = "2",
                name = "Colonia Centro",
                zoneName = "Zona Centro",
                address = "Parque Nacional, Bogotá",
                feedingTime = "08:00",
                latitud = 4.6097,
                longitud = -74.0817,
                cantidadGatos = 8,
                catsCountText = "8 gatos",
                ordenParada = 2
            ),
            ColonyItem(
                numericId = 3L,
                id = "3",
                name = "Colonia Sur",
                zoneName = "Zona Sur",
                address = "Ciudad Jardín, Bogotá",
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
        .ifEmpty { defaultColombiaColonies }

    val userLat = MiauCareRepository.lastDeviceLat.takeIf {
        MiauCareApiClient.isInsideColombia(it, MiauCareRepository.lastDeviceLon)
    } ?: 4.6243
    val userLon = MiauCareRepository.lastDeviceLon.takeIf {
        MiauCareApiClient.isInsideColombia(MiauCareRepository.lastDeviceLat, it)
    } ?: -74.0636

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    val countText = "${routeList.size} colonias en Colombia"

    // Fuente de tiles reales de Colombia (CartoDB Voyager HD) que no sufre bloqueos 403 por User-Agent
    val colombiaTileSource = remember {
        object : OnlineTileSourceBase(
            "CartoVoyagerColombiaV2",
            0,
            20,
            256,
            ".png",
            arrayOf(
                "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
                "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
                "https://c.basemaps.cartocdn.com/rastertiles/voyager/"
            )
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return baseUrl +
                    MapTileIndex.getZoom(pMapTileIndex) + "/" +
                    MapTileIndex.getX(pMapTileIndex) + "/" +
                    MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
            }
        }
    }

    // Solicita ubicación GPS real apenas se muestra el mapa
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = "MiauCareColombiaApp/2.0 (Android; bogota@miaucare.co)"
        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
            MiauCareRepository.lastDeviceLat = lat
            MiauCareRepository.lastDeviceLon = lon
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef?.onDetach()
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(265.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEDEAE2))
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().userAgentValue = "MiauCareColombiaApp/2.0 (Android; bogota@miaucare.co)"
                    Configuration.getInstance().additionalHttpRequestProperties["Referer"] = "https://miaucare.co"
                    MapView(ctx).apply {
                        tileProvider.clearTileCache()
                        setTileSource(colombiaTileSource)
                        setMultiTouchControls(true)
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        minZoomLevel = 5.5
                        maxZoomLevel = 19.5
                        controller.setZoom(13.0)
                        controller.setCenter(GeoPoint(userLat, userLon))

                        // Permite mover y hacer zoom en el mapa sin que el ScrollView vertical intercepte el gesto
                        setOnTouchListener { v, event ->
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                                    v.parent?.requestDisallowInterceptTouchEvent(true)
                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                                    v.parent?.requestDisallowInterceptTouchEvent(false)
                            }
                            false
                        }
                        onResume()
                        mapViewRef = this
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()

                    // Permite tocar cualquier punto del mapa de Colombia para mover/actualizar tu ubicación GPS en tiempo real
                    val receiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            if (p != null && MiauCareApiClient.isInsideColombia(p.latitude, p.longitude)) {
                                MiauCareRepository.lastDeviceLat = p.latitude
                                MiauCareRepository.lastDeviceLon = p.longitude
                                mapView.controller.animateTo(p)
                                scope.launch {
                                    if (MiauCareRepository.sessionUserId > 0L &&
                                        MiauCareRepository.currentRole == MiauRole.VOLUNTARIO
                                    ) {
                                        MiauCareRepository.actualizarUbicacionGpsVoluntario(p.latitude, p.longitude)
                                    } else {
                                        MiauCareRepository.fetchRutaVoluntario(p.latitude, p.longitude)
                                    }
                                }
                            }
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            return singleTapConfirmedHelper(p)
                        }
                    }
                    mapView.overlays.add(MapEventsOverlay(receiver))

                    val userGeoPoint = GeoPoint(userLat, userLon)

                    // Línea de ruta real entre la ubicación GPS del usuario y las colonias en Colombia
                    val routePoints = mutableListOf(userGeoPoint)
                    routeList.forEach { col ->
                        val lat = col.latitud
                        val lon = col.longitud
                        if (lat != null && lon != null && MiauCareApiClient.isInsideColombia(lat, lon)) {
                            routePoints.add(GeoPoint(lat, lon))
                        }
                    }
                    if (routePoints.size >= 2) {
                        val polyline = Polyline(mapView).apply {
                            setPoints(routePoints)
                            outlinePaint.color = android.graphics.Color.rgb(241, 120, 101)
                            outlinePaint.strokeWidth = 7f
                            outlinePaint.isAntiAlias = true
                        }
                        mapView.overlays.add(polyline)
                    }

                    // Marcadores de cada colonia felina en Colombia
                    routeList.forEachIndexed { idx, col ->
                        val lat = col.latitud ?: (4.6097 + idx * 0.018)
                        val lon = col.longitud ?: (-74.0817 - idx * 0.006)
                        val colPoint = GeoPoint(lat, lon)
                        val orderNum = col.ordenParada ?: (idx + 1)

                        val marker = Marker(mapView).apply {
                            position = colPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            icon = createColonyMarkerDrawable(context, orderNum)
                            title = "$orderNum. ${col.name} (${col.catsCountText})"
                            snippet = "${col.address} · Alimentación: ${col.feedingTime}"
                            setOnMarkerClickListener { m, mv ->
                                mv.controller.animateTo(m.position)
                                m.showInfoWindow()
                                onColonyPinClick()
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }

                    // Marcador de ubicación GPS real del usuario ("Estás aquí")
                    val userMarker = Marker(mapView).apply {
                        position = userGeoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        icon = createUserGpsMarkerDrawable(context)
                        title = "Estás aquí (Tu ubicación GPS)"
                        snippet = String.format(Locale.US, "Colombia: %.4f, %.4f", userLat, userLon)
                    }
                    mapView.overlays.add(userMarker)
                    mapView.invalidate()
                }
            )

            // Píldora superior izquierda: Colonias y coordenadas GPS en Colombia
            Surface(
                color = McCardWhite.copy(alpha = 0.94f),
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

            // Botón superior derecho: "Usar mi ubicación GPS" (RN-07) y centrar mapa en Colombia
            Surface(
                color = McBlack,
                shape = RoundedCornerShape(999.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp)
                    .clickable {
                        MiauCareApiClient.requestRealtimeColombiaLocation(context) { lat, lon ->
                            mapViewRef?.controller?.animateTo(GeoPoint(lat, lon))
                            mapViewRef?.controller?.setZoom(13.8)
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
                    color = McCardWhite.copy(alpha = 0.94f),
                    shape = CircleShape,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { mapViewRef?.controller?.zoomIn() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Acercar", tint = McTextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
                Surface(
                    color = McCardWhite.copy(alpha = 0.94f),
                    shape = CircleShape,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { mapViewRef?.controller?.zoomOut() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Alejar", tint = McTextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Etiqueta inferior central con coordenadas GPS activas en Colombia
            Surface(
                color = McCardWhite.copy(alpha = 0.92f),
                shape = RoundedCornerShape(999.dp),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "GPS Colombia: %.4f, %.4f", userLat, userLon),
                    style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 9.5.sp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Botón flotante '+' para reportar gato no censado
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
                        contentDescription = "Reportar",
                        tint = McTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Tarjetas interactivas de paradas de la ruta (al tocar una colonia, el mapa de Colombia se mueve a ella)
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
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val lat = stop.latitud
                                val lon = stop.longitud
                                if (lat != null && lon != null) {
                                    mapViewRef?.controller?.animateTo(GeoPoint(lat, lon))
                                    mapViewRef?.controller?.setZoom(15.0)
                                }
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

