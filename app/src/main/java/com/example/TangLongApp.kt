package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.database.AppDatabase
import com.example.data.repository.BotRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TangLongApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BotRepository(database.appDao()) }
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        applicationScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_AUTOPLAY,
                "Tàng Long Auto Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Kênh thông báo dịch vụ chạy nền rảnh tay cho game Tàng Long Bất Bại"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_AUTOPLAY = "tanglong_autoplay_channel"
        const val NOTIFICATION_ID = 1001
    }
}
