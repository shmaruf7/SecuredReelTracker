package com.privatereelstracker.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.privatereelstracker.PrivateReelsTrackerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Native AccessibilityService configured explicitly for Instagram only.
 * Runs strictly locally, preserving user privacy.
 */
class ReelsAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val detector = ReelsDetector()

    companion object {
        private const val TAG = "ReelsAccessibility"
        private const val TARGET_PACKAGE = "com.instagram.android"
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

        // Ignore all applications that are not Instagram
        val packageName = event.packageName?.toString() ?: return
        if (packageName != TARGET_PACKAGE) return

        val result = detector.evaluateEvent(event)
        if (!result.isReelTransition) return

        serviceScope.launch {
            val app = application as? PrivateReelsTrackerApp ?: return@launch
            val repository = app.repository

            repository.incrementReelCount()

            // Record non-sensitive diagnostic entry only on counted transitions
            repository.recordDiagnostic(
                eventType = result.eventTypeString,
                details = "${result.structuralDigest} | ${result.reason}",
                wasIncremented = true
            )
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
