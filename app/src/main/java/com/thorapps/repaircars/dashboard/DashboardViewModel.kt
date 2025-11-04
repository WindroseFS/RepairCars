package com.thorapps.repaircars.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading

            try {
                delay(1000)

                val mockData = DashboardData(
                    activeRepairs = 5,
                    completedToday = 3,
                    pendingRequests = 2
                )

                _dashboardState.value = DashboardState.Success(mockData)
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Erro ao carregar dados: ${e.message}")
            }
        }
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

data class DashboardData(
    val activeRepairs: Int,
    val completedToday: Int,
    val pendingRequests: Int
)