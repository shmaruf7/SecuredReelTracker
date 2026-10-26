package com.privatereelstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.privatereelstracker.data.local.entity.DailyReelCount
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {

    @Query("SELECT * FROM daily_reel_counts WHERE dateString = :date LIMIT 1")
    fun getDailyCountFlow(date: String): Flow<DailyReelCount?>

    @Query("SELECT * FROM daily_reel_counts WHERE dateString = :date LIMIT 1")
    suspend fun getDailyCount(date: String): DailyReelCount?

    @Query("SELECT * FROM daily_reel_counts ORDER BY dateString DESC LIMIT :limit")
    fun getRecentDaysFlow(limit: Int = 7): Flow<List<DailyReelCount>>

    @Query("SELECT * FROM daily_reel_counts ORDER BY dateString DESC")
    suspend fun getAllHistory(): List<DailyReelCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dailyReelCount: DailyReelCount)

    @Query("UPDATE daily_reel_counts SET reelCount = reelCount + 1, lastUpdatedTimestamp = :now WHERE dateString = :date")
    suspend fun incrementReelCount(date: String, now: Long = System.currentTimeMillis()): Int

    @Query("UPDATE daily_reel_counts SET reelCount = reelCount + 1, instagramReelsCount = instagramReelsCount + 1, lastUpdatedTimestamp = :now WHERE dateString = :date")
    suspend fun incrementInstagramReelsCount(date: String, now: Long = System.currentTimeMillis()): Int

    @Query("UPDATE daily_reel_counts SET reelCount = reelCount + 1, youtubeShortsCount = youtubeShortsCount + 1, lastUpdatedTimestamp = :now WHERE dateString = :date")
    suspend fun incrementYouTubeShortsCount(date: String, now: Long = System.currentTimeMillis()): Int

    @Query("UPDATE daily_reel_counts SET reelCount = reelCount + 1, facebookReelsCount = facebookReelsCount + 1, lastUpdatedTimestamp = :now WHERE dateString = :date")
    suspend fun incrementFacebookReelsCount(date: String, now: Long = System.currentTimeMillis()): Int

    @Query("UPDATE daily_reel_counts SET reelCount = reelCount + 1, tiktokReelsCount = tiktokReelsCount + 1, lastUpdatedTimestamp = :now WHERE dateString = :date")
    suspend fun incrementTikTokReelsCount(date: String, now: Long = System.currentTimeMillis()): Int

    @Query("UPDATE daily_reel_counts SET instagramScreenTimeMs = :screenTimeMs, estimatedReelsTimeMs = :estimatedReelsMs WHERE dateString = :date")
    suspend fun updateScreenTime(date: String, screenTimeMs: Long, estimatedReelsMs: Long)

    @Query("UPDATE daily_reel_counts SET dailyGoal = :goal WHERE dateString = :date")
    suspend fun updateDailyGoal(date: String, goal: Int)

    @Query("DELETE FROM daily_reel_counts")
    suspend fun clearAllData()
}
