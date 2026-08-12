package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SkinItem
import com.example.ui.components.CoinIcon
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun ShopScreen(viewModel: RageViewModel) {
    val context = LocalContext.current
    val progress by viewModel.userProgress.collectAsState()
    val skins = viewModel.repository.defaultSkinsList

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C0C12), Color(0xFF140F20), Color(0xFF0C0C12))
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text("SKIN SHOP", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF161622))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoinIcon(size = 20.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${progress.coins}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(skins) { skin ->
                    val isUnlocked = progress.unlockedSkins.contains(skin.id)
                    val isSelected = progress.activeSkin == skin.id

                    SkinCard(
                        skin = skin,
                        isUnlocked = isUnlocked,
                        isSelected = isSelected,
                        onAction = {
                            if (isUnlocked) {
                                viewModel.selectSkin(skin.id)
                            } else {
                                val success = viewModel.purchaseSkin(skin.id, skin.price)
                                if (success) {
                                    Toast.makeText(context, "Unlocked ${skin.name}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Not enough coins!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SkinCard(
    skin: SkinItem,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onAction: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF00E5FF) else Color(0xFF28283E)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161622))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("skin_card_${skin.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Skin Color Preview Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(skin.mainColor)
                        .border(2.dp, skin.accentColor, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(skin.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (!isUnlocked) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CoinIcon(size = 14.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${skin.price}", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isSelected -> Color(0xFF00E5FF)
                        isUnlocked -> Color(0xFF222234)
                        else -> Color(0xFFFF2A42)
                    }
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = when {
                        isSelected -> "EQUIPPED"
                        isUnlocked -> "EQUIP"
                        else -> "BUY"
                    },
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
