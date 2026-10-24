package com.privatereelstracker.service

import android.view.accessibility.AccessibilityEvent

/**
 * Detects Instagram Reels transitions using strictly non-sensitive structural heuristics.
 * Differentiates the Reels viewer from the standard Home Feed.
 * Rejects partial/aborted rubber-band drag gestures (requires a distinct index progression).
 */
class ReelsDetector(
    private val duplicateFilter: DuplicateFilter = DuplicateFilter(debounceWindowMs = 800L)
) {

    data class DetectionResult(
        val isReelTransition: Boolean,
        val eventTypeString: String,
        val structuralDigest: String,
        val reason: String
    )

    private var lastObservedIndex: Int = -1

    fun evaluateEvent(event: AccessibilityEvent): DetectionResult {
        val eventType = event.eventType
        val className = event.className?.toString() ?: ""

        if (eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "EVENT_$eventType",
                structuralDigest = "class=$className",
                reason = "Not a scroll event"
            )
        }

        // On Instagram:
        // 1. Home Feed uses a vertical multi-child RecyclerView (class contains RecyclerView)
        // 2. Reels Viewer is a full-screen vertical pager using ViewPager or ViewPager2
        val isPager = className.contains("ViewPager", ignoreCase = true)

        val fromIndex = event.fromIndex
        val toIndex = event.toIndex
        val itemCount = event.itemCount
        val scrollY = event.scrollY

        val structuralDigest = "class=$className;items=$itemCount;from=$fromIndex;to=$toIndex;scrollY=$scrollY"

        // If it's a standard RecyclerView without ViewPager, it is likely the Home Feed, DMs, or Profile grid
        if (!isPager) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored: Regular Home Feed / Grid scroll"
            )
        }

        // Issue 2 Fix: Handle dragging up/down without actually changing reel (aborted swipe).
        // A true full Reel swipe changes the active item index in the ViewPager.
        val currentIndex = if (fromIndex >= 0) fromIndex else toIndex

        if (currentIndex >= 0 && currentIndex == lastObservedIndex) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored: Partial bounce / drag without changing Reel"
            )
        }

        // Debounce filter check
        val shouldCount = duplicateFilter.shouldCount(
            now = System.currentTimeMillis(),
            contentDigest = "reel_idx_$currentIndex"
        )

        return if (shouldCount) {
            if (currentIndex >= 0) {
                lastObservedIndex = currentIndex
            }
            DetectionResult(
                isReelTransition = true,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Valid distinct Reel transition (Index: $currentIndex)"
            )
        } else {
            DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Duplicate / debounced within time window"
            )
        }
    }
}
