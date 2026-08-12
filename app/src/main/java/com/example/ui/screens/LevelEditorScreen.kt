package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.model.CoinItem
import com.example.model.LevelData
import com.example.model.Platform
import com.example.model.PlatformType
import com.example.model.Trap
import com.example.model.TrapType
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

enum class EditorObjectType {
    PLATFORM_SOLID,
    PLATFORM_FAKE,
    PLATFORM_BOUNCE,
    SPIKE,
    ROTATING_SAW,
    COIN
}

@Composable
fun LevelEditorScreen(viewModel: RageViewModel) {
    val context = LocalContext.current
    var selectedTool by remember { mutableStateOf(EditorObjectType.PLATFORM_SOLID) }

    val editorPlatforms = remember { mutableStateListOf<Platform>() }
    val editorTraps = remember { mutableStateListOf<Trap>() }
    val editorCoins = remember { mutableStateListOf<CoinItem>() }

    LaunchedEffect(Unit) {
        if (editorPlatforms.isEmpty()) {
            editorPlatforms.add(Platform(0f, 600f, 800f, 640f, PlatformType.SOLID))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C12))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedTool) {
                    detectTapGestures { offset ->
                        val gridX = (offset.x / 40f).toInt() * 40f
                        val gridY = (offset.y / 40f).toInt() * 40f

                        when (selectedTool) {
                            EditorObjectType.PLATFORM_SOLID -> {
                                editorPlatforms.add(Platform(gridX, gridY, gridX + 120f, gridY + 30f, PlatformType.SOLID))
                            }
                            EditorObjectType.PLATFORM_FAKE -> {
                                editorPlatforms.add(Platform(gridX, gridY, gridX + 120f, gridY + 30f, PlatformType.FAKE))
                            }
                            EditorObjectType.PLATFORM_BOUNCE -> {
                                editorPlatforms.add(Platform(gridX, gridY, gridX + 120f, gridY + 30f, PlatformType.BOUNCE))
                            }
                            EditorObjectType.SPIKE -> {
                                editorTraps.add(Trap(gridX, gridY, gridX + 40f, gridY + 40f, TrapType.STATIC_SPIKE))
                            }
                            EditorObjectType.ROTATING_SAW -> {
                                editorTraps.add(Trap(gridX, gridY, gridX + 60f, gridY + 60f, TrapType.ROTATING_SAW))
                            }
                            EditorObjectType.COIN -> {
                                editorCoins.add(CoinItem(gridX + 20f, gridY + 20f))
                            }
                        }
                    }
                }
        ) {
            val gridSpacing = 40f
            for (x in 0..(size.width / gridSpacing).toInt()) {
                drawLine(
                    color = Color(0xFF1E1E2C),
                    start = Offset(x * gridSpacing, 0f),
                    end = Offset(x * gridSpacing, size.height)
                )
            }
            for (y in 0..(size.height / gridSpacing).toInt()) {
                drawLine(
                    color = Color(0xFF1E1E2C),
                    start = Offset(0f, y * gridSpacing),
                    end = Offset(size.width, y * gridSpacing)
                )
            }

            editorPlatforms.forEach { plat ->
                val color = when (plat.type) {
                    PlatformType.FAKE -> Color(0xFFFF8C00)
                    PlatformType.BOUNCE -> Color(0xFF00E676)
                    else -> Color(0xFF383850)
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(plat.rect.left, plat.rect.top),
                    size = Size(plat.rect.width, plat.rect.height),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            editorTraps.forEach { trap ->
                if (trap.type == TrapType.ROTATING_SAW) {
                    drawCircle(
                        color = Color(0xFFFF2A42),
                        radius = trap.rect.width / 2f,
                        center = Offset(trap.rect.centerX, trap.rect.centerY)
                    )
                } else {
                    drawRect(
                        color = Color(0xFFFF2A42),
                        topLeft = Offset(trap.rect.left, trap.rect.top),
                        size = Size(trap.rect.width, trap.rect.height)
                    )
                }
            }

            editorCoins.forEach { coin ->
                drawCircle(color = Color(0xFFFFD700), radius = coin.radius, center = Offset(coin.x, coin.y))
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF222234))
                        .clickable { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                    contentAlignment = Alignment.Center
                ) {
                    VectorIcon(imageVector = RageIcons.Back, contentDescription = "Back", size = 22.dp, tint = Color.White)
                }

                Text("LEVEL EDITOR", color = Color(0xFFD31027), fontSize = 20.sp, fontWeight = FontWeight.Black)

                Button(
                    onClick = {
                        val customLevel = LevelData(
                            id = 999,
                            name = "CUSTOM MAP",
                            world = 1,
                            theme = "NEON_RED",
                            width = 2000f,
                            height = 1000f,
                            startX = 100f,
                            startY = 500f,
                            exitX = 700f,
                            exitY = 500f,
                            platforms = editorPlatforms.toList(),
                            traps = editorTraps.toList(),
                            coins = editorCoins.toList()
                        )
                        viewModel.startCustomLevel(customLevel)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("TEST PLAY", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161622))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(EditorObjectType.values()) { tool ->
                            ToolChip(
                                tool = tool,
                                isSelected = selectedTool == tool,
                                onClick = { selectedTool = tool }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF2A42))
                            .clickable {
                                editorPlatforms.clear()
                                editorTraps.clear()
                                editorCoins.clear()
                                Toast.makeText(context, "Cleared Canvas", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        VectorIcon(imageVector = RageIcons.Close, contentDescription = "Clear", size = 20.dp, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolChip(
    tool: EditorObjectType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val title = when (tool) {
        EditorObjectType.PLATFORM_SOLID -> "Solid"
        EditorObjectType.PLATFORM_FAKE -> "Fake"
        EditorObjectType.PLATFORM_BOUNCE -> "Bounce"
        EditorObjectType.SPIKE -> "Spike"
        EditorObjectType.ROTATING_SAW -> "Saw"
        EditorObjectType.COIN -> "Coin"
    }

    val chipColor = if (isSelected) Color(0xFFFF2A42) else Color(0xFF222234)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(chipColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("tool_chip_${tool.name}")
    ) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
