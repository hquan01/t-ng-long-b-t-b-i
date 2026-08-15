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
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.platform.LocalContext
import com.example.service.AutoClickerAccessibilityService
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.data.entity.DailyStatsEntity
import com.example.engine.LiveBotState
import com.example.model.TaskCategory
import com.example.ui.components.BotLiveMonitorCard
import com.example.ui.components.HeroHeaderCard
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MainDashboardScreen(
    liveState: LiveBotState,
    botConfig: BotConfigEntity,
    dailyStats: DailyStatsEntity?,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onToggleBlackScreen: () -> Unit,
    onToggleFloatingWidget: () -> Unit,
    onToggleGuild: (Boolean) -> Unit,
    onToggleFarming: (Boolean) -> Unit,
    onToggleMining: (Boolean) -> Unit,
    onTogglePunishEvil: (Boolean) -> Unit,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Header with start/stop/pause
        HeroHeaderCard(
            liveState = liveState,
            onStart = onStart,
            onPause = onPause,
            onResume = onResume,
            onStop = onStop,
            onToggleBlackScreen = onToggleBlackScreen,
            onToggleFloatingWidget = onToggleFloatingWidget
        )

        // Banner Hướng Dẫn Tự Động Chạm Trực Tiếp Trên Màn Hình Game Tàng Long
        val context = LocalContext.current
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13221C)),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, JadePrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = Color(0xFF0F3B29),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = "Chạm Tự Động",
                                    tint = JadePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tự Động Chạm Trực Tiếp Trên Game",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextJade
                            )
                            Text(
                                text = "Bật Trợ Năng & Bong Bóng Nổi để auto chạm NPC Ngô Giới",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { AutoClickerAccessibilityService.openAccessibilitySettings(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text("1. Bật Trợ Năng", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextJade)
                    }

                    Button(
                        onClick = onToggleFloatingWidget,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (liveState.isFloatingWidgetVisible) JadePrimary else Color(0xFF263A32)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text(
                            if (liveState.isFloatingWidgetVisible) "Đang Bật Bóng Nổi" else "2. Bật Bóng Nổi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (liveState.isFloatingWidgetVisible) Color.Black else TextPrimary
                        )
                    }
                }
            }
        }

        // 2. Live Bot Monitor & Radar
        BotLiveMonitorCard(liveState = liveState)

        // 3. Four Core Module Cards
        Text(
            text = "4 TÍNH NĂNG RẢNH TAY CHÍNH",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Guild Quest Card (Nhiệm Vụ Bang)
            FeatureCard(
                title = "Nhiệm Vụ Bang Hội",
                subtitle = "Tự động Hội Vụ, Vận Tiêu, Tuần Tra, Luyện Công",
                icon = Icons.Default.Castle,
                iconTint = GoldPrimary,
                iconBg = Color(0xFF382A08),
                isEnabled = botConfig.isGuildQuestEnabled,
                onToggle = onToggleGuild,
                onClickConfig = { onNavigateToTab(1) },
                testTag = "card_guild_feature"
            )

            // Farming Card (Trồng Trọt)
            FeatureCard(
                title = "Trồng Trọt Gia Viên",
                subtitle = "8 ô đất linh dược, tự gieo hạt, tưới nước & thu hoạch",
                icon = Icons.Default.Agriculture,
                iconTint = JadePrimary,
                iconBg = Color(0xFF0F3B29),
                isEnabled = botConfig.isFarmingEnabled,
                onToggle = onToggleFarming,
                onClickConfig = { onNavigateToTab(2) },
                testTag = "card_farming_feature"
            )

            // Mining Card (Đào Khoáng)
            FeatureCard(
                title = "Khai Thác Đào Khoáng",
                subtitle = "Khai khoáng tại Thái Sơn, Côn Lôn, né PK & sửa cuốc",
                icon = Icons.Default.Terrain,
                iconTint = Color(0xFF38BDF8),
                iconBg = Color(0xFF0C2A38),
                isEnabled = botConfig.isMiningEnabled,
                onToggle = onToggleMining,
                onClickConfig = { onNavigateToTab(3) },
                testTag = "card_mining_feature"
            )

            // Punish Evil Card (Nhiệm Vụ Trừng Ác)
            FeatureCard(
                title = "Trừng Trị Hung Đồ (Trừng Ác)",
                subtitle = "8 bước: NPC Ngô Giới -> Trừng Ác Lệnh -> Di chuyển -> Trảm Ngô Nhân Hách -> Nhặt Tàng Bảo Đồ -> Về Tô Châu",
                icon = Icons.Default.MilitaryTech,
                iconTint = CrimsonPrimary,
                iconBg = Color(0xFF381212),
                isEnabled = botConfig.isPunishEvilEnabled,
                onToggle = onTogglePunishEvil,
                onClickConfig = { onNavigateToTab(4) },
                testTag = "card_punish_evil_feature"
            )
        }

        // 4. Daily Lifetime Stats
        Card(
            colors = CardDefaults.cardColors(containerColor = InkCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, InkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TỔNG KẾT NĂNG SUẤT HÔM NAY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Nhiệm vụ Bang hoàn thành", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${dailyStats?.guildQuestsDone ?: 0} lượt", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Dược thảo thu hoạch", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${dailyStats?.cropsHarvested ?: 0} củ/cây", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = JadePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Khoáng thạch khai thác", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${dailyStats?.oresMined ?: 0} viên", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Ác nhân đã trảm sát", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${dailyStats?.evilBossesSlain ?: 0} tên", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onClickConfig: () -> Unit,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = InkCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) iconTint.copy(alpha = 0.35f) else InkCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClickConfig() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = iconBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("${testTag}_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = JadePrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color(0xFF1E2923)
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Chi tiết",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
