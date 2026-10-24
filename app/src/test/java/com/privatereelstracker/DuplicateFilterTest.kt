package com.privatereelstracker

import com.privatereelstracker.service.DuplicateFilter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DuplicateFilterTest {

    private lateinit var filter: DuplicateFilter

    @Before
    fun setUp() {
        filter = DuplicateFilter(debounceWindowMs = 1000L)
    }

    @Test
    fun testFirstEventAllowed() {
        val result = filter.shouldCount(now = 1000L, contentDigest = "item_1")
        assertTrue("First event should always be counted", result)
    }

    @Test
    fun testDuplicateEventWithinWindowBlocked() {
        filter.shouldCount(now = 1000L, contentDigest = "item_1")
        val rapidNextEvent = filter.shouldCount(now = 1400L, contentDigest = "item_2")
        assertFalse("Event occurring within 1000ms debounce window should be blocked", rapidNextEvent)
    }

    @Test
    fun testEventAfterDebounceWindowAllowed() {
        filter.shouldCount(now = 1000L, contentDigest = "item_1")
        val nextEvent = filter.shouldCount(now = 2100L, contentDigest = "item_2")
        assertTrue("Event occurring after debounce window should be accepted", nextEvent)
    }

    @Test
    fun testIdenticalContentDigestDebounceExtended() {
        filter.shouldCount(now = 1000L, contentDigest = "same_item")
        // Same content at 1500ms (within 2 * debounce)
        val duplicateDigestEvent = filter.shouldCount(now = 1500L, contentDigest = "same_item")
        assertFalse("Identical digest within extended window should be rejected", duplicateDigestEvent)
    }

    @Test
    fun testResetClearsState() {
        filter.shouldCount(now = 1000L, contentDigest = "item_1")
        filter.reset()
        val afterReset = filter.shouldCount(now = 1100L, contentDigest = "item_1")
        assertTrue("After reset, previous timestamp should not block new event", afterReset)
    }
}
