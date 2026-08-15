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
    val customActionSteps: Flow<List<com.example.data.entity.CustomActionStepEntity>> = appDao.getAllCustomActionSteps()
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

        // Seed 10 Default Punish Evil Steps
        val defaultSteps = listOf(
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 1,
                actionName = "1. Chạm NPC Ngô Giới (Tô Châu)",
                description = "Chạm vào bong bóng icon hội thoại E của NPC Ngô Giới",
                delaySeconds = 2,
                screenXPercent = 0.930f,
                screenYPercent = 0.440f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 2,
                actionName = "2. Chọn dòng Trừng Trị Hung Đồ",
                description = "Chạm vào mục nhiệm vụ Trừng Ác trên bảng hội thoại",
                delaySeconds = 1,
                screenXPercent = 0.620f,
                screenYPercent = 0.380f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 3,
                actionName = "3. Bấm Nhận Nhiệm Vụ",
                description = "Nhận nhiệm vụ và nhận Trừng Ác Lệnh vào túi đồ",
                delaySeconds = 2,
                screenXPercent = 0.120f,
                screenYPercent = 0.880f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 4,
                actionName = "4. Mở Túi Đồ (Phím B)",
                description = "Mở giao diện Túi Đồ góc trên bên phải màn hình",
                delaySeconds = 1,
                screenXPercent = 0.770f,
                screenYPercent = 0.055f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 5,
                actionName = "5. Chọn Tab Nhiệm Vụ",
                description = "Chuyển sang ngăn đựng vật phẩm nhiệm vụ",
                delaySeconds = 1,
                screenXPercent = 0.540f,
                screenYPercent = 0.135f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 6,
                actionName = "6. Bấm Trừng Ác Lệnh",
                description = "Chạm vào ô chứa Trừng Ác Lệnh",
                delaySeconds = 2,
                screenXPercent = 0.235f,
                screenYPercent = 0.220f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 7,
                actionName = "7. Bấm Tọa Độ Boss (Chạy đường)",
                description = "Bấm link tọa độ để nhân vật tự phi thân chạy tới bãi Boss",
                delaySeconds = 15,
                screenXPercent = 0.350f,
                screenYPercent = 0.550f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 8,
                actionName = "8. Xuống Ngựa & Gọi Boss",
                description = "Tắt thú cưỡi và dùng lại Lệnh Bài để gọi Boss xuất hiện",
                delaySeconds = 3,
                screenXPercent = 0.445f,
                screenYPercent = 0.825f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 9,
                actionName = "9. Bật Auto Đánh diệt Boss",
                description = "Bật Auto & xả combo chiêu thức tiêu diệt Boss nhặt Bảo Tàng Đồ",
                delaySeconds = 18,
                screenXPercent = 0.305f,
                screenYPercent = 0.905f
            ),
            com.example.data.entity.CustomActionStepEntity(
                stepOrder = 10,
                actionName = "10. Bạch Sắc Định Vị Phù (Về Tô Châu)",
                description = "Bấm dùng phù F2/F3 biến về Tô Châu trả nhiệm vụ & lặp lại",
                delaySeconds = 4,
                screenXPercent = 0.770f,
                screenYPercent = 0.285f
            )
        )
        appDao.insertCustomActionSteps(defaultSteps)
    }

    suspend fun updateBotConfig(config: BotConfigEntity) = appDao.saveBotConfig(config)
    suspend fun updateGuildConfig(config: GuildQuestConfigEntity) = appDao.saveGuildQuestConfig(config)
    suspend fun updateFarmingConfig(config: FarmingConfigEntity) = appDao.saveFarmingConfig(config)
    suspend fun updateFarmPlot(plot: FarmPlotEntity) = appDao.updateFarmPlot(plot)
    suspend fun insertFarmPlots(plots: List<FarmPlotEntity>) = appDao.insertFarmPlots(plots)
    suspend fun updateMiningConfig(config: MiningConfigEntity) = appDao.saveMiningConfig(config)
    suspend fun updatePunishEvilConfig(config: PunishEvilConfigEntity) = appDao.savePunishEvilConfig(config)
    suspend fun addCustomActionStep(step: com.example.data.entity.CustomActionStepEntity) = appDao.insertCustomActionStep(step)
    suspend fun insertCustomActionSteps(steps: List<com.example.data.entity.CustomActionStepEntity>) = appDao.insertCustomActionSteps(steps)
    suspend fun updateCustomActionStep(step: com.example.data.entity.CustomActionStepEntity) = appDao.updateCustomActionStep(step)
    suspend fun deleteCustomActionStep(id: Long) = appDao.deleteCustomActionStep(id)
    suspend fun clearAllCustomActionSteps() = appDao.clearAllCustomActionSteps()
    suspend fun addLog(log: BotLogEntity) = appDao.insertLog(log)
    suspend fun clearLogs() = appDao.clearAllLogs()
    suspend fun updateDailyStats(stats: DailyStatsEntity) = appDao.saveDailyStats(stats)
}
