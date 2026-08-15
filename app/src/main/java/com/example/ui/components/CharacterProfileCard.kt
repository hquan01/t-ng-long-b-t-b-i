package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BotConfigEntity
import com.example.engine.LiveBotState
import com.example.model.BotStatus
import com.example.model.SectType
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterProfileCard(
    botConfig: BotConfigEntity,
    liveState: LiveBotState,
    onUpdateCharacterProfile: (BotConfigEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    // Màu sắc theo Môn phái
    val sectColor = when (botConfig.sect) {
        SectType.TIEU_DAO -> Color(0xFF00E676) // Xanh lục Tiêu Dao
        SectType.VO_DANG -> Color(0xFF29B6F6)  // Xanh dương Thái Cực Võ Đang
        SectType.THIEN_LONG -> Color(0xFFFFD54F) // Hoàng kim Thiên Long
        SectType.MINH_GIAO -> Color(0xFFFF5252) // Đỏ lửa Minh Giáo
        SectType.THIEN_SON -> Color(0xFF80D8FF) // Băng hàn Thiên Sơn
        SectType.CAI_BANG -> Color(0xFFFFB74D) // Cam Độc Cái Bang
        SectType.NGA_MI -> Color(0xFFFF80AB)   // Hồng Trị Liệu Nga Mi
        SectType.THIEU_LAM -> Color(0xFFFFC107) // Vàng Kim Chung Tráo
        SectType.TINH_TUC -> Color(0xFFB388FF)  // Tím U Minh Tinh Túc
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1714)),
        border = androidx.compose.foundation.BorderStroke(1.dp, sectColor.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_character_profile")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Tiêu đề Card + Nút Sửa Thông Tin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = sectColor.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = sectColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NHÂN VẬT ĐANG CHẠY AUTO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TextGold,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    color = Color(0xFF162721),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263F35)),
                    modifier = Modifier.clickable { showEditDialog = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa thông tin",
                            tint = TextJade,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đổi Nhân Vật",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextJade
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Identity Header: Avatar + Tên + Danh Hiệu + Server + Bang
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Khung Avatar nhân vật với vòng hào quang môn phái
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        color = sectColor.copy(alpha = 0.15f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(2.dp, sectColor),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Avatar",
                                tint = sectColor,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                    // Huy hiệu phái nhỏ
                    Surface(
                        color = Color(0xFF0F1815),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, sectColor),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 2.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = botConfig.sect.sectName.take(2).uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = sectColor,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Cột Tên & Danh Hiệu
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = botConfig.characterName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Danh hiệu giang hồ
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = botConfig.characterTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Server & Bang Hội Tags
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color(0xFF1B2C25),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = botConfig.serverName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextJade,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFF292212),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Castle,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = botConfig.guildName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextGold
                                )
                            }
                        }
                    }
                }

                // Điểm Lực Chiến (Combat Power) & Cấp Độ
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = Color(0xFF2B1F0A),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Lực Chiến",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "LỰC CHIẾN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGold
                                )
                            }
                            Text(
                                text = numberFormat.format(botConfig.combatPower),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = TextGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cấp ${botConfig.characterLevel} • ${botConfig.sect.sectName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Thanh Máu (HP) & Nội Lực (MP) & Kinh Nghiệm (Exp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF070D0B))
                    .border(1.dp, Color(0xFF1B2D26), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sinh mệnh (HP)
                StatBar(
                    label = "Sinh Mệnh (HP)",
                    current = botConfig.currentHp,
                    max = botConfig.maxHp,
                    color = CrimsonPrimary,
                    icon = Icons.Default.Favorite
                )

                // Khí lực (MP)
                StatBar(
                    label = "Khí Lực (MP)",
                    current = botConfig.currentMp,
                    max = botConfig.maxMp,
                    color = Color(0xFF00B0FF),
                    icon = Icons.Default.WaterDrop
                )

                // Kinh nghiệm thăng cấp (Exp)
                StatBar(
                    label = "Kinh Nghiệm (Exp)",
                    current = botConfig.expCurrent.toInt(),
                    max = botConfig.expMax.toInt(),
                    color = JadePrimary,
                    icon = Icons.Default.AutoFixHigh
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Vị Trí Hiện Tại & Tài Nguyên Ngân Lượng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tọa độ hiện tại
                Surface(
                    color = Color(0xFF131F1A),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22382E)),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Tọa độ",
                            tint = CrimsonPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Tọa Độ Hiện Tại", fontSize = 9.sp, color = TextMuted)
                            Text(
                                text = botConfig.currentMap,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Vàng / Kim Nguyên Bảo
                Surface(
                    color = Color(0xFF1F1C12),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38321B)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Vàng",
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Vàng Không Khóa", fontSize = 9.sp, color = TextMuted)
                            Text(
                                text = "${numberFormat.format(botConfig.goldAmount)} V",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog Chỉnh Sửa Thông Tin Nhân Vật
    if (showEditDialog) {
        EditCharacterProfileDialog(
            botConfig = botConfig,
            onDismiss = { showEditDialog = false },
            onSave = { updatedConfig ->
                onUpdateCharacterProfile(updatedConfig)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun StatBar(
    label: String,
    current: Int,
    max: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val percent = if (max > 0) ((current.toFloat() / max) * 100).toInt() else 100

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            }
            Text(
                text = "${numberFormat.format(current)} / ${numberFormat.format(max)} ($percent%)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (current.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF18221E)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterProfileDialog(
    botConfig: BotConfigEntity,
    onDismiss: () -> Unit,
    onSave: (BotConfigEntity) -> Unit
) {
    var name by remember { mutableStateOf(botConfig.characterName) }
    var selectedSect by remember { mutableStateOf(botConfig.sect) }
    var levelText by remember { mutableStateOf(botConfig.characterLevel.toString()) }
    var serverName by remember { mutableStateOf(botConfig.serverName) }
    var guildName by remember { mutableStateOf(botConfig.guildName) }
    var combatPowerText by remember { mutableStateOf(botConfig.combatPower.toString()) }
    var title by remember { mutableStateOf(botConfig.characterTitle) }
    var sectExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1815),
        title = {
            Text(
                text = "Cập Nhật Thông Tin Nhân Vật Game",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextGold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tên nhân vật
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên nhân vật trong game") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = JadePrimary,
                        unfocusedBorderColor = Color(0xFF263A32)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Chọn môn phái
                ExposedDropdownMenuBox(
                    expanded = sectExpanded,
                    onExpandedChange = { sectExpanded = !sectExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSect.sectName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Môn phái") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = JadePrimary,
                            unfocusedBorderColor = Color(0xFF263A32)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = sectExpanded,
                        onDismissRequest = { sectExpanded = false },
                        containerColor = Color(0xFF131D19)
                    ) {
                        SectType.values().forEach { sect ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = sect.sectName, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = sect.specialty, fontSize = 10.sp, color = TextMuted)
                                    }
                                },
                                onClick = {
                                    selectedSect = sect
                                    sectExpanded = false
                                }
                            )
                        }
                    }
                }

                // Cấp độ & Server
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = levelText,
                        onValueChange = { levelText = it.filter { char -> char.isDigit() } },
                        label = { Text("Cấp độ (Lv)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = JadePrimary,
                            unfocusedBorderColor = Color(0xFF263A32)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = serverName,
                        onValueChange = { serverName = it },
                        label = { Text("Máy chủ (Server)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = JadePrimary,
                            unfocusedBorderColor = Color(0xFF263A32)
                        ),
                        modifier = Modifier.weight(1.4f)
                    )
                }

                // Lực chiến & Bang hội
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = combatPowerText,
                        onValueChange = { combatPowerText = it.filter { char -> char.isDigit() } },
                        label = { Text("Lực chiến") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = JadePrimary,
                            unfocusedBorderColor = Color(0xFF263A32)
                        ),
                        modifier = Modifier.weight(1.2f)
                    )

                    OutlinedTextField(
                        value = guildName,
                        onValueChange = { guildName = it },
                        label = { Text("Bang hội") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = JadePrimary,
                            unfocusedBorderColor = Color(0xFF263A32)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Danh hiệu giang hồ
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Danh hiệu giang hồ") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = JadePrimary,
                        unfocusedBorderColor = Color(0xFF263A32)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lvl = levelText.toIntOrNull() ?: botConfig.characterLevel
                    val cp = combatPowerText.toLongOrNull() ?: botConfig.combatPower
                    val updated = botConfig.copy(
                        characterName = name.ifBlank { botConfig.characterName },
                        sect = selectedSect,
                        characterLevel = lvl,
                        serverName = serverName.ifBlank { botConfig.serverName },
                        guildName = guildName.ifBlank { botConfig.guildName },
                        combatPower = cp,
                        characterTitle = title.ifBlank { botConfig.characterTitle }
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = JadePrimary, contentColor = Color.Black)
            ) {
                Text("Lưu Thông Tin", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = TextSecondary)
            }
        }
    )
}
