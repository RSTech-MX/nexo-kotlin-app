package com.rstech.inventory.presentation.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val notas by viewModel.notas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFEB3B)) // Amarillo de notas
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFFEB3B)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar nota", tint = Color.Black)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                notas.isEmpty() -> Text("No hay notas registradas", color = Color.Gray)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notas, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onEdit = { noteToEdit = note },
                                onDelete = { viewModel.eliminarNota(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo para Agregar Nota
    if (showAddDialog) {
        NoteDialog(
            title = "Agregar nota",
            initialTitle = "",
            initialContent = "",
            initialColor = 0xFFFFEB3B, // Color amarillo por defecto
            onDismiss = { showAddDialog = false },
            onSave = { title, content, color ->
                viewModel.agregarNota(title, content, color)
                showAddDialog = false
            }
        )
    }

    // Diálogo para Editar Nota
    noteToEdit?.let { note ->
        NoteDialog(
            title = "Editar nota",
            initialTitle = note.titulo,
            initialContent = note.contenido,
            initialColor = note.color,
            onDismiss = { noteToEdit = null },
            onSave = { title, content, color ->
                viewModel.actualizarNota(note.id, title, content, color)
                noteToEdit = null
            }
        )
    }
}

@Composable
fun NoteCard(
    note: NoteItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(note.color)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "contenido: ${note.contenido}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar nota",
                    tint = Color.Red
                )
            }
        }
    }
}