package com.rstech.inventory.presentation.components // O tu paquete correspondiente

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorDialog(
    onDismiss: () -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }

    // Lógica pura de la calculadora (sin componentes visuales dentro)
    fun presionar(valor: String) {
        when (valor) {
            "C" -> {
                display = "0"
                firstNumber = null
                operation = null
            }
            in listOf("+", "-", "×", "÷") -> {
                firstNumber = display.toDoubleOrNull() ?: 0.0
                operation = valor
                display = "0"
            }
            "=" -> {
                val secondNumber = display.toDoubleOrNull() ?: 0.0
                var result = 0.0

                when (operation) {
                    "+" -> result = (firstNumber ?: 0.0) + secondNumber
                    "-" -> result = (firstNumber ?: 0.0) - secondNumber
                    "×" -> result = (firstNumber ?: 0.0) * secondNumber
                    "÷" -> result = if (secondNumber == 0.0) 0.0 else (firstNumber ?: 0.0) / secondNumber
                }

                display = if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    String.format("%.2f", result)
                }
                firstNumber = null
                operation = null
            }
            else -> {
                display = if (display == "0") valor else display + valor
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Calculadora") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = display,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val buttonColor = Color(0xFFE0E0E0)

                // Fila 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("7", buttonColor, Color.Black) { presionar("7") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("8", buttonColor, Color.Black) { presionar("8") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("9", buttonColor, Color.Black) { presionar("9") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("÷", MaterialTheme.colorScheme.primary, Color.White) { presionar("÷") } }
                }

                // Fila 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("4", buttonColor, Color.Black) { presionar("4") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("5", buttonColor, Color.Black) { presionar("5") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("6", buttonColor, Color.Black) { presionar("6") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("×", MaterialTheme.colorScheme.primary, Color.White) { presionar("×") } }
                }

                // Fila 3
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("1", buttonColor, Color.Black) { presionar("1") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("2", buttonColor, Color.Black) { presionar("2") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("3", buttonColor, Color.Black) { presionar("3") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("-", MaterialTheme.colorScheme.primary, Color.White) { presionar("-") } }
                }

                // Fila 4
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("0", buttonColor, Color.Black) { presionar("0") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("C", Color.Red, Color.White) { presionar("C") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("=", MaterialTheme.colorScheme.secondary, Color.White) { presionar("=") } }
                    Box(modifier = Modifier.weight(1f)) { CalculatorButton("+", MaterialTheme.colorScheme.primary, Color.White) { presionar("+") } }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Cerrar", color = Color.White)
            }
        }
    )
}

// 🟢 Componente auxiliar fuera de CalculatorDialog para evitar errores de contexto @Composable
@Composable
private fun CalculatorButton(
    texto: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        modifier = Modifier
            .padding(4.dp)
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = texto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}