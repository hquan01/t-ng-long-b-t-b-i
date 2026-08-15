package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.CropType
import com.example.model.EvilLevel
import com.example.model.GuildQuestType
import com.example.model.MineMap
import com.example.model.SectType
import com.example.model.TargetOre

@Entity(tableName = "bot_config")
data class BotConfigEntity(
    @PrimaryKey val id: Int = 1,
    val characterName: String = "asuna",
    val sect: SectType = SectType.TIEU_DAO,
    val characterLevel: Int = 49,
    val serverName: String = "S1 - Tàng Long",
    val guildName: String = "Ngũ Giới",
    val combatPower: Long = 368500L,
    val characterTitle: String = "Hòa Bình",
    val currentMap: String = "Tô Châu [131, 134]",
    val maxHp: Int = 68000,
    val currentHp: Int = 68000,
    val maxMp: Int = 35000,
    val currentMp: Int = 35000,
    val expCurrent: Long = 4500000L,
    val expMax: Long = 6200000L,
    val goldAmount: Long = 45000L,
    val silverAmount: Long = 1850000L,
    val isGuildQuestEnabled: Boolean = true,
    val isFarmingEnabled: Boolean = true,
    val isMiningEnabled: Boolean = true,
    val isPunishEvilEnabled: Boolean = true,
    // Anti-detection human-like delays (milliseconds)
    val minActionDelayMs: Long = 1200L,
    val maxActionDelayMs: Long = 2500L,
    // Automation safety
    val autoUseHpPotionThreshold: Int = 40, // HP %
    val autoUseMpPotionThreshold: Int = 30, // MP %
    val autoReviveAtSpawn: Boolean = true,
    val autoFleeIfPkAttacked: Boolean = true,
    val autoRepairEquipment: Boolean = true,
    val lowBatteryBlackScreenMode: Boolean = false,
    val floatingOverlayEnabled: Boolean = true,
    val soundAlertOnRareDrop: Boolean = true
)

@Entity(tableName = "guild_quest_config")
data class GuildQuestConfigEntity(
    @PrimaryKey val id: Int = 1,
    val targetLoops: Int = 50,
    val currentLoop: Int = 0,
    val enableNpcDialogQuests: Boolean = true, // Đối thoại các NPC & làm theo yêu cầu
    val enableSeparateEscort: Boolean = false, // Vận tiêu bang chạy riêng
    val escortDailyCount: Int = 3, // Số chuyến vận tiêu mỗi ngày
    val autoContributeItems: Boolean = true,
    val autoClaimDevotionBonus: Boolean = true,
    val escortQualityPriority: String = "Cam / Tím (Cao Cấp)"
)

@Entity(tableName = "farming_config")
data class FarmingConfigEntity(
    @PrimaryKey val id: Int = 1,
    val defaultSeed: CropType = CropType.LINH_CHI,
    val autoReplant: Boolean = true,
    val autoWaterAndFertilize: Boolean = true,
    val autoDefendPlotFromThieves: Boolean = true,
    val autoSellCommonHerbs: Boolean = false,
    val notifyWhenHarvestReady: Boolean = true
)

@Entity(tableName = "farm_plots")
data class FarmPlotEntity(
    @PrimaryKey val plotIndex: Int, // 1 to 8
    val isUnlocked: Boolean = true,
    val crop: CropType = CropType.LINH_CHI,
    val plantTimestamp: Long = 0L,
    val matureDurationSec: Int = 60,
    val waterLevel: Int = 100, // 0-100%
    val fertilizerApplied: Boolean = true,
    val isReadyToHarvest: Boolean = false,
    val totalHarvestCount: Int = 0
)

@Entity(tableName = "mining_config")
data class MiningConfigEntity(
    @PrimaryKey val id: Int = 1,
    val selectedMap: MineMap = MineMap.THAI_SON,
    val targetOre: TargetOre = TargetOre.HUYEN_THIET,
    val durationMinutes: Int = 60,
    val autoUseStaminaPotion: Boolean = true,
    val autoRepairPickaxe: Boolean = true,
    val autoStoreOreInVault: Boolean = true,
    val escapeWhenPkDetected: Boolean = true
)

@Entity(tableName = "punish_evil_config")
data class PunishEvilConfigEntity(
    @PrimaryKey val id: Int = 1,
    val evilLevel: EvilLevel = EvilLevel.CAP_70_80,
    val runUntilLimitReached: Boolean = true,
    val dailyTokensToUse: Int = 50,
    val tokensUsedToday: Int = 0,
    val autoBuyTokensFromStore: Boolean = true,
    val autoMatchParty: Boolean = true,
    val autoUseSupremeCombo: Boolean = true,
    val autoOpenBountyChests: Boolean = true
)

@Entity(tableName = "bot_logs")
data class BotLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String,
    val actionText: String,
    val detail: String = "",
    val expEarned: Int = 0,
    val goldEarned: Int = 0,
    val itemDrop: String = "",
    val isHighlight: Boolean = false
)

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey val dateString: String, // e.g. "2026-08-15"
    val guildQuestsDone: Int = 0,
    val cropsHarvested: Int = 0,
    val oresMined: Int = 0,
    val evilBossesSlain: Int = 0,
    val totalExpGained: Long = 0L,
    val totalGoldGained: Long = 0L,
    val rareItemsFound: Int = 0,
    val runningTimeSeconds: Long = 0L
)
