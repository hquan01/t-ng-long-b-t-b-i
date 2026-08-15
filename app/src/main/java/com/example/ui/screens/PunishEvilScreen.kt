package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.PunishEvilConfigEntity
import com.example.model.EvilLevel
import com.example.ui.components.SettingToggleItem
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InkCard
import com.example.ui.theme.InkCardBorder
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextJade
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PunishEvilScreen(
    botConfig: BotConfigEntity,
    punishEvilConfig: PunishEvilConfigEntity,
    customActionSteps: List<com.example.data.entity.CustomActionStepEntity>,
    onUpdateBotConfig: ((BotConfigEntity) -> BotConfigEntity) -> Unit,
    onUpdatePunishEvilConfig: ((PunishEvilConfigEntity) -> PunishEvilConfigEntity) -> Unit,
    onAddActionStep: (com.example.data.entity.CustomActionStepEntity) -> Unit,
    onUpdateActionStep: (com.example.data.entity.CustomActionStepEntity) -> Unit,
    onDeleteActionStep: (Long) -> Unit,
    onResetDefaultSteps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStep by remember { mutableStateOf<com.example.data.entity.CustomActionStepEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Punish Evil Toggle Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF221414)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF381212),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = "Trừng Ác",
                                    tint = CrimsonPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tự Động Nhiệm Vụ Trừng Ác",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonPrimary
                            )
                            Text(
                                text = "Nhận & Trả tại NPC Tổng bắt đầu Tô Châu - Ngô Giới",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    SettingToggleItem(
                        title = "",
                        description = "",
                        checked = botConfig.isPunishEvilEnabled,
                        onCheckedChange = { isEnabled ->
                            onUpdateBotConfig { it.copy(isPunishEvilEnabled = isEnabled) }
                        },
                        testTag = "switch_punish_evil_enabled",
                        modifier = Modifier.width(60.dp)
                    )
                }
            }
        }

        // Chi Tiết Quy Trình 8 Bước Auto Trừng Trị Hung Đồ Chuẩn 100% Theo Game
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161C19)),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, JadePrimary.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Quy Trình 8 Bước Auto Trừng Trị Hung Đồ:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextJade
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val steps = listOf(
                    "1. Tự động tìm đường đến Tổng bắt đầu Tô Châu - Ngô Giới đối thoại & nhận nhiệm vụ [Trừng Trị Hung Đồ].",
                    "2. Mở Túi Đồ -> Chọn thẻ ô [Nhiệm Vụ] -> Tìm Trừng Ác Lệnh -> Ấn [Sử Dụng].",
                    "3. Hệ thống hiện hướng dẫn: \"Các hạ phải đến (Tọa Độ) mới có thể dùng Trừng Ác Lệnh\".",
                    "4. Tự động ấn vào [Tọa Độ Gợi Ý] để nhân vật tự phi thân di chuyển đến sào huyệt truy kích Đầu Mục Ác Nhân.",
                    "5. Khi đến tọa độ chỉ định: Tự động xuống tọa kỵ -> Mở Túi Đồ (Ô Nhiệm Vụ) -> Chọn Trừng Ác Lệnh ấn [Sử Dụng] để gọi quái.",
                    "6. Tự động bật Auto xuất chiêu đánh bại quái [Ngô Nhân Hách] -> Nhặt vật phẩm rơi [Tàng Bảo Đồ] -> Tắt Auto.",
                    "7. Vào Túi Đồ chọn vật phẩm [Bạch Sắc Định Vị Phù] ấn [Sử Dụng] để lập tức bay về thành Tô Châu.",
                    "8. Lên tọa kỵ và tìm NPC Ngô Giới trả nhiệm vụ. Tự động lặp lại liên tục cho đến khi Ngô Giới báo \"Đã hết hạn nhận nhiệm vụ hôm nay\"."
                )

                steps.forEach { stepText ->
                    Text(
                        text = stepText,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        // Evil Level Boss Selection Card
        EvilLevelSelectorCard(
            selectedLevel = punishEvilConfig.evilLevel,
            onSelectLevel = { level ->
                onUpdatePunishEvilConfig { it.copy(evilLevel = level) }
            }
        )

        // Run until limit reached option
        SettingToggleItem(
            title = "Làm Đến Khi Báo Không Nhận Được Nữa (Tối Đa Giới Hạn Ngày)",
            description = "Tự động nhận nhiệm vụ và đánh liên tục không ngừng cho đến khi NPC báo đã đạt giới hạn hôm nay / không thể nhận thêm.",
            checked = punishEvilConfig.runUntilLimitReached,
            onCheckedChange = { checked -> onUpdatePunishEvilConfig { it.copy(runUntilLimitReached = checked) } },
            testTag = "switch_run_until_limit"
        )

        // Daily Tokens Selector (if not running until limit or as backup target)
        if (!punishEvilConfig.runUntilLimitReached) {
            Card(
                colors = CardDefaults.cardColors(containerColor = InkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, InkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Số Trừng Ác Lệnh Mục Tiêu",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Dừng lại khi làm đủ số lệnh đã chọn",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1814))
                            .border(1.dp, Color(0xFF263A32), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            onClick = {
                                if (punishEvilConfig.dailyTokensToUse > 5) {
                                    onUpdatePunishEvilConfig { it.copy(dailyTokensToUse = it.dailyTokensToUse - 5) }
                                }
                            },
                            modifier = Modifier.size(36.dp).testTag("btn_decrease_tokens")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = TextPrimary)
                        }

                        Text(
                            text = "${punishEvilConfig.dailyTokensToUse}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        IconButton(
                            onClick = {
                                if (punishEvilConfig.dailyTokensToUse < 100) {
                                    onUpdatePunishEvilConfig { it.copy(dailyTokensToUse = it.dailyTokensToUse + 5) }
                                }
                            },
                            modifier = Modifier.size(36.dp).testTag("btn_increase_tokens")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Tăng", tint = TextPrimary)
                        }
                    }
                }
            }
        }

        // Combat & Bounty Options
        Text(
            text = "CHIẾN ĐẤU & PHẦN THƯỞNG TRỪNG ÁC",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "Tự Động Ghép Đội Trừng Ác",
            description = "Tự tạo tổ đội hoặc xin vào đội có sẵn để kích hoạt thêm 20% Exp đội nhóm.",
            checked = punishEvilConfig.autoMatchParty,
            onCheckedChange = { checked -> onUpdatePunishEvilConfig { it.copy(autoMatchParty = checked) } },
            testTag = "switch_auto_match_party"
        )

        SettingToggleItem(
            title = "Tự Động Tung Combo Tuyệt Kỹ Môn Phái",
            description = "Tối ưu hóa vòng chiêu: Định thân -> Khống chế diện rộng -> Xuất đòn bạo kích dứt điểm.",
            checked = punishEvilConfig.autoUseSupremeCombo,
            onCheckedChange = { checked -> onUpdatePunishEvilConfig { it.copy(autoUseSupremeCombo = checked) } },
            testTag = "switch_auto_combo_skills"
        )

        SettingToggleItem(
            title = "Tự Động Mở Rương Bảo Vật Trừng Ác",
            description = "Khi tiêu diệt ác nhân và nhặt được Rương Vàng/Bạc, tự động mở ngay để lấy Bí Tịch & Ngân Phiếu.",
            checked = punishEvilConfig.autoOpenBountyChests,
            onCheckedChange = { checked -> onUpdatePunishEvilConfig { it.copy(autoOpenBountyChests = checked) } },
            testTag = "switch_auto_open_chests"
        )

        SettingToggleItem(
            title = "Tự Động Mua Bổ Sung Trừng Ác Lệnh",
            description = "Nếu hết lệnh bài trong ngày, tự dùng ngân lượng đổi mua tại Tiệm Bang Hội hoặc Cửa Hàng.",
            checked = punishEvilConfig.autoBuyTokensFromStore,
            onCheckedChange = { checked -> onUpdatePunishEvilConfig { it.copy(autoBuyTokensFromStore = checked) } },
            testTag = "switch_auto_buy_tokens"
        )

        // QUẢN LÝ DANH SÁCH HÀNH ĐỘNG AUTO (THÊM, SỬA, XÓA & LƯU LẠI)
        Card(
            colors = CardDefaults.cardColors(containerColor = InkCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "TRÌNH QUẢN LÝ CHUỖI HÀNH ĐỘNG",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold
                            )
                            Text(
                                text = "Thêm, sửa, xóa các bước & lưu cố định vào bộ nhớ",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Row {
                        OutlinedButton(
                            onClick = onResetDefaultSteps,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            modifier = Modifier.height(34.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Khôi phục", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mặc Định", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                            modifier = Modifier.height(34.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thêm Bước", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (customActionSteps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có bước hành động nào. Hãy bấm 'Mặc Định' hoặc 'Thêm Bước'!",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                } else {
                    customActionSteps.forEachIndexed { index, step ->
                        ActionStepItemRow(
                            stepIndex = index + 1,
                            step = step,
                            onToggleEnabled = { isChecked ->
                                onUpdateActionStep(step.copy(isEnabled = isChecked))
                            },
                            onEdit = {
                                editingStep = step
                            },
                            onDelete = {
                                onDeleteActionStep(step.id)
                            },
                            onQuickDelayChange = { newDelay ->
                                onUpdateActionStep(step.copy(delaySeconds = newDelay))
                            }
                        )
                    }
                }
            }
        }

        // TÙY CHỈNH THỜI GIAN CHỜ TỪNG HÀNH ĐỘNG (ACTION DELAY TIMINGS)
        Card(
            colors = CardDefaults.cardColors(containerColor = InkCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TÙY CHỈNH THỜI GIAN CHỜ TỪNG HÀNH ĐỘNG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                        Text(
                            text = "Tăng/giảm số giây chờ phù hợp với tốc độ mạng & cấu hình máy của bạn",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                ActionDelayAdjuster(
                    title = "1. Chờ mở bảng Ngô Giới",
                    subtitle = "Thời gian chờ sau khi chạm bong bóng đối thoại NPC",
                    valueSec = punishEvilConfig.delayOpenNpcDialogSec,
                    minVal = 1,
                    maxVal = 10,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayOpenNpcDialogSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "2. Chờ chọn dòng Trừng Trị",
                    subtitle = "Thời gian chờ để hệ thống chọn mục Trừng Trị Hung Đồ",
                    valueSec = punishEvilConfig.delaySelectQuestSec,
                    minVal = 1,
                    maxVal = 8,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delaySelectQuestSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "3. Chờ nhận nhiệm vụ",
                    subtitle = "Thời gian chờ server phát Lệnh bài vào Túi đồ",
                    valueSec = punishEvilConfig.delayAcceptQuestSec,
                    minVal = 1,
                    maxVal = 10,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayAcceptQuestSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "4. Chờ mở bảng Túi Đồ",
                    subtitle = "Thời gian chờ hiển thị giao diện Túi Đồ",
                    valueSec = punishEvilConfig.delayOpenBagSec,
                    minVal = 1,
                    maxVal = 8,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayOpenBagSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "5. Chờ chọn Tab Nhiệm Vụ",
                    subtitle = "Thời gian chờ chuyển sang ngăn chứa Lệnh bài",
                    valueSec = punishEvilConfig.delaySelectTabSec,
                    minVal = 1,
                    maxVal = 8,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delaySelectTabSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "6. Chờ bấm Lệnh Bài",
                    subtitle = "Thời gian chờ hiện popup sử dụng & tọa độ gợi ý",
                    valueSec = punishEvilConfig.delayUseTokenSec,
                    minVal = 1,
                    maxVal = 10,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayUseTokenSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "7. Chờ chạy đến bãi Boss (Tọa độ)",
                    subtitle = "Thời gian phi thân / cưỡi thú chạy từ Tô Châu tới bãi quái",
                    valueSec = punishEvilConfig.delayTravelToBossSec,
                    minVal = 5,
                    maxVal = 60,
                    step = 2,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayTravelToBossSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "8. Chờ xuống ngựa & gọi Boss",
                    subtitle = "Thời gian tắt thú cưỡi và dùng lại Lệnh bài để quái xuất hiện",
                    valueSec = punishEvilConfig.delayDismountAndSummonSec,
                    minVal = 1,
                    maxVal = 10,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayDismountAndSummonSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "9. Thời gian Auto Đánh diệt Boss",
                    subtitle = "Thời gian bật Auto treo máy & xả combo chiêu cho Boss chết",
                    valueSec = punishEvilConfig.delayCombatDurationSec,
                    minVal = 5,
                    maxVal = 60,
                    step = 2,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayCombatDurationSec = newVal) } }
                )

                ActionDelayAdjuster(
                    title = "10. Chờ Phù biến về Tô Châu",
                    subtitle = "Thời gian sử dụng Bạch Sắc Định Vị Phù & load map về thành",
                    valueSec = punishEvilConfig.delayTeleportRecallSec,
                    minVal = 2,
                    maxVal = 15,
                    onValueChange = { newVal -> onUpdatePunishEvilConfig { it.copy(delayTeleportRecallSec = newVal) } }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Dialog Thêm Bước Hành Động Mới
    if (showAddDialog) {
        AddActionStepDialog(
            nextOrder = customActionSteps.size + 1,
            onDismiss = { showAddDialog = false },
            onConfirm = { newStep ->
                onAddActionStep(newStep)
                showAddDialog = false
            }
        )
    }

    // Dialog Sửa Bước Hành Động
    editingStep?.let { stepToEdit ->
        EditActionStepDialog(
            step = stepToEdit,
            onDismiss = { editingStep = null },
            onConfirm = { updatedStep ->
                onUpdateActionStep(updatedStep)
                editingStep = null
            }
        )
    }
}

@Composable
private fun EvilLevelSelectorCard(
    selectedLevel: EvilLevel,
    onSelectLevel: (EvilLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = InkCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, InkCardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "MỨC ĐỘ ÁC NHÂN TRUY QUÉT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFF160E0E),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B1E1E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedLevel.levelName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF381010),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Cấp ${selectedLevel.levelReq}+",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Phần thưởng: ${selectedLevel.rewardDesc}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Text(text = "Đổi ▼", color = CrimsonPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF1A1111))
                ) {
                    EvilLevel.values().forEach { evil ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = evil.levelName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "Yêu cầu: Cấp ${evil.levelReq}+ • ${evil.rewardDesc}", color = TextMuted, fontSize = 11.sp)
                                }
                            },
                            onClick = {
                                onSelectLevel(evil)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionDelayAdjuster(
    title: String,
    subtitle: String,
    valueSec: Int,
    minVal: Int = 1,
    maxVal: Int = 30,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF140D0D))
            .border(1.dp, Color(0xFF2E1A1A), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F0808))
                .border(1.dp, Color(0xFF3D1F1F), RoundedCornerShape(8.dp))
        ) {
            IconButton(
                onClick = {
                    if (valueSec - step >= minVal) {
                        onValueChange(valueSec - step)
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }

            Text(
                text = "${valueSec}s",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = {
                    if (valueSec + step <= maxVal) {
                        onValueChange(valueSec + step)
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tăng", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ActionStepItemRow(
    stepIndex: Int,
    step: com.example.data.entity.CustomActionStepEntity,
    onToggleEnabled: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuickDelayChange: (Int) -> Unit
) {
    Surface(
        color = if (step.isEnabled) Color(0xFF141A17) else Color(0xFF101211),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (step.isEnabled) Color(0xFF284436) else Color(0xFF222825)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (step.isEnabled) JadePrimary else Color.DarkGray,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$stepIndex",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = step.actionName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (step.isEnabled) TextPrimary else TextMuted
                        )
                        if (step.description.isNotEmpty()) {
                            Text(
                                text = step.description,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Switch Bật/Tắt bước này
                Switch(
                    checked = step.isEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = JadePrimary,
                        checkedTrackColor = Color(0xFF0F3826),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color(0xFF1F2623)
                    ),
                    modifier = Modifier.size(width = 44.dp, height = 28.dp)
                )
            }

            // Thanh điều khiển độ trễ & nút Sửa/Xóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tọa độ % màn hình
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tọa độ: (${(step.screenXPercent * 100).toInt()}%, ${(step.screenYPercent * 100).toInt()}%)",
                        fontSize = 11.sp,
                        color = TextGold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Điều chỉnh nhanh giây chờ
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0B1410))
                            .border(1.dp, Color(0xFF1E3328), RoundedCornerShape(6.dp))
                    ) {
                        IconButton(
                            onClick = {
                                if (step.delaySeconds > 1) {
                                    onQuickDelayChange(step.delaySeconds - 1)
                                }
                            },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = TextPrimary, modifier = Modifier.size(12.dp))
                        }

                        Text(
                            text = "${step.delaySeconds}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = {
                                if (step.delaySeconds < 60) {
                                    onQuickDelayChange(step.delaySeconds + 1)
                                }
                            },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Tăng", tint = TextPrimary, modifier = Modifier.size(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Nút Sửa
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                    }

                    // Nút Xóa
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddActionStepDialog(
    nextOrder: Int,
    onDismiss: () -> Unit,
    onConfirm: (com.example.data.entity.CustomActionStepEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var delaySec by remember { mutableStateOf("2") }
    var xPercent by remember { mutableStateOf(50f) }
    var yPercent by remember { mutableStateOf(50f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF151D18),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm Bước Hành Động Mới", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên hành động (VD: Bấm Nút Túi Đồ)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Mô tả chi tiết (Tùy chọn)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = delaySec,
                    onValueChange = { if (it.all { char -> char.isDigit() }) delaySec = it },
                    label = { Text("Thời gian chờ sau khi click (Giây)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Vị trí chạm trên màn hình: X = ${xPercent.toInt()}%, Y = ${yPercent.toInt()}%",
                    color = TextGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text("Tọa độ ngang X: ${xPercent.toInt()}%", fontSize = 11.sp, color = TextSecondary)
                    Slider(
                        value = xPercent,
                        onValueChange = { xPercent = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                    )
                }

                Column {
                    Text("Tọa độ dọc Y: ${yPercent.toInt()}%", fontSize = 11.sp, color = TextSecondary)
                    Slider(
                        value = yPercent,
                        onValueChange = { yPercent = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = JadePrimary, activeTrackColor = JadePrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val delay = delaySec.toIntOrNull() ?: 2
                        onConfirm(
                            com.example.data.entity.CustomActionStepEntity(
                                stepOrder = nextOrder,
                                actionName = name.trim(),
                                description = desc.trim(),
                                delaySeconds = delay.coerceIn(1, 120),
                                screenXPercent = xPercent / 100f,
                                screenYPercent = yPercent / 100f,
                                isEnabled = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
                Text("Lưu Vào Bộ Nhớ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
            ) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun EditActionStepDialog(
    step: com.example.data.entity.CustomActionStepEntity,
    onDismiss: () -> Unit,
    onConfirm: (com.example.data.entity.CustomActionStepEntity) -> Unit
) {
    var name by remember { mutableStateOf(step.actionName) }
    var desc by remember { mutableStateOf(step.description) }
    var delaySec by remember { mutableStateOf("${step.delaySeconds}") }
    var xPercent by remember { mutableStateOf(step.screenXPercent * 100f) }
    var yPercent by remember { mutableStateOf(step.screenYPercent * 100f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF151D18),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF60A5FA))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chỉnh Sửa Bước Hành Động", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên hành động") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Mô tả chi tiết") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = delaySec,
                    onValueChange = { if (it.all { char -> char.isDigit() }) delaySec = it },
                    label = { Text("Thời gian chờ sau khi click (Giây)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Vị trí chạm trên màn hình: X = ${xPercent.toInt()}%, Y = ${yPercent.toInt()}%",
                    color = TextGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text("Tọa độ ngang X: ${xPercent.toInt()}%", fontSize = 11.sp, color = TextSecondary)
                    Slider(
                        value = xPercent,
                        onValueChange = { xPercent = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                    )
                }

                Column {
                    Text("Tọa độ dọc Y: ${yPercent.toInt()}%", fontSize = 11.sp, color = TextSecondary)
                    Slider(
                        value = yPercent,
                        onValueChange = { yPercent = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = JadePrimary, activeTrackColor = JadePrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val delay = delaySec.toIntOrNull() ?: step.delaySeconds
                        onConfirm(
                            step.copy(
                                actionName = name.trim(),
                                description = desc.trim(),
                                delaySeconds = delay.coerceIn(1, 120),
                                screenXPercent = xPercent / 100f,
                                screenYPercent = yPercent / 100f
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6), contentColor = Color.White)
            ) {
                Text("Cập Nhật & Lưu", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
            ) {
                Text("Hủy")
            }
        }
    )
}
