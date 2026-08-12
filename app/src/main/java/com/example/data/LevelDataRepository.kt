package com.example.data

import com.example.model.CoinItem
import com.example.model.LevelData
import com.example.model.Platform
import com.example.model.PlatformType
import com.example.model.Trap
import com.example.model.TrapType

object LevelDataRepository {

    fun getLevel(levelId: Int): LevelData {
        val world = when {
            levelId <= 20 -> 1
            levelId <= 40 -> 2
            levelId <= 60 -> 3
            levelId <= 80 -> 4
            else -> 5
        }

        val themeName = when (world) {
            1 -> "NEON_RED"
            2 -> "TOXIC_GREEN"
            3 -> "MAGMA_ORANGE"
            4 -> "CYBER_CYAN"
            else -> "GOLDEN_TROLL"
        }

        val platforms = mutableListOf<Platform>()
        val traps = mutableListOf<Trap>()
        val coins = mutableListOf<CoinItem>()

        // Base Starting Platform
        platforms.add(Platform(0f, 600f, 350f, 650f, PlatformType.SOLID))

        // Dynamic level layout generated based on levelId seed
        val stepCount = 5 + (levelId % 8)
        var lastX = 350f
        var lastY = 600f

        for (i in 0 until stepCount) {
            val gap = 120f + (i * 25f) % 180f
            val platWidth = 140f + (levelId * 7 + i * 13) % 120f
            val nextX = lastX + gap
            val nextY = (lastY + if (i % 2 == 0) -80f else 60f).coerceIn(300f, 700f)

            val pType = when ((levelId + i) % 7) {
                1 -> PlatformType.BOUNCE
                2 -> PlatformType.FAKE
                3 -> PlatformType.CONVEYOR_RIGHT
                4 -> PlatformType.CRUMBLING
                else -> PlatformType.SOLID
            }

            platforms.add(Platform(nextX, nextY, nextX + platWidth, nextY + 30f, pType))

            // Add Traps
            if (i % 2 == 0) {
                val trapType = when ((levelId + i) % 5) {
                    0 -> TrapType.STATIC_SPIKE
                    1 -> TrapType.ROTATING_SAW
                    2 -> TrapType.FALLING_CEILING
                    3 -> TrapType.INVISIBLE_SPIKE
                    else -> TrapType.STATIC_SPIKE
                }
                traps.add(
                    Trap(
                        x = nextX + 30f,
                        y = nextY - 35f,
                        width = 35f,
                        height = 35f,
                        type = trapType
                    )
                )
            }

            // Add Coins
            coins.add(CoinItem(x = nextX + platWidth / 2f, y = nextY - 50f))

            lastX = nextX + platWidth
            lastY = nextY
        }

        // Final Exit Platform
        val exitX = lastX + 150f
        val exitY = lastY
        platforms.add(Platform(exitX, exitY, exitX + 300f, exitY + 50f, PlatformType.SOLID))

        return LevelData(
            id = levelId,
            name = "STAGE $levelId",
            world = world,
            theme = themeName,
            width = exitX + 400f,
            height = 1000f,
            startX = 100f,
            startY = 520f,
            exitX = exitX + 120f,
            exitY = exitY - 60f,
            platforms = platforms,
            traps = traps,
            coins = coins
        )
    }
}
