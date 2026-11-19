package com.rstech.inventory.presentation.reports.sales

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    viewModel: SalesReportViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var expandedDropdown by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateTimeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte de Ventas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF237552),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F7F5))
        ) {
            // ================= SECCIÓN FILTROS =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SalesFilterType.values().forEach { filter ->
                        FilterChip(
                            selected = uiState.filterType == filter,
                            onClick = { viewModel.setFilterType(filter) },
                            label = { Text(filter.name) }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = {
                            showDatePicker(context, uiState.selectedDate) { newDate ->
                                viewModel.setSelectedDate(newDate)
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(dateFormat.format(uiState.selectedDate))
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Método de pago: ${uiState.paymentMethod}")
                    }
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        listOf("Todos", "efectivo", "tarjeta").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.setPaymentMethod(option)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { exportSalesToPdf(context, uiState) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PDF")
                    }

                    Button(
                        onClick = { exportSalesToExcel(context, uiState) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Rounded.TableChart, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Excel")
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // ================= LISTADO DE VENTAS =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(color = Color(0xFF173D30))
                    uiState.errorMessage != null -> Text(uiState.errorMessage ?: "Error", color = Color.Red)
                    uiState.sales.isEmpty() -> Text("No hay ventas registradas", color = Color.Gray)
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.sales, key = { it.id }) { sale ->
                                SaleReportCard(sale = sale, dateTimeFormat = dateTimeFormat)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaleReportCard(sale: SaleReportItem, dateTimeFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Venta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(dateTimeFormat.format(sale.date), fontSize = 12.sp, color = Color.Gray)
            }

            Text("Método de pago: ${sale.paymentMethod}", fontSize = 13.sp, color = Color(0xFF5B6862))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

            sale.products.forEach { prod ->
                Text(
                    text = "• ${prod.name} x${prod.quantity} ($${prod.price}) = $${String.format(Locale.US, "%.2f", prod.subtotal)}",
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

            Text(
                text = "Total: $${String.format(Locale.US, "%.2f", sale.total)}",
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF173D30),
                    fontSize = 16.sp
                )
            )
        }
    }
}

private fun showDatePicker(context: Context, currentDate: Date, onDateSelected: (Date) -> Unit) {
    val cal = Calendar.getInstance().apply { time = currentDate }
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth)
            onDateSelected(c.time)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun exportSalesToPdf(context: Context, state: SalesReportUiState) {
    if (state.sales.isEmpty()) return

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    val paint = Paint().apply { textSize = 11f; color = android.graphics.Color.BLACK }
    val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true }

    var y = 40f
    canvas.drawText("Reporte de Ventas", 40f, y, titlePaint)
    y += 25f
    canvas.drawText("Filtro: ${state.filterType.name} | Método: ${state.paymentMethod}", 40f, y, paint)
    y += 20f
    canvas.drawText("Fecha | Método | Producto | Precio | Cant. | Subtotal", 40f, y, paint)
    y += 10f
    canvas.drawText("--------------------------------------------------------------------------------", 40f, y, paint)
    y += 15f

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    for (sale in state.sales) {
        for (p in sale.products) {
            val line = "${dateFormat.format(sale.date)} | ${sale.paymentMethod} | ${p.name} | \$${p.price} | ${p.quantity} | \$${p.subtotal}"
            canvas.drawText(line, 40f, y, paint)
            y += 18f
            if (y > 780f) break
        }
    }

    y += 10f
    canvas.drawText("--------------------------------------------------------------------------------", 40f, y, paint)
    y += 20f
    titlePaint.textSize = 14f
    canvas.drawText("TOTAL GENERAL: \$${String.format(Locale.US, "%.2f", state.grandTotal)}", 40f, y, titlePaint)

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "reporte_ventas_${System.currentTimeMillis()}.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun exportSalesToExcel(context: Context, state: SalesReportUiState) {
    if (state.sales.isEmpty()) return

    val csvData = StringBuilder()
    csvData.append("Fecha,Metodo,Producto,Precio,Cantidad,Subtotal\n")

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    for (sale in state.sales) {
        for (p in sale.products) {
            csvData.append("\"${dateFormat.format(sale.date)}\",\"${sale.paymentMethod}\",\"${p.name}\",${p.price},${p.quantity},${p.subtotal}\n")
        }
    }
    csvData.append("\n,,,,,TOTAL GENERAL,${state.grandTotal}\n")

    val file = File(context.cacheDir, "reporte_ventas_${System.currentTimeMillis()}.csv")
    try {
        file.writeText(csvData.toString(), charset = Charsets.UTF_8)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}