package com.example.engine

import androidx.compose.ui.graphics.Color
import com.example.model.CoinItem
import com.example.model.LevelData
import com.example.model.Particle
import com.example.model.Platform
import com.example.model.PlatformType
import com.example.model.Player
import com.example.model.PlayerState
import com.example.model.RectF
import com.example.model.Trap
import com.example.model.TrapType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class GameEngine(
    val levelData: LevelData,
    val audioEngine: AudioEngine,
    val vibrationManager: VibrationManager,
    val isTwoPlayerMode: Boolean = false
) {
    val player1 = Player(id = 1, x = levelData.startX, y = levelData.startY)
    val player2 = Player(id = 2, x = levelData.startX + 50f, y = levelData.startY)

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver

    private val _isVictory = MutableStateFlow(false)
    val isVictory: StateFlow<Boolean> = _isVictory

    private val _cameraX = MutableStateFlow(0f)
    val cameraX: StateFlow<Float> = _cameraX

    val particles = mutableListOf<Particle>()

    private val gravity = 1800f
    private val moveSpeed = 380f
    private val jumpVelocity = -750f

    var coinsCollected = 0
    var deathsInSession = 0

    fun update(dt: Float) {
        if (_isVictory.value) return

        updatePlayer(player1, dt)
        if (isTwoPlayerMode) {
            updatePlayer(player2, dt)
        }

        // Camera Follows Leading Player
        val leadX = if (isTwoPlayerMode) maxOf(player1.x, player2.x) else player1.x
        _cameraX.value = (leadX - 300f).coerceAtLeast(0f)

        // Update Moving Platforms
        levelData.platforms.forEach { plat ->
            if (plat.type == PlatformType.CRUMBLING && plat.isCrumbling) {
                plat.crumbleTimer += dt
                if (plat.crumbleTimer > 0.6f) {
                    plat.currentY += 1500f * dt
                }
            }
        }

        // Update Particles
        for (i in particles.indices.reversed()) {
            val p = particles[i]
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.life -= dt * 2f
            p.alpha = p.life.coerceAtLeast(0f)
            if (p.life <= 0f) {
                particles.removeAt(i)
            }
        }
    }

    private fun updatePlayer(player: Player, dt: Float) {
        if (player.state == PlayerState.DYING) return

        // Gravity
        player.vy += gravity * dt
        player.x += player.vx * dt
        player.y += player.vy * dt

        // Out of Bounds Death
        if (player.y > levelData.height + 200f) {
            killPlayer(player)
            return
        }

        // Platform Collisions
        val playerRect = player.getRect()
        player.isGrounded = false

        for (plat in levelData.platforms) {
            val platRect = plat.rect
            if (playerRect.intersects(platRect)) {
                if (plat.type == PlatformType.FAKE) {
                    continue
                }

                // Top Collision (Landing)
                if (player.vy > 0 && player.y + player.height - (player.vy * dt) <= platRect.top + 10f) {
                    player.y = platRect.top - player.height
                    player.vy = if (plat.type == PlatformType.BOUNCE) {
                        audioEngine.playBounce()
                        vibrationManager.vibrateBounce()
                        jumpVelocity * 1.4f
                    } else 0f

                    player.isGrounded = true
                    player.jumpsRemaining = 2

                    if (plat.type == PlatformType.CONVEYOR_RIGHT) {
                        player.x += 180f * dt
                    } else if (plat.type == PlatformType.CRUMBLING) {
                        plat.isCrumbling = true
                    }
                }
            }
        }

        // Trap Collisions
        for (trap in levelData.traps) {
            val trapRect = trap.rect
            if (playerRect.intersects(trapRect)) {
                killPlayer(player)
                return
            }
        }

        // Coin Collections
        for (coin in levelData.coins) {
            if (!coin.isCollected && playerRect.intersects(coin.rect)) {
                coin.isCollected = true
                coinsCollected++
                audioEngine.playCoin()
                spawnParticles(coin.x, coin.y, Color(0xFFFFD700), 8)
            }
        }

        // Level Exit Goal Collision
        val exitRect = RectF(levelData.exitX, levelData.exitY, levelData.exitX + 60f, levelData.exitY + 80f)
        if (playerRect.intersects(exitRect)) {
            _isVictory.value = true
            player.state = PlayerState.WON
        }
    }

    fun handleMove(playerNumber: Int, dirX: Float) {
        val player = if (playerNumber == 1) player1 else player2
        if (player.state == PlayerState.DYING) return

        player.vx = dirX * moveSpeed
        if (dirX != 0f) {
            player.facingRight = dirX > 0
            if (player.isGrounded) player.state = PlayerState.RUNNING
        } else {
            if (player.isGrounded) player.state = PlayerState.IDLE
        }
    }

    fun handleJump(playerNumber: Int) {
        val player = if (playerNumber == 1) player1 else player2
        if (player.state == PlayerState.DYING) return

        if (player.isGrounded || player.jumpsRemaining > 0) {
            player.vy = jumpVelocity
            player.isGrounded = false
            player.jumpsRemaining--
            player.state = PlayerState.JUMPING
            audioEngine.playJump()
            vibrationManager.vibrateClick()
            spawnParticles(player.x + player.width / 2f, player.y + player.height, Color.White, 6)
        }
    }

    private fun killPlayer(player: Player) {
        player.state = PlayerState.DYING
        deathsInSession++
        player.deaths++
        audioEngine.playDeath()
        vibrationManager.vibrateDeath()
        spawnParticles(player.x + player.width / 2f, player.y + player.height / 2f, Color(0xFFFF2A42), 20)

        if (!isTwoPlayerMode) {
            _isGameOver.value = true
        } else if (player1.state == PlayerState.DYING && player2.state == PlayerState.DYING) {
            _isGameOver.value = true
        }
    }

    fun restartLevel() {
        player1.x = levelData.startX
        player1.y = levelData.startY
        player1.vx = 0f
        player1.vy = 0f
        player1.state = PlayerState.IDLE

        if (isTwoPlayerMode) {
            player2.x = levelData.startX + 50f
            player2.y = levelData.startY
            player2.vx = 0f
            player2.vy = 0f
            player2.state = PlayerState.IDLE
        }

        _isGameOver.value = false
        _isVictory.value = false

        levelData.platforms.forEach {
            it.currentX = it.x1
            it.currentY = it.y1
            it.isCrumbling = false
            it.crumbleTimer = 0f
        }
        levelData.coins.forEach { it.isCollected = false }
    }

    private fun spawnParticles(x: Float, y: Float, color: Color, count: Int) {
        if (particles.size > 100) return
        for (i in 0 until count) {
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (Random.nextFloat() - 0.5f) * 400f,
                    vy = (Random.nextFloat() - 0.5f) * 400f,
                    color = color,
                    size = Random.nextFloat() * 6f + 4f,
                    life = 1f
                )
            )
        }
    }
}
