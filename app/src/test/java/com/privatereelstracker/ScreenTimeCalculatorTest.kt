package com.privatereelstracker

import com.privatereelstracker.tracker.ScreenTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTimeCalculatorTest {

    @Test
    fun testEstimatedReelsTimeCalculation() {
        val reelCount = 10
        val totalIgMs = 300_000L // 5 minutes
        val averagePerReelMs = 15_000L // 15 seconds

        val estimated = ScreenTimeCalculator.calculateEstimatedReelsTime(
            reelCount = reelCount,
            totalInstagramForegroundMs = totalIgMs,
            averageReelDurationMs = averagePerReelMs
        )

        assertEquals(150_000L, estimated)
    }

    @Test
    fun testEstimatedReelsTimeCannotExceedTotalForegroundTime() {
        val reelCount = 100
        val totalIgMs = 60_000L // Only 1 minute of total Instagram foreground time
        val averagePerReelMs = 15_000L

        val estimated = ScreenTimeCalculator.calculateEstimatedReelsTime(
            reelCount = reelCount,
            totalInstagramForegroundMs = totalIgMs,
            averageReelDurationMs = averagePerReelMs
        )

        assertEquals("Estimated reels time must be clamped to total IG foreground time", totalIgMs, estimated)
    }

    @Test
    fun testAverageReelsPerDay() {
        val counts = listOf(20, 30, 40, 10)
        val avg = ScreenTimeCalculator.calculateAverageReels(counts)
        assertEquals(25.0, avg, 0.001)
    }

    @Test
    fun testAverageReelsEmptyList() {
        val avg = ScreenTimeCalculator.calculateAverageReels(emptyList())
        assertEquals(0.0, avg, 0.001)
    }

    @Test
    fun testTotalScreenTime() {
        val times = listOf(1000L, 2500L, 3500L)
        val total = ScreenTimeCalculator.calculateTotalScreenTime(times)
        assertEquals(7000L, total)
    }
}
