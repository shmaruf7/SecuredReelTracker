package com.privatereelstracker.tracker

object ScreenTimeCalculator {

    /**
     * Estimates Reels-specific viewing duration.
     * Calculates time spent on reels dynamically based on actual reel count and average reel view time.
     * If total Instagram usage is low (e.g. user just opened IG for 1 minute), estimated time
     * cannot exceed that total time. If user spent 20 seconds swiping 20 reels, each fast swipe
     * is ~2-3 seconds, not a static 18 seconds.
     */
     fun calculateEstimatedReelsTime(
        reelCount: Int,
        totalInstagramForegroundMs: Long,
        sessionDurationMs: Long = 0L
    ): Long {
        if (reelCount <= 0 || totalInstagramForegroundMs <= 0L) return 0L

        // If a specific session duration was recorded, use it
        if (sessionDurationMs > 0L) {
            return sessionDurationMs.coerceAtMost(totalInstagramForegroundMs)
        }

        // Dynamically estimate: when swiping rapidly (many reels in short IG foreground time),
        // average time per reel is proportional to total IG time / count.
        val avgTimePerReel = (totalInstagramForegroundMs / reelCount).coerceIn(2_000L, 25_000L)
        val estimatedTotal = reelCount * avgTimePerReel

        return estimatedTotal.coerceAtMost(totalInstagramForegroundMs)
    }

    /**
     * Calculates rolling average reels count over a list of daily counts.
     */
    fun calculateAverageReels(counts: List<Int>): Double {
        if (counts.isEmpty()) return 0.0
        return counts.sum().toDouble() / counts.size
    }

    /**
     * Calculates total screen time across a list of daily durations.
     */
    fun calculateTotalScreenTime(durationsMs: List<Long>): Long {
        return durationsMs.sum()
    }
}
