package com.privatereelstracker.tracker

object ScreenTimeCalculator {

    /**
     * Estimates Reels-specific viewing duration.
     * Android does not expose internal app tabs (e.g. DMs vs Feed vs Reels) via UsageStatsManager.
     * We calculate an estimate based on the average viewing time per detected Reel swipe
     * bounded by the total foreground time of Instagram.
     *
     * @param reelCount Number of detected reels swiped
     * @param totalInstagramForegroundMs Total time Instagram app was in foreground
     * @param averageReelDurationMs Average estimated duration spent on a single Reel (e.g., 18s)
     */
    fun calculateEstimatedReelsTime(
        reelCount: Int,
        totalInstagramForegroundMs: Long,
        averageReelDurationMs: Long = 18_000L
    ): Long {
        if (reelCount <= 0 || totalInstagramForegroundMs <= 0L) return 0L
        val estimatedRaw = reelCount * averageReelDurationMs
        // Estimated reels time cannot exceed total Instagram foreground time
        return estimatedRaw.coerceAtMost(totalInstagramForegroundMs)
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
