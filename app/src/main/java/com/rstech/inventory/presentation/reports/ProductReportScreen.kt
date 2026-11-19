package com.rstech.inventory.presentation.reports

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rstech.inventory.domain.model.Product
import java.util.Locale
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reporte de Productos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Regresar"
                        )
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
            // ================= SECCIÓN BOTONES DE EXPORTACIÓN =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { exportToPdf(context, uiState.products, uiState.totalInventoryValue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar PDF", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { exportToExcel(context, uiState.products, uiState.totalInventoryValue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TableChart,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar Excel", fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // ================= LISTADO O ESTADOS =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(color = Color(0xFF173D30))
                    }
                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                    uiState.products.isEmpty() -> {
                        Text(
                            text = "No hay productos registrados",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.products, key = { it.id }) { product ->
                                ProductReportCard(product = product)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductReportCard(product: Product) {
    val valorInventario = product.cost * product.stock

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF17201C)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Costo: $${String.format(Locale.US, "%.2f", product.cost)}",
                fontSize = 14.sp,
                color = Color(0xFF5B6862)
            )
            Text(
                text = "Precio: $${String.format(Locale.US, "%.2f", product.price)}",
                fontSize = 14.sp,
                color = Color(0xFF5B6862)
            )
            Text(
                text = "Stock: ${product.stock}",
                fontSize = 14.sp,
                color = Color(0xFF5B6862)
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Valor inventario: $${String.format(Locale.US, "%.2f", valorInventario)}",
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF173D30),
                    fontSize = 15.sp
                )
            )
        }
    }
}
private fun exportToPdf(context: Context, products: List<Product>, totalValue: Double) {
    val pdfDocument = PdfDocument()
    // Formato de página A4 (595 x 842 píxeles)
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    val paint = Paint().apply {
        textSize = 12f
        color = android.graphics.Color.BLACK
    }
    val titlePaint = Paint().apply {
        textSize = 20f
        isFakeBoldText = true
        color = android.graphics.Color.BLACK
    }

    var y = 50f
    canvas.drawText("Reporte de Productos", 40f, y, titlePaint)
    y += 30f
    canvas.drawText("Inventario actual", 40f, y, paint)
    y += 25f

    // Cabecera de la tabla
    canvas.drawText("Producto | Costo | Precio | Stock | Valor", 40f, y, paint)
    y += 15f
    canvas.drawText("--------------------------------------------------------------------------------", 40f, y, paint)
    y += 20f

    // Listado de productos
    for (product in products) {
        val valor = product.cost * product.stock
        val line = "${product.name} | \$${product.cost} | \$${product.price} | ${product.stock} | \$${String.format(Locale.US, "%.2f", valor)}"
        canvas.drawText(line, 40f, y, paint)
        y += 20f

        // Salto simple si se acaba el espacio de la página
        if (y > 780f) break
    }

    y += 10f
    canvas.drawText("--------------------------------------------------------------------------------", 40f, y, paint)
    y += 25f

    // Total del inventario
    val totalPaint = Paint().apply {
        textSize = 14f
        isFakeBoldText = true
        color = android.graphics.Color.BLACK
    }
    canvas.drawText("VALOR TOTAL INVENTARIO: \$${String.format(Locale.US, "%.2f", totalValue)}", 40f, y, totalPaint)

    pdfDocument.finishPage(page)

    // Guardar y abrir el archivo PDF
    val file = File(context.cacheDir, "reporte_productos_${System.currentTimeMillis()}.pdf")
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

private fun exportToExcel(context: Context, products: List<Product>, totalValue: Double) {
    val csvData = StringBuilder()
    // Cabeceras del CSV (compatible con Excel)
    csvData.append("Producto,Costo,Precio,Stock,Valor\n")

    for (product in products) {
        val valor = product.cost * product.stock
        csvData.append("\"${product.name}\",${product.cost},${product.price},${product.stock},$valor\n")
    }

    csvData.append("\n,,,,,VALOR TOTAL INVENTARIO,$totalValue\n")

    // Guardar y abrir el archivo CSV con Excel
    val file = File(context.cacheDir, "reporte_productos_${System.currentTimeMillis()}.csv")
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