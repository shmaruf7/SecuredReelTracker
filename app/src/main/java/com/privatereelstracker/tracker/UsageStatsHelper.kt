package com.privatereelstracker.tracker

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import com.privatereelstracker.util.DateUtils

object UsageStatsHelper {

    private const val INSTAGRAM_PACKAGE_NAME = "com.instagram.android"

    /**
     * Checks whether the user has granted PACKAGE_USAGE_STATS.
     */
    fun hasUsagePermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Creates an Intent to navigate directly to the system Usage Access settings page.
     */
    fun createUsageAccessIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    /**
     * Queries foreground screen time for Instagram today (midnight to now).
     * Distinguishes total Instagram foreground time from Reels activity.
     */
    fun getInstagramTodayForegroundTime(context: Context): Long {
        if (!hasUsagePermission(context)) return 0L

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return 0L

        val startTime = DateUtils.getStartOfDayMs()
        val endTime = System.currentTimeMillis()

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        var totalTimeMs = 0L
        var lastResumeTimestamp = 0L

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.packageName == INSTAGRAM_PACKAGE_NAME) {
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        lastResumeTimestamp = event.timeStamp
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.ACTIVITY_STOPPED -> {
                        if (lastResumeTimestamp > 0L) {
                            totalTimeMs += (event.timeStamp - lastResumeTimestamp)
                            lastResumeTimestamp = 0L
                        }
                    }
                }
            }
        }

        // If currently still in foreground
        if (lastResumeTimestamp > 0L) {
            totalTimeMs += (endTime - lastResumeTimestamp)
        }

        return totalTimeMs
    }
}
