package com.privatereelstracker.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.privatereelstracker.PrivateReelsTrackerApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class SettingsUiState(
    val isExporting: Boolean = false,
    val exportMessage: String? = null,
    val isDataWiped: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as PrivateReelsTrackerApp).repository

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportLocalData(onFileReady: (Uri) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            val history = repository.getAllHistory()

            val jsonArray = JSONArray()
            history.forEach { item ->
                val obj = JSONObject().apply {
                    put("date", item.dateString)
                    put("reels_count", item.reelCount)
                    put("instagram_screen_time_ms", item.instagramScreenTimeMs)
                    put("estimated_reels_time_ms", item.estimatedReelsTimeMs)
                    put("daily_goal", item.dailyGoal)
                }
                jsonArray.put(obj)
            }

            val exportJson = JSONObject().apply {
                put("app", "SecuredReelTracker")
                put("exported_at_ms", System.currentTimeMillis())
                put("records", jsonArray)
            }.toString(2)

            val context = getApplication<Application>()
            val uri = withContext(Dispatchers.IO) {
                val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
                val exportFile = File(exportDir, "reels_tracker_history_${System.currentTimeMillis()}.json")
                FileOutputStream(exportFile).use { it.write(exportJson.toByteArray()) }

                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    exportFile
                )
            }

            _uiState.value = _uiState.value.copy(isExporting = false, exportMessage = "Export complete!")
            onFileReady(uri)
        }
    }

    fun wipeAllHistory() {
        viewModelScope.launch {
            repository.clearAllData()
            _uiState.value = _uiState.value.copy(isDataWiped = true)
        }
    }
}
