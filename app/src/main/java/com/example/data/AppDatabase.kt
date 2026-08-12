package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 100,
    val totalDeaths: Int = 0,
    val levelsCompleted: Int = 0,
    val unlockedSkinsJson: String = "[\"default_red\"]",
    val activeSkin: String = "default_red",
    val musicVolume: Float = 0.8f,
    val sfxVolume: Float = 1.0f,
    val vibrationEnabled: Boolean = true,
    val buttonSizeDp: Int = 60,
    val buttonOpacity: Float = 0.85f,
    val leftHanded: Boolean = false
)

@Entity(tableName = "achievement_progress")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val progress: Int = 0,
    val isUnlocked: Boolean = false
)

@Entity(tableName = "level_records")
data class LevelRecordEntity(
    @PrimaryKey val levelId: Int,
    val bestTimeMs: Long = 0L,
    val deathsInLevel: Int = 0,
    val starsEarned: Int = 0,
    val isCompleted: Boolean = false
)

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProgress(progress: UserProgressEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement_progress")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAchievement(achievement: AchievementEntity)
}

@Database(
    entities = [UserProgressEntity::class, AchievementEntity::class, LevelRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rage_runner_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
