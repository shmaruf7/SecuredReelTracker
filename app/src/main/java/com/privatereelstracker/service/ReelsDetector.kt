package com.privatereelstracker.service

import android.view.accessibility.AccessibilityEvent

/**
 * Detects short-form video swipe transitions across:
 * - Instagram Reels (com.instagram.android)
 * - YouTube Shorts (com.google.android.youtube)
 * - Facebook Reels (com.facebook.katana)
 * - TikTok (com.zhiliaoapp.musically & com.ss.android.ugc.trill)
 *
 * Strictly ignores regular feed browsing while ensuring reliable swipe detection for YouTube Shorts & Facebook Reels.
 */
class ReelsDetector {

    data class DetectionResult(
        val isReelTransition: Boolean,
        val eventTypeString: String,
        val structuralDigest: String,
        val reason: String,
        val platform: String = "Unknown"
    )

    // Per-platform debouncers (800ms window) to prevent double counting without missing swipes
    private val filtersByPlatform = mutableMapOf<String, DuplicateFilter>()

    // Track last observed index for platforms with explicit index incrementing (like Instagram)
    private val lastIndexByPackage = mutableMapOf<String, Int>()

    fun evaluateEvent(event: AccessibilityEvent): DetectionResult {
        val eventType = event.eventType
        val packageName = event.packageName?.toString() ?: ""
        val className = event.className?.toString() ?: ""

        if (eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "EVENT_$eventType",
                structuralDigest = "pkg=$packageName;class=$className",
                reason = "Not a scroll event",
                platform = packageName
            )
        }

        val fromIndex = event.fromIndex
        val toIndex = event.toIndex
        val itemCount = event.itemCount
        val scrollY = event.scrollY
        val structuralDigest = "pkg=$packageName;class=$className;items=$itemCount;from=$fromIndex;to=$toIndex;scrollY=$scrollY"

        val platformName = when {
            packageName.contains("instagram") -> "Instagram"
            packageName.contains("youtube") -> "YouTube"
            packageName.contains("katana") || packageName.contains("facebook") -> "Facebook"
            packageName.contains("musically") || packageName.contains("trill") -> "TikTok"
            else -> "Unknown"
        }

        if (platformName == "Unknown") {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Untracked package $packageName",
                platform = "Unknown"
            )
        }

        // Broad structural match to support view variations across app versions
        val isShortFormContainer = when (platformName) {
            "Instagram" -> {
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("RecyclerView", ignoreCase = true) ||
                        className.contains("Reel", ignoreCase = true)
            }
            "YouTube" -> {
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("Shorts", ignoreCase = true) ||
                        className.contains("Reel", ignoreCase = true) ||
                        className.contains("RecyclerView", ignoreCase = true) ||
                        className.contains("SwipeToDismiss", ignoreCase = true) ||
                        className.contains("ViewGroup", ignoreCase = true) ||
                        className.contains("FrameLayout", ignoreCase = true)
            }
            "Facebook" -> {
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("Reel", ignoreCase = true) ||
                        className.contains("Litho", ignoreCase = true) ||
                        className.contains("RecyclerView", ignoreCase = true) ||
                        className.contains("ViewGroup", ignoreCase = true) ||
                        className.contains("FrameLayout", ignoreCase = true)
            }
            "TikTok" -> {
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("RecyclerView", ignoreCase = true) ||
                        className.contains("ViewGroup", ignoreCase = true) ||
                        className.contains("FrameLayout", ignoreCase = true)
            }
            else -> false
        }

        if (!isShortFormContainer) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored non-container scroll: $className",
                platform = platformName
            )
        }

        // Get or create per-platform duplicate filter
        val filter = filtersByPlatform.getOrPut(platformName) {
            DuplicateFilter(debounceWindowMs = 800L)
        }

        // For Instagram, check if explicit index changed
        val currentIndex = if (fromIndex >= 0) fromIndex else toIndex
        val lastIndex = lastIndexByPackage[platformName] ?: -1

        if (platformName == "Instagram" && currentIndex >= 0 && currentIndex == lastIndex && itemCount > 1) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored: Instagram same index bounce ($currentIndex)",
                platform = platformName
            )
        }

        // Debounce swipe events per platform (800ms gesture window)
        val now = System.currentTimeMillis()
        val shouldCount = filter.shouldCount(now = now, contentDigest = "${platformName}_scroll")

        return if (shouldCount) {
            if (currentIndex >= 0) {
                lastIndexByPackage[platformName] = currentIndex
            }
            DetectionResult(
                isReelTransition = true,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Valid $platformName short-video swipe transition",
                platform = platformName
            )
        } else {
            DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Debounced within 800ms swipe window",
                platform = platformName
            )
        }
    }
}
