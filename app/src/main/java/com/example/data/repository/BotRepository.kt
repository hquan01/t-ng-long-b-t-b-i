package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.BotLogEntity
import com.example.data.entity.DailyStatsEntity
import com.example.data.entity.FarmPlotEntity
import com.example.data.entity.FarmingConfigEntity
import com.example.data.entity.GuildQuestConfigEntity
import com.example.data.entity.MiningConfigEntity
import com.example.data.entity.PunishEvilConfigEntity
import com.example.model.CropType
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BotRepository(private val appDao: AppDao) {

    val botConfig: Flow<BotConfigEntity?> = appDao.getBotConfig()
    val guildConfig: Flow<GuildQuestConfigEntity?> = appDao.getGuildQuestConfig()
    val farmingConfig: Flow<FarmingConfigEntity?> = appDao.getFarmingConfig()
    val farmPlots: Flow<List<FarmPlotEntity>> = appDao.getAllFarmPlots()
    val miningConfig: Flow<MiningConfigEntity?> = appDao.getMiningConfig()
    val punishEvilConfig: Flow<PunishEvilConfigEntity?> = appDao.getPunishEvilConfig()
    val recentLogs: Flow<List<BotLogEntity>> = appDao.getRecentLogs()

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    val dailyStats: Flow<DailyStatsEntity?> = appDao.getDailyStats(getTodayDateString())

    suspend fun initializeDefaultDataIfEmpty() {
        // Initialize Default Farm Plots if needed
        val defaultPlots = (1..8).map { index ->
            val crop = when (index % 4) {
                0 -> CropType.LINH_CHI
                1 -> CropType.NHAN_SAM
                2 -> CropType.TUYET_LIEN
                else -> CropType.HOANG_TINH
            }
            val elapsedMock = (index * 15 * 1000L)
            val plantTime = System.currentTimeMillis() - elapsedMock
            FarmPlotEntity(
                plotIndex = index,
                isUnlocked = true,
                crop = crop,
                plantTimestamp = plantTime,
                matureDurationSec = crop.growthTimeSeconds,
                waterLevel = (80 + index * 2).coerceAtMost(100),
                fertilizerApplied = true,
                isReadyToHarvest = false,
                totalHarvestCount = index * 4
            )
        }
        appDao.insertFarmPlots(defaultPlots)

        // Initialize Configs
        appDao.saveBotConfig(BotConfigEntity())
        appDao.saveGuildQuestConfig(GuildQuestConfigEntity())
        appDao.saveFarmingConfig(FarmingConfigEntity())
        appDao.saveMiningConfig(MiningConfigEntity())
        appDao.savePunishEvilConfig(PunishEvilConfigEntity())

        val today = getTodayDateString()
        appDao.saveDailyStats(
            DailyStatsEntity(
                dateString = today,
                guildQuestsDone = 14,
                cropsHarvested = 32,
                oresMined = 48,
                evilBossesSlain = 18,
                totalExpGained = 385000L,
                totalGoldGained = 42500L,
                rareItemsFound = 5,
                runningTimeSeconds = 3600L
            )
        )

        // Seed initial log
        appDao.insertLog(
            BotLogEntity(
                category = "HỆ THỐNG",
                actionText = "Khởi tạo hệ thống Trợ Lý Rảnh Tay Tàng Long Bất Bại",
                detail = "Đã tải cấu hình: 4 mô-đun Bang Hội, Trồng Trọt, Đào Khoáng, Trừng Ác sẵn sàng.",
                isHighlight = true
            )
        )
    }

    suspend fun updateBotConfig(config: BotConfigEntity) = appDao.saveBotConfig(config)
    suspend fun updateGuildConfig(config: GuildQuestConfigEntity) = appDao.saveGuildQuestConfig(config)
    suspend fun updateFarmingConfig(config: FarmingConfigEntity) = appDao.saveFarmingConfig(config)
    suspend fun updateFarmPlot(plot: FarmPlotEntity) = appDao.updateFarmPlot(plot)
    suspend fun insertFarmPlots(plots: List<FarmPlotEntity>) = appDao.insertFarmPlots(plots)
    suspend fun updateMiningConfig(config: MiningConfigEntity) = appDao.saveMiningConfig(config)
    suspend fun updatePunishEvilConfig(config: PunishEvilConfigEntity) = appDao.savePunishEvilConfig(config)
    suspend fun addLog(log: BotLogEntity) = appDao.insertLog(log)
    suspend fun clearLogs() = appDao.clearAllLogs()
    suspend fun updateDailyStats(stats: DailyStatsEntity) = appDao.saveDailyStats(stats)
}
