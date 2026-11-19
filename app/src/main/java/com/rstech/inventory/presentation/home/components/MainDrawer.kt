package com.rstech.inventory.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rstech.inventory.core.ui.components.Logo
import com.rstech.inventory.presentation.home.HomeUiState


private val DrawerDark = Color(0xFF0D2B21)
private val DrawerLime = Color(0xFFB7F25C)
private val DrawerMuted = Color(0xFFB2C2BC)

@Composable
fun MainDrawer(
    uiState: HomeUiState,
    onProfile: (() -> Unit)? = null,
    onProductRegister: (() -> Unit)? = null,
    onPurchaseRegister: (() -> Unit)? = null,
    onInventory: (() -> Unit)? = null,
    openReport: (() -> Unit)? = null,
    onUsers: (() -> Unit)? = null,
    onSettings: (() -> Unit)? = null,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = DrawerDark,
        drawerShape = RoundedCornerShape(0.dp)
    ) {
        SafeAreaContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Sección superior: Logo y Opciones del Menú
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Cabecera con el Logo "Nexo"
                    Padding(modifier = Modifier.padding(horizontal = 10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Logo()
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Nexo",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Elementos del menú lateral
                    DrawerItem(
                        icon = Icons.Rounded.BarChart,
                        label = "Resumen",
                        selected = true,
                        onTap = {}
                    )
                    DrawerItem(
                        icon = Icons.Rounded.AttachMoney,
                        label = "Perfil",
                        selected = false,
                        onTap = onProfile
                    )
                    DrawerItem(
                        icon = Icons.Rounded.AttachMoney,
                        label = "Registrar Nuevo Producto",
                        selected = false,
                        onTap = onProductRegister
                    )
                    DrawerItem(
                        icon = Icons.Rounded.AttachMoney,
                        label = "Registrar Nueva compra",
                        selected = false,
                        onTap = onPurchaseRegister
                    )
                    DrawerItem(
                        icon = Icons.Outlined.Inventory2,
                        label = "Inventario",
                        selected = false,
                        onTap = onInventory
                    )
                    DrawerItem(
                        icon = Icons.Outlined.Inventory2,
                        label = "Reportes",
                        selected = false,
                        onTap = openReport
                    )
                    DrawerItem(
                        icon = Icons.Outlined.Inventory2,
                        label = "Usuarios",
                        selected = false,
                        onTap = onUsers
                    )
                }

                // Sección inferior: Configuración, Perfil de Usuario y Botón Salir
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    DrawerItem(
                        icon = Icons.Outlined.Settings,
                        label = "Configuración",
                        selected = false,
                        onTap = onSettings
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = Color(0xFF2E493F), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Perfil inferior dinámico consumiendo el ViewModel
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE7B5FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.iniciales,
                                style = TextStyle(
                                    color = Color(0xFF57346E),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(13.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = uiState.primerNombre,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.isLoading) "Cargando..." else "Administrador",
                                style = TextStyle(
                                    color = Color(0xFF71B091),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de Cerrar Sesión
                    Padding(modifier = Modifier.padding(bottom = 25.dp)) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Button(
                                onClick = onSignOut,
                                modifier = Modifier
                                    .width(240.dp)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Logout,
                                    contentDescription = "Cerrar sesión",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cerrar sesión",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onTap: (() -> Unit)?
) {
    val foreground = if (selected) DrawerDark else DrawerMuted

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Surface(
            color = if (selected) DrawerLime else Color.Transparent,
            shape = RoundedCornerShape(15.dp),
            onClick = { onTap?.invoke() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = foreground,
                    modifier = Modifier.size(23.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = TextStyle(
                        color = foreground,
                        fontSize = 16.sp,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun SafeAreaContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        content()
    }
}

@Composable
fun Padding(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) { content() }
}