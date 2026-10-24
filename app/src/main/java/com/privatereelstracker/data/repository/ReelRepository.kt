package com.privatereelstracker.data.repository

import com.privatereelstracker.data.local.dao.DiagnosticDao
import com.privatereelstracker.data.local.dao.ReelDao
import com.privatereelstracker.data.local.entity.DailyReelCount
import com.privatereelstracker.data.local.entity.DiagnosticLog
import com.privatereelstracker.util.DateUtils
import kotlinx.coroutines.flow.Flow

class ReelRepository(
    private val reelDao: ReelDao,
    private val diagnosticDao: DiagnosticDao
) {
    fun getTodayReelsFlow(): Flow<DailyReelCount?> {
        return reelDao.getDailyCountFlow(DateUtils.getTodayString())
    }

    suspend fun getTodayReels(): DailyReelCount {
        val today = DateUtils.getTodayString()
        var entity = reelDao.getDailyCount(today)
        if (entity == null) {
            entity = DailyReelCount(dateString = today)
            reelDao.insertOrUpdate(entity)
        }
        return entity
    }

    suspend fun getYesterdayReels(): DailyReelCount? {
        return reelDao.getDailyCount(DateUtils.getYesterdayString())
    }

    fun getRecentDaysFlow(limit: Int = 7): Flow<List<DailyReelCount>> {
        return reelDao.getRecentDaysFlow(limit)
    }

    suspend fun incrementReelCount(now: Long = System.currentTimeMillis()) {
        val today = DateUtils.getTodayString()
        val updatedRows = reelDao.incrementReelCount(today, now)
        if (updatedRows == 0) {
            // First reel of today
            val newEntry = DailyReelCount(
                dateString = today,
                reelCount = 1,
                lastUpdatedTimestamp = now
            )
            reelDao.insertOrUpdate(newEntry)
        }
    }

    suspend fun updateScreenTime(instagramScreenTimeMs: Long, estimatedReelsTimeMs: Long) {
        val today = DateUtils.getTodayString()
        var existing = reelDao.getDailyCount(today)
        if (existing == null) {
            existing = DailyReelCount(
                dateString = today,
                instagramScreenTimeMs = instagramScreenTimeMs,
                estimatedReelsTimeMs = estimatedReelsTimeMs
            )
            reelDao.insertOrUpdate(existing)
        } else {
            reelDao.updateScreenTime(today, instagramScreenTimeMs, estimatedReelsTimeMs)
        }
    }

    suspend fun updateDailyGoal(newGoal: Int) {
        val today = DateUtils.getTodayString()
        var existing = reelDao.getDailyCount(today)
        if (existing == null) {
            existing = DailyReelCount(dateString = today, dailyGoal = newGoal)
            reelDao.insertOrUpdate(existing)
        } else {
            reelDao.updateDailyGoal(today, newGoal)
        }
    }

    suspend fun recordDiagnostic(eventType: String, details: String, wasIncremented: Boolean) {
        diagnosticDao.insertLog(
            DiagnosticLog(
                eventType = eventType,
                details = details,
                wasIncremented = wasIncremented
            )
        )
    }

    fun getDiagnosticLogs(): Flow<List<DiagnosticLog>> {
        return diagnosticDao.getRecentLogs(100)
    }

    suspend fun getAllHistory(): List<DailyReelCount> {
        return reelDao.getAllHistory()
    }

    suspend fun clearAllData() {
        reelDao.clearAllData()
        diagnosticDao.clearLogs()
    }
}
