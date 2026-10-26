package com.privatereelstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores aggregate metrics per calendar day (formatted as YYYY-MM-DD).
 */
@Entity(tableName = "daily_reel_counts")
data class DailyReelCount(
    @PrimaryKey
    val dateString: String, // e.g. "2026-10-24"
    val reelCount: Int = 0,
    val instagramReelsCount: Int = 0,
    val youtubeShortsCount: Int = 0,
    val facebookReelsCount: Int = 0,
    val tiktokReelsCount: Int = 0,
    val instagramScreenTimeMs: Long = 0L,
    val estimatedReelsTimeMs: Long = 0L,
    val dailyGoal: Int = 50,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
