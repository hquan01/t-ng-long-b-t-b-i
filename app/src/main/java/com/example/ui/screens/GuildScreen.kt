package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.GuildQuestConfigEntity
import com.example.ui.components.SettingToggleItem
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
fun GuildScreen(
    botConfig: BotConfigEntity,
    guildConfig: GuildQuestConfigEntity,
    onUpdateBotConfig: ((BotConfigEntity) -> BotConfigEntity) -> Unit,
    onUpdateGuildConfig: ((GuildQuestConfigEntity) -> GuildQuestConfigEntity) -> Unit,
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
        // Master Guild Toggle Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14201B)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, JadePrimary.copy(alpha = 0.4f)),
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
                            color = Color(0xFF1B3D2F),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Castle,
                                    contentDescription = "Bang Hội",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nhiệm Vụ Bang Hội",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold
                            )
                            Text(
                                text = "Tự động hoàn thành chuỗi việc bang",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    SettingToggleItem(
                        title = "",
                        description = "",
                        checked = botConfig.isGuildQuestEnabled,
                        onCheckedChange = { isEnabled ->
                            onUpdateBotConfig { it.copy(isGuildQuestEnabled = isEnabled) }
                        },
                        testTag = "switch_guild_enabled",
                        modifier = Modifier.width(60.dp)
                    )
                }
            }
        }

        // Loop counter selector
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Số Vòng Nhiệm Vụ Bang Mỗi Ngày",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Chuỗi 50 vòng Hội Vụ Bang chuẩn theo game Tàng Long Bất Bại",
                        fontSize = 12.sp,
                        color = TextJade
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
                            if (guildConfig.targetLoops > 5) {
                                onUpdateGuildConfig { it.copy(targetLoops = it.targetLoops - 5) }
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("btn_decrease_guild_loops")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = TextPrimary)
                    }

                    Text(
                        text = "${guildConfig.targetLoops}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGold,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    IconButton(
                        onClick = {
                            if (guildConfig.targetLoops < 100) {
                                onUpdateGuildConfig { it.copy(targetLoops = it.targetLoops + 5) }
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("btn_increase_guild_loops")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tăng", tint = TextPrimary)
                    }
                }
            }
        }

        // Sub-quests list
        Text(
            text = "DANH SÁCH TÁC VỤ BANG TỰ ĐỘNG",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "1. Hội Vụ Bang Hội (Nhiệm Vụ Thường)",
            description = "Tự động chạy tới NPC Bang, nhận thư, đối thoại, trảm yêu quái và nộp nhiệm vụ.",
            checked = guildConfig.enableHoiVu,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(enableHoiVu = checked) } },
            testTag = "switch_enable_hoivu"
        )

        SettingToggleItem(
            title = "2. Vận Tiêu Bang Hội (Bảo Tiêu Xa)",
            description = "Tự động nhận Tiêu Xa Bang, hộ tống an toàn, tự động dùng kỹ năng tăng tốc và đánh cướp tiêu.",
            checked = guildConfig.enableVanTieu,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(enableVanTieu = checked) } },
            testTag = "switch_enable_vantieu"
        )

        SettingToggleItem(
            title = "3. Tuần Tra Lãnh Địa",
            description = "Quét 4 góc cứ địa Bang Hội, phát hiện và tiêu diệt Thám Tử Ngoại Bang thâm nhập.",
            checked = guildConfig.enableTuanTra,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(enableTuanTra = checked) } },
            testTag = "switch_enable_tuantra"
        )

        SettingToggleItem(
            title = "4. Cứu Trợ Bang Hội",
            description = "Tự động gom và nộp Dược Liệu/Khoáng Thạch vào kho quân nhu bang để lấy điểm Cống Hiến.",
            checked = guildConfig.enableCuuTro,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(enableCuuTro = checked) } },
            testTag = "switch_enable_cuutro"
        )

        SettingToggleItem(
            title = "5. Luyện Công Bang Hội",
            description = "Tham gia Luyện Công Động cùng các thành viên, ngồi thiền nhận Exp và Tâm Pháp.",
            checked = guildConfig.enableLuyenCong,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(enableLuyenCong = checked) } },
            testTag = "switch_enable_luyencong"
        )

        // Additional Guild Automation features
        Text(
            text = "TÙY CHỌN NÂNG CAO",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "Tự Động Nộp Vật Phẩm Bang Yêu Cầu",
            description = "Nếu túi đồ có sẵn vật phẩm nhiệm vụ cần, tự động giao nộp ngay không cần mua shop.",
            checked = guildConfig.autoContributeItems,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(autoContributeItems = checked) } },
            testTag = "switch_auto_contribute_items"
        )

        SettingToggleItem(
            title = "Tự Động Nhận Thưởng Rương Cống Hiến",
            description = "Tự động mở các mốc thưởng cống hiến bang 50, 100, 150 điểm.",
            checked = guildConfig.autoClaimDevotionBonus,
            onCheckedChange = { checked -> onUpdateGuildConfig { it.copy(autoClaimDevotionBonus = checked) } },
            testTag = "switch_auto_claim_devotion"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
