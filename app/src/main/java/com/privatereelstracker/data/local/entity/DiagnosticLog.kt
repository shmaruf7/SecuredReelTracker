package com.privatereelstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores non-sensitive diagnostic events locally for user inspection.
 * Explicitly avoids any private content, user messages, typed text, or media.
 */
@Entity(tableName = "diagnostic_logs")
data class DiagnosticLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val details: String,
    val wasIncremented: Boolean
)
