package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LiveBotState
import com.example.model.BotStatus
import com.example.model.TaskCategory
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InkCard
import com.example.ui.theme.InkCardBorder
import com.example.ui.theme.JadeAccent
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextJade
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BotLiveMonitorCard(
    liveState: LiveBotState,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1411)),
        border = androidx.compose.foundation.BorderStroke(1.dp, InkCardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Radar Pulse Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (liveState.status == BotStatus.RUNNING) JadePrimary else Color.Gray
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RADAR QUÉT TRẠNG THÁI GAME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextJade,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    color = Color(0xFF16231E),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263A32))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map",
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = liveState.currentMapName,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Character Indicator Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF101C17))
                    .border(1.dp, Color(0xFF1D352B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nhân vật: ",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${liveState.characterName} (${liveState.sectName} Lv.${liveState.characterLevel})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGold
                    )
                }
                Text(
                    text = liveState.serverName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextJade
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Terminal / Console Feed
            Surface(
                color = Color(0xFF060A08),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F332A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val categoryIcon = when (liveState.currentCategory) {
                                TaskCategory.GUILD_QUEST -> Icons.Default.Castle
                                TaskCategory.FARMING -> Icons.Default.Grass
                                TaskCategory.MINING -> Icons.Default.Terrain
                                TaskCategory.PUNISH_EVIL -> Icons.Default.MilitaryTech
                            }
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = when (liveState.currentCategory) {
                                    TaskCategory.GUILD_QUEST -> GoldPrimary
                                    TaskCategory.FARMING -> JadePrimary
                                    TaskCategory.MINING -> Color(0xFF38BDF8)
                                    TaskCategory.PUNISH_EVIL -> CrimsonPrimary
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[ ${liveState.currentCategory.displayName.uppercase()} ]",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "Vòng #${liveState.loopCount}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "> ${liveState.actionText}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "  ${liveState.subActionDetail}",
                        fontSize = 11.sp,
                        color = TextJade.copy(alpha = 0.8f),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Player Status Meters (HP, MP, Stamina)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // HP Bar
                ResourceBar(
                    label = "HP",
                    currentValue = liveState.playerHpPercent,
                    barColor = CrimsonPrimary,
                    modifier = Modifier.weight(1f)
                )
                // MP Bar
                ResourceBar(
                    label = "MP",
                    currentValue = liveState.playerMpPercent,
                    barColor = Color(0xFF00B0FF),
                    modifier = Modifier.weight(1f)
                )
                // Stamina / Thể lực
                ResourceBar(
                    label = "Thể Lực",
                    currentValue = liveState.playerStaminaPercent,
                    barColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Session Yield Grid
            Text(
                text = "THÀNH QUẢ TRONG PHIÊN CHẠY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                YieldBox(
                    label = "Kinh Nghiệm",
                    value = "+${numberFormat.format(liveState.sessionExp)}",
                    valueColor = TextJade,
                    modifier = Modifier.weight(1f)
                )
                YieldBox(
                    label = "Ngân Lượng",
                    value = "+${numberFormat.format(liveState.sessionGold)}",
                    valueColor = TextGold,
                    modifier = Modifier.weight(1f)
                )
                YieldBox(
                    label = "Dược Thảo",
                    value = "${liveState.sessionHerbs} cái",
                    valueColor = JadePrimary,
                    modifier = Modifier.weight(1f)
                )
                YieldBox(
                    label = "Khoáng Thạch",
                    value = "${liveState.sessionOres} viên",
                    valueColor = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResourceBar(
    label: String,
    currentValue: Int,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF131D19),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22382E)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text(text = "$currentValue%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { currentValue / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = barColor,
                trackColor = Color(0xFF223028),
            )
        }
    }
}

@Composable
private fun YieldBox(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF131D19),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22382E)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, color = TextMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor, maxLines = 1)
        }
    }
}
