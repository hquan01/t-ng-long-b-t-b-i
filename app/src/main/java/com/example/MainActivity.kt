package com.example

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.FarmingConfigEntity
import com.example.data.entity.GuildQuestConfigEntity
import com.example.data.entity.MiningConfigEntity
import com.example.data.entity.PunishEvilConfigEntity
import com.example.service.FloatingOverlayService
import com.example.ui.components.BlackScreenBatterySaver
import com.example.ui.components.FloatingOverlaySimulator
import com.example.ui.screens.FarmingScreen
import com.example.ui.screens.GuildScreen
import com.example.ui.screens.LogScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.MiningScreen
import com.example.ui.screens.PunishEvilScreen
import com.example.ui.screens.SettingsSecurityScreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkCardBorder
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var secondaryTab by remember { mutableIntStateOf(0) } // For Logs vs Settings

    val liveBotState by viewModel.liveBotState.collectAsStateWithLifecycle()
    val botConfig by viewModel.botConfig.collectAsStateWithLifecycle()
    val guildConfig by viewModel.guildConfig.collectAsStateWithLifecycle()
    val farmingConfig by viewModel.farmingConfig.collectAsStateWithLifecycle()
    val farmPlots by viewModel.farmPlots.collectAsStateWithLifecycle()
    val miningConfig by viewModel.miningConfig.collectAsStateWithLifecycle()
    val punishEvilConfig by viewModel.punishEvilConfig.collectAsStateWithLifecycle()
    val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()
    val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()

    val safeBotConfig = botConfig ?: BotConfigEntity()
    val safeGuildConfig = guildConfig ?: GuildQuestConfigEntity()
    val safeFarmingConfig = farmingConfig ?: FarmingConfigEntity()
    val safeMiningConfig = miningConfig ?: MiningConfigEntity()
    val safePunishEvilConfig = punishEvilConfig ?: PunishEvilConfigEntity()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = InkBlack,
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0B120F),
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Tổng Quan") },
                        label = { Text("Tổng Quan", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = JadePrimary,
                            indicatorColor = JadePrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_dashboard")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Castle, contentDescription = "Bang Hội") },
                        label = { Text("Bang Hội", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_guild")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Agriculture, contentDescription = "Trồng Trọt") },
                        label = { Text("Trồng Trọt", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = JadePrimary,
                            indicatorColor = JadePrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_farming")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Terrain, contentDescription = "Đào Khoáng") },
                        label = { Text("Đào Khoáng", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF38BDF8),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_mining")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Default.MilitaryTech, contentDescription = "Trừng Ác") },
                        label = { Text("Trừng Ác", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color(0xFFFF5252),
                            indicatorColor = Color(0xFFDC2626),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_punish_evil")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 5,
                        onClick = { selectedTab = 5 },
                        icon = { Icon(Icons.Default.History, contentDescription = "Nhật Ký") },
                        label = { Text("Nhật Ký", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = JadePrimary,
                            indicatorColor = JadePrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_logs_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> MainDashboardScreen(
                        liveState = liveBotState,
                        botConfig = safeBotConfig,
                        dailyStats = dailyStats,
                        onStart = { viewModel.startAuto(context) },
                        onPause = { viewModel.pauseAuto() },
                        onResume = { viewModel.resumeAuto(context) },
                        onStop = { viewModel.stopAuto(context) },
                        onToggleBlackScreen = { viewModel.toggleBlackScreenMode(!liveBotState.isBlackScreenMode) },
                        onToggleFloatingWidget = {
                            val newVisible = !liveBotState.isFloatingWidgetVisible
                            if (newVisible) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                } else {
                                    FloatingOverlayService.start(context)
                                }
                            } else {
                                FloatingOverlayService.stop(context)
                            }
                            viewModel.toggleFloatingWidget(newVisible)
                        },
                        onToggleGuild = { enabled -> viewModel.updateBotConfig { it.copy(isGuildQuestEnabled = enabled) } },
                        onToggleFarming = { enabled -> viewModel.updateBotConfig { it.copy(isFarmingEnabled = enabled) } },
                        onToggleMining = { enabled -> viewModel.updateBotConfig { it.copy(isMiningEnabled = enabled) } },
                        onTogglePunishEvil = { enabled -> viewModel.updateBotConfig { it.copy(isPunishEvilEnabled = enabled) } },
                        onNavigateToTab = { tabIndex -> selectedTab = tabIndex }
                    )

                    1 -> GuildScreen(
                        botConfig = safeBotConfig,
                        guildConfig = safeGuildConfig,
                        onUpdateBotConfig = viewModel::updateBotConfig,
                        onUpdateGuildConfig = viewModel::updateGuildConfig
                    )

                    2 -> FarmingScreen(
                        botConfig = safeBotConfig,
                        farmingConfig = safeFarmingConfig,
                        farmPlots = farmPlots,
                        onUpdateBotConfig = viewModel::updateBotConfig,
                        onUpdateFarmingConfig = viewModel::updateFarmingConfig,
                        onPlantSeed = viewModel::plantSeedOnPlot,
                        onHarvestPlot = viewModel::harvestPlot,
                        onWaterAllPlots = viewModel::waterAllPlots
                    )

                    3 -> MiningScreen(
                        botConfig = safeBotConfig,
                        miningConfig = safeMiningConfig,
                        onUpdateBotConfig = viewModel::updateBotConfig,
                        onUpdateMiningConfig = viewModel::updateMiningConfig
                    )

                    4 -> PunishEvilScreen(
                        botConfig = safeBotConfig,
                        punishEvilConfig = safePunishEvilConfig,
                        onUpdateBotConfig = viewModel::updateBotConfig,
                        onUpdatePunishEvilConfig = viewModel::updatePunishEvilConfig
                    )

                    5 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
                                TabRow(
                                    selectedTabIndex = secondaryTab,
                                    containerColor = Color(0xFF0D1612),
                                    contentColor = JadePrimary,
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[secondaryTab]),
                                            color = JadePrimary
                                        )
                                    }
                                ) {
                                    Tab(
                                        selected = secondaryTab == 0,
                                        onClick = { secondaryTab = 0 },
                                        text = { Text("Nhật Ký Tác Vụ", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                                    )
                                    Tab(
                                        selected = secondaryTab == 1,
                                        onClick = { secondaryTab = 1 },
                                        text = { Text("Cài Đặt An Toàn", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                                    )
                                }

                                if (secondaryTab == 0) {
                                    LogScreen(
                                        logs = recentLogs,
                                        onClearLogs = viewModel::clearLogs
                                    )
                                } else {
                                    SettingsSecurityScreen(
                                        botConfig = safeBotConfig,
                                        onUpdateBotConfig = viewModel::updateBotConfig
                                    )
                                }
                            }
                        }
                    }
                }

                // Floating Widget Simulator Overlay
                if (liveBotState.isFloatingWidgetVisible) {
                    FloatingOverlaySimulator(
                        liveState = liveBotState,
                        onStart = { viewModel.startAuto(context) },
                        onPause = { viewModel.pauseAuto() },
                        onResume = { viewModel.resumeAuto(context) },
                        onStop = { viewModel.stopAuto(context) },
                        onClose = { viewModel.toggleFloatingWidget(false) }
                    )
                }
            }
        }

        // AMOLED Black Screen Mode Overlay
        AnimatedVisibility(
            visible = liveBotState.isBlackScreenMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BlackScreenBatterySaver(
                liveState = liveBotState,
                onDismiss = { viewModel.toggleBlackScreenMode(false) }
            )
        }
    }
}
