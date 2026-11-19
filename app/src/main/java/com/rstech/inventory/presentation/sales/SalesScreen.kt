package com.rstech.inventory.presentation.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val carrito = viewModel.carrito
    val total = viewModel.total

    var showCartSheet by remember { mutableStateOf(false) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Punto de Venta", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: \$${String.format("%.2f", total)}",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )

                    Button(
                        onClick = { showCartSheet = true },
                        enabled = carrito.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver carrito (${carrito.values.sumOf { it.cantidad }})")
                    }
                }
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
                productos.isEmpty() -> Text("No hay productos disponibles")
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(productos, key = { it.id }) { product ->
                            val cantidadEnCarrito = carrito[product.id]?.cantidad ?: 0

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                            text = product.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Precio: \$${String.format("%.2f", product.precio)} | Stock: ${product.stock}",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.eliminarDelCarrito(product.id) },
                                            enabled = cantidadEnCarrito > 0
                                        ) {
                                            Icon(
                                                Icons.Default.RemoveCircle,
                                                contentDescription = "Quitar",
                                                tint = if (cantidadEnCarrito > 0) Color.Red else Color.Gray
                                            )
                                        }
                                        Text(
                                            text = "$cantidadEnCarrito",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.agregarAlCarrito(product.id, product.nombre, product.precio) },
                                            enabled = product.stock > cantidadEnCarrito
                                        ) {
                                            Icon(
                                                Icons.Default.AddCircle,
                                                contentDescription = "Agregar",
                                                tint = Color(0xFF4CAF50)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet para el Carrito
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Carrito",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (carrito.isEmpty()) {
                    Text("Carrito vacío", modifier = Modifier.padding(24.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(carrito.entries.toList()) { entry ->
                            val item = entry.value
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("${item.cantidad} x \$${String.format("%.2f", item.precio)}", color = Color.Gray, fontSize = 13.sp)
                                    }
                                    Text(
                                        text = "\$${String.format("%.2f", item.subtotal)}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50),
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "Total: \$${String.format("%.2f", total)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showCartSheet = false
                        showPaymentSheet = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = carrito.isNotEmpty()
                ) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cobrar")
                }

                OutlinedButton(
                    onClick = { showCartSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Regresar")
                }
            }
        }
    }

    // Modal BottomSheet para Método de Pago
    if (showPaymentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Método de pago", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.actualizarMetodoPago("efectivo")
                        showPaymentSheet = false
                        scope.launch {
                            viewModel.cobrar().fold(
                                onSuccess = {
                                    snackbarHostState.showSnackbar("Venta realizada correctamente")
                                },
                                onFailure = { e ->
                                    snackbarHostState.showSnackbar("Error al cobrar: ${e.localizedMessage}")
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Money, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Efectivo")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.actualizarMetodoPago("tarjeta")
                        showPaymentSheet = false
                        scope.launch {
                            viewModel.cobrar().fold(
                                onSuccess = {
                                    snackbarHostState.showSnackbar("Venta realizada correctamente")
                                },
                                onFailure = { e ->
                                    snackbarHostState.showSnackbar("Error al cobrar: ${e.localizedMessage}")
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tarjeta")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
