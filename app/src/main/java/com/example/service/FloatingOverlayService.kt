package com.example.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PointF
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Service hiển thị Floating Bubble (Bong bóng nổi) & Các Điểm Chạm Mục Tiêu đè trực tiếp
 * lên giao diện Game Tàng Long Bất Bại Mobile.
 * Cho phép điều khiển Play / Pause, ghim vị trí NPC Ngô Giới / Chiêu thức / Nhiệm vụ
 * và thực hiện click vật lý thông qua AccessibilityService.
 */
class FloatingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var autoClickJob: Job? = null

    // Danh sách các điểm chạm mục tiêu nổi trên màn hình game
    private val targetPoints = mutableListOf<TargetPointViewData>()

    data class TargetPointViewData(
        val view: View,
        val params: WindowManager.LayoutParams,
        var coordinate: PointF
    )

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createFloatingView()
        observeBotState()
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
            y = 200
        }

        val btnToggle = floatingView?.findViewById<ImageView>(R.id.btn_floating_toggle)
        val btnPauseResume = floatingView?.findViewById<ImageView>(R.id.btn_floating_pause_resume)
        val btnAddTarget = floatingView?.findViewById<ImageView>(R.id.btn_floating_add_target)
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
                    "Vui lòng bật Dịch vụ Trợ Năng 'Tàng Long Auto' để chạm tự động trên game!",
                    Toast.LENGTH_LONG
                ).show()
                AutoClickerAccessibilityService.openAccessibilitySettings(this)
                return@setOnClickListener
            }

            val currentState = BotAutomationEngine.liveState.value.status
            if (currentState == BotStatus.RUNNING) {
                stopTargetAutoClickLoop()
                BotAutomationEngine.pause()
            } else if (currentState == BotStatus.PAUSED) {
                BotAutomationEngine.resume(this)
                startTargetAutoClickLoop()
            } else {
                BotAutomationEngine.startService(this)
                startTargetAutoClickLoop()
            }
        }

        // Thêm điểm chạm mục tiêu (Target 1, Target 2...) để đặt vào NPC Ngô Giới, Nút nhiệm vụ...
        btnAddTarget?.setOnClickListener {
            addTargetPoint()
        }

        // Xóa các điểm chạm
        btnClearTargets?.setOnClickListener {
            clearAllTargetPoints()
            Toast.makeText(this, "Đã xóa các điểm chạm mục tiêu", Toast.LENGTH_SHORT).show()
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
    private fun addTargetPoint() {
        val pointIndex = targetPoints.size + 1
        val layoutInflater = LayoutInflater.from(this)
        val targetView = layoutInflater.inflate(R.layout.layout_target_point, null)
        val tvTargetNumber = targetView.findViewById<TextView>(R.id.tv_target_number)
        tvTargetNumber.text = "$pointIndex"

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val targetParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100 + (pointIndex * 60)
            y = 300 + (pointIndex * 40)
        }

        val targetData = TargetPointViewData(
            view = targetView,
            params = targetParams,
            coordinate = PointF(targetParams.x + 60f, targetParams.y + 60f)
        )

        targetView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = targetParams.x
                        initialY = targetParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        targetParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        targetParams.y = initialY + (event.rawY - initialTouchY).toInt()
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
            Toast.makeText(
                this,
                "Đã tạo Mục Tiêu #$pointIndex. Hãy kéo đặt vào vị trí NPC Ngô Giới hoặc Nút Game!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
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
     * Chạy luồng tự động chạm các điểm mục tiêu trên màn hình game
     */
    private fun startTargetAutoClickLoop() {
        stopTargetAutoClickLoop()
        val service = AutoClickerAccessibilityService.instance ?: return
        if (targetPoints.isEmpty()) return

        autoClickJob = serviceScope.launch {
            while (isActive && BotAutomationEngine.liveState.value.status == BotStatus.RUNNING) {
                for (target in targetPoints.toList()) {
                    if (!isActive) break
                    // Thực hiện click vào tọa độ thực tế trên màn hình game
                    service.clickAt(target.coordinate.x, target.coordinate.y)
                    delay(1200L + (Math.random() * 400).toLong())
                }
                delay(2000L)
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
