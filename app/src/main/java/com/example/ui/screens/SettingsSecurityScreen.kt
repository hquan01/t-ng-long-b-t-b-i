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
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.example.model.SectType
import com.example.ui.components.SettingToggleItem
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
fun SettingsSecurityScreen(
    botConfig: BotConfigEntity,
    onUpdateBotConfig: ((BotConfigEntity) -> BotConfigEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sect & Character Profile
        SectSelectorCard(
            selectedSect = botConfig.sect,
            onSelectSect = { sect ->
                onUpdateBotConfig { it.copy(sect = sect) }
            }
        )

        // Anti-Ban & Human-like Jitter Delay
        Card(
            colors = CardDefaults.cardColors(containerColor = InkCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, InkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = JadePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CƠ CHẾ CHỐNG BAN & MÔ PHỎNG NGƯỜI THẬT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextJade
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hệ thống tự động thêm độ trễ ngẫu nhiên (Jitter Delay) giữa các thao tác bấm chuột, tung chiêu và di chuyển để chống hệ thống quét tự động của nhà phát hành.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Độ trễ thao tác tối thiểu: ${botConfig.minActionDelayMs}ms",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1814))
                            .border(1.dp, Color(0xFF263A32), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            onClick = {
                                if (botConfig.minActionDelayMs > 800L) {
                                    onUpdateBotConfig { it.copy(minActionDelayMs = it.minActionDelayMs - 200L) }
                                }
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_decrease_delay")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = TextPrimary)
                        }
                        IconButton(
                            onClick = {
                                if (botConfig.minActionDelayMs < 4000L) {
                                    onUpdateBotConfig { it.copy(minActionDelayMs = it.minActionDelayMs + 200L) }
                                }
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_increase_delay")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Tăng", tint = TextPrimary)
                        }
                    }
                }
            }
        }

        // Auto Potion & Survival Settings
        Text(
            text = "TỰ ĐỘNG HỒI MÁU & BẢO VỆ NHÂN VẬT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "Tự Động Uống Bình Máu Khi HP < ${botConfig.autoUseHpPotionThreshold}%",
            description = "Khi lượng máu tụt thấp trong lúc đánh quái hoặc trừng ác, tự động dùng Kim Sang Dược.",
            checked = true,
            onCheckedChange = {},
            testTag = "switch_auto_hp_potion"
        )

        SettingToggleItem(
            title = "Tự Động Hồi Sinh Tại Chỗ Hoặc Về Điểm Lưu",
            description = "Nếu chẳng may bị trọng thương do quái tinh anh hoặc người chơi, tự động hồi sinh để tiếp tục.",
            checked = botConfig.autoReviveAtSpawn,
            onCheckedChange = { checked -> onUpdateBotConfig { it.copy(autoReviveAtSpawn = checked) } },
            testTag = "switch_auto_revive"
        )

        SettingToggleItem(
            title = "Tự Động Sửa Chữa Toàn Bộ Trang Bị",
            description = "Định kỳ kiểm tra độ bền giáp trụ, vũ khí và sửa chữa khi về thành nộp nhiệm vụ.",
            checked = botConfig.autoRepairEquipment,
            onCheckedChange = { checked -> onUpdateBotConfig { it.copy(autoRepairEquipment = checked) } },
            testTag = "switch_auto_repair_equipment"
        )

        // Background service & battery
        Text(
            text = "DỊCH VỤ CHẠY NỀN & TIẾT KIỆM PIN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "Kích Hoạt Bóng Nổi Điều Khiển Nhanh (Floating Widget)",
            description = "Hiển thị nút tròn mini nổi trên game để bắt đầu/tạm dừng auto nhanh không cần chuyển ứng dụng.",
            checked = botConfig.floatingOverlayEnabled,
            onCheckedChange = { checked -> onUpdateBotConfig { it.copy(floatingOverlayEnabled = checked) } },
            testTag = "switch_floating_overlay"
        )

        SettingToggleItem(
            title = "Rung & Chuông Khi Rớt Đồ Cực Phẩm (Hoàng Kim)",
            description = "Gửi thông báo âm thanh nổi bật khi nhặt được Bí Tịch quý, Thần Binh Toái Phiến hoặc Quặng Hoàng Kim.",
            checked = botConfig.soundAlertOnRareDrop,
            onCheckedChange = { checked -> onUpdateBotConfig { it.copy(soundAlertOnRareDrop = checked) } },
            testTag = "switch_rare_drop_alert"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SectSelectorCard(
    selectedSect: SectType,
    onSelectSect: (SectType) -> Unit,
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
                text = "MÔN PHÁI NHÂN VẬT (TỐI ƯU HÓA COMBO CHIÊU)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFF0F1814),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263A32)),
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
                            Text(
                                text = selectedSect.sectName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold
                            )
                            Text(
                                text = selectedSect.specialty,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Text(text = "Đổi Môn Phái ▼", color = JadePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF111E18))
                ) {
                    SectType.values().forEach { sect ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = sect.sectName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = sect.specialty, color = TextMuted, fontSize = 11.sp)
                                }
                            },
                            onClick = {
                                onSelectSect(sect)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
