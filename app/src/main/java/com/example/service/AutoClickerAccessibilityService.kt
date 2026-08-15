package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.PointF
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Dịch vụ Trợ Năng (Accessibility Service) cho phép Tàng Long Auto thực hiện các cử chỉ
 * chạm (Tap / Click), vuốt (Swipe) trực tiếp lên giao diện game Tàng Long Bất Bại Mobile
 * (nhận nhiệm vụ tại NPC Ngô Giới, xuất chiêu, nhặt rương...).
 */
class AutoClickerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceActive.value = true
        Log.i(TAG, "Tàng Long Auto Accessibility Service đã kết nối thành công.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Có thể nhận diện tên cửa sổ hoặc text thông báo từ game nếu cần
    }

    override fun onInterrupt() {
        Log.w(TAG, "Tàng Long Auto Accessibility Service bị gián đoạn.")
        _isServiceActive.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
        _isServiceActive.value = false
    }

    /**
     * Thực hiện một thao tác click/chạm thực tế vào tọa độ (x, y) trên màn hình game
     */
    fun clickAt(x: Float, y: Float, durationMs: Long = 75L, onResult: ((Boolean) -> Unit)? = null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            onResult?.invoke(false)
            return
        }

        val clickPath = Path().apply {
            moveTo(x, y)
        }

        val gestureBuilder = GestureDescription.Builder()
        val stroke = GestureDescription.StrokeDescription(clickPath, 0, durationMs)
        gestureBuilder.addStroke(stroke)

        try {
            val success = dispatchGesture(
                gestureBuilder.build(),
                object : GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        super.onCompleted(gestureDescription)
                        onResult?.invoke(true)
                    }

                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        super.onCancelled(gestureDescription)
                        onResult?.invoke(false)
                    }
                },
                null
            )
            if (!success) {
                onResult?.invoke(false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi dispatchGesture: ${e.message}")
            onResult?.invoke(false)
        }
    }

    /**
     * Thực hiện một chuỗi thao tác click tuần tự các điểm với độ trễ (delay) giữa các bước
     */
    fun clickSequence(points: List<PointF>, delayMs: Long = 800L, onFinished: (() -> Unit)? = null) {
        serviceScope.launch {
            for (point in points) {
                clickAt(point.x, point.y)
                delay(delayMs)
            }
            onFinished?.invoke()
        }
    }

    companion object {
        private const val TAG = "AutoClickerService"
        var instance: AutoClickerAccessibilityService? = null
            private set

        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

        /**
         * Kiểm tra xem dịch vụ Trợ Năng đã được người dùng bật trong Cài Đặt của máy hay chưa
         */
        fun isEnabled(context: Context): Boolean {
            val serviceName = "${context.packageName}/${AutoClickerAccessibilityService::class.java.canonicalName}"
            val accessibilityEnabled = try {
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
            } catch (e: Exception) {
                0
            }
            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: ""
                return settingValue.contains(serviceName) || instance != null
            }
            return instance != null
        }

        /**
         * Mở màn hình Cài Đặt Trợ Năng để người dùng bật công tắc cho Tàng Long Auto
         */
        fun openAccessibilitySettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Không thể mở Accessibility Settings: ${e.message}")
            }
        }
    }
}
