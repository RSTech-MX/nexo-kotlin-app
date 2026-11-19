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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rstech.inventory.presentation.profile.components.ChangePasswordDialog
import com.rstech.inventory.presentation.profile.components.ChangeEmailDialog

private val NexoGreen = Color(0xFF173D30)
private val NexoDark = Color(0xFF10271F)
private val NexoLime = Color(0xFFB7F25C)
private val NexoBackground = Color(0xFFF4F7F5)
private val NexoText = Color(0xFF17201C)
private val NexoMuted = Color(0xFF748079)
private val NexoBorder = Color(0xFFE1E8E4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onOpenChangePasswordDialog: () -> Unit,
    onOpenChangedEmaiDialog: () -> Unit,
    //onNavigateToChangeEmail: () -> Unit,
) {
    val state by viewModel.profileState.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = NexoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi perfil",
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
                    containerColor = NexoGreen,
                    scrolledContainerColor = NexoGreen,
                ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (val currentState = state) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(color = NexoGreen)
                }

                is ProfileState.Unauthenticated -> {
                    EmptyProfileMessage(
                        title = "No hay una sesión activa",
                        message = "Inicia sesión para consultar los datos de tu perfil.",
                    )
                }

                is ProfileState.Error -> {
                    EmptyProfileMessage(
                        title = "No pudimos cargar tu perfil",
                        message = currentState.message,
                    )
                }

                is ProfileState.Success -> {
                    val data = currentState.data
                    ProfileContent(
                        name = data["nombre"]?.toString().orEmpty(),
                        username = data["nombre_usuario"]?.toString().orEmpty(),
                        email = data["correo"]?.toString().orEmpty(),
                        age = data["edad"]?.toString().orEmpty(),
                        onEditProfile = onNavigateToEditProfile,
                        onChangePassword = {
                            // Conserva el evento externo por compatibilidad con tu NavHost.
                            onOpenChangePasswordDialog()
                            showPasswordDialog = true
                        },
                        onChangeEmail = {
                            // Conserva el evento externo por compatibilidad con tu NavHost.
                            onOpenChangedEmaiDialog()
                            showEmailDialog = true
                        },
                        //onChangeEmail = onNavigateToChangeEmail,
                    )
                }
            }

            if (showPasswordDialog) {
                ChangePasswordDialog(
                    onDismiss = { showPasswordDialog = false },
                )
            }

            if (showEmailDialog) {
                ChangeEmailDialog(
                    onDismiss = { showEmailDialog = false },
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    name: String,
    username: String,
    email: String,
    age: String,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onChangeEmail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ProfileHero(
            name = name,
            username = username,
            email = email,
            onEditProfile = onEditProfile,
        )

        Text(
            text = "INFORMACIÓN PERSONAL",
            color = Color(0xFF4A755C),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "Nombre completo",
                value = name.ifBlank { "Sin registrar" },
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = NexoBorder,
            )
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "Nombre de usuario",
                value = username.ifBlank { "Sin registrar" },
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = NexoBorder,
            )
            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Correo electrónico",
                value = email.ifBlank { "Sin registrar" },
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = NexoBorder,
            )
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "Edad",
                value = if (age.isBlank()) "Sin registrar" else "$age años",
            )
        }

        Text(
            text = "CUENTA Y SEGURIDAD",
            color = Color(0xFF4A755C),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp,
        )

        AccountAction(
            icon = Icons.Default.Lock,
            title = "Cambiar contraseña",
            subtitle = "Actualiza tu clave de acceso",
            onClick = onChangePassword,
        )
        AccountAction(
            icon = Icons.Default.Email,
            title = "Cambiar correo",
            subtitle = "Administra tu correo de inicio de sesión",
            onClick = onChangeEmail,
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ProfileHero(
    name: String,
    username: String,
    email: String,
    onEditProfile: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = NexoGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(NexoLime),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initialsOf(name),
                    color = NexoDark,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = name.ifBlank { "Tu nombre" },
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = when {
                    username.isNotBlank() -> "@$username"
                    email.isNotBlank() -> email
                    else -> "Completa tu información"
                },
                color = Color(0xFFB7CCC4),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexoLime,
                    contentColor = NexoDark,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                )
                Spacer(modifier = Modifier.width(9.dp))
                Text("Editar perfil", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE7F2EB),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NexoGreen,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = NexoMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                color = NexoText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AccountAction(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE7F2EB),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NexoGreen,
                        modifier = Modifier.size(21.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = NexoText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Text(text = subtitle, color = NexoMuted, fontSize = 12.sp)
            }
            TextButton(onClick = onClick) {
                Text("Abrir", color = NexoGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyProfileMessage(
    title: String,
    message: String,
) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color(0xFFE7F2EB),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = NexoGreen,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = NexoText,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            color = NexoMuted,
            fontSize = 14.sp,
        )
    }
}

private fun initialsOf(name: String): String {
    val initials = name
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")

    return initials.ifBlank { "NP" }
}
