package com.example.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.model.AchievementItem
import com.example.model.SkinItem
import com.example.model.UserProgressData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RageRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userProgressDao = database.userProgressDao()
    private val achievementDao = database.achievementDao()

    val userProgressFlow: Flow<UserProgressData> = userProgressDao.getUserProgress().map { entity ->
        if (entity == null) {
            UserProgressData()
        } else {
            val skinsList = parseSkinsJson(entity.unlockedSkinsJson)
            UserProgressData(
                coins = entity.coins,
                totalDeaths = entity.totalDeaths,
                levelsCompleted = entity.levelsCompleted,
                unlockedSkins = skinsList,
                activeSkin = entity.activeSkin,
                musicVolume = entity.musicVolume,
                sfxVolume = entity.sfxVolume,
                vibrationEnabled = entity.vibrationEnabled,
                buttonSizeDp = entity.buttonSizeDp,
                buttonOpacity = entity.buttonOpacity,
                leftHanded = entity.leftHanded
            )
        }
    }

    val achievementProgressFlow: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    val defaultSkinsList = listOf(
        SkinItem("default_red", "Rage Red", 0, Color(0xFFFF2A42), Color(0xFFFF8C00), true),
        SkinItem("neon_blue", "Neon Cyber", 150, Color(0xFF00E5FF), Color(0xFF00E676), false),
        SkinItem("toxic_slime", "Toxic Slime", 300, Color(0xFF00E676), Color(0xFFFFD700), false),
        SkinItem("shadow_ninja", "Shadow Ops", 500, Color(0xFF383850), Color(0xFFFF2A42), false),
        SkinItem("golden_god", "Golden Troll", 1000, Color(0xFFFFD700), Color(0xFFFFFFFF), false)
    )

    val defaultAchievementsList = listOf(
        AchievementItem("first_death", "Welcome to Rage", "Die for the very first time", 1, 20),
        AchievementItem("death_10", "Pain Enthusiast", "Die 10 times in total", 10, 50),
        AchievementItem("death_50", "Unstoppable Fool", "Die 50 times in total", 50, 150),
        AchievementItem("level_5", "Getting Warm", "Complete Level 5", 5, 100),
        AchievementItem("coin_hoarder", "Coin Collector", "Collect 200 total coins", 200, 250)
    )

    suspend fun saveProgress(data: UserProgressData) {
        val skinsJson = serializeSkins(data.unlockedSkins)
        userProgressDao.saveUserProgress(
            UserProgressEntity(
                id = 1,
                coins = data.coins,
                totalDeaths = data.totalDeaths,
                levelsCompleted = data.levelsCompleted,
                unlockedSkinsJson = skinsJson,
                activeSkin = data.activeSkin,
                musicVolume = data.musicVolume,
                sfxVolume = data.sfxVolume,
                vibrationEnabled = data.vibrationEnabled,
                buttonSizeDp = data.buttonSizeDp,
                buttonOpacity = data.buttonOpacity,
                leftHanded = data.leftHanded
            )
        )
    }

    private fun parseSkinsJson(json: String): List<String> {
        return json.replace("[", "").replace("]", "").replace("\"", "")
            .split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun serializeSkins(skins: List<String>): String {
        return "[" + skins.joinToString(",") { "\"$it\"" } + "]"
    }
}
