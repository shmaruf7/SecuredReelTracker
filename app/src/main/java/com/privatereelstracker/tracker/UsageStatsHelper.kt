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

    val TARGET_PACKAGES = mapOf(
        "com.instagram.android" to "Instagram",
        "com.google.android.youtube" to "YouTube",
        "com.facebook.katana" to "Facebook",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok"
    )

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
     * Queries total foreground screen time for all tracked platforms today (midnight to now).
     */
    fun getTotalTrackedForegroundTime(context: Context): Long {
        if (!hasUsagePermission(context)) return 0L

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return 0L

        val startTime = DateUtils.getStartOfDayMs()
        val endTime = System.currentTimeMillis()

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        var totalTimeMs = 0L
        val activeResumes = mutableMapOf<String, Long>()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val pkg = event.packageName
            if (TARGET_PACKAGES.containsKey(pkg)) {
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        activeResumes[pkg] = event.timeStamp
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.ACTIVITY_STOPPED -> {
                        val resumeTs = activeResumes.remove(pkg)
                        if (resumeTs != null && resumeTs > 0L) {
                            totalTimeMs += (event.timeStamp - resumeTs)
                        }
                    }
                }
            }
        }

        // If any app is currently still in foreground
        activeResumes.forEach { (_, resumeTs) ->
            if (resumeTs > 0L) {
                totalTimeMs += (endTime - resumeTs)
            }
        }

        return totalTimeMs
    }

    /**
     * Legacy helper for Instagram-only foreground query.
     */
    fun getInstagramTodayForegroundTime(context: Context): Long {
        return getTotalTrackedForegroundTime(context)
    }
}
