package com.example.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.engine.BotAutomationEngine
import com.example.model.BotStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Service hiển thị Floating Bubble (Bong bóng nổi) đè lên màn hình Game Tàng Long Bất Bại
 * Cho phép điều khiển Play / Pause / Stop và xem trạng thái trực tiếp trên game.
 */
class FloatingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createFloatingView()
        observeBotState()
    }

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
            x = 50
            y = 200
        }

        val rootLayout = floatingView?.findViewById<LinearLayout>(R.id.floating_root)
        val btnToggle = floatingView?.findViewById<ImageView>(R.id.btn_floating_toggle)
        val btnPauseResume = floatingView?.findViewById<ImageView>(R.id.btn_floating_pause_resume)
        val btnOpenApp = floatingView?.findViewById<ImageView>(R.id.btn_floating_open_app)
        val tvStatus = floatingView?.findViewById<TextView>(R.id.tv_floating_status)
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
                        windowManager?.updateViewLayout(floatingView, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isClick) {
                            isExpanded = !isExpanded
                            controlPanel?.visibility = if (isExpanded) View.VISIBLE else View.GONE
                        }
                        return true
                    }
                }
                return false
            }
        })

        btnPauseResume?.setOnClickListener {
            val currentState = BotAutomationEngine.liveState.value.status
            if (currentState == BotStatus.RUNNING) {
                BotAutomationEngine.pause()
            } else if (currentState == BotStatus.PAUSED) {
                BotAutomationEngine.resume(this)
            } else {
                BotAutomationEngine.startService(this)
            }
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
                    }
                    BotStatus.PAUSED -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Tạm dừng auto"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_orange_light))
                    }
                    BotStatus.IDLE -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Sẵn sàng"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.white))
                    }
                    BotStatus.COMPLETED -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Hoàn thành"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_blue_light))
                    }
                    BotStatus.ERROR -> {
                        btnPauseResume?.setImageResource(android.R.drawable.ic_media_play)
                        tvStatus?.text = "Sự cố"
                        tvStatus?.setTextColor(ContextCompat.getColor(this@FloatingOverlayService, android.R.color.holo_red_light))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
