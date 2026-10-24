package com.privatereelstracker.service

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Detects Instagram Reels transitions using strictly non-sensitive structural heuristics.
 * NEVER inspects text, messages, passwords, camera/video buffers, or user names.
 */
class ReelsDetector(
    private val duplicateFilter: DuplicateFilter = DuplicateFilter()
) {

    data class DetectionResult(
        val isReelTransition: Boolean,
        val eventTypeString: String,
        val structuralDigest: String,
        val reason: String
    )

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

        val itemCount = event.itemCount
        val fromIndex = event.fromIndex
        val toIndex = event.toIndex
        val structuralDigest = "class=$className;items=$itemCount;idx=$fromIndex-$toIndex"

        val isPagerOrScroll = className.contains("ViewPager", ignoreCase = true) ||
                className.contains("RecyclerView", ignoreCase = true)

        if (!isPagerOrScroll) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Ignored non-feed element"
            )
        }

        val shouldCount = duplicateFilter.shouldCount(
            now = System.currentTimeMillis(),
            contentDigest = structuralDigest
        )

        return if (shouldCount) {
            DetectionResult(
                isReelTransition = true,
                eventTypeString = "TYPE_VIEW_SCROLLED",
                structuralDigest = structuralDigest,
                reason = "Valid Reel swipe detected (debounced)"
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
