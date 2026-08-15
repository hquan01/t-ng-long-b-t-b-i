package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.TangLongApp
import com.example.engine.BotAutomationEngine
import com.example.model.BotStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AutoPlayForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var wakeLock: PowerManager.WakeLock? = null
    private var observerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
        observeEngineState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val notification = buildNotification("Đang chuẩn bị khởi động auto...")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        TangLongApp.NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                } else {
                    startForeground(TangLongApp.NOTIFICATION_ID, notification)
                }
            }
            ACTION_PAUSE -> {
                BotAutomationEngine.pause()
            }
            ACTION_RESUME -> {
                BotAutomationEngine.resume(this)
            }
            ACTION_STOP -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    private fun observeEngineState() {
        observerJob?.cancel()
        observerJob = serviceScope.launch {
            BotAutomationEngine.liveState.collectLatest { state ->
                if (state.status == BotStatus.IDLE) {
                    stopForegroundService()
                } else {
                    val notif = buildNotification("${state.currentCategory.displayName}: ${state.actionText}")
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
                    notificationManager?.notify(TangLongApp.NOTIFICATION_ID, notif)
                }
            }
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseIntent = Intent(this, AutoPlayForegroundService::class.java).apply {
            action = ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this, 1, pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val resumeIntent = Intent(this, AutoPlayForegroundService::class.java).apply {
            action = ACTION_RESUME
        }
        val resumePendingIntent = PendingIntent.getService(
            this, 2, resumeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, AutoPlayForegroundService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 3, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val currentState = BotAutomationEngine.liveState.value

        val builder = NotificationCompat.Builder(this, TangLongApp.CHANNEL_ID_AUTOPLAY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tàng Long Bất Bại - Trợ Lý Rảnh Tay")
            .setContentText(statusText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$statusText\nEXP: +${currentState.sessionExp} | Vàng: +${currentState.sessionGold} | Chu kỳ #${currentState.loopCount}")
            )
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (currentState.status == BotStatus.RUNNING) {
            builder.addAction(android.R.drawable.ic_media_pause, "Tạm Dừng", pausePendingIntent)
        } else if (currentState.status == BotStatus.PAUSED) {
            builder.addAction(android.R.drawable.ic_media_play, "Tiếp Tục", resumePendingIntent)
        }
        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dừng Auto", stopPendingIntent)

        return builder.build()
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TangLong:AutoPlayWakeLock")?.apply {
            acquire(24 * 60 * 60 * 1000L) // 24 hours max
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    private fun stopForegroundService() {
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        releaseWakeLock()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.example.ACTION_START"
        const val ACTION_PAUSE = "com.example.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.ACTION_RESUME"
        const val ACTION_STOP = "com.example.ACTION_STOP"
    }
}
