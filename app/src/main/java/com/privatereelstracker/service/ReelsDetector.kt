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

    fun evaluateEvent(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?): DetectionResult {
        val eventType = event.eventType
        val className = event.className?.toString() ?: "Unknown"

        val typeString = when (eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> "TYPE_VIEW_SCROLLED"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "TYPE_WINDOW_CONTENT_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "TYPE_WINDOW_STATE_CHANGED"
            else -> "EVENT_$eventType"
        }

        // We only care about scroll or significant content transitions
        if (eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = typeString,
                structuralDigest = "class=$className",
                reason = "Ignored event type"
            )
        }

        // Structural signature: item count, scroll indexes, class type
        val itemCount = event.itemCount
        val fromIndex = event.fromIndex
        val toIndex = event.toIndex
        val structuralDigest = "class=$className;items=$itemCount;idx=$fromIndex-$toIndex"

        // Check if event belongs to a vertical pager or reel viewer
        val isPagerOrScroll = className.contains("ViewPager", ignoreCase = true) ||
                className.contains("RecyclerView", ignoreCase = true) ||
                className.contains("TouchImageView", ignoreCase = true) ||
                eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED

        if (!isPagerOrScroll) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = typeString,
                structuralDigest = structuralDigest,
                reason = "Not a scrollable container"
            )
        }

        val shouldCount = duplicateFilter.shouldCount(
            now = System.currentTimeMillis(),
            contentDigest = structuralDigest
        )

        return if (shouldCount) {
            DetectionResult(
                isReelTransition = true,
                eventTypeString = typeString,
                structuralDigest = structuralDigest,
                reason = "Valid Reel swipe detected (debounced)"
            )
        } else {
            DetectionResult(
                isReelTransition = false,
                eventTypeString = typeString,
                structuralDigest = structuralDigest,
                reason = "Duplicate / debounced within time window"
            )
        }
    }
}
