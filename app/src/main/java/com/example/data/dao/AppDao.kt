package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.BotLogEntity
import com.example.data.entity.DailyStatsEntity
import com.example.data.entity.FarmPlotEntity
import com.example.data.entity.FarmingConfigEntity
import com.example.data.entity.GuildQuestConfigEntity
import com.example.data.entity.MiningConfigEntity
import com.example.data.entity.PunishEvilConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Bot Config
    @Query("SELECT * FROM bot_config WHERE id = 1")
    fun getBotConfig(): Flow<BotConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBotConfig(config: BotConfigEntity)

    // Guild Quest Config
    @Query("SELECT * FROM guild_quest_config WHERE id = 1")
    fun getGuildQuestConfig(): Flow<GuildQuestConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGuildQuestConfig(config: GuildQuestConfigEntity)

    // Farming Config & Plots
    @Query("SELECT * FROM farming_config WHERE id = 1")
    fun getFarmingConfig(): Flow<FarmingConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFarmingConfig(config: FarmingConfigEntity)

    @Query("SELECT * FROM farm_plots ORDER BY plotIndex ASC")
    fun getAllFarmPlots(): Flow<List<FarmPlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmPlots(plots: List<FarmPlotEntity>)

    @Update
    suspend fun updateFarmPlot(plot: FarmPlotEntity)

    // Mining Config
    @Query("SELECT * FROM mining_config WHERE id = 1")
    fun getMiningConfig(): Flow<MiningConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMiningConfig(config: MiningConfigEntity)

    // Punish Evil Config
    @Query("SELECT * FROM punish_evil_config WHERE id = 1")
    fun getPunishEvilConfig(): Flow<PunishEvilConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePunishEvilConfig(config: PunishEvilConfigEntity)

    // Custom Action Steps
    @Query("SELECT * FROM custom_action_steps ORDER BY stepOrder ASC")
    fun getAllCustomActionSteps(): Flow<List<com.example.data.entity.CustomActionStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomActionStep(step: com.example.data.entity.CustomActionStepEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomActionSteps(steps: List<com.example.data.entity.CustomActionStepEntity>)

    @Update
    suspend fun updateCustomActionStep(step: com.example.data.entity.CustomActionStepEntity)

    @Query("DELETE FROM custom_action_steps WHERE id = :id")
    suspend fun deleteCustomActionStep(id: Long)

    @Query("DELETE FROM custom_action_steps")
    suspend fun clearAllCustomActionSteps()

    // Logs
    @Query("SELECT * FROM bot_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<BotLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BotLogEntity)

    @Query("DELETE FROM bot_logs")
    suspend fun clearAllLogs()

    // Daily Stats
    @Query("SELECT * FROM daily_stats WHERE dateString = :dateStr")
    fun getDailyStats(dateStr: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats ORDER BY dateString DESC LIMIT 1")
    fun getLatestDailyStats(): Flow<DailyStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyStats(stats: DailyStatsEntity)
}
