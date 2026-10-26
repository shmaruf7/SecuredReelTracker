package com.privatereelstracker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.privatereelstracker.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "reels_milestone_channel"
    private const val CHANNEL_NAME = "Reel Milestones & Limits"

    // Milestones defined in user requirements: 20, 50, 70, 90, 100, 110, 120, 130, 140, 150
    val MILESTONES = listOf(20, 50, 70, 90, 100, 110, 120, 130, 140, 150)

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when Reel viewing reaches critical limits"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun sendMilestoneNotification(context: Context, count: Int, isLockTriggered: Boolean) {
        sendAppLimitNotification(
            context = context,
            platform = "Global",
            appCount = count,
            appLimit = 100,
            isBlocked = isLockTriggered
        )
    }

    fun sendAppLimitNotification(
        context: Context,
        platform: String,
        appCount: Int,
        appLimit: Int,
        isBlocked: Boolean
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isBlocked) {
            "🛑 $platform Limit Reached ($appCount/$appLimit)!"
        } else {
            "⚠️ $platform Milestone: $appCount Videos"
        }

        val message = if (isBlocked) {
            "You reached your daily limit of $appLimit videos on $platform. The app was minimized to protect your focus."
        } else {
            "You've watched $appCount $platform short videos today (Limit: $appLimit). Take a mindful break!"
        }

        val notificationId = (platform.hashCode() and 0xFFFF) + appCount

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.privatereelstracker.R.drawable.ic_notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(notificationId, notification)
    }
}
