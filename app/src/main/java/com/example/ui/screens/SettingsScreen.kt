package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RageIcons
import com.example.ui.components.VectorIcon
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun SettingsScreen(viewModel: RageViewModel) {
    val userProgress by viewModel.userProgress.collectAsState()

    var musicVol by remember(userProgress) { mutableFloatStateOf(userProgress.musicVolume) }
    var sfxVol by remember(userProgress) { mutableFloatStateOf(userProgress.sfxVolume) }
    var vibration by remember(userProgress) { mutableStateOf(userProgress.vibrationEnabled) }
    var btnSize by remember(userProgress) { mutableFloatStateOf(userProgress.buttonSizeDp.toFloat()) }
    var btnOpacity by remember(userProgress) { mutableFloatStateOf(userProgress.buttonOpacity) }
    var leftHanded by remember(userProgress) { mutableStateOf(userProgress.leftHanded) }

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
                .verticalScroll(rememberScrollState())
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
                Text("SETTINGS & CONTROLS", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingSection(title = "AUDIO & HAPTICS") {
                SettingSlider(
                    label = "Music Volume",
                    value = musicVol,
                    onValueChange = {
                        musicVol = it
                        viewModel.updateSettings(musicVol, sfxVol, vibration, btnSize.toInt(), btnOpacity, leftHanded)
                    }
                )

                SettingSlider(
                    label = "SFX Volume",
                    value = sfxVol,
                    onValueChange = {
                        sfxVol = it
                        viewModel.updateSettings(musicVol, sfxVol, vibration, btnSize.toInt(), btnOpacity, leftHanded)
                    }
                )

                SettingSwitch(
                    label = "Vibration Feedback",
                    checked = vibration,
                    onCheckedChange = {
                        vibration = it
                        viewModel.updateSettings(musicVol, sfxVol, vibration, btnSize.toInt(), btnOpacity, leftHanded)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SettingSection(title = "TOUCH CONTROLS") {
                SettingSlider(
                    label = "Button Size (${btnSize.toInt()} dp)",
                    value = ((btnSize - 40f) / 60f).coerceIn(0f, 1f),
                    onValueChange = {
                        btnSize = 40f + it * 60f
                        viewModel.updateSettings(musicVol, sfxVol, vibration, btnSize.toInt(), btnOpacity, leftHanded)
                    }
                )

                SettingSlider(
                    label = "Control Opacity (${(btnOpacity * 100).toInt()}%)",
                    value = btnOpacity,
                    onValueChange = {
                        btnOpacity = it
                        viewModel.updateSettings(musicVol, sfxVol, vibration, btnSize.toInt(), btnOpacity, leftHanded)
                    }
                )

                SettingSwitch(
                    label = "Left-Handed Mode",
                    checked = leftHanded,
                    onCheckedChange = {
                        leftHanded = it
                        viewModel.updateSettings(musicVol, sfxVol, vibration, btnSize.toInt(), btnOpacity, leftHanded)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("RAGE RUNNER v1.0.0", color = Color(0xFF8E95A5), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Developed by smxcobra", color = Color(0xFFFF2A42), fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF161622))
            .border(1.dp, Color(0xFF28283E), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, color = Color(0xFFFF8C00), fontSize = 14.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SettingSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF2A42),
                activeTrackColor = Color(0xFFFF2A42),
                inactiveTrackColor = Color(0xFF222234)
            )
        )
    }
}

@Composable
private fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFF2A42),
                uncheckedThumbColor = Color(0xFF8E95A5),
                uncheckedTrackColor = Color(0xFF222234)
            )
        )
    }
}
