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
import com.rstech.inventory.presentation.profile.ChangePasswordUiEvent
import com.rstech.inventory.presentation.profile.ChangePasswordViewModel
import kotlinx.coroutines.flow.collectLatest

private val PasswordGreen = Color(0xFF173D30)
private val PasswordDark = Color(0xFF10271F)
private val PasswordLime = Color(0xFFB7F25C)
private val PasswordText = Color(0xFF17201C)
private val PasswordMuted = Color(0xFF748079)
private val PasswordBorder = Color(0xFFDCE5E0)

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showCurrentPassword by rememberSaveable { mutableStateOf(false) }
    var showNewPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }
    var uiMessage by remember { mutableStateOf<String?>(null) }

    val newPasswordIsValid = viewModel.passwordNueva.length >= 6
    val passwordsMatch = viewModel.passwordNueva == confirmPassword
    val passwordIsDifferent =
        viewModel.passwordNueva != viewModel.passwordActual
    val canSave = viewModel.passwordActual.isNotBlank() &&
            newPasswordIsValid &&
            confirmPassword.isNotBlank() &&
            passwordsMatch &&
            passwordIsDifferent &&
            !viewModel.cargando

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ChangePasswordUiEvent.ShowMessage -> {
                    uiMessage = event.message
                }

                is ChangePasswordUiEvent.PasswordUpdated -> {
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
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = PasswordGreen,
                        modifier = Modifier.size(27.dp),
                    )
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Protege tu cuenta",
                    color = PasswordText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Crea una contraseña segura y fácil de recordar.",
                    color = PasswordMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        },
        text = {
            PasswordForm(
                currentPassword = viewModel.passwordActual,
                onCurrentPasswordChange = {
                    viewModel.passwordActual = it
                    uiMessage = null
                },
                newPassword = viewModel.passwordNueva,
                onNewPasswordChange = {
                    viewModel.passwordNueva = it
                    uiMessage = null
                },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = {
                    confirmPassword = it
                    uiMessage = null
                },
                showCurrentPassword = showCurrentPassword,
                onToggleCurrentPassword = {
                    showCurrentPassword = !showCurrentPassword
                },
                showNewPassword = showNewPassword,
                onToggleNewPassword = {
                    showNewPassword = !showNewPassword
                },
                showConfirmPassword = showConfirmPassword,
                onToggleConfirmPassword = {
                    showConfirmPassword = !showConfirmPassword
                },
                newPasswordIsValid = newPasswordIsValid,
                passwordsMatch = passwordsMatch,
                passwordIsDifferent = passwordIsDifferent,
                uiMessage = uiMessage,
                loading = viewModel.cargando,
                onSubmit = {
                    if (canSave) viewModel.cambiarPassword()
                },
            )
        },
        confirmButton = {
            Button(
                onClick = viewModel::cambiarPassword,
                enabled = canSave,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PasswordLime,
                    contentColor = PasswordDark,
                    disabledContainerColor = Color(0xFFDDE9D9),
                    disabledContentColor = PasswordMuted,
                ),
            ) {
                if (viewModel.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = PasswordGreen,
                        strokeWidth = 2.3.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando…", fontWeight = FontWeight.Bold)
                } else {
                    Text("Actualizar contraseña", fontWeight = FontWeight.Bold)
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
                    color = PasswordGreen,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

@Composable
private fun PasswordForm(
    currentPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    showCurrentPassword: Boolean,
    onToggleCurrentPassword: () -> Unit,
    showNewPassword: Boolean,
    onToggleNewPassword: () -> Unit,
    showConfirmPassword: Boolean,
    onToggleConfirmPassword: () -> Unit,
    newPasswordIsValid: Boolean,
    passwordsMatch: Boolean,
    passwordIsDifferent: Boolean,
    uiMessage: String?,
    loading: Boolean,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        PasswordField(
            value = currentPassword,
            onValueChange = onCurrentPasswordChange,
            label = "Contraseña actual",
            visible = showCurrentPassword,
            onToggleVisibility = onToggleCurrentPassword,
            enabled = !loading,
            imeAction = ImeAction.Next,
        )

        PasswordField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = "Nueva contraseña",
            visible = showNewPassword,
            onToggleVisibility = onToggleNewPassword,
            enabled = !loading,
            imeAction = ImeAction.Next,
        )

        PasswordField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirmar contraseña",
            visible = showConfirmPassword,
            onToggleVisibility = onToggleConfirmPassword,
            enabled = !loading,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSubmit()
                },
            ),
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
                text = "Tu nueva contraseña debe:",
                color = PasswordText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            PasswordRequirement(
                text = "Tener al menos 6 caracteres",
                completed = newPasswordIsValid,
            )
            PasswordRequirement(
                text = "Ser diferente a la contraseña actual",
                completed = newPassword.isNotEmpty() && passwordIsDifferent,
            )
            PasswordRequirement(
                text = "Coincidir con la confirmación",
                completed = confirmPassword.isNotEmpty() && passwordsMatch,
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
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    enabled: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
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
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = PasswordGreen,
                modifier = Modifier.size(20.dp),
            )
        },
        trailingIcon = {
            TextButton(
                onClick = onToggleVisibility,
                enabled = enabled,
            ) {
                Text(
                    text = if (visible) "Ocultar" else "Ver",
                    color = PasswordGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PasswordGreen,
            unfocusedBorderColor = PasswordBorder,
            focusedLabelColor = PasswordGreen,
            cursorColor = PasswordGreen,
            focusedContainerColor = Color(0xFFF9FBFA),
            unfocusedContainerColor = Color(0xFFF9FBFA),
            disabledContainerColor = Color(0xFFF4F7F5),
        ),
    )
}

@Composable
private fun PasswordRequirement(
    text: String,
    completed: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(17.dp)
                .background(
                    color = if (completed) PasswordLime else PasswordBorder,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (completed) "✓" else "·",
                color = PasswordDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (completed) PasswordGreen else PasswordMuted,
            fontSize = 11.sp,
            fontWeight = if (completed) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}