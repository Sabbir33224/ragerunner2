package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LevelDataRepository
import com.example.data.RageRepository
import com.example.engine.AudioEngine
import com.example.engine.GameEngine
import com.example.engine.VibrationManager
import com.example.model.LevelData
import com.example.model.UserProgressData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
    SPLASH, MAIN_MENU, LEVEL_SELECT, GAME, TWO_PLAYER_GAME, DAILY_CHALLENGE, SHOP, ACHIEVEMENTS, LEVEL_EDITOR, SETTINGS
}

class RageViewModel(application: Application) : AndroidViewModel(application) {
    val repository = RageRepository(application)
    val audioEngine = AudioEngine()
    val vibrationManager = VibrationManager(application)

    private val _currentScreen = MutableStateFlow(ScreenState.SPLASH)
    val currentScreen: StateFlow<ScreenState> = _currentScreen

    val userProgress: StateFlow<UserProgressData> = repository.userProgressFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProgressData()
    )

    private val _activeLevelData = MutableStateFlow<LevelData?>(null)
    val activeLevelData: StateFlow<LevelData?> = _activeLevelData

    private val _activeGameEngine = MutableStateFlow<GameEngine?>(null)
    val activeGameEngine: StateFlow<GameEngine?> = _activeGameEngine

    init {
        audioEngine.startMusic()
    }

    fun navigateTo(screen: ScreenState) {
        _currentScreen.value = screen
    }

    fun startLevel(levelId: Int, isTwoPlayer: Boolean = false) {
        val levelData = LevelDataRepository.getLevel(levelId)
        _activeLevelData.value = levelData
        val engine = GameEngine(levelData, audioEngine, vibrationManager, isTwoPlayer)
        _activeGameEngine.value = engine

        if (isTwoPlayer) {
            navigateTo(ScreenState.TWO_PLAYER_GAME)
        } else {
            navigateTo(ScreenState.GAME)
        }
    }

    fun startCustomLevel(levelData: LevelData) {
        _activeLevelData.value = levelData
        val engine = GameEngine(levelData, audioEngine, vibrationManager, isTwoPlayerMode = false)
        _activeGameEngine.value = engine
        navigateTo(ScreenState.GAME)
    }

    fun onLevelCompleted(coinsEarned: Int) {
        viewModelScope.launch {
            val current = userProgress.value
            val updated = current.copy(
                coins = current.coins + coinsEarned,
                levelsCompleted = current.levelsCompleted + 1
            )
            repository.saveProgress(updated)
        }
    }

    fun onPlayerDied() {
        viewModelScope.launch {
            val current = userProgress.value
            val updated = current.copy(
                totalDeaths = current.totalDeaths + 1
            )
            repository.saveProgress(updated)
        }
    }

    fun purchaseSkin(skinId: String, price: Int): Boolean {
        val current = userProgress.value
        if (current.coins >= price && !current.unlockedSkins.contains(skinId)) {
            viewModelScope.launch {
                val newSkins = current.unlockedSkins + skinId
                val updated = current.copy(
                    coins = current.coins - price,
                    unlockedSkins = newSkins,
                    activeSkin = skinId
                )
                repository.saveProgress(updated)
            }
            return true
        }
        return false
    }

    fun selectSkin(skinId: String) {
        val current = userProgress.value
        if (current.unlockedSkins.contains(skinId)) {
            viewModelScope.launch {
                repository.saveProgress(current.copy(activeSkin = skinId))
            }
        }
    }

    fun updateSettings(
        musicVol: Float,
        sfxVol: Float,
        vibration: Boolean,
        btnSizeDp: Int,
        btnOpacity: Float,
        leftHanded: Boolean
    ) {
        audioEngine.musicVolume = musicVol
        audioEngine.sfxVolume = sfxVol
        vibrationManager.enabled = vibration

        viewModelScope.launch {
            val current = userProgress.value
            val updated = current.copy(
                musicVolume = musicVol,
                sfxVolume = sfxVol,
                vibrationEnabled = vibration,
                buttonSizeDp = btnSizeDp,
                buttonOpacity = btnOpacity,
                leftHanded = leftHanded
            )
            repository.saveProgress(updated)
        }
    }
}
