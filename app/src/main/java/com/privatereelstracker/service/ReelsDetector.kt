package com.privatereelstracker.service

import android.view.accessibility.AccessibilityEvent

/**
 * Detects short-form video swipe transitions across:
 * - Instagram Reels (com.instagram.android)
 * - YouTube Shorts (com.google.android.youtube)
 * - Facebook Reels (com.facebook.katana)
 * - TikTok (com.zhiliaoapp.musically & com.ss.android.ugc.trill)
 *
 * Strictly ignores regular feed browsing (e.g. Facebook feed, YouTube search/home feed, Instagram feed).
 * Rejects partial/rubber-band drag gestures by requiring distinct item index transitions.
 */
class ReelsDetector(
    private val duplicateFilter: DuplicateFilter = DuplicateFilter(debounceWindowMs = 700L)
) {

    data class DetectionResult(
        val isReelTransition: Boolean,
        val eventTypeString: String,
        val structuralDigest: String,
        val reason: String,
        val platform: String = "Unknown"
    )

    // Track last observed index per package to prevent cross-app state leakage
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

        // Structural verification to isolate Reels/Shorts/TikTok from regular feeds:
        val isShortFormPager = when (platformName) {
            "Instagram" -> {
                // Reels viewer uses ViewPager or ViewPager2. Regular feed is RecyclerView.
                className.contains("ViewPager", ignoreCase = true)
            }
            "YouTube" -> {
                // YouTube Shorts uses a full-screen vertical ViewPager/ReelRecyclerView.
                // Standard YouTube home feed and search results are standard RecyclerViews with multiple items visible.
                // In Shorts, the pager container class contains ViewPager or ViewPager2 or Reel.
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("Shorts", ignoreCase = true) ||
                        (className.contains("RecyclerView", ignoreCase = true) && itemCount > 1 && fromIndex == toIndex)
            }
            "Facebook" -> {
                // Facebook Reels viewer runs in a distinct ViewPager container.
                // Regular Facebook newsfeed uses multi-item FeedRecyclerView / LithoView.
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("Reel", ignoreCase = true)
            }
            "TikTok" -> {
                // TikTok is almost exclusively vertical video swipes (ViewPager / VerticalViewPager).
                className.contains("ViewPager", ignoreCase = true) ||
                        className.contains("RecyclerView", ignoreCase = true)
            }
            else -> false
        }

        if (!isShortFormPager) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored: Regular $platformName feed scroll",
                platform = platformName
            )
        }

        // Issue 2 Prevention: Rejection of partial bounces / uncommitted drags.
        // A true full swipe moves to a new index in the pager.
        val currentIndex = if (fromIndex >= 0) fromIndex else toIndex
        val lastIndex = lastIndexByPackage[packageName] ?: -1

        if (currentIndex >= 0 && currentIndex == lastIndex) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored: Partial bounce / drag without changing video",
                platform = platformName
            )
        }

        val shouldCount = duplicateFilter.shouldCount(
            now = System.currentTimeMillis(),
            contentDigest = "${platformName}_idx_$currentIndex"
        )

        return if (shouldCount) {
            if (currentIndex >= 0) {
                lastIndexByPackage[packageName] = currentIndex
            }
            DetectionResult(
                isReelTransition = true,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Valid $platformName short-video transition (Index: $currentIndex)",
                platform = platformName
            )
        } else {
            DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Duplicate / debounced within time window",
                platform = platformName
            )
        }
    }
}
