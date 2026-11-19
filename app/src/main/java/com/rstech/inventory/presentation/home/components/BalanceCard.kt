package com.rstech.inventory.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rstech.inventory.presentation.home.BalanceViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun BalanceCard(
    viewModel: BalanceViewModel
) {
    val ingresos by viewModel.ingresos.collectAsState()
    val gastos by viewModel.gastos.collectAsState()
    val balanceTotal by viewModel.balanceTotal.collectAsState()
    val porcentajeCambio by viewModel.porcentajeCambio.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val nombreMes by viewModel.nombreMes.collectAsState()

    val cardBackgroundColor = Color(0xFF173D30)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp), // Ajustado a unidades de Compose
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFB7F25C))
        }
        return
    }

    val esPositivo = porcentajeCambio >= 0
    val simboloCambio = if (esPositivo) "↑" else "↓"
    val colorPorcentaje = if (esPositivo) Color(0xFFC7FA7B) else Color(0xFFFFB49F)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Balance de $nombreMes",
                color = Color(0xFFB7C8C0),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatearMoneda(balanceTotal),
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.5).sp
            )
            Spacer(modifier = Modifier.height(13.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Surface(
                    color = colorPorcentaje.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "$simboloCambio ${kotlin.math.abs(porcentajeCambio).format(1)}%",
                        color = colorPorcentaje,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
                Text(
                    text = "frente al mes anterior",
                    color = Color(0xFFA8BBB3),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BalanceItem(
                        icon = Icons.Rounded.ArrowUpward,
                        label = "Ingresos",
                        value = formatearMoneda(ingresos),
                        color = Color(0xFFB7F25C)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Box(modifier = Modifier.weight(1f)) {
                    BalanceItem(
                        icon = Icons.Rounded.ArrowDownward,
                        label = "Gastos",
                        value = formatearMoneda(gastos),
                        color = Color(0xFFFFB49F)
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(35.dp),
            color = color.copy(alpha = 0.12f),
            shape = RoundedCornerShape(11.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(9.dp))
        Column {
            Text(
                text = label,
                color = Color(0xFF9FB3AA),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

// Extensiones de utilidad para formato de moneda y decimales
private fun formatearMoneda(cantidad: Double): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val formatter = DecimalFormat("$ #,##0.00", symbols)
    return formatter.format(cantidad)
}

private fun Double.format(digits: Int) = String.format(Locale.US, "%.${digits}f", this)