package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlatformType
import com.example.model.TrapType
import com.example.ui.components.CoinIcon
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun GameScreen(viewModel: RageViewModel) {
    val engine by viewModel.activeGameEngine.collectAsState()
    val levelData by viewModel.activeLevelData.collectAsState()

    if (engine == null || levelData == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF09090E)),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading Stage...", color = Color.White, fontWeight = FontWeight.Bold)
        }
        return
    }

    val activeEngine = engine!!
    val isGameOver by activeEngine.isGameOver.collectAsState()
    val isVictory by activeEngine.isVictory.collectAsState()
    val cameraX by activeEngine.cameraX.collectAsState()

    var frameTick by remember { mutableStateOf(0L) }

    // 60 FPS Engine Loop
    LaunchedEffect(activeEngine) {
        var lastTime = System.nanoTime()
        while (true) {
            withFrameNanos { frameTime ->
                val dt = ((frameTime - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = frameTime
                activeEngine.update(dt)
                frameTick = frameTime
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090E))
    ) {
        // 1. GAME CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ox = cameraX

            // Draw Background Grids
            val gridSpacing = 80f
            for (x in 0..(size.width / gridSpacing).toInt() + 2) {
                drawLine(
                    color = Color(0xFF141420),
                    start = Offset((x * gridSpacing) - (ox % gridSpacing), 0f),
                    end = Offset((x * gridSpacing) - (ox % gridSpacing), size.height)
                )
            }

            // Draw Platforms
            activeEngine.levelData.platforms.forEach { plat ->
                val platRect = plat.rect
                val color = when (plat.type) {
                    PlatformType.BOUNCE -> Color(0xFF00E676)
                    PlatformType.FAKE -> Color(0xFFFF8C00)
                    PlatformType.CONVEYOR_RIGHT -> Color(0xFF00E5FF)
                    else -> Color(0xFF2C2C3E)
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(platRect.left - ox, platRect.top),
                    size = Size(platRect.width, platRect.height),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            // Draw Traps
            activeEngine.levelData.traps.forEach { trap ->
                val tr = trap.rect
                if (trap.type == TrapType.ROTATING_SAW) {
                    drawCircle(
                        color = Color(0xFFFF2A42),
                        radius = tr.width / 2f,
                        center = Offset(tr.centerX - ox, tr.centerY)
                    )
                } else {
                    drawRect(
                        color = Color(0xFFFF2A42),
                        topLeft = Offset(tr.left - ox, tr.top),
                        size = Size(tr.width, tr.height)
                    )
                }
            }

            // Draw Coins
            activeEngine.levelData.coins.forEach { coin ->
                if (!coin.isCollected) {
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = coin.radius,
                        center = Offset(coin.x - ox, coin.y)
                    )
                }
            }

            // Draw Exit Door
            val exitX = activeEngine.levelData.exitX - ox
            val exitY = activeEngine.levelData.exitY
            drawRect(
                color = Color(0xFF00E676),
                topLeft = Offset(exitX, exitY),
                size = Size(60f, 80f)
            )

            // Draw Particles
            activeEngine.particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(p.x - ox, p.y)
                )
            }

            // Draw Player 1
            val p1 = activeEngine.player1
            drawRoundRect(
                color = Color(0xFFFF2A42),
                topLeft = Offset(p1.x - ox, p1.y),
                size = Size(p1.width, p1.height),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }

        // 2. HUD TOP BAR
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
                    .background(Color(0xFF161622))
                    .clickable { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                contentAlignment = Alignment.Center
            ) {
                VectorIcon(imageVector = RageIcons.Back, contentDescription = "Back", size = 20.dp, tint = Color.White)
            }

            Text(
                text = activeEngine.levelData.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                CoinIcon(size = 18.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("${activeEngine.coinsCollected}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
            }
        }

        // 3. TOUCH CONTROLS OVERLAY
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // LEFT / RIGHT D-PAD
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xBB161622))
                            .border(2.dp, Color(0xFF383850), CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        activeEngine.handleMove(1, -1f)
                                        tryAwaitRelease()
                                        activeEngine.handleMove(1, 0f)
                                    }
                                )
                            }
                            .testTag("btn_left"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("◄", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xBB161622))
                            .border(2.dp, Color(0xFF383850), CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        activeEngine.handleMove(1, 1f)
                                        tryAwaitRelease()
                                        activeEngine.handleMove(1, 0f)
                                    }
                                )
                            }
                            .testTag("btn_right"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("►", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }

                // JUMP BUTTON
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color(0xDDFF2A42))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { activeEngine.handleJump(1) }
                        .testTag("btn_jump"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("JUMP", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // 4. GAME OVER OVERLAY
        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE09090E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("YOU DIED!", color = Color(0xFFFF2A42), fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.onPlayerDied()
                            activeEngine.restartLevel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A42)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("INSTANT RESTART", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 5. VICTORY OVERLAY
        if (isVictory) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE09090E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("STAGE CLEAR!", color = Color(0xFF00E676), fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.onLevelCompleted(activeEngine.coinsCollected)
                            viewModel.startLevel(activeEngine.levelData.id + 1)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("NEXT LEVEL", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
