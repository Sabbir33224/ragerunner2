package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object RageIcons {
    val Back: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val Play: ImageVector = Icons.Default.PlayArrow
    val Settings: ImageVector = Icons.Default.Settings
    val Shop: ImageVector = Icons.Default.ShoppingCart
    val Achievement: ImageVector = Icons.Default.Star
    val Restart: ImageVector = Icons.Default.Refresh
    val Home: ImageVector = Icons.Default.Home
    val Close: ImageVector = Icons.Default.Close
    val Lock: ImageVector = Icons.Default.Lock
}

@Composable
fun VectorIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.White
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint
    )
}

@Composable
fun CoinIcon(size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        drawCircle(color = Color(0xFFFFD700), radius = size.toPx() / 2f)
        drawCircle(color = Color(0xFFFF8C00), radius = size.toPx() / 3f)
    }
}
