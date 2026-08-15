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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
    onUpdateBotConfig: ((BotConfigEntity) -> BotConfigEntity) -> Unit,
    onUpdatePunishEvilConfig: ((PunishEvilConfigEntity) -> PunishEvilConfigEntity) -> Unit,
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
                                text = "Săn lùng ác nhân, dùng lệnh bài & nhặt rương",
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

        // Evil Level Boss Selection Card
        EvilLevelSelectorCard(
            selectedLevel = punishEvilConfig.evilLevel,
            onSelectLevel = { level ->
                onUpdatePunishEvilConfig { it.copy(evilLevel = level) }
            }
        )

        // Daily Tokens Selector
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
                        text = "Số Trừng Ác Lệnh Mỗi Ngày",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Khuyến nghị 20-30 lệnh để tối đa hóa Exp",
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
                            if (punishEvilConfig.dailyTokensToUse < 50) {
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

        Spacer(modifier = Modifier.height(20.dp))
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
