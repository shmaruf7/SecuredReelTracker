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
    val instagramCount: Int = 0,
    val youtubeCount: Int = 0,
    val facebookCount: Int = 0,
    val tiktokCount: Int = 0,
    val instagramLimit: Int = 30,
    val youtubeLimit: Int = 30,
    val facebookLimit: Int = 20,
    val tiktokLimit: Int = 20,
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
        refreshAppLimits()
        refreshScreenTime()
        fetchYesterdayComparison()
    }

    private fun refreshAppLimits() {
        val context = getApplication<Application>()
        _uiState.value = _uiState.value.copy(
            instagramLimit = com.privatereelstracker.util.AppPreferences.getAppLimit(context, "Instagram"),
            youtubeLimit = com.privatereelstracker.util.AppPreferences.getAppLimit(context, "YouTube"),
            facebookLimit = com.privatereelstracker.util.AppPreferences.getAppLimit(context, "Facebook"),
            tiktokLimit = com.privatereelstracker.util.AppPreferences.getAppLimit(context, "TikTok")
        )
    }

    private fun observeTodayData() {
        viewModelScope.launch {
            repository.getTodayReelsFlow().collect { todayEntity ->
                val current = todayEntity ?: DailyReelCount(dateString = DateUtils.getTodayString())
                val isGoalExceeded = current.reelCount >= current.dailyGoal

                _uiState.value = _uiState.value.copy(
                    todayReels = current.reelCount,
                    dailyGoal = current.dailyGoal,
                    instagramCount = current.instagramReelsCount,
                    youtubeCount = current.youtubeShortsCount,
                    facebookCount = current.facebookReelsCount,
                    tiktokCount = current.tiktokReelsCount,
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

    fun updateAppLimit(platform: String, newLimit: Int) {
        val context = getApplication<Application>()
        com.privatereelstracker.util.AppPreferences.setAppLimit(context, platform, newLimit)
        refreshAppLimits()
    }

    // Manual test trigger for testing counting in sandbox/test mode
    fun triggerManualTestIncrement(platform: String = "Instagram") {
        viewModelScope.launch {
            repository.incrementReelCount(platform = platform)
            repository.recordDiagnostic(
                eventType = "MANUAL_TEST_TRIGGER",
                details = "Simulated $platform Reel scroll event manually triggered by user",
                wasIncremented = true
            )
        }
    }
}
