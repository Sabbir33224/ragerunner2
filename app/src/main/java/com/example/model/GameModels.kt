package com.example.model

import androidx.compose.ui.graphics.Color

data class RectF(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f

    fun intersects(other: RectF): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }
}

data class Vector2D(
    var x: Float = 0f,
    var y: Float = 0f
)

enum class PlayerState {
    IDLE, RUNNING, JUMPING, DASHING, DYING, WON
}

data class Player(
    var id: Int = 1,
    var x: Float = 100f,
    var y: Float = 500f,
    var width: Float = 36f,
    var height: Float = 52f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var isGrounded: Boolean = false,
    var state: PlayerState = PlayerState.IDLE,
    var deaths: Int = 0,
    var skinId: String = "default_red",
    var facingRight: Boolean = true,
    var jumpsRemaining: Int = 2
) {
    fun getRect(): RectF = RectF(x, y, x + width, y + height)
}

enum class PlatformType {
    SOLID, FAKE, BOUNCE, MOVING, ICE, CONVEYOR_LEFT, CONVEYOR_RIGHT, CRUMBLING
}

data class Platform(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val type: PlatformType = PlatformType.SOLID,
    var moveRangeX: Float = 0f,
    var moveRangeY: Float = 0f,
    var moveSpeed: Float = 0f,
    var isCrumbling: Boolean = false,
    var crumbleTimer: Float = 0f
) {
    var currentX: Float = x1
    var currentY: Float = y1
    val rect: RectF get() = RectF(currentX, currentY, currentX + (x2 - x1), currentY + (y2 - y1))
}

enum class TrapType {
    STATIC_SPIKE, ROTATING_SAW, FALLING_CEILING, INVISIBLE_SPIKE, TELEPORT_TRAP,
    FAKE_COIN, TROLL_PORTAL, HOMING_MISSILE, LASER_GRID, REVERSE_GRAVITY_ZONE
}

enum class TrapTrigger {
    ALWAYS, TOUCH, PROXIMITY, TIMER
}

data class Trap(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val type: TrapType,
    val trigger: TrapTrigger = TrapTrigger.TOUCH,
    var triggerDistance: Float = 120f,
    var isTriggered: Boolean = false,
    var isVisible: Boolean = true,
    var vx: Float = 0f,
    var vy: Float = 0f
) {
    val rect: RectF get() = RectF(x, y, x + width, y + height)
}

data class CoinItem(
    val x: Float,
    val y: Float,
    val radius: Float = 12f,
    var isCollected: Boolean = false
) {
    val rect: RectF get() = RectF(x - radius, y - radius, x + radius, y + radius)
}

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var size: Float,
    var alpha: Float = 1f,
    var life: Float = 1f
)

data class LevelData(
    val id: Int,
    val name: String,
    val world: Int,
    val theme: String,
    val width: Float = 2000f,
    val height: Float = 1000f,
    val startX: Float = 100f,
    val startY: Float = 500f,
    val exitX: Float = 1800f,
    val exitY: Float = 500f,
    val platforms: List<Platform> = emptyList(),
    val traps: List<Trap> = emptyList(),
    val coins: List<CoinItem> = emptyList()
)

data class SkinItem(
    val id: String,
    val name: String,
    val price: Int,
    val mainColor: Color,
    val accentColor: Color,
    val isUnlocked: Boolean = false
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val maxProgress: Int,
    val rewardCoins: Int
)

data class UserProgressData(
    val coins: Int = 100,
    val totalDeaths: Int = 0,
    val levelsCompleted: Int = 0,
    val unlockedSkins: List<String> = listOf("default_red"),
    val activeSkin: String = "default_red",
    val musicVolume: Float = 0.8f,
    val sfxVolume: Float = 1.0f,
    val vibrationEnabled: Boolean = true,
    val buttonSizeDp: Int = 60,
    val buttonOpacity: Float = 0.85f,
    val leftHanded: Boolean = false
)
