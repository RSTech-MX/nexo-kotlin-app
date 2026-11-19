package com.rstech.inventory.presentation.purchase

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

private val PurchaseGreen = Color(0xFF173D30)
private val PurchaseDark = Color(0xFF10271F)
private val PurchaseLime = Color(0xFFB7F25C)
private val PurchaseBackground = Color(0xFFF4F7F5)
private val PurchaseText = Color(0xFF17201C)
private val PurchaseMuted = Color(0xFF748079)
private val PurchaseBorder = Color(0xFFDCE5E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseRegisterScreen(
    viewModel: PurchaseRegisterViewModel,
    onNavigateBack: () -> Unit,
    onPhotoCaptured: (Bitmap?) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showCardSheet by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            onPhotoCaptured(bitmap)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is PurchaseRegisterUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is PurchaseRegisterUiEvent.PurchaseRegistered -> {
                    snackbarHostState.showSnackbar("Compra registrada correctamente")
                }
            }
        }
    }

    Scaffold(
        containerColor = PurchaseBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar compra",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurchaseGreen,
                    scrolledContainerColor = PurchaseGreen,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            PurchaseSaveBar(
                saving = viewModel.guardando,
                onSave = viewModel::registrarCompra,
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            PurchaseSummary(
                cost = viewModel.costo,
                supplier = viewModel.proveedor,
                paymentType = viewModel.tipoPago,
            )

            ReceiptPhotoCard(
                bitmap = capturedBitmap,
                onTakePhoto = { cameraLauncher.launch(null) },
                onRemovePhoto = {
                    capturedBitmap = null
                    onPhotoCaptured(null)
                },
            )

            PurchaseSection(
                eyebrow = "DATOS DE LA COMPRA",
                title = "Información principal",
                subtitle = "Registra cuándo y por qué se realizó esta compra.",
            ) {
                PurchaseTextField(
                    value = viewModel.fechaCompra,
                    onValueChange = { viewModel.fechaCompra = formatPurchaseDate(it) },
                    label = "Fecha de compra",
                    placeholder = "dd/mm/aaaa",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                )
                PurchaseTextField(
                    value = viewModel.descripcion,
                    onValueChange = { viewModel.descripcion = it },
                    label = "Descripción",
                    placeholder = "Ej. Compra de materia prima",
                    imeAction = ImeAction.Next,
                )
                PurchaseTextField(
                    value = viewModel.costo,
                    onValueChange = { value ->
                        viewModel.costo = value.filter { character ->
                            character.isDigit() || character == '.'
                        }
                    },
                    label = "Costo total",
                    placeholder = "0.00",
                    prefix = "$",
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                )
            }

            PurchaseSection(
                eyebrow = "PROVEEDOR",
                title = "Datos del proveedor",
                subtitle = "Información fiscal de quien emitió el comprobante.",
            ) {
                PurchaseTextField(
                    value = viewModel.proveedor,
                    onValueChange = { viewModel.proveedor = it },
                    label = "Proveedor",
                    placeholder = "Nombre o razón social",
                    imeAction = ImeAction.Next,
                )
                PurchaseTextField(
                    value = viewModel.rfcProveedor,
                    onValueChange = { value ->
                        viewModel.rfcProveedor = value
                            .uppercase()
                            .filter { character -> character.isLetterOrDigit() }
                            .take(13)
                    },
                    label = "RFC del proveedor",
                    placeholder = "XAXX010101000",
                    imeAction = ImeAction.Next,
                )
            }

            PurchaseSection(
                eyebrow = "PAGO",
                title = "Método de pago",
                subtitle = "Selecciona cómo se liquidó esta compra.",
            ) {
                PurchaseSelector(
                    label = "Tipo de pago",
                    value = viewModel.tipoPago,
                    placeholder = "Selecciona una opción",
                    onClick = { showPaymentSheet = true },
                )
                if (viewModel.tipoPago == "Tarjeta") {
                    PurchaseSelector(
                        label = "Tipo de tarjeta",
                        value = viewModel.tipoTarjeta,
                        placeholder = "Crédito o débito",
                        onClick = { showCardSheet = true },
                    )
                }
                PurchaseTextField(
                    value = viewModel.ticket,
                    onValueChange = { viewModel.ticket = it },
                    label = "Número de ticket",
                    placeholder = "Opcional",
                    imeAction = ImeAction.Next,
                )
            }

            PurchaseSection(
                eyebrow = "FACTURACIÓN",
                title = "Datos de la factura",
                subtitle = "Completa estos campos si cuentas con factura.",
            ) {
                PurchaseTextField(
                    value = viewModel.fechaFactura,
                    onValueChange = { viewModel.fechaFactura = formatPurchaseDate(it) },
                    label = "Fecha de factura",
                    placeholder = "dd/mm/aaaa",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                )
                PurchaseTextField(
                    value = viewModel.folioFiscal,
                    onValueChange = { viewModel.folioFiscal = it.uppercase() },
                    label = "Folio fiscal",
                    placeholder = "UUID o folio de la factura",
                    imeAction = ImeAction.Next,
                )
                PurchaseTextField(
                    value = viewModel.set,
                    onValueChange = { viewModel.set = it },
                    label = "SET",
                    placeholder = "Dato complementario",
                    imeAction = ImeAction.Done,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showPaymentSheet) {
        PurchaseOptionsSheet(
            title = "Tipo de pago",
            subtitle = "¿Cómo pagaste esta compra?",
            options = listOf(
                "Efectivo" to "Pago realizado en efectivo",
                "Transferencia" to "Transferencia bancaria",
                "Tarjeta" to "Tarjeta de crédito o débito",
            ),
            selected = viewModel.tipoPago,
            onDismiss = { showPaymentSheet = false },
            onSelected = { paymentType ->
                viewModel.tipoPago = paymentType
                if (paymentType != "Tarjeta") viewModel.tipoTarjeta = ""
                showPaymentSheet = false
            },
        )
    }

    if (showCardSheet) {
        PurchaseOptionsSheet(
            title = "Tipo de tarjeta",
            subtitle = "Selecciona la tarjeta utilizada",
            options = listOf(
                "Crédito" to "Compra realizada a crédito",
                "Débito" to "Cargo directo a tu cuenta",
            ),
            selected = viewModel.tipoTarjeta,
            onDismiss = { showCardSheet = false },
            onSelected = { cardType ->
                viewModel.tipoTarjeta = cardType
                showCardSheet = false
            },
        )
    }
}

@Composable
private fun PurchaseSummary(
    cost: String,
    supplier: String,
    paymentType: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PurchaseGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(21.dp)) {
            Text(
                text = "TOTAL DE LA COMPRA",
                color = Color(0xFF9BC3B1),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "$${cost.ifBlank { "0.00" }}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryPill(
                    text = supplier.ifBlank { "Sin proveedor" },
                    modifier = Modifier.weight(1f),
                )
                SummaryPill(
                    text = paymentType.ifBlank { "Pago pendiente" },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SummaryPill(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 11.dp, vertical = 9.dp),
        color = Color(0xFFD8E7E0),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ReceiptPhotoCard(
    bitmap: Bitmap?,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = Color(0xFFE7F2EB),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = PurchaseGreen,
                            modifier = Modifier.size(21.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comprobante de compra",
                        color = PurchaseText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Fotografía el ticket o factura",
                        color = PurchaseMuted,
                        fontSize = 11.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Comprobante capturado",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(17.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onTakePhoto,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(13.dp),
                    ) {
                        Text("Repetir foto", color = PurchaseGreen)
                    }
                    TextButton(
                        onClick = onRemovePhoto,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Eliminar", color = Color(0xFF9D3626))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            color = Color(0xFFF4F7F5),
                            shape = RoundedCornerShape(17.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFF91A39A),
                            modifier = Modifier.size(30.dp),
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Text(
                            text = "Aún no hay una imagen",
                            color = PurchaseMuted,
                            fontSize = 12.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onTakePhoto,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PurchaseGreen,
                    ),
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar fotografía", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PurchaseSection(
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
                        color = PurchaseText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = subtitle,
                        color = PurchaseMuted,
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
private fun PurchaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction,
    prefix: String? = null,
) {
    val prefixContent: (@Composable () -> Unit)? = if (prefix == null) {
        null
    } else {
        { Text(prefix) }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        prefix = prefixContent,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        shape = RoundedCornerShape(15.dp),
        colors = purchaseFieldColors(),
    )
}

@Composable
private fun PurchaseSelector(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        trailingIcon = {
            TextButton(onClick = onClick) {
                Text(
                    text = "Elegir",
                    color = PurchaseGreen,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        shape = RoundedCornerShape(15.dp),
        colors = purchaseFieldColors(),
    )
}

@Composable
private fun purchaseFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurchaseGreen,
    unfocusedBorderColor = PurchaseBorder,
    focusedLabelColor = PurchaseGreen,
    cursorColor = PurchaseGreen,
    focusedContainerColor = Color(0xFFF9FBFA),
    unfocusedContainerColor = Color(0xFFF9FBFA),
)

@Composable
private fun PurchaseSaveBar(
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
                containerColor = PurchaseLime,
                contentColor = PurchaseDark,
                disabledContainerColor = Color(0xFFDDE9D9),
                disabledContentColor = PurchaseMuted,
            ),
        ) {
            if (saving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PurchaseGreen,
                    strokeWidth = 2.5.dp,
                )
                Spacer(modifier = Modifier.width(9.dp))
                Text("Registrando compra…", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(9.dp))
                Text("Registrar compra", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseOptionsSheet(
    title: String,
    subtitle: String,
    options: List<Pair<String, String>>,
    selected: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
        ) {
            Text(
                text = title,
                color = PurchaseText,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = subtitle,
                color = PurchaseMuted,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(18.dp))
            options.forEach { (value, description) ->
                val isSelected = value == selected
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    onClick = { onSelected(value) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFFE7F2EB) else Color(0xFFF8FAF9),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isSelected) PurchaseLime else PurchaseBorder,
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Text(
                                    text = "✓",
                                    color = PurchaseDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(13.dp))
                        Column {
                            Text(
                                text = value,
                                color = PurchaseText,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = description,
                                color = PurchaseMuted,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatPurchaseDate(input: String): String {
    val digits = input.filter { character -> character.isDigit() }.take(8)
    return buildString {
        digits.forEachIndexed { index, character ->
            if (index == 2 || index == 4) append('/')
            append(character)
        }
    }
}