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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlatformType
import com.example.model.TrapType
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun TwoPlayerGameScreen(viewModel: RageViewModel) {
    val engine by viewModel.activeGameEngine.collectAsState()
    val levelData by viewModel.activeLevelData.collectAsState()

    if (engine == null || levelData == null) return

    val activeEngine = engine!!
    val isGameOver by activeEngine.isGameOver.collectAsState()
    val isVictory by activeEngine.isVictory.collectAsState()
    val cameraX by activeEngine.cameraX.collectAsState()

    var frameTick by remember { mutableStateOf(0L) }

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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ox = cameraX

            activeEngine.levelData.platforms.forEach { plat ->
                val pr = plat.rect
                drawRoundRect(
                    color = Color(0xFF2C2C3E),
                    topLeft = Offset(pr.left - ox, pr.top),
                    size = Size(pr.width, pr.height),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            activeEngine.levelData.traps.forEach { trap ->
                val tr = trap.rect
                drawRect(
                    color = Color(0xFFFF2A42),
                    topLeft = Offset(tr.left - ox, tr.top),
                    size = Size(tr.width, tr.height)
                )
            }

            val p1 = activeEngine.player1
            drawRoundRect(
                color = Color(0xFFFF2A42),
                topLeft = Offset(p1.x - ox, p1.y),
                size = Size(p1.width, p1.height),
                cornerRadius = CornerRadius(8f, 8f)
            )

            val p2 = activeEngine.player2
            drawRoundRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(p2.x - ox, p2.y),
                size = Size(p2.width, p2.height),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }

        // PLAYER 2 CONTROLS (TOP)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xBB00E5FF))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    activeEngine.handleMove(2, -1f)
                                    tryAwaitRelease()
                                    activeEngine.handleMove(2, 0f)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("P2 ◄", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xBB00E5FF))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    activeEngine.handleMove(2, 1f)
                                    tryAwaitRelease()
                                    activeEngine.handleMove(2, 0f)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("P2 ►", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E5FF))
                    .clickable { activeEngine.handleJump(2) },
                contentAlignment = Alignment.Center
            ) {
                Text("JUMP", color = Color.Black, fontWeight = FontWeight.Black)
            }
        }

        // PLAYER 1 CONTROLS (BOTTOM)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xBBFF2A42))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        activeEngine.handleMove(1, -1f)
                                        tryAwaitRelease()
                                        activeEngine.handleMove(1, 0f)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("P1 ◄", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xBBFF2A42))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        activeEngine.handleMove(1, 1f)
                                        tryAwaitRelease()
                                        activeEngine.handleMove(1, 0f)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("P1 ►", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF2A42))
                        .clickable { activeEngine.handleJump(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("JUMP", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }

        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE09090E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BOTH PLAYERS DIED!", color = Color(0xFFFF2A42), fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { activeEngine.restartLevel() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A42))
                    ) {
                        Text("RETRY", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
