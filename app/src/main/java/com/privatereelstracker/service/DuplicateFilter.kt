package com.privatereelstracker.service

/**
 * Ensures swipe and transition events are not double-counted.
 * Applies a debouncing window and trackable transition signatures.
 */
class DuplicateFilter(
    private val debounceWindowMs: Long = 1000L
) {
    private var lastIncrementTimestamp: Long = 0L
    private var lastContentDigest: String? = null

    /**
     * Determines whether an event should trigger an increment.
     * @param now Current timestamp in milliseconds
     * @param contentDigest Non-sensitive structural signature of the event (e.g. child count, scroll orientation)
     * @return true if the event is a distinct, valid new Reel transition; false if duplicate or debounced.
     */
    @Synchronized
    fun shouldCount(now: Long = System.currentTimeMillis(), contentDigest: String? = null): Boolean {
        val elapsed = now - lastIncrementTimestamp
        if (elapsed < debounceWindowMs) {
            return false
        }

        if (contentDigest != null && contentDigest == lastContentDigest && elapsed < (debounceWindowMs * 2)) {
            return false
        }

        lastIncrementTimestamp = now
        lastContentDigest = contentDigest
        return true
    }

    @Synchronized
    fun reset() {
        lastIncrementTimestamp = 0L
        lastContentDigest = null
    }
}
