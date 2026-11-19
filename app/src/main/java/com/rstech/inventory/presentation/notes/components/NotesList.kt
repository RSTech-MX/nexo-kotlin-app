package com.rstech.inventory.presentation.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rstech.inventory.presentation.notes.NoteItem // 🟢 Importamos NoteItem del ViewModel

val FallbackColors = listOf(
    Color(0xFFE7F6ED),
    Color(0xFFFFF2D2),
    Color(0xFFF0E8F7),
    Color(0xFFE3EFFC)
)

fun noteColor(rawColor: Long?, index: Int): Color {
    return if (rawColor != null && rawColor != 0L) {
        Color(rawColor.toInt()).copy(alpha = 0.85f)
    } else {
        FallbackColors[index % FallbackColors.size]
    }
}

@Composable
fun NotesList(
    notes: List<NoteItem>, // 🟢 Actualizado a List<NoteItem>
    isLoading: Boolean,
    errorMessage: String? = null,
    onCreate: () -> Unit,
    onDeleteNote: (String) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF173D30))
        }
        return
    }

    if (!errorMessage.isNullOrEmpty()) {
        MessageCard(
            icon = Icons.Outlined.ErrorOutline,
            title = "No fue posible cargar las notas",
            description = "Comprueba tu conexión e inténtalo nuevamente."
        )
        return
    }

    if (notes.isEmpty()) {
        EmptyNotes(onCreate = onCreate)
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        notes.forEachIndexed { index, note ->
            var showDeleteDialog by remember { mutableStateOf(false) }

            NoteCard(
                title = note.titulo, // 🟢 Apunta a 'titulo' de NoteItem
                content = note.contenido, // 🟢 Apunta a 'contenido' de NoteItem
                color = noteColor(note.color, index),
                onDelete = { showDeleteDialog = true }
            )

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(22.dp),
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color(0xFFFFE9E5), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = null,
                                tint = Color(0xFFB95343)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "¿Eliminar nota?",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "La nota “${note.titulo}” se eliminará permanentemente.",
                            color = Color(0xFF5B6862)
                        )
                    },
                    confirmButton = {
                        FilledTonalButton(
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFB95343),
                                contentColor = Color.White
                            ),
                            onClick = {
                                showDeleteDialog = false
                                onDeleteNote(note.id)
                            }
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar", color = Color(0xFF77837D))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NoteCard(
    title: String,
    content: String,
    color: Color,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 155.dp)
            .background(color, RoundedCornerShape(19.dp))
            .border(1.dp, Color(0x0F1E3C30), RoundedCornerShape(19.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF638170), CircleShape)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "NOTA",
                    style = TextStyle(
                        color = Color(0xFF687970),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W700,
                        letterSpacing = 0.6.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = Color(0xFF17201C),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W800
                )
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = content,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = Color(0xFF5B6862),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.BottomEnd),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White.copy(alpha = 0.65f),
                contentColor = Color(0xFF93574B)
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "Eliminar nota",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyNotes(onCreate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFCFB), RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFCED9D3), RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(Color(0xFFE7F2EB), RoundedCornerShape(17.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.NoteAlt,
                contentDescription = null,
                tint = Color(0xFF3C7157)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Todavía no hay notas",
            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.W800)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Crea una nota para mantener todo bajo control.",
            textAlign = TextAlign.Center,
            style = TextStyle(color = Color(0xFF77837D), fontSize = 13.sp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(
            onClick = onCreate,
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF285B45))
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Crear mi primera nota")
        }
    }
}

@Composable
private fun MessageCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFECE8), RoundedCornerShape(18.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFB95343))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = TextStyle(fontWeight = FontWeight.W700))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            style = TextStyle(color = Color(0xFF7B625D), fontSize = 12.sp)
        )
    }
}