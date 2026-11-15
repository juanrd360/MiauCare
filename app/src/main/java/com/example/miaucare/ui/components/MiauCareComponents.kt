package com.example.miaucare.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.miaucare.data.MiauCareApiClient
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.theme.*


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
// 5. INTERACTIVE COLONY MAP (`Voluntario · Inicio` + GET /api/zonas/ruta)
// =========================================================================
@Composable
fun McColonyMapCard(
    onAddClick: () -> Unit,
    onColonyPinClick: () -> Unit,
    onUseGpsClick: (() -> Unit)? = null
) {
    val routeList = MiauCareRepository.routeColonies.ifEmpty { MiauCareRepository.colonies }
    val countText = if (routeList.isNotEmpty()) "${routeList.size} colonias cerca" else "3 colonias cerca"
    val firstStop = routeList.getOrNull(0)
    val secondStop = routeList.getOrNull(1)
    val thirdStop = routeList.getOrNull(2)

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFAF8F5))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val beigeBlock = Color(0xFFEBE6DC)
                val pinkParkBlock = Color(0xFFF9E5E1)
                val roadLine = Color(0xFFE2DDD3)

                drawRoundRect(beigeBlock, Offset(w * 0.04f, h * 0.14f), Size(w * 0.25f, h * 0.22f), CornerRadius(12f))
                drawRoundRect(beigeBlock, Offset(w * 0.33f, h * 0.06f), Size(w * 0.23f, h * 0.30f), CornerRadius(12f))
                drawRoundRect(beigeBlock, Offset(w * 0.60f, h * 0.06f), Size(w * 0.20f, h * 0.30f), CornerRadius(12f))
                drawRoundRect(beigeBlock, Offset(w * 0.84f, h * 0.06f), Size(w * 0.12f, h * 0.30f), CornerRadius(12f))

                drawRoundRect(beigeBlock, Offset(w * 0.04f, h * 0.44f), Size(w * 0.20f, h * 0.22f), CornerRadius(12f))
                drawRoundRect(pinkParkBlock, Offset(w * 0.27f, h * 0.44f), Size(w * 0.20f, h * 0.22f), CornerRadius(16f))
                drawRoundRect(pinkParkBlock, Offset(w * 0.58f, h * 0.44f), Size(w * 0.22f, h * 0.26f), CornerRadius(16f))
                drawRoundRect(beigeBlock, Offset(w * 0.84f, h * 0.44f), Size(w * 0.12f, h * 0.26f), CornerRadius(12f))

                drawRoundRect(pinkParkBlock, Offset(w * 0.04f, h * 0.72f), Size(w * 0.20f, h * 0.24f), CornerRadius(14f))
                drawRoundRect(beigeBlock, Offset(w * 0.33f, h * 0.72f), Size(w * 0.22f, h * 0.24f), CornerRadius(14f))

                drawLine(roadLine, Offset(0f, h * 0.39f), Offset(w, h * 0.41f), strokeWidth = 2.5f)
                drawLine(roadLine, Offset(0f, h * 0.69f), Offset(w, h * 0.68f), strokeWidth = 2.5f)
                drawLine(roadLine, Offset(w * 0.53f, 0f), Offset(w * 0.53f, h), strokeWidth = 2.5f)
            }

            // Dynamic street / stop labels from GET /api/zonas/ruta
            Text(
                text = firstStop?.address ?: "C. del Olivo",
                style = McLabel11Regular.copy(fontSize = 9.5.sp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 74.dp, end = 60.dp)
            )
            Text(
                text = firstStop?.name ?: "Parque Norte",
                style = McLabel11Regular.copy(fontSize = 9.5.sp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 68.dp, top = 8.dp)
            )
            Text(
                text = secondStop?.address ?: thirdStop?.address ?: "C. del Sol",
                style = McLabel11Regular.copy(fontSize = 9.5.sp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 66.dp, end = 56.dp)
            )

            // "Estás aquí" indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp, end = 36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(McCoral.copy(alpha = 0.28f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(McCoral)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Estás aquí",
                    style = McLabel11Regular.copy(fontSize = 9.sp, color = McTextPrimary)
                )
            }

            // Top-left floating pill "3 colonias cerca"
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(999.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 14.dp, top = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = McTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = countText,
                        style = McLabel11Regular.copy(
                            color = McTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Top-right button "Usar mi ubicación GPS" (RN-07)
            if (onUseGpsClick != null) {
                Surface(
                    color = McBlack,
                    shape = RoundedCornerShape(999.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 14.dp, top = 14.dp)
                        .clickable { onUseGpsClick() }
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
            }

            // 3 Paw Print Pins
            McPawPin(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 88.dp, top = 6.dp)
                    .clickable { onColonyPinClick() }
            )
            McPawPin(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 68.dp, top = 56.dp)
                    .clickable { onColonyPinClick() }
            )
            McPawPin(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 100.dp, bottom = 22.dp)
                    .clickable { onColonyPinClick() }
            )

            // Bottom-right Coral '+' FAB
            Surface(
                color = McCoral,
                shape = CircleShape,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
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

        // Route stops summary strip from GET /api/zonas/ruta
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
                        modifier = Modifier.weight(1f)
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
    context: android.content.Context
) {
    var showServerDialog by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(MiauCareApiClient.baseUrl) }

    Surface(
        color = McCardWhite.copy(alpha = 0.85f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, McBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showServerDialog = true }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF28A745))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "API: ${MiauCareApiClient.baseUrl}",
                    style = McLabel11Regular.copy(color = McTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
                )
            }
            Text(
                text = "Cambiar",
                style = McLabel11Regular.copy(color = McCoralDark, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            )
        }
    }

    if (showServerDialog) {
        Dialog(onDismissRequest = { showServerDialog = false }) {
            Surface(
                color = McCardWhite,
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Servidor Backend MiauCare", style = McTitle18)
                    Text(
                        text = "Usa 10.0.2.2:8080 en emulador local, la IP de tu PC en celular físico, o Render Cloud.",
                        style = McLabel11Regular
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        "Emulador Android (Local 8080)" to MiauCareApiClient.DEFAULT_EMULATOR_URL,
                        "Render Cloud (En línea)" to MiauCareApiClient.RENDER_CLOUD_URL
                    ).forEach { (label, url) ->
                        Surface(
                            color = if (currentUrl == url) McCoralSoft else McBackground,
                            shape = RoundedCornerShape(14.dp),
                            border = if (currentUrl == url) BorderStroke(1.dp, McCoralBorder) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { currentUrl = url }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = label, style = McBody14Bold)
                                Text(text = url, style = McLabel11Regular)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    McInputField(
                        label = "URL / IP Local personalizada",
                        value = currentUrl,
                        onValueChange = { currentUrl = it },
                        placeholder = "http://192.168.1.10:8080"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    McCoralButton(
                        text = "Guardar servidor",
                        onClick = {
                            MiauCareRepository.saveBaseUrl(context, currentUrl)
                            showServerDialog = false
                            MiauCareRepository.showToast("Servidor: ${MiauCareApiClient.baseUrl}")
                        }
                    )
                }
            }
        }
    }
}

