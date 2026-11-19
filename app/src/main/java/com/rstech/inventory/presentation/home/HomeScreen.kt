package com.rstech.inventory.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rstech.inventory.core.ui.components.Logo
import com.rstech.inventory.presentation.home.components.MainDrawer
import kotlinx.coroutines.launch
import com.rstech.inventory.presentation.home.components.BalanceCard
import androidx.hilt.navigation.compose.hiltViewModel
import com.rstech.inventory.presentation.home.components.ActionCard
import com.rstech.inventory.presentation.components.CalculatorDialog
import com.rstech.inventory.presentation.home.HomeViewModel
import com.rstech.inventory.presentation.notes.NotesViewModel
import com.rstech.inventory.presentation.notes.components.NotesList
import androidx.compose.material.icons.automirrored.rounded.ArrowForward


private val GreenColor = Color(0xFF173D30)
private val LimeColor = Color(0xFFB7F25C)
private val BackgroundColor = Color(0xFFF4F7F5)
private val TextColor = Color(0xFF17201C)
private val MutedColor = Color(0xFF748079)

private val DarkColor = Color(0xFFF4F7F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    balanceViewModel: BalanceViewModel = hiltViewModel(),
    notesViewModel: NotesViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToProductRegister: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToSales: () -> Unit = {},
    onNavigateToNotes: () -> Unit = {},
    onOpenReport: () -> Unit = {},
    onOpenPurchaseDialog: () -> Unit = {},
    onOpenCalculatorBottomSheet: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onNavigateToPurchaseRegister: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    // Recolectamos el estado del ViewModel de notas de forma limpia
    val notasList by notesViewModel.notas.collectAsState()
    val isLoadingNotes by notesViewModel.isLoading.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showCalculatorDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainDrawer(
                uiState = uiState,
                onProfile = { scope.launch { drawerState.close() }; onNavigateToProfile() },
                onProductRegister = { scope.launch { drawerState.close() }; onNavigateToProductRegister() },
                onPurchaseRegister = { scope.launch { drawerState.close() }; onNavigateToPurchaseRegister() },
                onInventory = { scope.launch { drawerState.close() }; onNavigateToInventory() },
                openReport = { scope.launch { drawerState.close() }; onOpenReport() },
                onUsers = { scope.launch { drawerState.close() }; onNavigateToUsers() },
                onSettings = { scope.launch { drawerState.close() }; onSettings() },
                onSignOut = { scope.launch { drawerState.close() }; onSignOut() }
            )
        }
    ) {
        Scaffold(
            containerColor = BackgroundColor,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Logo()
                            Text(
                                text = "Nexo",
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Rounded.Menu, contentDescription = "Abrir menú", tint = Color.Black)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "Notificaciones", tint = TextColor)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE7C9FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.iniciales,
                                style = TextStyle(
                                    color = Color(0xFF57346E),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkColor,
                        scrolledContainerColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 28.dp, bottom = 110.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        item {
                            Text(
                                text = "PANEL PRINCIPAL",
                                style = TextStyle(
                                    color = Color(0xFF4A755C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.4.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                        }

                        item {
                            Text(
                                text = "${uiState.saludo}, ${uiState.primerNombre}",
                                style = TextStyle(
                                    color = TextColor,
                                    fontSize = 28.sp,
                                    lineHeight = 31.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-1).sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                text = "Todo está en orden. Aquí tienes el resumen de tu negocio.",
                                style = TextStyle(
                                    color = MutedColor,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(26.dp))
                        }
                        item {
                            BalanceCard(viewModel = balanceViewModel)
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // --- Fila 1: Nueva venta y Nueva compra ---
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ActionCard(
                                        title = "Nueva venta",
                                        subtitle = "Registra un ingreso",
                                        icon = Icons.Rounded.AttachMoney,
                                        color = Color(0xFF237552),
                                        background = Color(0xFFDFF6E9),
                                        onTap = { onNavigateToSales() }
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    ActionCard(
                                        title = "Nueva compra",
                                        subtitle = "Agrega una compra",
                                        icon = Icons.Outlined.ShoppingCart,
                                        color = Color(0xFF386BAA),
                                        background = Color(0xFFE3EFFC),
                                        onTap = { onNavigateToPurchaseRegister() }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // --- Fila 2: Ver notas y Calculadora ---
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ActionCard(
                                        title = "Ver notas",
                                        subtitle = "Revisa tus pendientes",
                                        icon = Icons.Outlined.NoteAlt,
                                        color = Color(0xFF99701D),
                                        background = Color(0xFFFFF1C9),
                                        onTap = { onNavigateToNotes() }
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    ActionCard(
                                        title = "Calculadora",
                                        subtitle = "Haz una operación",
                                        icon = Icons.Outlined.Calculate,
                                        color = Color(0xFF8055A3),
                                        background = Color(0xFFF1E6F9),
                                        onTap = { showCalculatorDialog = true }                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        // --- Sección de Notas Recientes ---
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Notas recientes",
                                        style = TextStyle(
                                            color = TextColor,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Pendientes y recordatorios",
                                        style = TextStyle(
                                            color = MutedColor,
                                            fontSize = 13.sp
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = { onNavigateToNotes() },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color(0xFFE7F2EB),
                                        contentColor = GreenColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = "Ver todas"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // --- Componente de la lista de notas recientes conectada al ViewModel ---
                        item {
                            NotesList(
                                notes = notasList.take(3), // Muestra solo las 3 más recientes en el Home
                                isLoading = isLoadingNotes,
                                errorMessage = null,
                                onCreate = { onNavigateToNotes() },
                                onDeleteNote = { noteId -> notesViewModel.eliminarNota(noteId) }
                            )
                        }
                    }
                }
                if (showCalculatorDialog) {
                    CalculatorDialog(
                        onDismiss = { showCalculatorDialog = false }
                    )
                }

            }
        }
    }
}