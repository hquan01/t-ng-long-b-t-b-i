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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BotLogEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogScreen(
    logs: List<BotLogEntity>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NHẬT KÝ HOẠT ĐỘNG AUTO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextJade,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tổng cộng ${logs.size} bản ghi gần nhất",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Button(
                onClick = onClearLogs,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF241818),
                    contentColor = CrimsonPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp).testTag("btn_clear_logs")
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Xóa Nhật Ký", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chưa có nhật ký hoạt động nào",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Bắt đầu auto để hệ thống ghi nhận tiến trình",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs, key = { it.id }) { log ->
                    LogItemCard(log = log, timeStr = timeFormat.format(Date(log.timestamp)))
                }
            }
        }
    }
}

@Composable
private fun LogItemCard(log: BotLogEntity, timeStr: String) {
    val (catColor, catIcon) = when (log.category) {
        "BANG HỘI" -> Pair(GoldPrimary, Icons.Default.Castle)
        "TRỒNG TRỌT" -> Pair(JadePrimary, Icons.Default.Grass)
        "ĐÀO KHOÁNG" -> Pair(Color(0xFF38BDF8), Icons.Default.Terrain)
        "TRỪNG ÁC" -> Pair(CrimsonPrimary, Icons.Default.MilitaryTech)
        else -> Pair(TextSecondary, Icons.Default.Info)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (log.isHighlight) Color(0xFF13281E) else InkCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (log.isHighlight) JadePrimary.copy(alpha = 0.5f) else InkCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = catIcon, contentDescription = null, tint = catColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = catColor
                    )
                }
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = log.actionText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            if (log.detail.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = log.detail,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            if (log.expEarned > 0 || log.goldEarned > 0 || log.itemDrop.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (log.expEarned > 0) {
                        Surface(
                            color = Color(0xFF0F261C),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "+${log.expEarned} EXP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextJade,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (log.goldEarned > 0) {
                        Surface(
                            color = Color(0xFF2E240D),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "+${log.goldEarned} Vàng",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (log.itemDrop.isNotEmpty()) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "🎁 ${log.itemDrop}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
