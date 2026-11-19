package com.rstech.inventory.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

private val ProductGreen = Color(0xFF173D30)
private val ProductDark = Color(0xFF10271F)
private val ProductLime = Color(0xFFB7F25C)
private val ProductBackground = Color(0xFFF4F7F5)
private val ProductText = Color(0xFF17201C)
private val ProductMuted = Color(0xFF748079)
private val ProductBorder = Color(0xFFDCE5E0)
private val ProductDanger = Color(0xFFB54736)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRegisterScreen(
    viewModel: ProductRegisterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showExtraFields by remember { mutableStateOf(false) }

    val cost = uiState.cost.toDoubleOrNull()
    val price = uiState.price.toDoubleOrNull()
    val quantity = uiState.quantity.toIntOrNull()
    val unitMargin = if (cost != null && price != null) price - cost else null
    val marginPercentage = if (unitMargin != null && price != null && price > 0) {
        (unitMargin / price) * 100
    } else {
        null
    }
    val projectedProfit = if (unitMargin != null && quantity != null) {
        unitMargin * quantity
    } else {
        null
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProductRegisterUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is ProductRegisterUiEvent.ProductRegistered -> {
                    snackbarHostState.showSnackbar("Producto agregado al inventario")
                }
            }
        }
    }

    Scaffold(
        containerColor = ProductBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo producto",
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
                    containerColor = ProductGreen,
                    scrolledContainerColor = ProductGreen,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ProductSaveBar(
                saving = uiState.isSaving,
                onSave = viewModel::saveProduct,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ProductSummaryCard(
                name = uiState.name,
                sku = uiState.sku,
                unitMargin = unitMargin,
                marginPercentage = marginPercentage,
                projectedProfit = projectedProfit,
            )

            ProductFormSection(
                eyebrow = "IDENTIDAD",
                title = "Información del producto",
                subtitle = "Los datos que usarás para localizarlo en el inventario.",
            ) {
                ProductField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre del producto",
                    placeholder = "Ej. Café premium 500 g",
                    imeAction = ImeAction.Next,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProductField(
                        value = uiState.sku,
                        onValueChange = viewModel::onSkuChange,
                        label = "SKU / Código",
                        placeholder = "CAF-001",
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next,
                    )
                    ProductField(
                        value = uiState.category,
                        onValueChange = viewModel::onCategoryChange,
                        label = "Categoría",
                        placeholder = "Bebidas",
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next,
                    )
                }
            }

            ProductFormSection(
                eyebrow = "PRECIO Y RENTABILIDAD",
                title = "Costos del producto",
                subtitle = "La utilidad se calcula automáticamente con tus precios.",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProductField(
                        value = uiState.cost,
                        onValueChange = { value ->
                            viewModel.onCostChange(filterProductDecimal(value))
                        },
                        label = "Costo",
                        placeholder = "0.00",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        prefix = "$",
                    )
                    ProductField(
                        value = uiState.price,
                        onValueChange = { value ->
                            viewModel.onPriceChange(filterProductDecimal(value))
                        },
                        label = "Precio de venta",
                        placeholder = "0.00",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        prefix = "$",
                    )
                }
                MarginInsight(
                    unitMargin = unitMargin,
                    marginPercentage = marginPercentage,
                    projectedProfit = projectedProfit,
                )
            }

            ProductFormSection(
                eyebrow = "INVENTARIO",
                title = "Control de existencias",
                subtitle = "Define el stock inicial y cuándo quieres recibir una alerta.",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProductField(
                        value = uiState.quantity,
                        onValueChange = { value ->
                            viewModel.onQuantityChange(
                                value.filter { character -> character.isDigit() },
                            )
                        },
                        label = "Cantidad inicial",
                        placeholder = "0",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    )
                    ProductField(
                        value = uiState.minStock,
                        onValueChange = { value ->
                            viewModel.onMinStockChange(
                                value.filter { character -> character.isDigit() },
                            )
                        },
                        label = "Stock mínimo",
                        placeholder = "0",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    )
                }
                StockStatus(
                    quantity = quantity,
                    minStock = uiState.minStock.toIntOrNull(),
                )
            }

            ExtraDetailsCard(
                expanded = showExtraFields,
                onToggle = { showExtraFields = !showExtraFields },
            ) {
                ProductField(
                    value = uiState.unit,
                    onValueChange = viewModel::onUnitChange,
                    label = "Unidad de medida",
                    placeholder = "Pieza, paquete, kg…",
                    imeAction = ImeAction.Next,
                )
                ProductField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Descripción o notas",
                    placeholder = "Agrega información útil para tu equipo",
                    imeAction = ImeAction.Done,
                    singleLine = false,
                    minLines = 3,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun ProductSummaryCard(
    name: String,
    sku: String,
    unitMargin: Double?,
    marginPercentage: Double?,
    projectedProfit: Double?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(containerColor = ProductGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(21.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(15.dp),
                    color = ProductLime,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.AddShoppingCart,
                            contentDescription = null,
                            tint = ProductDark,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(13.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name.ifBlank { "Producto sin nombre" },
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = sku.ifBlank { "SKU pendiente" },
                        color = Color(0xFF9BC3B1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                ProductMetric(
                    label = "UTILIDAD / UNIDAD",
                    value = formatProductMoney(unitMargin),
                    modifier = Modifier.weight(1f),
                    positive = unitMargin == null || unitMargin >= 0,
                )
                ProductMetric(
                    label = "MARGEN",
                    value = marginPercentage?.let {
                        String.format(Locale.US, "%.1f%%", it)
                    } ?: "—",
                    modifier = Modifier.weight(1f),
                    positive = marginPercentage == null || marginPercentage >= 0,
                )
                ProductMetric(
                    label = "UTILIDAD STOCK",
                    value = formatProductMoney(projectedProfit),
                    modifier = Modifier.weight(1f),
                    positive = projectedProfit == null || projectedProfit >= 0,
                )
            }
        }
    }
}

@Composable
private fun ProductMetric(
    label: String,
    value: String,
    modifier: Modifier,
    positive: Boolean,
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(13.dp),
            )
            .padding(horizontal = 10.dp, vertical = 11.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF9BC3B1),
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = if (positive) Color.White else Color(0xFFFFB4A8),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ProductFormSection(
    eyebrow: String,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = eyebrow,
            color = Color(0xFF4A755C),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Column {
                    Text(
                        text = title,
                        color = ProductText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = subtitle,
                        color = ProductMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )
                }
                content()
            }
        }
    }
}

@Composable
private fun ProductField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    val prefixContent: (@Composable () -> Unit)? = if (prefix == null) {
        null
    } else {
        { Text(prefix) }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        prefix = prefixContent,
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ProductGreen,
            unfocusedBorderColor = ProductBorder,
            focusedLabelColor = ProductGreen,
            cursorColor = ProductGreen,
            focusedContainerColor = Color(0xFFF9FBFA),
            unfocusedContainerColor = Color(0xFFF9FBFA),
        ),
    )
}

@Composable
private fun MarginInsight(
    unitMargin: Double?,
    marginPercentage: Double?,
    projectedProfit: Double?,
) {
    val isNegative = unitMargin != null && unitMargin < 0
    val background = if (isNegative) Color(0xFFFFECE8) else Color(0xFFE7F2EB)
    val foreground = if (isNegative) ProductDanger else ProductGreen

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(15.dp))
            .padding(14.dp),
    ) {
        Text(
            text = if (unitMargin == null) {
                "Completa costo y precio para calcular tu utilidad"
            } else if (isNegative) {
                "El precio de venta está por debajo del costo"
            } else {
                "Tu producto genera ${formatProductMoney(unitMargin)} por unidad"
            },
            color = foreground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        if (marginPercentage != null && projectedProfit != null) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Margen ${String.format(Locale.US, "%.1f", marginPercentage)}% · " +
                        "Utilidad del stock ${formatProductMoney(projectedProfit)}",
                color = ProductMuted,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun StockStatus(
    quantity: Int?,
    minStock: Int?,
) {
    val isLowStock = quantity != null && minStock != null && quantity <= minStock
    val background = if (isLowStock) Color(0xFFFFF1D7) else Color(0xFFF4F7F5)
    val title = when {
        quantity == null || minStock == null -> "Configura una alerta de inventario"
        isLowStock -> "El stock inicial activará una alerta"
        else -> "Stock inicial saludable"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(15.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = if (isLowStock) Color(0xFFE09A2D) else ProductLime,
                    shape = CircleShape,
                ),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = ProductText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ExtraDetailsCard(
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            TextButton(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = ProductGreen),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Detalles adicionales",
                        color = ProductText,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Unidad de medida y notas internas",
                        color = ProductMuted,
                        fontSize = 11.sp,
                    )
                }
                Icon(
                    imageVector = if (expanded) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    },
                    contentDescription = null,
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    content = content,
                )
            }
        }
    }
}

@Composable
private fun ProductSaveBar(
    saving: Boolean,
    onSave: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp,
    ) {
        Button(
            onClick = onSave,
            enabled = !saving,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ProductLime,
                contentColor = ProductDark,
                disabledContainerColor = Color(0xFFDDE9D9),
                disabledContentColor = ProductMuted,
            ),
        ) {
            if (saving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = ProductGreen,
                    strokeWidth = 2.5.dp,
                )
                Spacer(modifier = Modifier.width(9.dp))
                Text("Agregando producto…", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Rounded.AddShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(9.dp))
                Text("Agregar producto", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

private fun filterProductDecimal(value: String): String {
    var decimalFound = false
    var decimals = 0
    return value.filter { character ->
        when {
            character.isDigit() && (!decimalFound || decimals++ < 2) -> true
            character == '.' && !decimalFound -> {
                decimalFound = true
                true
            }
            else -> false
        }
    }
}

private fun formatProductMoney(value: Double?): String {
    return value?.let {
        String.format(Locale.US, "${'$'}%.2f", it)
    } ?: "—"
}