package com.rstech.inventory.presentation.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NoteDialog(
    title: String,
    initialTitle: String,
    initialContent: String,
    initialColor: Long,
    onDismiss: () -> Unit,
    onSave: (String, String, Long) -> Unit
) {
    var tituloText by remember { mutableStateOf(initialTitle) }
    var contenidoText by remember { mutableStateOf(initialContent) }
    var colorSeleccionado by remember { mutableStateOf(initialColor) }

    // Colores disponibles para las notas (equivalente a tu selector de color)
    val availableColors = listOf(
        0xFFFFEB3B, // Amarillo
        0xFF8BC34A, // Verde claro
        0xFF03A9F4, // Azul claro
        0xFFFF9800, // Naranja
        0xFFE91E63  // Rosa
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = tituloText,
                    onValueChange = { tituloText = it },
                    label = { Text("Titulo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contenidoText,
                    onValueChange = { contenidoText = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Seleccionar color:", style = MaterialTheme.typography.bodySmall)

                // Selector de color horizontal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    availableColors.forEach { colorVal ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .clickable { colorSeleccionado = colorVal },
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorSeleccionado == colorVal) {
                                // Indicador visual de selección
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(tituloText, contenidoText, colorSeleccionado) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B))
            ) {
                Text(if (title.contains("Editar")) "Actualizar" else "Guardar", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}