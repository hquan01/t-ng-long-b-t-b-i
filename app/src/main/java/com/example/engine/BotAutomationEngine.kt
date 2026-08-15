package com.example.engine

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.entity.BotLogEntity
import com.example.data.entity.FarmPlotEntity
import com.example.data.repository.BotRepository
import com.example.model.BotStatus
import com.example.model.CropType
import com.example.model.EvilLevel
import com.example.model.GuildQuestType
import com.example.model.MineMap
import com.example.model.TargetOre
import com.example.model.TaskCategory
import com.example.service.AutoPlayForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

data class LiveBotState(
    val status: BotStatus = BotStatus.IDLE,
    val currentCategory: TaskCategory = TaskCategory.GUILD_QUEST,
    val actionText: String = "Sẵn sàng khởi động trợ lý rảnh tay",
    val subActionDetail: String = "Chọn tác vụ hoặc nhấn Bắt Đầu Auto để tiến hành",
    val currentMapName: String = "Thành Lạc Dương (Khu An Toàn)",
    val playerHpPercent: Int = 100,
    val playerMpPercent: Int = 100,
    val playerStaminaPercent: Int = 100,
    val currentStepIndex: Int = 1,
    val totalStepsInCycle: Int = 4,
    val loopCount: Int = 1,
    val sessionExp: Long = 0L,
    val sessionGold: Long = 0L,
    val sessionHerbs: Int = 0,
    val sessionOres: Int = 0,
    val sessionEvils: Int = 0,
    val sessionGuildQuests: Int = 0,
    val isBlackScreenMode: Boolean = false,
    val isFloatingWidgetVisible: Boolean = false
)

object BotAutomationEngine {

    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loopJob: Job? = null

    private val _liveState = MutableStateFlow(LiveBotState())
    val liveState: StateFlow<LiveBotState> = _liveState.asStateFlow()

    private var repository: BotRepository? = null

    fun initialize(repo: BotRepository) {
        repository = repo
    }

    fun startService(context: Context) {
        if (_liveState.value.status == BotStatus.RUNNING) return

        val intent = Intent(context, AutoPlayForegroundService::class.java).apply {
            action = AutoPlayForegroundService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        _liveState.value = _liveState.value.copy(status = BotStatus.RUNNING)
        startExecutionLoop()
    }

    fun pause() {
        _liveState.value = _liveState.value.copy(
            status = BotStatus.PAUSED,
            actionText = "Đang tạm dừng tác vụ auto rảnh tay",
            subActionDetail = "Nhấn Tiếp Tục để tiếp tục chu trình làm nhiệm vụ"
        )
        loopJob?.cancel()
    }

    fun resume(context: Context) {
        _liveState.value = _liveState.value.copy(status = BotStatus.RUNNING)
        startExecutionLoop()
    }

    fun stop(context: Context) {
        loopJob?.cancel()
        loopJob = null
        val intent = Intent(context, AutoPlayForegroundService::class.java).apply {
            action = AutoPlayForegroundService.ACTION_STOP
        }
        context.stopService(intent)
        _liveState.value = _liveState.value.copy(
            status = BotStatus.IDLE,
            actionText = "Đã dừng hoàn toàn auto rảnh tay",
            subActionDetail = "Hệ thống đang ở trạng thái nghỉ"
        )
    }

    fun toggleBlackScreenMode(enabled: Boolean) {
        _liveState.value = _liveState.value.copy(isBlackScreenMode = enabled)
    }

    fun toggleFloatingWidget(visible: Boolean) {
        _liveState.value = _liveState.value.copy(isFloatingWidgetVisible = visible)
    }

    private fun startExecutionLoop() {
        loopJob?.cancel()
        loopJob = engineScope.launch {
            val repo = repository ?: return@launch

            while (_liveState.value.status == BotStatus.RUNNING) {
                val botConfig = repo.botConfig.firstOrNull()
                val minDelay = botConfig?.minActionDelayMs ?: 1500L
                val maxDelay = botConfig?.maxActionDelayMs ?: 2500L

                // 1. Module: Trồng trọt (Farming)
                if (botConfig?.isFarmingEnabled != false && _liveState.value.status == BotStatus.RUNNING) {
                    runFarmingCycle(repo, minDelay, maxDelay)
                }

                // 2. Module: Nhiệm vụ Trừng Ác (Punish Evil)
                if (botConfig?.isPunishEvilEnabled != false && _liveState.value.status == BotStatus.RUNNING) {
                    runPunishEvilCycle(repo, minDelay, maxDelay)
                }

                // 3. Module: Nhiệm vụ Bang (Guild Quests)
                if (botConfig?.isGuildQuestEnabled != false && _liveState.value.status == BotStatus.RUNNING) {
                    runGuildQuestCycle(repo, minDelay, maxDelay)
                }

                // 4. Module: Đào Khoáng (Mining)
                if (botConfig?.isMiningEnabled != false && _liveState.value.status == BotStatus.RUNNING) {
                    runMiningCycle(repo, minDelay, maxDelay)
                }

                _liveState.value = _liveState.value.copy(
                    loopCount = _liveState.value.loopCount + 1,
                    actionText = "Hoàn tất chu kỳ lặp #${_liveState.value.loopCount}. Chờ nghỉ giãn cách an toàn...",
                    subActionDetail = "Mô phỏng hành vi người chơi chống phát hiện (Jitter delay)"
                )
                delay(randomJitter(3000L, 5000L))
            }
        }
    }

    private suspend fun runFarmingCycle(repo: BotRepository, minDelay: Long, maxDelay: Long) {
        _liveState.value = _liveState.value.copy(
            currentCategory = TaskCategory.FARMING,
            currentMapName = "Gia Viên - Vườn Dược Thảo",
            actionText = "Đang kiểm tra 8 ô đất nông nghiệp trong Gia Viên...",
            subActionDetail = "Kiểm tra độ ẩm đất, sâu bệnh và thời gian thu hoạch"
        )
        delay(randomJitter(minDelay, maxDelay))

        val plots = repo.farmPlots.firstOrNull() ?: emptyList()
        var harvestedInThisCycle = 0
        var totalExp = 0
        val updatedPlots = plots.map { plot ->
            val now = System.currentTimeMillis()
            val elapsedSec = ((now - plot.plantTimestamp) / 1000).toInt()
            val isMature = elapsedSec >= plot.matureDurationSec

            if (isMature || plot.isReadyToHarvest) {
                harvestedInThisCycle++
                totalExp += plot.crop.expPerHarvest
                plot.copy(
                    plantTimestamp = now,
                    waterLevel = 100,
                    fertilizerApplied = true,
                    isReadyToHarvest = false,
                    totalHarvestCount = plot.totalHarvestCount + plot.crop.harvestYield
                )
            } else {
                plot.copy(waterLevel = (plot.waterLevel - 5).coerceAtLeast(40))
            }
        }

        if (updatedPlots.isNotEmpty()) {
            repo.insertFarmPlots(updatedPlots)
        }

        if (harvestedInThisCycle > 0) {
            val herbName = updatedPlots.firstOrNull()?.crop?.cropName ?: "Linh Chi"
            _liveState.value = _liveState.value.copy(
                sessionExp = _liveState.value.sessionExp + totalExp,
                sessionHerbs = _liveState.value.sessionHerbs + (harvestedInThisCycle * 3),
                actionText = "Đã thu hoạch xong $harvestedInThisCycle ô đất ($herbName)",
                subActionDetail = "Tự động bón phân linh dược và gieo mầm hạt giống mới"
            )
            repo.addLog(
                BotLogEntity(
                    category = "TRỒNG TRỌT",
                    actionText = "Thu hoạch nông trại thành công ($harvestedInThisCycle ô)",
                    detail = "Thu được ${harvestedInThisCycle * 3}x $herbName, tự động gieo vụ mới.",
                    expEarned = totalExp,
                    itemDrop = "${harvestedInThisCycle * 3}x $herbName"
                )
            )
        } else {
            _liveState.value = _liveState.value.copy(
                actionText = "Đang tưới nước và chăm sóc dược thảo",
                subActionDetail = "Cây đang sinh trưởng tốt, dự kiến thu hoạch ở chu kỳ tiếp theo"
            )
        }
        delay(randomJitter(minDelay, maxDelay))
    }

    private suspend fun runPunishEvilCycle(repo: BotRepository, minDelay: Long, maxDelay: Long) {
        val evilConfig = repo.punishEvilConfig.firstOrNull()
        val evilLvl = evilConfig?.evilLevel ?: EvilLevel.CAP_70_80
        val isUntilLimit = evilConfig?.runUntilLimitReached ?: true
        val targetTokens = if (isUntilLimit) "Chạy đến khi hết hạn nhận hôm nay" else "${evilConfig?.dailyTokensToUse ?: 50} lệnh"
        val newEvilCount = _liveState.value.sessionEvils + 1

        // BƯỚC 1: Tìm đường đến Tổng bắt đầu Tô Châu - Ngô Giới để đối thoại và nhận nhiệm vụ Trừng Trị Hung Đồ
        _liveState.value = _liveState.value.copy(
            currentCategory = TaskCategory.PUNISH_EVIL,
            currentMapName = "Thành Tô Châu - NPC Ngô Giới",
            actionText = "Bước 1/8: Tìm Tổng bắt đầu Tô Châu - Ngô Giới nhận [Trừng Trị Hung Đồ]",
            subActionDetail = "Tự động đối thoại NPC Ngô Giới, nhận nhiệm vụ và lệnh bài ($targetTokens)"
        )
        delay(randomJitter(minDelay, maxDelay))

        // BƯỚC 2: Mở túi đồ -> Ô Nhiệm Vụ -> Tìm Trừng Ác Lệnh -> Ấn Sử Dụng
        _liveState.value = _liveState.value.copy(
            actionText = "Bước 2/8: Mở Túi Đồ -> Ô Nhiệm Vụ -> Sử dụng Trừng Ác Lệnh",
            subActionDetail = "Truy cập túi đồ cá nhân, chọn thẻ Nhiệm Vụ, click 'Trừng Ác Lệnh' và bấm Sử Dụng"
        )
        delay(randomJitter(minDelay, maxDelay))

        // BƯỚC 3 & 4: Đọc tọa độ gợi ý & ấn tọa độ để tự di chuyển đến sào huyệt Đầu Mục Ác Nhân
        _liveState.value = _liveState.value.copy(
            actionText = "Bước 3-4/8: Đọc gợi ý tọa độ -> Di chuyển đến Sào Huyệt Ác Nhân",
            subActionDetail = "Hệ thống thông báo: 'Các hạ phải đến (Tọa độ) mới có thể dùng lệnh' -> Tự click tọa độ di chuyển"
        )
        delay(randomJitter(minDelay + 400, maxDelay + 800))

        // BƯỚC 5: Đến tọa độ chỉ định -> Xuống tọa kỵ -> Mở túi đồ (Ô Nhiệm Vụ) -> Bấm Sử Dụng Trừng Ác Lệnh
        _liveState.value = _liveState.value.copy(
            currentMapName = "Sào Huyệt Ác Nhân (${evilLvl.levelName})",
            actionText = "Bước 5/8: Đến tọa độ -> Xuống tọa kỵ -> Sử dụng Trừng Ác Lệnh gọi quái",
            subActionDetail = "Tự động xuống ngựa/tọa kỵ, mở túi đồ nhiệm vụ và kích hoạt Trừng Ác Lệnh triệu hồi Ngô Nhân Hách"
        )
        delay(randomJitter(minDelay, maxDelay))

        // BƯỚC 6: Bật Auto đánh Ngô Nhân Hách -> Tiêu diệt quái nhặt Tàng Bảo Đồ -> Tắt Auto
        _liveState.value = _liveState.value.copy(
            actionText = "Bước 6/8: Bật Auto đánh Ngô Nhân Hách -> Nhặt Tàng Bảo Đồ -> Tắt Auto",
            subActionDetail = "Kích hoạt chế độ Auto chiến đấu môn phái. Sau khi trảm sát quái và nhận Tàng Bảo Đồ, tắt Auto"
        )
        delay(randomJitter(minDelay + 600, maxDelay + 1200))

        // BƯỚC 7: Vào túi đồ chọn Bạch Sắc Định Vị Phù -> Ấn sử dụng để bay về Tô Châu
        _liveState.value = _liveState.value.copy(
            currentMapName = "Thành Tô Châu",
            actionText = "Bước 7/8: Sử dụng [Bạch Sắc Định Vị Phù] dịch chuyển về Tô Châu",
            subActionDetail = "Mở túi đồ, kích hoạt Bạch Sắc Định Vị Phù để tức thì quay về trung tâm thành Tô Châu"
        )
        delay(randomJitter(minDelay, maxDelay))

        // BƯỚC 8: Lên tọa kỵ -> Tìm NPC Ngô Giới trả nhiệm vụ -> Lặp lại đến khi hết hạn
        val exp = 38000
        val gold = 5200
        _liveState.value = _liveState.value.copy(
            currentMapName = "Thành Tô Châu - NPC Ngô Giới",
            sessionExp = _liveState.value.sessionExp + exp,
            sessionGold = _liveState.value.sessionGold + gold,
            sessionEvils = newEvilCount,
            playerHpPercent = 100,
            playerMpPercent = 95,
            actionText = "Bước 8/8: Lên tọa kỵ -> Gặp Ngô Giới trả nhiệm vụ (Lượt #$newEvilCount)!",
            subActionDetail = "Nhận thưởng Exp, Vàng, Tàng Bảo Đồ và tiếp tục vòng tiếp theo cho đến khi Ngô Giới báo hết hạn hôm nay"
        )
        repo.addLog(
            BotLogEntity(
                category = "TRỪNG ÁC",
                actionText = "Hoàn thành vòng Trừng Trị Hung Đồ #$newEvilCount (Trảm Ngô Nhân Hách)",
                detail = "Quy trình 8 bước: Nhận Ngô Giới -> Dùng Lệnh bài -> Di chuyển tọa độ -> Xuống tọa kỵ -> Đánh Ngô Nhân Hách -> Nhặt Tàng Bảo Đồ -> Dùng Phù về Tô Châu -> Trả nhiệm vụ.",
                expEarned = exp,
                goldEarned = gold,
                itemDrop = "Tàng Bảo Đồ, Rương Trừng Ác",
                isHighlight = true
            )
        )
        delay(randomJitter(minDelay, maxDelay))
    }

    private suspend fun runGuildQuestCycle(repo: BotRepository, minDelay: Long, maxDelay: Long) {
        val guildConfig = repo.guildConfig.firstOrNull()
        val targetLoops = guildConfig?.targetLoops ?: 50
        val currentLoopNum = (_liveState.value.sessionGuildQuests % targetLoops) + 1

        val questTypes = listOf(
            GuildQuestType.HOI_VU,
            GuildQuestType.VAN_TIEU,
            GuildQuestType.TUAN_TRA,
            GuildQuestType.LUYEN_CONG
        )
        val selectedQuest = questTypes.random()

        _liveState.value = _liveState.value.copy(
            currentCategory = TaskCategory.GUILD_QUEST,
            currentMapName = "Tổng Đà Bang Hội - Lãnh Địa",
            actionText = "Bang Hội (Vòng #$currentLoopNum/$targetLoops): ${selectedQuest.title}...",
            subActionDetail = "Tự động di chuyển đến NPC Chưởng Quản Bang để nhận chuỗi 50 việc bang"
        )
        delay(randomJitter(minDelay, maxDelay))

        _liveState.value = _liveState.value.copy(
            actionText = "Đang thực hiện ${selectedQuest.title} (Vòng #$currentLoopNum/$targetLoops)...",
            subActionDetail = "Tự động đánh quái vật bảo tiêu & nộp quân lương bang hội"
        )
        delay(randomJitter(minDelay + 400, maxDelay + 800))

        val exp = selectedQuest.expGain
        val gold = 2800
        _liveState.value = _liveState.value.copy(
            sessionExp = _liveState.value.sessionExp + exp,
            sessionGold = _liveState.value.sessionGold + gold,
            sessionGuildQuests = _liveState.value.sessionGuildQuests + 1,
            actionText = "Hoàn tất vòng #$currentLoopNum/$targetLoops: ${selectedQuest.title} (+${selectedQuest.devGain} Cống Hiến)",
            subActionDetail = "Tự động nhận thưởng Exp Bang và tiếp tục vòng tiếp theo trong chuỗi 50"
        )
        repo.addLog(
            BotLogEntity(
                category = "BANG HỘI",
                actionText = "Hoàn tất vòng #$currentLoopNum/$targetLoops (${selectedQuest.title})",
                detail = "Nhận +$exp Exp, +$gold Vàng, +${selectedQuest.devGain} Điểm Cống Hiến Bang.",
                expEarned = exp,
                goldEarned = gold,
                itemDrop = "${selectedQuest.devGain} Cống Hiến Bang"
            )
        )
        delay(randomJitter(minDelay, maxDelay))
    }

    private suspend fun runMiningCycle(repo: BotRepository, minDelay: Long, maxDelay: Long) {
        val miningConfig = repo.miningConfig.firstOrNull()
        val targetOre = miningConfig?.targetOre ?: TargetOre.HUYEN_THIET
        val map = miningConfig?.selectedMap ?: MineMap.THAI_SON

        _liveState.value = _liveState.value.copy(
            currentCategory = TaskCategory.MINING,
            currentMapName = map.mapName,
            actionText = "Đang dò tìm mạch khoáng [${targetOre.oreName}] tại ${map.mapName}...",
            subActionDetail = "Kích hoạt radar quét quặng và kiểm tra an toàn khu vực"
        )
        delay(randomJitter(minDelay, maxDelay))

        _liveState.value = _liveState.value.copy(
            actionText = "Đang khai thác mạch khoáng ${targetOre.oreName}...",
            subActionDetail = "Chu kỳ đào 3/3 nhịp, kiểm tra độ bền cuốc và thể lực nhân vật",
            playerStaminaPercent = (_liveState.value.playerStaminaPercent - 4).coerceAtLeast(30)
        )
        delay(randomJitter(minDelay + 600, maxDelay + 1200))

        val minedCount = Random.nextInt(2, 5)
        val oreExp = minedCount * 1200
        val oreGold = minedCount * targetOre.sellPrice

        _liveState.value = _liveState.value.copy(
            sessionExp = _liveState.value.sessionExp + oreExp,
            sessionGold = _liveState.value.sessionGold + oreGold,
            sessionOres = _liveState.value.sessionOres + minedCount,
            actionText = "Khai thác thành công ${minedCount}x ${targetOre.oreName}!",
            subActionDetail = "Cất giữ vào túi đồ và tiếp tục tìm mạch khoáng tiếp theo"
        )
        repo.addLog(
            BotLogEntity(
                category = "ĐÀO KHOÁNG",
                actionText = "Khai thác ${minedCount}x ${targetOre.oreName}",
                detail = "Tại bản đồ ${map.mapName}. Phẩm chất: ${targetOre.rarity}.",
                expEarned = oreExp,
                goldEarned = oreGold,
                itemDrop = "${minedCount}x ${targetOre.oreName}"
            )
        )
        delay(randomJitter(minDelay, maxDelay))
    }

    private fun randomJitter(min: Long, max: Long): Long {
        return if (max > min) Random.nextLong(min, max) else min
    }
}
