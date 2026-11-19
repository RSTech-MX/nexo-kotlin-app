package com.rstech.inventory.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rstech.inventory.domain.model.Product
import java.util.Locale

private val InventoryGreen = Color(0xFF173D30)
private val InventoryDark = Color(0xFF10271F)
private val InventoryLime = Color(0xFFB7F25C)
private val InventoryBackground = Color(0xFFF4F7F5)
private val InventoryText = Color(0xFF17201C)
private val InventoryMuted = Color(0xFF748079)
private val InventoryBorder = Color(0xFFDCE5E0)
private val InventoryDanger = Color(0xFFB54736)

private enum class InventoryFilter(val label: String) {
    All("Todos"),
    LowStock("Stock bajo"),
    Active("Activos"),
    Inactive("Inactivos"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInventoryScreen(
    viewModel: ProductInventoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onProductClick: (String) -> Unit,
    lowStockAlertsEnabled: Boolean = true,
    onLowStockAlertsChange: (Boolean) -> Unit = {},
    onEditProduct: (String) -> Unit = onProductClick,
    onAddStock: (String) -> Unit = onProductClick,
) {
    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(InventoryFilter.All) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var alertsEnabled by rememberSaveable(lowStockAlertsEnabled) {
        mutableStateOf(lowStockAlertsEnabled)
    }

    val lowStockCount = remember(products) {
        products.count { product -> product.hasLowInventory() }
    }
    val totalUnits = remember(products) {
        products.sumOf { product -> product.stock }
    }
    val inventoryValue = remember(products) {
        products.sumOf { product -> product.price * product.stock }
    }
    val filteredProducts = remember(products, searchQuery, selectedFilter) {
        val query = searchQuery.trim().lowercase(Locale.ROOT)
        products.filter { product ->
            val matchesQuery = query.isEmpty() ||
                    product.name.lowercase(Locale.ROOT).contains(query) ||
                    product.sku.lowercase(Locale.ROOT).contains(query) ||
                    product.category.lowercase(Locale.ROOT).contains(query)
            val matchesFilter = when (selectedFilter) {
                InventoryFilter.All -> true
                InventoryFilter.LowStock -> product.hasLowInventory()
                InventoryFilter.Active -> product.isEffectivelyActive()
                InventoryFilter.Inactive -> !product.isEffectivelyActive()
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        containerColor = InventoryBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventario",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = InventoryGreen,
                    scrolledContainerColor = InventoryGreen,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = InventoryLime,
                contentColor = InventoryDark,
                shape = RoundedCornerShape(18.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                    )
                },
                text = {
                    Text("Nuevo producto", fontWeight = FontWeight.ExtraBold)
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (products.isEmpty()) {
                InventoryEmptyState(
                    title = "Tu inventario está vacío",
                    message = "Agrega tu primer producto para comenzar a controlar existencias.",
                    showAddHint = true,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                InventoryHeader(
                    totalProducts = products.size,
                    totalUnits = totalUnits,
                    lowStockCount = lowStockCount,
                    inventoryValue = inventoryValue,
                )
                LowStockAlertControl(
                    enabled = alertsEnabled,
                    lowStockCount = lowStockCount,
                    onEnabledChange = { enabled ->
                        alertsEnabled = enabled
                        onLowStockAlertsChange(enabled)
                    },
                )
                InventorySearch(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClear = { searchQuery = "" },
                )
                InventoryFilters(
                    selected = selectedFilter,
                    onSelected = { selectedFilter = it },
                )

                if (filteredProducts.isEmpty()) {
                    InventoryEmptyState(
                        title = "Sin coincidencias",
                        message = "Prueba otra búsqueda o cambia el filtro seleccionado.",
                        showAddHint = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    Row(
                        modifier = Modifier.padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 14.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "PRODUCTOS",
                            color = Color(0xFF4A755C),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${filteredProducts.size} resultados",
                            color = InventoryMuted,
                            fontSize = 11.sp,
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 18.dp,
                            top = 10.dp,
                            end = 18.dp,
                            bottom = 110.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredProducts, key = Product::id) { product ->
                            ProductInventoryCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onEdit = { onEditProduct(product.id) },
                                onAddStock = { onAddStock(product.id) },
                                onDelete = { productToDelete = product },
                            )
                        }
                    }
                }
            }
        }
    }

    productToDelete?.let { product ->
        DeleteProductDialog(
            productName = product.name,
            onDismiss = { productToDelete = null },
            onConfirm = {
                viewModel.deleteProduct(product.id)
                productToDelete = null
            },
        )
    }
}

@Composable
private fun LowStockAlertControl(
    enabled: Boolean,
    lowStockCount: Int,
    onEnabledChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.padding(horizontal = 18.dp),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFFE7F2EB) else Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (enabled) InventoryLime else Color(0xFFEEF1EF),
                        shape = RoundedCornerShape(13.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = if (enabled) InventoryDark else InventoryMuted,
                    modifier = Modifier.size(21.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alerta de stock mínimo",
                    color = InventoryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = when {
                        !enabled -> "Actívala para recibir avisos de inventario"
                        lowStockCount > 0 -> "$lowStockCount productos requieren atención"
                        else -> "Te avisaremos cuando un producto llegue al mínimo"
                    },
                    color = if (enabled && lowStockCount > 0) {
                        InventoryDanger
                    } else {
                        InventoryMuted
                    },
                    fontSize = 11.sp,
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = InventoryDark,
                    checkedTrackColor = InventoryLime,
                    uncheckedThumbColor = InventoryMuted,
                    uncheckedTrackColor = Color(0xFFDDE4E0),
                ),
            )
        }
    }
}

@Composable
private fun InventoryHeader(
    totalProducts: Int,
    totalUnits: Int,
    lowStockCount: Int,
    inventoryValue: Double,
) {
    Card(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(containerColor = InventoryGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "VALOR ESTIMADO DEL INVENTARIO",
                color = Color(0xFF9BC3B1),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.15.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatInventoryMoney(inventoryValue),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                InventoryMetric(
                    label = "PRODUCTOS",
                    value = totalProducts.toString(),
                    modifier = Modifier.weight(1f),
                )
                InventoryMetric(
                    label = "UNIDADES",
                    value = totalUnits.toString(),
                    modifier = Modifier.weight(1f),
                )
                InventoryMetric(
                    label = "STOCK BAJO",
                    value = lowStockCount.toString(),
                    modifier = Modifier.weight(1f),
                    alert = lowStockCount > 0,
                )
            }
        }
    }
}

@Composable
private fun InventoryMetric(
    label: String,
    value: String,
    modifier: Modifier,
    alert: Boolean = false,
) {
    Column(
        modifier = modifier
            .background(
                color = if (alert) {
                    Color(0xFF613A2F)
                } else {
                    Color.White.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(13.dp),
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = if (alert) Color(0xFFFFC3B8) else Color(0xFF9BC3B1),
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun InventorySearch(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        placeholder = { Text("Nombre, SKU o categoría") },
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Limpiar búsqueda",
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(17.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InventoryGreen,
            unfocusedBorderColor = InventoryBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = InventoryGreen,
        ),
    )
}

@Composable
private fun InventoryFilters(
    selected: InventoryFilter,
    onSelected: (InventoryFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        InventoryFilter.values().forEach { filter ->
            val isSelected = filter == selected
            Surface(
                modifier = Modifier.weight(1f),
                onClick = { onSelected(filter) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) InventoryGreen else Color.White,
                border = if (isSelected) null else {
                    androidx.compose.foundation.BorderStroke(1.dp, InventoryBorder)
                },
            ) {
                Text(
                    text = filter.label,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 9.dp),
                    color = if (isSelected) Color.White else InventoryMuted,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ProductInventoryCard(
    product: Product,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onAddStock: () -> Unit,
    onDelete: () -> Unit,
) {
    val lowStock = product.hasLowInventory()
    val unitLabel = product.unit.ifBlank { "unidad" }
    val margin = product.price - product.cost
    val stockProgress = if (product.minStock > 0) {
        (product.stock.toFloat() / (product.minStock * 2f)).coerceIn(0f, 1f)
    } else {
        if (product.stock > 0) 1f else 0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (lowStock) {
                                Color(0xFFFFECE8)
                            } else {
                                Color(0xFFE7F2EB)
                            },
                            shape = RoundedCornerShape(15.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = productInitials(product.name),
                        color = if (lowStock) InventoryDanger else InventoryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(modifier = Modifier.width(13.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        color = InventoryText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = buildProductMetadata(product),
                        color = InventoryMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Eliminar producto",
                        tint = InventoryDanger,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProductValue(
                    label = "PRECIO",
                    value = formatInventoryMoney(product.price),
                    modifier = Modifier.weight(1f),
                )
                ProductValue(
                    label = "COSTO",
                    value = formatInventoryMoney(product.cost),
                    modifier = Modifier.weight(1f),
                )
                ProductValue(
                    label = "UTILIDAD",
                    value = formatInventoryMoney(margin),
                    modifier = Modifier.weight(1f),
                    alert = margin < 0,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${product.stock} $unitLabel",
                    color = if (lowStock) InventoryDanger else InventoryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                if (product.minStock > 0) {
                    Text(
                        text = " · mínimo ${product.minStock}",
                        color = InventoryMuted,
                        fontSize = 11.sp,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                ProductStatusBadge(product = product, lowStock = lowStock)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = stockProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = if (lowStock) Color(0xFFE56B55) else InventoryLime,
                trackColor = Color(0xFFEDF1EF),
            )

            if (product.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(11.dp))
                Text(
                    text = product.description,
                    color = InventoryMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(13.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = InventoryGreen,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar", fontWeight = FontWeight.Bold)
                }
                androidx.compose.material3.Button(
                    onClick = onAddStock,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InventoryLime,
                        contentColor = InventoryDark,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Agregar stock", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun ProductValue(
    label: String,
    value: String,
    modifier: Modifier,
    alert: Boolean = false,
) {
    Column(
        modifier = modifier
            .background(Color(0xFFF6F8F7), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
    ) {
        Text(
            text = label,
            color = InventoryMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = value,
            color = if (alert) InventoryDanger else InventoryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ProductStatusBadge(
    product: Product,
    lowStock: Boolean,
) {
    val label = when {
        !product.isEffectivelyActive() -> "Inactivo"
        lowStock -> "Stock bajo"
        else -> "Disponible"
    }
    val background = when {
        !product.isActive -> Color(0xFFEEF0EF)
        lowStock -> Color(0xFFFFECE8)
        else -> Color(0xFFE7F2EB)
    }
    val foreground = when {
        !product.isActive -> InventoryMuted
        lowStock -> InventoryDanger
        else -> InventoryGreen
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = background,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (lowStock) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = foreground,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                color = foreground,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun InventoryEmptyState(
    title: String,
    message: String,
    showAddHint: Boolean,
    modifier: Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = Color(0xFFE7F2EB),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (showAddHint) {
                            Icons.Rounded.Add
                        } else {
                            Icons.Rounded.Search
                        },
                        contentDescription = null,
                        tint = InventoryGreen,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = InventoryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = message,
                color = InventoryMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun DeleteProductDialog(
    productName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(25.dp),
        containerColor = Color.White,
        icon = {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = Color(0xFFFFECE8),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = InventoryDanger,
                    )
                }
            }
        },
        title = {
            Text(
                text = "Eliminar producto",
                color = InventoryText,
                fontWeight = FontWeight.ExtraBold,
            )
        },
        text = {
            Text(
                text = "¿Quieres eliminar “$productName”? Esta acción no se puede deshacer.",
                color = InventoryMuted,
                lineHeight = 20.sp,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = InventoryDanger,
                ),
            ) {
                Text("Eliminar", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = InventoryGreen,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

private fun Product.hasLowInventory(): Boolean {
    return stock == 0 || (minStock > 0 && stock <= minStock)
}

private fun Product.isEffectivelyActive(): Boolean {
    return isActive && stock > 0
}

private fun buildProductMetadata(product: Product): String {
    return buildList {
        if (product.sku.isNotBlank()) add("SKU ${product.sku}")
        if (product.category.isNotBlank()) add(product.category)
    }.joinToString(" · ").ifBlank { "Sin categoría" }
}

private fun productInitials(name: String): String {
    return name
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifBlank { "PR" }
}

private fun formatInventoryMoney(value: Double): String {
    return String.format(Locale.US, "${'$'}%,.2f", value)
}
