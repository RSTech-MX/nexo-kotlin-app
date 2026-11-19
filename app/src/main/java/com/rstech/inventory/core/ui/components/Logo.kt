package com.rstech.inventory.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Logo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 11.dp,
                    topEnd = 11.dp,
                    bottomEnd = 11.dp,
                    bottomStart = 0.dp
                )
            )
            .background(Color(0xFFB7F25C)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "N",
            style = TextStyle(
                color = Color(0xFF10271F),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}