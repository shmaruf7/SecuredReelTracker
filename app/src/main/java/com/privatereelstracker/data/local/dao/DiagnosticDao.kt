package com.privatereelstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.privatereelstracker.data.local.entity.DiagnosticLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {

    @Insert
    suspend fun insertLog(log: DiagnosticLog)

    @Query("SELECT * FROM diagnostic_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<DiagnosticLog>>

    @Query("DELETE FROM diagnostic_logs")
    suspend fun clearLogs()
}
