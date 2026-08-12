package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun LevelSelectScreen(viewModel: RageViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    var selectedWorld by remember { mutableIntStateOf(1) }

    val worlds = listOf(
        "W1: NEON CITY",
        "W2: TOXIC FACTORY",
        "W3: MAGMA CAVERNS",
        "W4: CYBER VOID",
        "W5: TROLL HEAVEN"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C0C12), Color(0xFF141420), Color(0xFF0C0C12))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                Spacer(modifier = Modifier.width(16.dp))
                Text("SELECT STAGE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // World Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedWorld - 1,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFFF2A42),
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedWorld - 1]),
                        color = Color(0xFFFF2A42)
                    )
                }
            ) {
                worlds.forEachIndexed { index, title ->
                    val worldNum = index + 1
                    Tab(
                        selected = selectedWorld == worldNum,
                        onClick = { selectedWorld = worldNum },
                        text = {
                            Text(
                                title,
                                color = if (selectedWorld == worldNum) Color(0xFFFF2A42) else Color(0xFF8E95A5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grid of 20 Levels per World
            val startLevelId = (selectedWorld - 1) * 20 + 1
            val endLevelId = selectedWorld * 20

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(20) { index ->
                    val levelId = startLevelId + index
                    val isUnlocked = levelId <= progress.levelsCompleted + 1

                    LevelTile(
                        levelId = levelId,
                        isUnlocked = isUnlocked,
                        onClick = {
                            if (isUnlocked) {
                                viewModel.startLevel(levelId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelTile(
    levelId: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isUnlocked) Color(0xFF161622) else Color(0xFF0F0F18)
    val borderColor = if (isUnlocked) Color(0xFFFF2A42) else Color(0xFF222234)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_tile_$levelId"),
        contentAlignment = Alignment.Center
    ) {
        if (isUnlocked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$levelId",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
        } else {
            VectorIcon(
                imageVector = RageIcons.Lock,
                contentDescription = "Locked",
                size = 20.dp,
                tint = Color(0xFF555568)
            )
        }
    }
}
