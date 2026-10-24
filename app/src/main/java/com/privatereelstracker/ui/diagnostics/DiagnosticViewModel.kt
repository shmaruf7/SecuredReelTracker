package com.privatereelstracker.ui.diagnostics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.privatereelstracker.PrivateReelsTrackerApp
import com.privatereelstracker.data.local.entity.DiagnosticLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiagnosticUiState(
    val logs: List<DiagnosticLog> = emptyList(),
    val totalDetectedCount: Int = 0
)

class DiagnosticViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as PrivateReelsTrackerApp).repository

    private val _uiState = MutableStateFlow(DiagnosticUiState())
    val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

    init {
        observeLogs()
    }

    private fun observeLogs() {
        viewModelScope.launch {
            repository.getDiagnosticLogs().collect { list ->
                _uiState.value = DiagnosticUiState(
                    logs = list,
                    totalDetectedCount = list.count { it.wasIncremented }
                )
            }
        }
    }
}
