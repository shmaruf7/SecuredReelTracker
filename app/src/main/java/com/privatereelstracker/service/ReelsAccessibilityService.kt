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

            repository.incrementReelCount()
            val todayData = repository.getTodayReels()
            val currentCount = todayData.reelCount

            // Record non-sensitive diagnostic entry
            repository.recordDiagnostic(
                eventType = result.eventTypeString,
                details = "[${result.platform}] ${result.structuralDigest} | ${result.reason}",
                wasIncremented = true
            )

            // Check milestone notifications: 20, 50, 70, 90, 100, 110, 120, 130, 140, 150
            if (NotificationHelper.MILESTONES.contains(currentCount)) {
                val isLockEnabled = AppPreferences.isLockEnabled(this@ReelsAccessibilityService)
                val shouldLock = isLockEnabled && currentCount >= 100

                NotificationHelper.sendMilestoneNotification(
                    context = this@ReelsAccessibilityService,
                    count = currentCount,
                    isLockTriggered = shouldLock
                )

                // If lock enabled and at or over 100 reels: dismiss to Home Screen
                if (shouldLock) {
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
