package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.entity.FarmPlotEntity
import com.example.data.entity.FarmingConfigEntity
import com.example.model.CropType
import com.example.ui.components.SettingToggleItem
import com.example.ui.theme.GoldDark
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
import kotlinx.coroutines.delay

@Composable
fun FarmingScreen(
    botConfig: BotConfigEntity,
    farmingConfig: FarmingConfigEntity,
    farmPlots: List<FarmPlotEntity>,
    onUpdateBotConfig: ((BotConfigEntity) -> BotConfigEntity) -> Unit,
    onUpdateFarmingConfig: ((FarmingConfigEntity) -> FarmingConfigEntity) -> Unit,
    onPlantSeed: (plotIndex: Int, crop: CropType) -> Unit,
    onHarvestPlot: (plotIndex: Int) -> Unit,
    onWaterAllPlots: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Live ticking timer for accurate farm countdowns
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Farming Switch Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132219)),
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
                                    imageVector = Icons.Default.Agriculture,
                                    contentDescription = "Trồng Trọt",
                                    tint = JadePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tự Động Nông Trại Gia Viên",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextJade
                            )
                            Text(
                                text = "Gieo trồng, tưới nước & thu hoạch dược thảo",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    SettingToggleItem(
                        title = "",
                        description = "",
                        checked = botConfig.isFarmingEnabled,
                        onCheckedChange = { isEnabled ->
                            onUpdateBotConfig { it.copy(isFarmingEnabled = isEnabled) }
                        },
                        testTag = "switch_farming_enabled",
                        modifier = Modifier.width(60.dp)
                    )
                }
            }
        }

        // Quick Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onWaterAllPlots,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("btn_water_all_plots")
            ) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tưới Toàn Vườn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    farmPlots.forEach { plot ->
                        onHarvestPlot(plot.plotIndex)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = JadePrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("btn_harvest_all_plots")
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Thu Hoạch Tất Cả", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 8 Farm Plots Visualizer Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "8 Ô ĐẤT DƯỢC LIỆU GIA VIÊN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextGold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Tự động lặp lại khi chín",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        // 8 Plots Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val chunked = farmPlots.chunked(2)
            chunked.forEach { rowPlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowPlots.forEach { plot ->
                        FarmPlotCard(
                            plot = plot,
                            currentTimeMillis = currentTimeMillis,
                            onHarvest = { onHarvestPlot(plot.plotIndex) },
                            onSelectCrop = { crop -> onPlantSeed(plot.plotIndex, crop) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPlots.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Default Seed Selector
        DefaultSeedSelectorCard(
            selectedCrop = farmingConfig.defaultSeed,
            onSelectSeed = { crop ->
                onUpdateFarmingConfig { it.copy(defaultSeed = crop) }
            }
        )

        // Farming Automation Options
        Text(
            text = "CÀI ĐẶT TỰ ĐỘNG CANH TÁC",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextJade,
            letterSpacing = 0.5.sp
        )

        SettingToggleItem(
            title = "Tự Động Gieo Lại Hạt Khi Vừa Thu Hoạch",
            description = "Khi ô đất được thu hoạch xong, ngay lập tức gieo tiếp giống đã chọn.",
            checked = farmingConfig.autoReplant,
            onCheckedChange = { checked -> onUpdateFarmingConfig { it.copy(autoReplant = checked) } },
            testTag = "switch_auto_replant"
        )

        SettingToggleItem(
            title = "Tự Động Tưới Tiêu & Bón Phân Thần Dược",
            description = "Khi độ ẩm đất tụt xuống dưới 60%, tự động dùng Thủy Tinh Bình tưới ẩm.",
            checked = farmingConfig.autoWaterAndFertilize,
            onCheckedChange = { checked -> onUpdateFarmingConfig { it.copy(autoWaterAndFertilize = checked) } },
            testTag = "switch_auto_water"
        )

        SettingToggleItem(
            title = "Phản Kích & Chống Trộm Vườn",
            description = "Tự động thi triển chiêu thức xua đuổi người chơi khác lén hái trộm thuốc quý.",
            checked = farmingConfig.autoDefendPlotFromThieves,
            onCheckedChange = { checked -> onUpdateFarmingConfig { it.copy(autoDefendPlotFromThieves = checked) } },
            testTag = "switch_auto_defend_plot"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FarmPlotCard(
    plot: FarmPlotEntity,
    currentTimeMillis: Long,
    onHarvest: () -> Unit,
    onSelectCrop: (CropType) -> Unit,
    modifier: Modifier = Modifier
) {
    val elapsedSec = ((currentTimeMillis - plot.plantTimestamp) / 1000).toInt().coerceAtLeast(0)
    val remainingSec = (plot.matureDurationSec - elapsedSec).coerceAtLeast(0)
    val progress = (elapsedSec.toFloat() / plot.matureDurationSec.toFloat()).coerceIn(0f, 1f)
    val isMature = remainingSec == 0 || plot.isReadyToHarvest

    var showCropDropdown by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMature) Color(0xFF163827) else InkCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isMature) JadePrimary else InkCardBorder
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Plot Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF0C1411),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Ô ĐẤT #${plot.plotIndex}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Box {
                    Surface(
                        color = Color(0xFF1E2F26),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.clickable { showCropDropdown = true }
                    ) {
                        Text(
                            text = "Đổi giống ▼",
                            fontSize = 9.sp,
                            color = TextJade,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showCropDropdown,
                        onDismissRequest = { showCropDropdown = false },
                        modifier = Modifier.background(Color(0xFF111E18))
                    ) {
                        CropType.values().forEach { cropOption ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = cropOption.cropName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${cropOption.growthTimeSeconds}s • ${cropOption.harvestYield} dược liệu", color = TextMuted, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    onSelectCrop(cropOption)
                                    showCropDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Crop info
            Text(
                text = plot.crop.cropName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMature) TextJade else TextPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isMature) JadePrimary else Color(0xFF38BDF8),
                trackColor = Color(0xFF1A2620),
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Timer & Water level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isMature) "CHÍN RỒI!" else "${remainingSec}s nữa",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMature) JadePrimary else TextGold
                )
                Text(
                    text = "Ẩm: ${plot.waterLevel}%",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Harvest Button
            Button(
                onClick = onHarvest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMature) JadePrimary else Color(0xFF1B2E24),
                    contentColor = if (isMature) Color.Black else TextSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .testTag("btn_harvest_plot_${plot.plotIndex}")
            ) {
                Text(
                    text = if (isMature) "Thu Hoạch (+${plot.crop.harvestYield})" else "Gặt Sớm",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DefaultSeedSelectorCard(
    selectedCrop: CropType,
    onSelectSeed: (CropType) -> Unit,
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
                text = "HẠT GIỐNG TỰ ĐỘNG GIEO MẶC ĐỊNH",
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
                        .padding(vertical = 2.dp)
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
                                text = selectedCrop.cropName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold
                            )
                            Text(
                                text = "Thời gian: ${selectedCrop.growthTimeSeconds}s • Thu được: ${selectedCrop.harvestYield}x • Exp: +${selectedCrop.expPerHarvest}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Text(text = "Chọn ▼", color = JadePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF111E18))
                ) {
                    CropType.values().forEach { crop ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = crop.cropName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "${crop.growthTimeSeconds}s | ${crop.harvestYield} cái | +${crop.expPerHarvest} Exp", color = TextMuted, fontSize = 11.sp)
                                }
                            },
                            onClick = {
                                onSelectSeed(crop)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
