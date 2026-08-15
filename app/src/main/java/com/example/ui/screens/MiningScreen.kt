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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.example.data.entity.MiningConfigEntity
import com.example.model.MineMap
import com.example.model.TargetOre
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
fun MiningScreen(
    botConfig: BotConfigEntity,
    miningConfig: MiningConfigEntity,
    onUpdateBotConfig: ((BotConfigEntity) -> BotConfigEntity) -> Unit,
    onUpdateMiningConfig: ((MiningConfigEntity) -> MiningConfigEntity) -> Unit,
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
        // Master Mining Switch Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D22)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f)),
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
                            color = Color(0xFF0C2A38),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Terrain,
                                    contentDescription = "Đào Khoáng",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tự Động Đào Khoáng Sản",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7DD3FC)
                            )
                            Text(
                                text = "Khai thác mỏ quặng, né PK & bảo quản đồ",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    SettingToggleItem(
                        title = "",
                        description = "",
                        checked = botConfig.isMiningEnabled,
                        onCheckedChange = { isEnabled ->
                            onUpdateBotConfig { it.copy(isMiningEnabled = isEnabled) }
                        },
                        testTag = "switch_mining_enabled",
                        modifier = Modifier.width(60.dp)
                    )
                }
            }
        }

        // Map Selector Card
        MapSelectorCard(
            selectedMap = miningConfig.selectedMap,
            onSelectMap = { map ->
                onUpdateMiningConfig { it.copy(selectedMap = map) }
            }
        )

        // Target Ore Selector Card
        TargetOreSelectorCard(
            selectedOre = miningConfig.targetOre,
            onSelectOre = { ore ->
                onUpdateMiningConfig { it.copy(targetOre = ore) }
            }
        )

        // Mining Automation Settings
        Text(
            text = "CÀI ĐẶT BẢO VỆ & DUY TRÌ THỂ LỰC",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "Tự Động Dùng Đơn Dược Hồi Phục Thể Lực",
            description = "Khi điểm thể lực khai khoáng xuống dưới 20%, tự động dùng Thể Lực Đơn trong túi đồ.",
            checked = miningConfig.autoUseStaminaPotion,
            onCheckedChange = { checked -> onUpdateMiningConfig { it.copy(autoUseStaminaPotion = checked) } },
            testTag = "switch_auto_stamina_potion"
        )

        SettingToggleItem(
            title = "Tự Động Sửa Chữa Cuốc Khai Mỏ",
            description = "Khi độ bền cuốc khai khoáng giảm xuống dưới 10%, tự động dùng Búa Hàn Thiết sửa chữa tức thì.",
            checked = miningConfig.autoRepairPickaxe,
            onCheckedChange = { checked -> onUpdateMiningConfig { it.copy(autoRepairPickaxe = checked) } },
            testTag = "switch_auto_repair_pickaxe"
        )

        SettingToggleItem(
            title = "Tự Động Tẩu Thoát Khi Bị Đồ Sát / PK",
            description = "Khi phát hiện người chơi chữ đỏ hoặc nhận sát thương bất ngờ, tự động dùng Thần Hành Phù về thành.",
            checked = miningConfig.escapeWhenPkDetected,
            onCheckedChange = { checked -> onUpdateMiningConfig { it.copy(escapeWhenPkDetected = checked) } },
            testTag = "switch_escape_pk"
        )

        SettingToggleItem(
            title = "Tự Động Gửi Khoáng Thạch Vào Rương Tiêu Cục",
            description = "Khi túi đồ đầy 80%, tự động dịch chuyển về Thương Khố cất trữ khoáng thạch an toàn.",
            checked = miningConfig.autoStoreOreInVault,
            onCheckedChange = { checked -> onUpdateMiningConfig { it.copy(autoStoreOreInVault = checked) } },
            testTag = "switch_store_ore_vault"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun MapSelectorCard(
    selectedMap: MineMap,
    onSelectMap: (MineMap) -> Unit,
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
                text = "BẢN ĐỒ KHU VỰC KHAI KHOÁNG",
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedMap.mapName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (selectedMap.dangerLevel.contains("PK")) Color(0xFF3B1212) else Color(0xFF132D21),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = selectedMap.dangerLevel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedMap.dangerLevel.contains("PK")) CrimsonPrimary else JadePrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Yêu cầu cấp độ khuyến nghị: Lv.${selectedMap.recommendedLevel}+",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Text(text = "Đổi ▼", color = JadePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF111E18))
                ) {
                    MineMap.values().forEach { map ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = map.mapName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "Lv.${map.recommendedLevel} • ${map.dangerLevel}", color = TextMuted, fontSize = 11.sp)
                                }
                            },
                            onClick = {
                                onSelectMap(map)
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
private fun TargetOreSelectorCard(
    selectedOre: TargetOre,
    onSelectOre: (TargetOre) -> Unit,
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
                text = "LOẠI KHOÁNG SẢN MỤC TIÊU ƯU TIÊN",
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedOre.oreName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7DD3FC)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = selectedOre.rarity,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Giá trị giao thương: ~${selectedOre.sellPrice} Ngân Lượng/viên",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Text(text = "Đổi ▼", color = JadePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF111E18))
                ) {
                    TargetOre.values().forEach { ore ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = ore.oreName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "Phẩm chất: ${ore.rarity} • Giá: ${ore.sellPrice} vàng", color = TextMuted, fontSize = 11.sp)
                                }
                            },
                            onClick = {
                                onSelectOre(ore)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
