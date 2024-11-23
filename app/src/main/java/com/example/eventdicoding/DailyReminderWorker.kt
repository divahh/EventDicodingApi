package com.example.eventdicoding

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.data.api.ApiConfig

class DailyReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = EventRepository(ApiConfig.getServiceApi())

        // Fetch the nearest active event
        val eventResponse = repository.getReminderEvents(active = 1, limit = 1)
        val event = eventResponse.listEvents.firstOrNull() ?: return Result.success()

        // Create notification
        showNotification(event.nameEvent, event.beginTime)
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, content: String) {
        val channelId = "daily_reminder_channel"
        val channelName = "Daily Reminder"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText("Event Terdekat: $content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
    }
}
