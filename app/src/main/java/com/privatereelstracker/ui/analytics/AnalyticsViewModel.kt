package com.privatereelstracker.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.privatereelstracker.PrivateReelsTrackerApp
import com.privatereelstracker.data.local.entity.DailyReelCount
import com.privatereelstracker.tracker.ScreenTimeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val recentDays: List<DailyReelCount> = emptyList(),
    val averageReelsPerDay: Double = 0.0,
    val totalScreenTimeMs: Long = 0L,
    val averageScreenTimeMs: Long = 0L,
    val highestDayCount: Int = 0
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as PrivateReelsTrackerApp).repository

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadSevenDayData()
    }

    private fun loadSevenDayData() {
        viewModelScope.launch {
            repository.getRecentDaysFlow(7).collect { days ->
                val orderedDays = days.reversed() // Oldest to newest for left-to-right chart
                val reelCounts = orderedDays.map { it.reelCount }
                val screenTimes = orderedDays.map { it.instagramScreenTimeMs }

                val avgReels = ScreenTimeCalculator.calculateAverageReels(reelCounts)
                val totalScreenTime = ScreenTimeCalculator.calculateTotalScreenTime(screenTimes)
                val avgScreenTime = if (screenTimes.isNotEmpty()) totalScreenTime / screenTimes.size else 0L
                val maxCount = reelCounts.maxOrNull() ?: 0

                _uiState.value = AnalyticsUiState(
                    recentDays = orderedDays,
                    averageReelsPerDay = avgReels,
                    totalScreenTimeMs = totalScreenTime,
                    averageScreenTimeMs = avgScreenTime,
                    highestDayCount = maxCount
                )
            }
        }
    }
}
