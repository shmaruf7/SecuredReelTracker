package com.privatereelstracker.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.privatereelstracker.PrivateReelsTrackerApp
import com.privatereelstracker.tracker.UsageStatsHelper
import com.privatereelstracker.util.AppPreferences
import com.privatereelstracker.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Native AccessibilityService configured for Instagram, YouTube, Facebook, and TikTok.
 * Runs strictly locally, preserving user privacy.
 */
class ReelsAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val detector = ReelsDetector()

    companion object {
        private const val TAG = "ReelsAccessibility"
        @Volatile var isRunning: Boolean = false
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
        Log.i(TAG, "ReelsAccessibilityService connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        if (!UsageStatsHelper.TARGET_PACKAGES.containsKey(packageName)) return

        val result = detector.evaluateEvent(event)
        if (!result.isReelTransition) return

        serviceScope.launch {
            val app = application as? PrivateReelsTrackerApp ?: return@launch
            val repository = app.repository

            val platform = result.platform
            repository.incrementReelCount(platform = platform)
            val todayData = repository.getTodayReels()

            val appCount = when (platform) {
                "Instagram" -> todayData.instagramReelsCount
                "YouTube" -> todayData.youtubeShortsCount
                "Facebook" -> todayData.facebookReelsCount
                "TikTok" -> todayData.tiktokReelsCount
                else -> todayData.reelCount
            }

            // Record non-sensitive diagnostic entry
            repository.recordDiagnostic(
                eventType = result.eventTypeString,
                details = "[$platform] ${result.structuralDigest} | ${result.reason}",
                wasIncremented = true
            )

            val context = this@ReelsAccessibilityService
            val appLimit = AppPreferences.getAppLimit(context, platform)
            val notifFreq = AppPreferences.getNotificationFrequency(context)
            val isAutoBlock = AppPreferences.isAutoBlockEnabled(context)

            val isLimitReached = appCount >= appLimit || todayData.reelCount >= 100
            val isMilestone = appCount > 0 && (appCount % notifFreq == 0 || appCount == appLimit / 2)

            if (isLimitReached || isMilestone) {
                val shouldBlock = isAutoBlock && isLimitReached

                NotificationHelper.sendAppLimitNotification(
                    context = context,
                    platform = platform,
                    appCount = appCount,
                    appLimit = appLimit,
                    isBlocked = shouldBlock
                )

                if (shouldBlock) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "ReelsAccessibilityService interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
        Log.i(TAG, "ReelsAccessibilityService destroyed.")
    }
}
