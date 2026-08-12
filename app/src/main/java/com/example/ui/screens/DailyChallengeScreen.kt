package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CoinIcon
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun DailyChallengeScreen(viewModel: RageViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C0C12), Color(0xFF201300), Color(0xFF0C0C12))
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
                Text("DAILY CHALLENGE", color = Color(0xFFFF8C00), fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            // Challenge Banner
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TODAY'S TROLL SEED", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("SEED #884920", color = Color(0xFFFF8C00), fontSize = 14.sp, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinIcon(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("2X COIN MULTIPLIER", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }

            // Play Button
            Button(
                onClick = { viewModel.startLevel(88) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("PLAY TODAY'S STAGE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
