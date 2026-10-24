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

        val title = if (isLockTriggered) {
            "🛑 Reel Limit Reached ($count Reels)!"
        } else {
            "⚠️ Reels Checkpoint: $count Swipes"
        }

        val message = if (isLockTriggered) {
            "You reached your 100-Reel limit. Instagram has been minimized to protect your time."
        } else {
            "You've watched $count Reels today. Consider pausing your scrolling."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(count, notification)
    }
}
