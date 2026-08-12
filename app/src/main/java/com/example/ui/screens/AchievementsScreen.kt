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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.AchievementEntity
import com.example.model.AchievementItem
import com.example.ui.components.CoinIcon
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun AchievementsScreen(viewModel: RageViewModel) {
    val achEntities by viewModel.repository.achievementProgressFlow.collectAsState(initial = emptyList())
    val defaultList = viewModel.repository.defaultAchievementsList

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C0C12), Color(0xFF1B0C03), Color(0xFF0C0C12))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                Text("ACHIEVEMENTS", color = Color(0xFFFF8C00), fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(defaultList) { def ->
                    val entity = achEntities.find { it.achievementId == def.id }
                        ?: AchievementEntity(achievementId = def.id)

                    val prog = entity.progress
                    val isUnlocked = entity.isUnlocked || prog >= def.maxProgress

                    AchievementCard(item = def, currentProg = prog, isUnlocked = isUnlocked)
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    item: AchievementItem,
    currentProg: Int,
    isUnlocked: Boolean
) {
    val borderColor = if (isUnlocked) Color(0xFFFF8C00) else Color(0xFF383850)
    val progressFloat = (currentProg.toFloat() / item.maxProgress.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161622))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("ach_card_${item.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) Color(0xFFFF8C00).copy(alpha = 0.2f) else Color(0xFF222234)),
                contentAlignment = Alignment.Center
            ) {
                VectorIcon(
                    imageVector = RageIcons.Achievement,
                    contentDescription = "Ach Icon",
                    size = 26.dp,
                    tint = if (isUnlocked) Color(0xFFFFD700) else Color(0xFF8E95A5)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(item.description, color = Color(0xFF8E95A5), fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFFF8C00),
                    trackColor = Color(0xFF222234)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                CoinIcon(size = 18.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("+${item.rewardCoins}", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
