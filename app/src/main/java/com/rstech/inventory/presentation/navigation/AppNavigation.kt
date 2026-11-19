package com.rstech.inventory.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.rstech.inventory.presentation.auth.RegistrarUsuarioScreen
import com.rstech.inventory.presentation.auth.WelcomeScreen
import com.rstech.inventory.presentation.auth.components.LoginDialog
import com.rstech.inventory.presentation.components.CalculatorDialog
import com.rstech.inventory.presentation.home.HomeScreen
import com.rstech.inventory.presentation.notes.NotesScreen
import com.rstech.inventory.presentation.products.ProductInventoryScreen
import com.rstech.inventory.presentation.products.ProductRegisterScreen
import com.rstech.inventory.presentation.profile.EditProfileScreen
import com.rstech.inventory.presentation.profile.ProfileScreen
import com.rstech.inventory.presentation.profile.ChangeEmailScreen
import com.rstech.inventory.presentation.purchase.PurchaseRegisterScreen
import com.rstech.inventory.presentation.reports.ProductReportScreen
import com.rstech.inventory.presentation.reports.components.ReportDialog
import com.rstech.inventory.presentation.reports.sales.SalesReportScreen
import com.rstech.inventory.presentation.sales.SalesScreen
import com.rstech.inventory.presentation.users.UserListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var showLoginDialog by remember { mutableStateOf(false) }
    var showReportsDialog by remember { mutableStateOf(false) }
    var showCalculatorDialog by remember { mutableStateOf(false) }

    // Evaluamos si ya hay un usuario logueado en Firebase de forma nativa
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "welcome"

    // 🟢 1. Envolvemos todo en un Box raíz para compartir el contexto visual y permitir diálogos flotantes globales
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // 1. Pantalla de Bienvenida
            composable("welcome") {
                WelcomeScreen(
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onShowLoginDialog = {
                        showLoginDialog = true
                    },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )

                if (showLoginDialog) {
                    LoginDialog(
                        onDismiss = {
                            showLoginDialog = false
                        },
                        onLoginSuccess = {
                            showLoginDialog = false
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                }
            }

            // 2. Pantalla de Registro
            composable("register") {
                RegistrarUsuarioScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }

            // 3. Pantalla Principal (Home / Inventario)
            composable("home") {
                HomeScreen(
                    viewModel = hiltViewModel(), // Inyecta el HomeViewModel automáticamente con Hilt
                    onNavigateToSales = { navController.navigate("sales") },
                    //onOpenPurchaseDialog = { /* Tu lógica para abrir el diálogo de compras */ },
                    onNavigateToNotes = { navController.navigate("notes") },
                    onOpenCalculatorBottomSheet = { showCalculatorDialog = true },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToProductRegister = { navController.navigate("product_register") },
                    onNavigateToPurchaseRegister = { navController.navigate("purchase_register") },
                    onNavigateToInventory = { navController.navigate("inventory") },
                    onOpenReport = { navController.navigate("report") },
                    onNavigateToUsers = { navController.navigate("users") },
                    onSignOut = {
                        // Cerrar sesión en Firebase y navegar de regreso a la bienvenida
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("welcome") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = hiltViewModel(), // Hilt inyecta automáticamente el ProfileViewModel
                    onNavigateBack = {
                        navController.popBackStack() // Regresa al Home
                    },
                    onNavigateToEditProfile = {
                        navController.navigate("edit_profile")
                    },
                    onOpenChangePasswordDialog = {
                        // Próximamente: Lógica para el diálogo de cambiar contraseña
                    },
                    onOpenChangedEmaiDialog = {
                        //navController.navigate("change_email")
                    }
                )
            }

            // 5. Pantalla de Editar Perfil
            composable("edit_profile") {
                EditProfileScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            /*composable("change_email") {
                ChangeEmailScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }*/

            composable("product_register") {
                ProductRegisterScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("purchase_register") {
                PurchaseRegisterScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("inventory") {
                ProductInventoryScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddProduct = {
                        navController.navigate("product_register") // Te lleva a tu pantalla de registro de productos
                    },
                    onProductClick = { productId ->
                        // Si tienes pantalla de detalle/edición de producto, navegas aquí:
                        // navController.navigate("product_detail/$productId")
                    }
                )
            }

            composable("report") {
                ReportDialog(
                    onDismiss = {
                        navController.popBackStack()
                    },
                    onNavigateToSalesReport = {
                        navController.navigate("reporte_ventas")
                    },
                    onNavigateToPurchaseReport = {
                        navController.navigate("reporte_compras")
                    },
                    onNavigateToProductReport = {
                        navController.navigate("reporte_productos") // 🟢 Lleva a la pantalla de reporte de productos
                    }
                )
            }

            // 🟢 Ruta de la pantalla del reporte de productos conectada con Hilt
            composable("reporte_productos") {
                ProductReportScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("reporte_ventas") {
                SalesReportScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("users") {
                UserListScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onUserClick = { user ->
                        // Aquí puedes abrir tu diálogo de edición pasando los datos del usuario
                        // showEditUserDialog(user)
                    }
                )
            }

            composable("sales") {
                SalesScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("notes") {
                NotesScreen (
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // 2. El diálogo de la calculadora queda fuera del NavHost pero dentro del Box raíz, solucionando el error de contexto
        if (showCalculatorDialog) {
            CalculatorDialog(
                onDismiss = { showCalculatorDialog = false }
            )
        }
    }
}