package com.privatereelstracker

import com.privatereelstracker.tracker.ScreenTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTimeCalculatorTest {

    @Test
    fun testEstimatedReelsTimeCalculation() {
        val reelCount = 10
        val totalMs = 300_000L // 5 minutes
        val sessionMs = 150_000L // 2.5 minutes

        val estimated = ScreenTimeCalculator.calculateEstimatedReelsTime(
            reelCount = reelCount,
            totalForegroundMs = totalMs,
            sessionDurationMs = sessionMs
        )

        assertEquals(150_000L, estimated)
    }

    @Test
    fun testEstimatedReelsTimeCannotExceedTotalForegroundTime() {
        val reelCount = 100
        val totalMs = 60_000L // Only 1 minute of total foreground time
        val sessionMs = 120_000L // 2 minutes session

        val estimated = ScreenTimeCalculator.calculateEstimatedReelsTime(
            reelCount = reelCount,
            totalForegroundMs = totalMs,
            sessionDurationMs = sessionMs
        )

        assertEquals("Estimated reels time must be clamped to total foreground time", totalMs, estimated)
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
