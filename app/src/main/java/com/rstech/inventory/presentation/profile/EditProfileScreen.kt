package com.rstech.inventory.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

private val EditNexoGreen = Color(0xFF173D30)
private val EditNexoDark = Color(0xFF10271F)
private val EditNexoLime = Color(0xFFB7F25C)
private val EditNexoBackground = Color(0xFFF4F7F5)
private val EditNexoText = Color(0xFF17201C)
private val EditNexoMuted = Color(0xFF748079)
private val EditNexoBorder = Color(0xFFDCE5E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onNavigateBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is EditProfileUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is EditProfileUiEvent.ProfileUpdated -> {
                    snackbarHostState.showSnackbar("Perfil actualizado")
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        containerColor = EditNexoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar perfil",
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
                    containerColor = EditNexoGreen,
                    scrolledContainerColor = EditNexoGreen,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!viewModel.cargando) {
                SaveProfileBar(
                    saving = viewModel.guardando,
                    onSave = viewModel::guardarCambios,
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            if (viewModel.cargando) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = EditNexoGreen)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Preparando tu perfil…",
                        color = EditNexoMuted,
                        fontSize = 14.sp,
                    )
                }
            } else {
                EditProfileForm(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun EditProfileForm(viewModel: EditProfileViewModel) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EditProfileHeader(
            name = viewModel.nombre,
            username = viewModel.usuario,
        )

        Column {
            Text(
                text = "DATOS PERSONALES",
                color = Color(0xFF4A755C),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Mantén actualizada la información que identifica tu cuenta.",
                color = EditNexoMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NexoTextField(
                    value = viewModel.nombre,
                    onValueChange = { viewModel.nombre = it },
                    label = "Nombre completo",
                    supportingText = "Así aparecerás dentro de Nexo",
                    icon = Icons.Default.Person,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                NexoTextField(
                    value = viewModel.usuario,
                    onValueChange = { value ->
                        viewModel.usuario = value.filterNot { character ->
                            character.isWhitespace()
                        }
                    },
                    label = "Nombre de usuario",
                    supportingText = "Sin espacios; puedes usar letras, números y guion bajo",
                    icon = Icons.Default.Person,
                    prefix = "@",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                NexoTextField(
                    value = viewModel.edad,
                    onValueChange = { value ->
                        viewModel.edad = value
                            .filter { character -> character.isDigit() }
                            .take(3)
                    },
                    label = "Edad",
                    supportingText = "Usamos este dato para personalizar tu experiencia",
                    icon = Icons.Default.Edit,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                )
            }
        }

        PrivacyNotice()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EditProfileHeader(
    name: String,
    username: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = EditNexoGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(EditNexoLime),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = editInitialsOf(name),
                    color = EditNexoDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.ifBlank { "Tu perfil" },
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (username.isBlank()) {
                        "Personaliza tus datos"
                    } else {
                        "@$username"
                    },
                    color = Color(0xFFB7CCC4),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = EditNexoLime,
                        modifier = Modifier.size(19.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NexoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    supportingText: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    prefix: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        supportingText = {
            Text(
                text = supportingText,
                color = EditNexoMuted,
                fontSize = 11.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EditNexoGreen,
            )
        },
        prefix = prefix?.let {
            {
                Text(
                    text = it,
                    color = EditNexoMuted,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EditNexoGreen,
            unfocusedBorderColor = EditNexoBorder,
            focusedLabelColor = EditNexoGreen,
            cursorColor = EditNexoGreen,
            focusedContainerColor = Color(0xFFF9FBFA),
            unfocusedContainerColor = Color(0xFFF9FBFA),
        ),
    )
}

@Composable
private fun PrivacyNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE7F2EB),
                shape = RoundedCornerShape(18.dp),
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = EditNexoGreen,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Tus datos están protegidos",
                color = EditNexoText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Solo utilizamos esta información para administrar tu cuenta.",
                color = EditNexoMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun SaveProfileBar(
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
                containerColor = EditNexoLime,
                contentColor = EditNexoDark,
                disabledContainerColor = Color(0xFFDDE9D9),
                disabledContentColor = EditNexoMuted,
            ),
        ) {
            if (saving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = EditNexoGreen,
                    strokeWidth = 2.5.dp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Guardando…", fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(9.dp))
                Text("Guardar cambios", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

private fun editInitialsOf(name: String): String {
    val initials = name
        .trim()
        .split(Regex("\\s+"))
        .filter { word -> word.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")

    return initials.ifBlank { "NP" }
}
