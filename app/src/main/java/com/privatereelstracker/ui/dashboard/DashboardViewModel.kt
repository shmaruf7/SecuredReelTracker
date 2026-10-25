package com.privatereelstracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.privatereelstracker.PrivateReelsTrackerApp
import com.privatereelstracker.data.local.entity.DailyReelCount
import com.privatereelstracker.tracker.ScreenTimeCalculator
import com.privatereelstracker.tracker.UsageStatsHelper
import com.privatereelstracker.util.DateUtils
import com.privatereelstracker.util.PermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val todayReels: Int = 0,
    val yesterdayReels: Int = 0,
    val dailyGoal: Int = 50,
    val instagramScreenTimeMs: Long = 0L,
    val estimatedReelsTimeMs: Long = 0L,
    val isAccessibilityEnabled: Boolean = false,
    val isUsageAccessEnabled: Boolean = false,
    val isGoalExceeded: Boolean = false,
    val deltaPercentFromYesterday: Double? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as PrivateReelsTrackerApp).repository

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeTodayData()
    }

    fun refreshDashboard() {
        checkPermissions()
        refreshScreenTime()
        fetchYesterdayComparison()
    }

    private fun observeTodayData() {
        viewModelScope.launch {
            repository.getTodayReelsFlow().collect { todayEntity ->
                val current = todayEntity ?: DailyReelCount(dateString = DateUtils.getTodayString())
                val isGoalExceeded = current.reelCount >= current.dailyGoal

                _uiState.value = _uiState.value.copy(
                    todayReels = current.reelCount,
                    dailyGoal = current.dailyGoal,
                    instagramScreenTimeMs = current.instagramScreenTimeMs,
                    estimatedReelsTimeMs = current.estimatedReelsTimeMs,
                    isGoalExceeded = isGoalExceeded
                )
            }
        }
    }

    private fun refreshScreenTime() {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val igScreenTimeMs = UsageStatsHelper.getInstagramTodayForegroundTime(context)
            val currentToday = repository.getTodayReels()
            val estimatedReelsMs = ScreenTimeCalculator.calculateEstimatedReelsTime(
                reelCount = currentToday.reelCount,
                totalForegroundMs = igScreenTimeMs
            )

            repository.updateScreenTime(igScreenTimeMs, estimatedReelsMs)
        }
    }

    private fun fetchYesterdayComparison() {
        viewModelScope.launch {
            val yesterday = repository.getYesterdayReels()
            val yesterdayCount = yesterday?.reelCount ?: 0
            val todayCount = _uiState.value.todayReels

            val deltaPercent = if (yesterdayCount > 0) {
                ((todayCount - yesterdayCount).toDouble() / yesterdayCount) * 100.0
            } else null

            _uiState.value = _uiState.value.copy(
                yesterdayReels = yesterdayCount,
                deltaPercentFromYesterday = deltaPercent
            )
        }
    }

    fun checkPermissions() {
        val context = getApplication<Application>()
        _uiState.value = _uiState.value.copy(
            isAccessibilityEnabled = PermissionHelper.isAccessibilityServiceEnabled(context),
            isUsageAccessEnabled = PermissionHelper.isUsageAccessEnabled(context)
        )
    }

    fun updateGoal(newGoal: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(newGoal)
        }
    }

    // Manual test trigger for testing counting in sandbox/test mode
    fun triggerManualTestIncrement() {
        viewModelScope.launch {
            repository.incrementReelCount()
            repository.recordDiagnostic(
                eventType = "MANUAL_TEST_TRIGGER",
                details = "Simulated Reel scroll event manually triggered by user",
                wasIncremented = true
            )
        }
    }
}
