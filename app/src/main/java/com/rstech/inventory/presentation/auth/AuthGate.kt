package com.rstech.inventory.presentation.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rstech.inventory.presentation.home.HomeScreen
import com.rstech.inventory.presentation.home.HomeViewModel

private val GreenColor = Color(0xFF173D30)

@Composable
fun AuthGate(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    onNavigateToWelcome: @Composable () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            // Pantalla de carga mientras se verifica el token / sesión guardada
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenColor)
            }
        }
        is AuthState.Authenticated -> {
            // Usuario con sesión activa: se muestra el Home
            HomeScreen(viewModel = homeViewModel,)
        }
        is AuthState.Unauthenticated -> {
            // Usuario no autenticado: se muestra la pantalla de Bienvenida / Login
            onNavigateToWelcome()
        }
    }
}