package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TangLongApp
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.BotLogEntity
import com.example.data.entity.DailyStatsEntity
import com.example.data.entity.FarmPlotEntity
import com.example.data.entity.FarmingConfigEntity
import com.example.data.entity.GuildQuestConfigEntity
import com.example.data.entity.MiningConfigEntity
import com.example.data.entity.PunishEvilConfigEntity
import com.example.engine.BotAutomationEngine
import com.example.engine.LiveBotState
import com.example.model.BotStatus
import com.example.model.CropType
import com.example.model.EvilLevel
import com.example.model.MineMap
import com.example.model.TargetOre
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as TangLongApp
    val repository = app.repository

    val liveBotState: StateFlow<LiveBotState> = BotAutomationEngine.liveState

    val botConfig: StateFlow<BotConfigEntity?> = repository.botConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BotConfigEntity())

    val guildConfig: StateFlow<GuildQuestConfigEntity?> = repository.guildConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GuildQuestConfigEntity())

    val farmingConfig: StateFlow<FarmingConfigEntity?> = repository.farmingConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FarmingConfigEntity())

    val farmPlots: StateFlow<List<FarmPlotEntity>> = repository.farmPlots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val miningConfig: StateFlow<MiningConfigEntity?> = repository.miningConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MiningConfigEntity())

    val punishEvilConfig: StateFlow<PunishEvilConfigEntity?> = repository.punishEvilConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PunishEvilConfigEntity())

    val recentLogs: StateFlow<List<BotLogEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyStats: StateFlow<DailyStatsEntity?> = repository.dailyStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        BotAutomationEngine.initialize(repository)
    }

    fun startAuto(context: Context) {
        BotAutomationEngine.startService(context)
    }

    fun pauseAuto() {
        BotAutomationEngine.pause()
    }

    fun resumeAuto(context: Context) {
        BotAutomationEngine.resume(context)
    }

    fun stopAuto(context: Context) {
        BotAutomationEngine.stop(context)
    }

    fun toggleBlackScreenMode(enabled: Boolean) {
        BotAutomationEngine.toggleBlackScreenMode(enabled)
    }

    fun toggleFloatingWidget(visible: Boolean) {
        BotAutomationEngine.toggleFloatingWidget(visible)
    }

    // Bot Config Updates
    fun updateBotConfig(update: (BotConfigEntity) -> BotConfigEntity) {
        viewModelScope.launch {
            val current = botConfig.value ?: BotConfigEntity()
            val newConfig = update(current)
            repository.updateBotConfig(newConfig)
        }
    }

    // Guild Config Updates
    fun updateGuildConfig(update: (GuildQuestConfigEntity) -> GuildQuestConfigEntity) {
        viewModelScope.launch {
            val current = guildConfig.value ?: GuildQuestConfigEntity()
            val newConfig = update(current)
            repository.updateGuildConfig(newConfig)
        }
    }

    // Farming Config Updates & Plot actions
    fun updateFarmingConfig(update: (FarmingConfigEntity) -> FarmingConfigEntity) {
        viewModelScope.launch {
            val current = farmingConfig.value ?: FarmingConfigEntity()
            val newConfig = update(current)
            repository.updateFarmingConfig(newConfig)
        }
    }

    fun plantSeedOnPlot(plotIndex: Int, crop: CropType) {
        viewModelScope.launch {
            val currentPlots = farmPlots.value
            val target = currentPlots.find { it.plotIndex == plotIndex } ?: return@launch
            val updated = target.copy(
                crop = crop,
                plantTimestamp = System.currentTimeMillis(),
                matureDurationSec = crop.growthTimeSeconds,
                waterLevel = 100,
                fertilizerApplied = true,
                isReadyToHarvest = false
            )
            repository.updateFarmPlot(updated)
            repository.addLog(
                BotLogEntity(
                    category = "TRỒNG TRỌT",
                    actionText = "Gieo hạt [${crop.cropName}] vào Ô đất #$plotIndex",
                    detail = "Thời gian chín: ${crop.growthTimeSeconds} giây. Sản lượng dự kiến: ${crop.harvestYield} dược phẩm."
                )
            )
        }
    }

    fun harvestPlot(plotIndex: Int) {
        viewModelScope.launch {
            val currentPlots = farmPlots.value
            val target = currentPlots.find { it.plotIndex == plotIndex } ?: return@launch
            val yield = target.crop.harvestYield
            val exp = target.crop.expPerHarvest

            val updated = target.copy(
                plantTimestamp = System.currentTimeMillis(),
                waterLevel = 100,
                fertilizerApplied = true,
                isReadyToHarvest = false,
                totalHarvestCount = target.totalHarvestCount + yield
            )
            repository.updateFarmPlot(updated)
            repository.addLog(
                BotLogEntity(
                    category = "TRỒNG TRỌT",
                    actionText = "Thu hoạch Ô đất #$plotIndex (+${yield}x ${target.crop.cropName})",
                    detail = "Nhận +$exp Exp nông trang. Đã tự động gieo lại mầm mới.",
                    expEarned = exp,
                    itemDrop = "${yield}x ${target.crop.cropName}"
                )
            )
        }
    }

    fun waterAllPlots() {
        viewModelScope.launch {
            val updated = farmPlots.value.map { it.copy(waterLevel = 100, fertilizerApplied = true) }
            repository.insertFarmPlots(updated)
            repository.addLog(
                BotLogEntity(
                    category = "TRỒNG TRỌT",
                    actionText = "Tưới nước và bón Linh Dược Phì Nhiêu cho toàn bộ 8 ô đất",
                    detail = "Tất cả các luống thảo dược đã đạt độ ẩm 100%."
                )
            )
        }
    }

    // Mining Config Updates
    fun updateMiningConfig(update: (MiningConfigEntity) -> MiningConfigEntity) {
        viewModelScope.launch {
            val current = miningConfig.value ?: MiningConfigEntity()
            val newConfig = update(current)
            repository.updateMiningConfig(newConfig)
        }
    }

    // Punish Evil Config Updates
    fun updatePunishEvilConfig(update: (PunishEvilConfigEntity) -> PunishEvilConfigEntity) {
        viewModelScope.launch {
            val current = punishEvilConfig.value ?: PunishEvilConfigEntity()
            val newConfig = update(current)
            repository.updatePunishEvilConfig(newConfig)
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}
