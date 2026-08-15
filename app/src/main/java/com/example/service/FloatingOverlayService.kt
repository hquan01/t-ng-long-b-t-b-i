package com.example.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PointF
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.engine.BotAutomationEngine
import com.example.model.BotStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Service hiển thị Floating Bubble (Bong bóng nổi) & Các Điểm Chạm Mục Tiêu đè trực tiếp
 * lên giao diện Game Tàng Long Bát Bộ Mobile.
 * Cho phép điều khiển Play / Pause, ghim vị trí NPC Ngô Giới / Chiêu thức / Nhiệm vụ
 * và thực hiện click vật lý thông qua AccessibilityService.
 */
class FloatingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var clickIndicatorView: View? = null
    private var clickIndicatorParams: WindowManager.LayoutParams? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var autoClickJob: Job? = null

    // Danh sách các điểm chạm mục tiêu nổi trên màn hình game
    private val targetPoints = mutableListOf<TargetPointViewData>()

    data class TargetPointViewData(
        val view: View,
        val params: WindowManager.LayoutParams,
        var coordinate: PointF,
        val label: String = ""
    )

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createClickIndicatorView()
        createFloatingView()
        observeBotState()
    }

    private fun createClickIndicatorView() {
        val layoutInflater = LayoutInflater.from(this)
        clickIndicatorView = layoutInflater.inflate(R.layout.layout_target_point, null).apply {
            findViewById<TextView>(R.id.tv_target_number)?.text = "●"
            setBackgroundResource(R.drawable.bg_click_ripple)
            visibility = View.GONE
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        clickIndicatorParams = WindowManager.LayoutParams(
            100,
            100,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }

        try {
            windowManager?.addView(clickIndicatorView, clickIndicatorParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showClickFeedback(x: Float, y: Float) {
        val indicator = clickIndicatorView ?: return
        val params = clickIndicatorParams ?: return
        try {
            params.x = (x - 50).toInt()
            params.y = (y - 50).toInt()
            indicator.visibility = View.VISIBLE
            windowManager?.updateViewLayout(indicator, params)
            serviceScope.launch {
                delay(220L)
                indicator.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createFloatingView() {
        val layoutInflater = LayoutInflater.from(this)
        floatingView = layoutInflater.inflate(R.layout.layout_floating_widget, null)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 120
        }

        val btnToggle = floatingView?.findViewById<ImageView>(R.id.btn_floating_toggle)
        val btnPauseResume = floatingView?.findViewById<ImageView>(R.id.btn_floating_pause_resume)
        val btnAddTarget = floatingView?.findViewById<ImageView>(R.id.btn_floating_add_target)
        val btnPreset = floatingView?.findViewById<ImageView>(R.id.btn_floating_preset)
        val btnClearTargets = floatingView?.findViewById<ImageView>(R.id.btn_floating_clear_targets)
        val btnAccessibility = floatingView?.findViewById<ImageView>(R.id.btn_floating_accessibility)
        val btnOpenApp = floatingView?.findViewById<ImageView>(R.id.btn_floating_open_app)
        val controlPanel = floatingView?.findViewById<LinearLayout>(R.id.floating_controls)

        var isExpanded = false

        // Kéo thả floating widget trên màn hình game
        btnToggle?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isClick = true

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isClick = true
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                            isClick = false
                        }
                        params.x = initialX + dx
                        params.y = initialY + dy
                        try {
                            windowManager?.updateViewLayout(floatingView, params)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isClick) {
                            isExpanded = !isExpanded
                            controlPanel?.visibility = if (isExpanded) View.VISIBLE else View.GONE
                            checkAccessibilityState()
                        }
                        return true
                    }
                }
                return false
            }
        })

        // Nút Play / Pause auto click
        btnPauseResume?.setOnClickListener {
            if (!AutoClickerAccessibilityService.isEnabled(this)) {
                Toast.makeText(
                    this,
                    "⚠️ BẠN CẦN BẬT TRỢ NĂNG: Hãy tìm 'Tàng Long Auto' trong danh sách và gạt BẬT để app tự click!",
                    Toast.LENGTH_LONG
                ).show()
                AutoClickerAccessibilityService.openAccessibilitySettings(this)
                return@setOnClickListener
            }

            val currentState = BotAutomationEngine.liveState.value.status
            if (currentState == BotStatus.RUNNING) {
                stopTargetAutoClickLoop()
                BotAutomationEngine.pause()
                Toast.makeText(this, "Đã tạm dừng Auto", Toast.LENGTH_SHORT).show()
            } else if (currentState == BotStatus.PAUSED) {
                BotAutomationEngine.resume(this)
                startTargetAutoClickLoop()
                Toast.makeText(this, "Đang chạy Auto tự động chạm trên game...", Toast.LENGTH_SHORT).show()
            } else {
                BotAutomationEngine.startService(this)
                startTargetAutoClickLoop()
                Toast.makeText(this, "Đang khởi động Auto tự động chạm trên game...", Toast.LENGTH_SHORT).show()
            }
        }

        // Thêm điểm chạm thủ công
        btnAddTarget?.setOnClickListener {
            addTargetPoint()
        }

        // Ghim các điểm mục tiêu chuẩn của Game Tàng Long Bát Bộ
        btnPreset?.setOnClickListener {
            loadGamePresetTargets()
        }

        // Xóa các điểm chạm
        btnClearTargets?.setOnClickListener {
            clearAllTargetPoints()
            Toast.makeText(this, "Đã xóa các điểm ghim. Auto sẽ dùng chế độ tự động thông minh toàn màn hình!", Toast.LENGTH_SHORT).show()
        }

        // Mở cài đặt Trợ Năng
        btnAccessibility?.setOnClickListener {
            AutoClickerAccessibilityService.openAccessibilitySettings(this)
        }

        btnOpenApp?.setOnClickListener {
            val appIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(appIntent)
        }

        try {
            windowManager?.addView(floatingView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAccessibilityState() {
        val btnAccessibility = floatingView?.findViewById<ImageView>(R.id.btn_floating_accessibility)
        val isEnabled = AutoClickerAccessibilityService.isEnabled(this)
        btnAccessibility?.visibility = if (isEnabled) View.GONE else View.VISIBLE
    }

    /**
     * Tạo thêm 1 điểm chạm mục tiêu có thể kéo thả tự do trên màn hình game
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun addTargetPoint(initialX: Int = -1, initialY: Int = -1, customLabel: String? = null) {
        val pointIndex = targetPoints.size + 1
        val layoutInflater = LayoutInflater.from(this)
        val targetView = layoutInflater.inflate(R.layout.layout_target_point, null)
        val tvTargetNumber = targetView.findViewById<TextView>(R.id.tv_target_number)
        tvTargetNumber.text = customLabel ?: "$pointIndex"

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val posX = if (initialX >= 0) initialX else (120 + (pointIndex * 60))
        val posY = if (initialY >= 0) initialY else (250 + (pointIndex * 30))

        val targetParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = posX
            y = posY
        }

        val targetData = TargetPointViewData(
            view = targetView,
            params = targetParams,
            coordinate = PointF(posX + 44f, posY + 44f),
            label = customLabel ?: "$pointIndex"
        )

        targetView.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0
            private var startY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = targetParams.x
                        startY = targetParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        targetParams.x = startX + (event.rawX - initialTouchX).toInt()
                        targetParams.y = startY + (event.rawY - initialTouchY).toInt()
                        targetData.coordinate = PointF(event.rawX, event.rawY)
                        try {
                            windowManager?.updateViewLayout(targetView, targetParams)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }
                }
                return false
            }
        })

        try {
            windowManager?.addView(targetView, targetParams)
            targetPoints.add(targetData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Nạp sẵn các tọa độ chuẩn của Game Tàng Long Bát Bộ Mobile dựa trên đúng quy trình chuỗi Trừng Ác:
     * 1. Đối thoại NPC Ngô Giới (Bong bóng E)
     * 2. Dòng "Trừng Trị Hung Đồ"
     * 3. Nút "Nhận" nhiệm vụ
     * 4. Nút "Túi Đồ" (Phím B)
     * 5. Tab "Nhiệm Vụ"
     * 6. Chạm Lệnh Bài & Bấm Tọa Độ để chạy tới bãi Boss
     * 7. Xuống Thú Cưỡi (Nút Đầu Ngựa - Phím C) khi tới tọa độ
     * 8. Mở lại Túi Đồ -> Dùng Lệnh Bài để gọi Boss xuất hiện
     * 9. Bật Auto Đánh / Tung combo chiêu diệt Boss
     * 10. Dùng "Bạch Sắc Định Vị Phù" (Phím F2/F3) để biến phù về lại Tô Châu
     */
    private fun loadGamePresetTargets() {
        clearAllTargetPoints()
        val metrics = resources.displayMetrics
        val w = metrics.widthPixels.toFloat()
        val h = metrics.heightPixels.toFloat()

        serviceScope.launch {
            val dbSteps = BotAutomationEngine.repository?.customActionSteps?.firstOrNull()
            if (!dbSteps.isNullOrEmpty()) {
                val enabledSteps = dbSteps.filter { it.isEnabled }
                for (step in enabledSteps) {
                    addTargetPoint((w * step.screenXPercent).toInt(), (h * step.screenYPercent).toInt(), step.actionName)
                }
                Toast.makeText(
                    this@FloatingOverlayService,
                    "Đã nạp ${enabledSteps.size} bước hành động từ cấu hình đã lưu!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // 1. Icon Hội thoại Ngô Giới (chữ E)
                addTargetPoint((w * 0.692f).toInt(), (h * 0.460f).toInt(), "1.NgôGiới")
                // 2. Dòng Trừng Trị Hung Đồ trong bảng hội thoại
                addTargetPoint((w * 0.320f).toInt(), (h * 0.325f).toInt(), "2.DòngTrừngÁc")
                // 3. Nút Nhận nhiệm vụ ở góc dưới bảng
                addTargetPoint((w * 0.175f).toInt(), (h * 0.935f).toInt(), "3.NhậnNV")
                // 4. Icon Túi Đồ (Phím B trên góc phải)
                addTargetPoint((w * 0.838f).toInt(), (h * 0.125f).toInt(), "4.TúiĐồ")
                // 5. Tab Nhiệm Vụ trong Túi Đồ
                addTargetPoint((w * 0.540f).toInt(), (h * 0.135f).toInt(), "5.TabNV")
                // 6. Ô Lệnh Bài Trừng Ác (Ô 1 hàng 1)
                addTargetPoint((w * 0.235f).toInt(), (h * 0.220f).toInt(), "6.LệnhBài")
                // 7. Nút Sử Dụng / Bấm Tọa Độ Boss để chạy tới nơi
                addTargetPoint((w * 0.350f).toInt(), (h * 0.550f).toInt(), "7.ChạyBoss")
                // 8. Nút Xuống Ngựa (Icon đầu ngựa - Phím C)
                addTargetPoint((w * 0.445f).toInt(), (h * 0.825f).toInt(), "8.XuốngNgựa")
                // 9. Nút Auto Đánh / Kỹ năng diệt Boss
                addTargetPoint((w * 0.305f).toInt(), (h * 0.905f).toInt(), "9.AutoĐánh")
                // 10. Bạch Sắc Định Vị Phù (Phím F2/F3 trên thanh phím tắt) để về Tô Châu
                addTargetPoint((w * 0.770f).toInt(), (h * 0.285f).toInt(), "10.PhùTôChâu")

                Toast.makeText(
                    this@FloatingOverlayService,
                    "Đã ghim trọn gói 10 bước Trừng Ác mặc định!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearAllTargetPoints() {
        for (target in targetPoints) {
            try {
                windowManager?.removeView(target.view)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        targetPoints.clear()
    }

    /**
     * Chạy luồng tự động chạm liên tục trên màn hình game (cả chế độ tùy chọn và chế độ tự động chuẩn)
     */
    private fun startTargetAutoClickLoop() {
        stopTargetAutoClickLoop()

        autoClickJob = serviceScope.launch {
            while (isActive && BotAutomationEngine.liveState.value.status == BotStatus.RUNNING) {
                val service = AutoClickerAccessibilityService.instance
                if (service == null) {
                    Log.w("FloatingOverlay", "Accessibility Service chưa được kích hoạt!")
                    delay(2000L)
                    continue
                }

                val metrics = resources.displayMetrics
                val w = metrics.widthPixels.toFloat()
                val h = metrics.heightPixels.toFloat()

                // Đọc cài đặt thời gian chờ từ cơ sở dữ liệu (Database)
                val punishEvilConfig = BotAutomationEngine.repository?.punishEvilConfig?.firstOrNull()
                val dOpenNpc = (punishEvilConfig?.delayOpenNpcDialogSec ?: 2) * 1000L
                val dSelectQuest = (punishEvilConfig?.delaySelectQuestSec ?: 1) * 1000L
                val dAcceptQuest = (punishEvilConfig?.delayAcceptQuestSec ?: 2) * 1000L
                val dOpenBag = (punishEvilConfig?.delayOpenBagSec ?: 1) * 1000L
                val dSelectTab = (punishEvilConfig?.delaySelectTabSec ?: 1) * 1000L
                val dUseToken = (punishEvilConfig?.delayUseTokenSec ?: 2) * 1000L
                val dTravel = (punishEvilConfig?.delayTravelToBossSec ?: 15) * 1000L
                val dDismountSummon = (punishEvilConfig?.delayDismountAndSummonSec ?: 3) * 1000L
                val dCombatDuration = (punishEvilConfig?.delayCombatDurationSec ?: 18) * 1000L
                val dTeleportRecall = (punishEvilConfig?.delayTeleportRecallSec ?: 4) * 1000L

                val customSteps = BotAutomationEngine.repository?.customActionSteps?.firstOrNull()?.filter { it.isEnabled }

                if (targetPoints.isNotEmpty()) {
                    // CHẾ ĐỘ 1: Click tuần tự theo chu trình ghim mục tiêu hoặc danh sách tùy chỉnh
                    for ((index, target) in targetPoints.toList().withIndex()) {
                        if (!isActive || BotAutomationEngine.liveState.value.status != BotStatus.RUNNING) break
                        val jitterX = target.coordinate.x + Random.nextInt(-3, 4)
                        val jitterY = target.coordinate.y + Random.nextInt(-3, 4)

                        showClickFeedback(jitterX, jitterY)
                        service.clickAt(jitterX, jitterY)

                        val customStep = customSteps?.getOrNull(index)
                        if (customStep != null) {
                            // Sử dụng thời gian chờ được lưu cho chính bước này
                            val customDelay = customStep.delaySeconds * 1000L
                            when {
                                customStep.actionName.contains("Đánh", ignoreCase = true) || 
                                customStep.actionName.contains("Boss", ignoreCase = true) && customStep.actionName.contains("Diệt", ignoreCase = true) -> {
                                    // Thực hiện liên hoàn combo kỹ năng trong suốt thời gian đánh boss
                                    val combatStartTime = System.currentTimeMillis()
                                    val attackPoint = PointF(w * 0.915f, h * 0.835f)
                                    val skill1 = PointF(w * 0.885f, h * 0.575f)
                                    val skill2 = PointF(w * 0.965f, h * 0.575f)
                                    val skill3 = PointF(w * 0.805f, h * 0.690f)
                                    val skill4 = PointF(w * 0.875f, h * 0.690f)
                                    val hpPotion = PointF(w * 0.825f, h * 0.465f)

                                    val skillList = listOf(attackPoint, skill1, skill2, attackPoint, skill3, skill4, hpPotion)
                                    var skillIdx = 0

                                    while (isActive &&
                                        BotAutomationEngine.liveState.value.status == BotStatus.RUNNING &&
                                        (System.currentTimeMillis() - combatStartTime) < customDelay
                                    ) {
                                        val sp = skillList[skillIdx % skillList.size]
                                        val sx = sp.x + Random.nextInt(-4, 5)
                                        val sy = sp.y + Random.nextInt(-4, 5)
                                        showClickFeedback(sx, sy)
                                        service.clickAt(sx, sy)
                                        skillIdx++
                                        delay(750L + Random.nextLong(100, 250))
                                    }

                                    // Tắt Auto sau khi xong
                                    showClickFeedback(target.coordinate.x, target.coordinate.y)
                                    service.clickAt(target.coordinate.x, target.coordinate.y)
                                    delay(600L)
                                }
                                else -> {
                                    delay(customDelay + Random.nextLong(100, 250))
                                }
                            }
                        } else {
                            // Fallback theo index mặc định
                            when (index) {
                                0 -> delay(dOpenNpc + Random.nextLong(100, 300)) // Bước 1: Mở bảng Ngô Giới
                                1 -> delay(dSelectQuest + Random.nextLong(100, 200))  // Bước 2: Chọn Trừng Trị Hung Đồ
                                2 -> delay(dAcceptQuest + Random.nextLong(100, 300)) // Bước 3: Bấm Nhận nhiệm vụ
                                3 -> delay(dOpenBag + Random.nextLong(100, 200)) // Bước 4: Mở Túi Đồ
                                4 -> delay(dSelectTab + Random.nextLong(100, 200))  // Bước 5: Chọn Tab Nhiệm Vụ
                                5 -> delay(dUseToken + Random.nextLong(100, 200)) // Bước 6: Bấm Lệnh Bài
                                6 -> {
                                    // Bước 7: Bấm Tọa Độ để chạy tới bãi Boss
                                    delay(dTravel)
                                }
                                7 -> {
                                    // Bước 8: Tắt Cưỡi Thú (Xuống ngựa) & Dùng lại Lệnh bài để triệu hồi Boss
                                    delay(dDismountSummon)
                                    val bagPoint = targetPoints.getOrNull(3)
                                    val questTabPoint = targetPoints.getOrNull(4)
                                    val itemPoint = targetPoints.getOrNull(5)
                                    val usePoint = targetPoints.getOrNull(6)

                                    if (bagPoint != null && itemPoint != null) {
                                        showClickFeedback(bagPoint.coordinate.x, bagPoint.coordinate.y)
                                        service.clickAt(bagPoint.coordinate.x, bagPoint.coordinate.y)
                                        delay(900L)

                                        if (questTabPoint != null) {
                                            showClickFeedback(questTabPoint.coordinate.x, questTabPoint.coordinate.y)
                                            service.clickAt(questTabPoint.coordinate.x, questTabPoint.coordinate.y)
                                            delay(700L)
                                        }

                                        showClickFeedback(itemPoint.coordinate.x, itemPoint.coordinate.y)
                                        service.clickAt(itemPoint.coordinate.x, itemPoint.coordinate.y)
                                        delay(800L)

                                        if (usePoint != null) {
                                            showClickFeedback(usePoint.coordinate.x, usePoint.coordinate.y)
                                            service.clickAt(usePoint.coordinate.x, usePoint.coordinate.y)
                                            delay(1200L)
                                        }
                                    }
                                }
                                8 -> {
                                    // Bước 9: Bật Auto Đánh & Xả liên hoàn kỹ năng để diệt Boss
                                    val combatStartTime = System.currentTimeMillis()
                                    val attackPoint = PointF(w * 0.915f, h * 0.835f)
                                    val skill1 = PointF(w * 0.885f, h * 0.575f)
                                    val skill2 = PointF(w * 0.965f, h * 0.575f)
                                    val skill3 = PointF(w * 0.805f, h * 0.690f)
                                    val skill4 = PointF(w * 0.875f, h * 0.690f)
                                    val hpPotion = PointF(w * 0.825f, h * 0.465f)

                                    val skillList = listOf(attackPoint, skill1, skill2, attackPoint, skill3, skill4, hpPotion)
                                    var skillIdx = 0

                                    while (isActive &&
                                        BotAutomationEngine.liveState.value.status == BotStatus.RUNNING &&
                                        (System.currentTimeMillis() - combatStartTime) < dCombatDuration
                                    ) {
                                        val sp = skillList[skillIdx % skillList.size]
                                        val sx = sp.x + Random.nextInt(-4, 5)
                                        val sy = sp.y + Random.nextInt(-4, 5)
                                        showClickFeedback(sx, sy)
                                        service.clickAt(sx, sy)
                                        skillIdx++
                                        delay(750L + Random.nextLong(100, 250))
                                    }

                                    // Tắt Auto Đánh sau khi Boss chết
                                    showClickFeedback(target.coordinate.x, target.coordinate.y)
                                    service.clickAt(target.coordinate.x, target.coordinate.y)
                                    delay(1000L)
                                }
                                9 -> {
                                    // Bước 10: Dùng Bạch Sắc Định Vị Phù (F2/F3) để biến phù về Tô Châu
                                    delay(dTeleportRecall)
                                }
                                else -> delay(1200L)
                            }
                        }
                    }
                    delay(2000L)
                } else {
                    // CHẾ ĐỘ 2: TỰ ĐỘNG THÔNG MINH THEO CHU KỲ GAME TÀNG LONG BÁT BỘ
                    // 1. Chạm Nhiệm Vụ (để tự tìm đường / nhận trả nhiệm vụ)
                    val questX = w * 0.08f + Random.nextInt(-5, 6)
                    val questY = h * 0.42f + Random.nextInt(-5, 6)
                    showClickFeedback(questX, questY)
                    service.clickAt(questX, questY)
                    delay(1200L)

                    // 2. Chạm Hội thoại NPC Ngô Giới (chọn dòng nhiệm vụ Trừng Ác / Dã tẩu)
                    val npcX = w * 0.83f + Random.nextInt(-5, 6)
                    val npcY = h * 0.57f + Random.nextInt(-5, 6)
                    showClickFeedback(npcX, npcY)
                    service.clickAt(npcX, npcY)
                    delay(1000L)

                    // 3. Chạm Nút Auto Đánh của game (cạnh thanh chat)
                    val autoBtnX = w * 0.305f + Random.nextInt(-4, 5)
                    val autoBtnY = h * 0.905f + Random.nextInt(-4, 5)
                    showClickFeedback(autoBtnX, autoBtnY)
                    service.clickAt(autoBtnX, autoBtnY)
                    delay(900L)

                    // 4. Luân phiên xuất chiêu môn phái & đánh thường
                    val skills = listOf(
                        PointF(w * 0.915f, h * 0.835f), // Đánh thường (Chưởng)
                        PointF(w * 0.885f, h * 0.575f), // Chiêu 1
                        PointF(w * 0.965f, h * 0.575f), // Chiêu 2
                        PointF(w * 0.805f, h * 0.690f), // Chiêu 3
                        PointF(w * 0.875f, h * 0.690f), // Chiêu 4
                        PointF(w * 0.825f, h * 0.465f)  // Bình máu F1
                    )

                    for (skillPoint in skills) {
                        if (!isActive || BotAutomationEngine.liveState.value.status != BotStatus.RUNNING) break
                        val sx = skillPoint.x + Random.nextInt(-4, 5)
                        val sy = skillPoint.y + Random.nextInt(-4, 5)
                        showClickFeedback(sx, sy)
                        service.clickAt(sx, sy)
                        delay(750L + Random.nextLong(100, 300))
                    }

                    delay(2000L)
                }
            }
        }
    }

    private fun stopTargetAutoClickLoop() {
        autoClickJob?.cancel()
        autoClickJob = null
    }

    private fun observeBotState() {
        serviceScope.launch {
            BotAutomationEngine.liveState.collectLatest { state ->
                val btnPauseResume = floatingView?.findViewById<ImageView>(R.id.btn_floating_pause_resume)
                val tvStatus = floatingView?.findViewById<TextView>(R.id.tv_floating_status)
                val tvCharacter = floatingView?.findViewById<TextView>(R.id.tv_floating_character)

                tvCharacter?.text = "${state.characterName} • ${state.sectName} Lv.${state.characterLevel}"

                when (state.status) {
                    BotStatus.RUNNING -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_pause)
                        tvStatus?.text = "Đang chạy: ${state.currentCategory.displayName}"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_green_light))
                        if (autoClickJob == null || autoClickJob?.isActive == false) {
                            startTargetAutoClickLoop()
                        }
                    }
                    BotStatus.PAUSED -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Tạm dừng"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_orange_light))
                        stopTargetAutoClickLoop()
                    }
                    BotStatus.IDLE -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Sẵn sàng"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.white))
                        stopTargetAutoClickLoop()
                    }
                    BotStatus.COMPLETED -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Hoàn thành"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_blue_light))
                        stopTargetAutoClickLoop()
                    }
                    BotStatus.ERROR -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Sự cố"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_red_light))
                        stopTargetAutoClickLoop()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTargetAutoClickLoop()
        clearAllTargetPoints()
        serviceScope.cancel()
        if (clickIndicatorView != null) {
            try {
                windowManager?.removeView(clickIndicatorView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (floatingView != null) {
            try {
                windowManager?.removeView(floatingView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            context.stopService(intent)
        }
    }
}

