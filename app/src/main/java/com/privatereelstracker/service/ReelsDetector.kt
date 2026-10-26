package com.privatereelstracker.service

import android.view.accessibility.AccessibilityEvent

/**
 * Detects short-form video swipe transitions across:
 * - Instagram Reels (com.instagram.android)
 * - YouTube Shorts (com.google.android.youtube)
 * - Facebook Reels (com.facebook.katana)
 * - TikTok (com.zhiliaoapp.musically & com.ss.android.ugc.trill)
 *
 * Guarantees exactly 1 count per swipe (1500ms window), ignores window-switch/app-resume events,
 * and strictly filters out messaging, regular feed browsing, and long YouTube videos.
 */
class ReelsDetector {

    data class DetectionResult(
        val isReelTransition: Boolean,
        val eventTypeString: String,
        val structuralDigest: String,
        val reason: String,
        val platform: String = "Unknown"
    )

    // Per-platform debouncers (1500ms gesture window)
    private val filtersByPlatform = mutableMapOf<String, DuplicateFilter>()

    // Track last swipe timestamp per package
    private val lastSwipeTimeByPackage = mutableMapOf<String, Long>()

    // Track last observed index for platforms with explicit index incrementing (like Instagram)
    private val lastIndexByPackage = mutableMapOf<String, Int>()

    fun evaluateEvent(event: AccessibilityEvent): DetectionResult {
        val eventType = event.eventType
        val packageName = event.packageName?.toString() ?: ""
        val className = event.className?.toString() ?: ""

        // NEVER count TYPE_WINDOW_STATE_CHANGED as a Reel swipe (prevents +2 counts on app switch/resume)
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_WINDOW_STATE_CHANGED",
                structuralDigest = "pkg=$packageName;class=$className",
                reason = "Ignored: Window state change / App switch",
                platform = packageName
            )
        }

        val isSwipeEvent = eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
                eventType == AccessibilityEvent.TYPE_VIEW_SELECTED

        if (!isSwipeEvent) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "EVENT_$eventType",
                structuralDigest = "pkg=$packageName;class=$className",
                reason = "Not a scroll/selected gesture event",
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
                eventTypeString = "TYPE_$eventType",
                structuralDigest = structuralDigest,
                reason = "Untracked package $packageName",
                platform = "Unknown"
            )
        }

        // 1. FILTER OUT MESSAGING & CHATS (Instagram Direct, Facebook Messenger, etc.)
        val isMessaging = className.contains("Message", ignoreCase = true) ||
                className.contains("Chat", ignoreCase = true) ||
                className.contains("Direct", ignoreCase = true) ||
                className.contains("Thread", ignoreCase = true) ||
                className.contains("Composer", ignoreCase = true) ||
                className.contains("Inbox", ignoreCase = true)

        if (isMessaging) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_$eventType",
                structuralDigest = structuralDigest,
                reason = "Ignored: Messaging / Chat interaction",
                platform = platformName
            )
        }

        // 2. FILTER OUT YOUTUBE LONG VIDEOS & HOME/SEARCH FEED
        if (platformName == "YouTube") {
            val isLongVideoOrSearch = className.contains("Watch", ignoreCase = true) ||
                    className.contains("Search", ignoreCase = true) ||
                    className.contains("Feed", ignoreCase = true) ||
                    (itemCount > 2 && fromIndex > 0 && fromIndex != toIndex)

            if (isLongVideoOrSearch) {
                return DetectionResult(
                    isReelTransition = false,
                    eventTypeString = "TYPE_$eventType",
                    structuralDigest = structuralDigest,
                    reason = "Ignored: YouTube Regular Video / Search Feed",
                    platform = "YouTube"
                )
            }
        }

        // 3. FILTER OUT REGULAR FACEBOOK NEWSFEED
        if (platformName == "Facebook") {
            val isRegularFeed = className.contains("NewsFeed", ignoreCase = true) ||
                    className.contains("FeedRecyclerView", ignoreCase = true) ||
                    (itemCount > 3 && fromIndex > 0 && fromIndex != toIndex)

            if (isRegularFeed) {
                return DetectionResult(
                    isReelTransition = false,
                    eventTypeString = "TYPE_$eventType",
                    structuralDigest = structuralDigest,
                    reason = "Ignored: Facebook Newsfeed Scroll",
                    platform = "Facebook"
                )
            }
        }

        // 4. PREVENT MULTI-COUNTING PER SWIPE GESTURE (1500ms Gesture Window)
        val now = System.currentTimeMillis()
        val lastSwipeTime = lastSwipeTimeByPackage[platformName] ?: 0L
        if (now - lastSwipeTime < 1500L) {
            return DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_$eventType",
                structuralDigest = structuralDigest,
                reason = "Debounced: Same swipe gesture within 1.5s window",
                platform = platformName
            )
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

        // Get or create per-platform duplicate filter (1500ms gesture window)
        val filter = filtersByPlatform.getOrPut(platformName) {
            DuplicateFilter(debounceWindowMs = 1500L)
        }

        val shouldCount = filter.shouldCount(now = now, contentDigest = "${platformName}_scroll")

        return if (shouldCount) {
            lastSwipeTimeByPackage[platformName] = now
            if (currentIndex >= 0) {
                lastIndexByPackage[platformName] = currentIndex
            }
            DetectionResult(
                isReelTransition = true,
                eventTypeString = "TYPE_$eventType",
                structuralDigest = structuralDigest,
                reason = "Valid $platformName short-video swipe transition",
                platform = platformName
            )
        } else {
            DetectionResult(
                isReelTransition = false,
                eventTypeString = "TYPE_$eventType",
                structuralDigest = structuralDigest,
                reason = "Debounced within 1.5s swipe window",
                platform = platformName
            )
        }
    }
}
