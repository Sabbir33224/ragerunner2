package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CoinIcon
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun MainMenuScreen(viewModel: RageViewModel) {
    val progress by viewModel.userProgress.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C0C12), Color(0xFF1B070A), Color(0xFF0C0C12))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP HEADER STATS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coins Counter
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF161622))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoinIcon(size = 20.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${progress.coins}",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Total Deaths
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF161622))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DEATHS: ", color = Color(0xFF8E95A5), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${progress.totalDeaths}", color = Color(0xFFFF2A42), fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }

            // CENTER LOGO BANNER
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RAGE RUNNER",
                    color = Color(0xFFFF2A42),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "THE ULTIMATE TRAP PLATFORMER",
                    color = Color(0xFFFF8C00),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // MAIN ACTION BUTTONS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SINGLE PLAYER
                Button(
                    onClick = { viewModel.navigateTo(ScreenState.LEVEL_SELECT) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A42)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_single_player")
                ) {
                    Text("SINGLE PLAYER", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }

                // 2-PLAYER LOCAL CO-OP
                Button(
                    onClick = { viewModel.startLevel(1, isTwoPlayer = true) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_two_player")
                ) {
                    Text("2-PLAYER CO-OP", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }

                // DAILY CHALLENGE
                Button(
                    onClick = { viewModel.navigateTo(ScreenState.DAILY_CHALLENGE) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_daily_challenge")
                ) {
                    Text("DAILY CHALLENGE", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            // BOTTOM NAVIGATION BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = RageIcons.Shop,
                    label = "Shop",
                    testTag = "nav_shop",
                    onClick = { viewModel.navigateTo(ScreenState.SHOP) }
                )

                BottomNavItem(
                    icon = RageIcons.Achievement,
                    label = "Badges",
                    testTag = "nav_achievements",
                    onClick = { viewModel.navigateTo(ScreenState.ACHIEVEMENTS) }
                )

                BottomNavItem(
                    icon = RageIcons.Play,
                    label = "Editor",
                    testTag = "nav_editor",
                    onClick = { viewModel.navigateTo(ScreenState.LEVEL_EDITOR) }
                )

                BottomNavItem(
                    icon = RageIcons.Settings,
                    label = "Settings",
                    testTag = "nav_settings",
                    onClick = { viewModel.navigateTo(ScreenState.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF161622))
                .border(1.dp, Color(0xFF28283E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            VectorIcon(imageVector = icon, contentDescription = label, size = 22.dp, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color(0xFF8E95A5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
