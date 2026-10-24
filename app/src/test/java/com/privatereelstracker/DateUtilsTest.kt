package com.privatereelstracker

import com.privatereelstracker.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun testFormatDuration() {
        val fortyFiveMinutes = 45 * 60 * 1000L
        assertEquals("45m", DateUtils.formatDuration(fortyFiveMinutes))

        val oneHourTwentyMin = (1 * 3600 + 20 * 60) * 1000L
        assertEquals("1h 20m", DateUtils.formatDuration(oneHourTwentyMin))
    }

    @Test
    fun testTodayFormatMatchesPattern() {
        val today = DateUtils.getTodayString()
        assertNotNull(today)
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        org.junit.Assert.assertTrue("Date should match YYYY-MM-DD", regex.matches(today))
    }

    @Test
    fun testStartAndEndOfDay() {
        val startMs = DateUtils.getStartOfDayMs()
        val endMs = DateUtils.getEndOfDayMs()

        val calStart = Calendar.getInstance().apply { timeInMillis = startMs }
        val calEnd = Calendar.getInstance().apply { timeInMillis = endMs }

        assertEquals(0, calStart.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calStart.get(Calendar.MINUTE))
        assertEquals(0, calStart.get(Calendar.SECOND))

        assertEquals(23, calEnd.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, calEnd.get(Calendar.MINUTE))
        assertEquals(59, calEnd.get(Calendar.SECOND))
    }
}
