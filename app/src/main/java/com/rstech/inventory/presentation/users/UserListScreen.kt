package com.rstech.inventory.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onNavigateBack: () -> Unit,
    onUserClick: (UserItem) -> Unit // Para abrir el diálogo de edición
) {
    val isAdmin by viewModel.isAdmin.collectAsState()
    val isMaster by viewModel.isMaster.collectAsState()
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                isAdmin == false -> {
                    Text(
                        text = "No tienes permisos para ver esta sección",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
                users.isEmpty() -> {
                    Text(
                        text = "No hay usuarios registrados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users, key = { it.id }) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUserClick(user) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text(text = user.nombre, style = MaterialTheme.typography.titleMedium)
                                    },
                                    supportingContent = {
                                        Text(text = "Usuario: ${user.nombreUsuario} | Edad: ${user.edad}")
                                    },
                                    trailingContent = {
                                        // Solo se muestra el botón de eliminar si el usuario actual es Master
                                        if (isMaster) {
                                            IconButton(onClick = { viewModel.deleteUser(user.id) }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Eliminar usuario",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}