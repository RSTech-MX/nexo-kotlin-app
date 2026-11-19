package com.rstech.inventory.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rstech.inventory.presentation.profile.ChangeEmailUiEvent
import com.rstech.inventory.presentation.profile.ChangeEmailViewModel
import kotlinx.coroutines.flow.collectLatest

private val EmailGreen = Color(0xFF173D30)
private val EmailDark = Color(0xFF10271F)
private val EmailLime = Color(0xFFB7F25C)
private val EmailText = Color(0xFF17201C)
private val EmailMuted = Color(0xFF748079)
private val EmailBorder = Color(0xFFDCE5E0)

@Composable
fun ChangeEmailDialog(
    onDismiss: () -> Unit,
    viewModel: ChangeEmailViewModel = hiltViewModel(),
) {
    var emailConfirmation by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var uiMessage by remember { mutableStateOf<String?>(null) }

    val normalizedEmail = viewModel.nuevoCorreo.trim()
    val normalizedConfirmation = emailConfirmation.trim()
    val validEmail = remember(normalizedEmail) {
        normalizedEmail.matches(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"),
        )
    }
    val emailsMatch =
        normalizedEmail.equals(normalizedConfirmation, ignoreCase = true)
    val canSave = validEmail &&
            normalizedConfirmation.isNotBlank() &&
            emailsMatch &&
            viewModel.password.isNotBlank() &&
            !viewModel.cargando

    fun submit() {
        if (!canSave) return
        viewModel.nuevoCorreo = normalizedEmail
        viewModel.cambiarCorreo()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ChangeEmailUiEvent.ShowMessage -> {
                    uiMessage = event.message
                }

                is ChangeEmailUiEvent.EmailUpdated -> {
                    onDismiss()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!viewModel.cargando) onDismiss()
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = {
            Surface(
                modifier = Modifier.size(58.dp),
                shape = CircleShape,
                color = Color(0xFFE7F2EB),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = EmailGreen,
                        modifier = Modifier.size(27.dp),
                    )
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Actualiza tu correo",
                    color = EmailText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Este será tu nuevo correo para iniciar sesión.",
                    color = EmailMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        },
        text = {
            ChangeEmailForm(
                email = viewModel.nuevoCorreo,
                onEmailChange = {
                    viewModel.nuevoCorreo = it
                    uiMessage = null
                },
                confirmation = emailConfirmation,
                onConfirmationChange = {
                    emailConfirmation = it
                    uiMessage = null
                },
                password = viewModel.password,
                onPasswordChange = {
                    viewModel.password = it
                    uiMessage = null
                },
                showPassword = showPassword,
                onTogglePassword = { showPassword = !showPassword },
                validEmail = validEmail,
                emailsMatch = emailsMatch,
                uiMessage = uiMessage,
                loading = viewModel.cargando,
                onSubmit = ::submit,
            )
        },
        confirmButton = {
            Button(
                onClick = ::submit,
                enabled = canSave,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmailLime,
                    contentColor = EmailDark,
                    disabledContainerColor = Color(0xFFDDE9D9),
                    disabledContentColor = EmailMuted,
                ),
            ) {
                if (viewModel.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = EmailGreen,
                        strokeWidth = 2.3.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizando…", fontWeight = FontWeight.Bold)
                } else {
                    Text("Actualizar correo", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !viewModel.cargando,
            ) {
                Text(
                    text = "Cancelar",
                    color = EmailGreen,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

@Composable
private fun ChangeEmailForm(
    email: String,
    onEmailChange: (String) -> Unit,
    confirmation: String,
    onConfirmationChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onTogglePassword: () -> Unit,
    validEmail: Boolean,
    emailsMatch: Boolean,
    uiMessage: String?,
    loading: Boolean,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        EmailField(
            value = email,
            onValueChange = onEmailChange,
            label = "Nuevo correo",
            enabled = !loading,
            imeAction = ImeAction.Next,
        )

        EmailField(
            value = confirmation,
            onValueChange = onConfirmationChange,
            label = "Confirmar nuevo correo",
            enabled = !loading,
            imeAction = ImeAction.Next,
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            singleLine = true,
            label = { Text("Contraseña actual") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = EmailGreen,
                    modifier = Modifier.size(20.dp),
                )
            },
            trailingIcon = {
                TextButton(
                    onClick = onTogglePassword,
                    enabled = !loading,
                ) {
                    Text(
                        text = if (showPassword) "Ocultar" else "Ver",
                        color = EmailGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSubmit()
                },
            ),
            shape = RoundedCornerShape(15.dp),
            colors = nexoEmailFieldColors(),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF4F7F5),
                    shape = RoundedCornerShape(15.dp),
                )
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(
                text = "Antes de continuar:",
                color = EmailText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            EmailRequirement(
                text = "El correo tiene un formato válido",
                completed = email.isNotBlank() && validEmail,
            )
            EmailRequirement(
                text = "Los dos correos coinciden",
                completed = confirmation.isNotBlank() && emailsMatch,
            )
            EmailRequirement(
                text = "Ingresaste tu contraseña actual",
                completed = password.isNotBlank(),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE7F2EB),
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = EmailGreen,
                modifier = Modifier.size(17.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Es posible que debas verificar el nuevo correo antes de usarlo.",
                color = EmailGreen,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (uiMessage != null) {
            Text(
                text = uiMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFECE8),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                color = Color(0xFF9D3626),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    imeAction: ImeAction,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = EmailGreen,
                modifier = Modifier.size(20.dp),
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
        ),
        shape = RoundedCornerShape(15.dp),
        colors = nexoEmailFieldColors(),
    )
}

@Composable
private fun nexoEmailFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EmailGreen,
    unfocusedBorderColor = EmailBorder,
    focusedLabelColor = EmailGreen,
    cursorColor = EmailGreen,
    focusedContainerColor = Color(0xFFF9FBFA),
    unfocusedContainerColor = Color(0xFFF9FBFA),
    disabledContainerColor = Color(0xFFF4F7F5),
)

@Composable
private fun EmailRequirement(
    text: String,
    completed: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(17.dp)
                .background(
                    color = if (completed) EmailLime else EmailBorder,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (completed) "✓" else "·",
                color = EmailDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (completed) EmailGreen else EmailMuted,
            fontSize = 11.sp,
            fontWeight = if (completed) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
