package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LevelEditorScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TwoPlayerGameScreen
import com.example.ui.theme.RageRunnerTheme
import com.example.ui.viewmodel.RageViewModel
import com.example.ui.viewmodel.ScreenState

class MainActivity : ComponentActivity() {
    private val viewModel: RageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RageRunnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF09090E)
                ) {
                    RageRunnerApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun RageRunnerApp(viewModel: RageViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    BackHandler(enabled = currentScreen != ScreenState.MAIN_MENU && currentScreen != ScreenState.SPLASH) {
        viewModel.navigateTo(ScreenState.MAIN_MENU)
    }

    when (currentScreen) {
        ScreenState.SPLASH -> SplashScreen {
            viewModel.navigateTo(ScreenState.MAIN_MENU)
        }
        ScreenState.MAIN_MENU -> MainMenuScreen(viewModel = viewModel)
        ScreenState.LEVEL_SELECT -> LevelSelectScreen(viewModel = viewModel)
        ScreenState.GAME -> GameScreen(viewModel = viewModel)
        ScreenState.TWO_PLAYER_GAME -> TwoPlayerGameScreen(viewModel = viewModel)
        ScreenState.DAILY_CHALLENGE -> DailyChallengeScreen(viewModel = viewModel)
        ScreenState.SHOP -> ShopScreen(viewModel = viewModel)
        ScreenState.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
        ScreenState.LEVEL_EDITOR -> LevelEditorScreen(viewModel = viewModel)
        ScreenState.SETTINGS -> SettingsScreen(viewModel = viewModel)
    }
}
