package com.rstech.inventory.presentation.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onNavigateToSalesReport: () -> Unit,
    onNavigateToPurchaseReport: () -> Unit,
    onNavigateToProductReport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Elige un Reporte") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón 1: Reporte Ventas
                Button(
                    onClick = onNavigateToSalesReport,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Verde
                    modifier = Modifier.size(width = 190.dp, height = 40.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reporte Ventas", color = Color.White)
                }

                // Botón 2: Reporte Compras
                Button(
                    onClick = onNavigateToPurchaseReport,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), // Azul
                    modifier = Modifier.size(width = 190.dp, height = 40.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reporte Compras", color = Color.White)
                }

                // Botón 3: Reporte Productos
                Button(
                    onClick = onNavigateToProductReport,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)), // Gris
                    modifier = Modifier.size(width = 190.dp, height = 40.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reporte Productos", color = Color.White)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}