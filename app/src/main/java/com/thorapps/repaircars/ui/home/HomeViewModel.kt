package com.thorapps.repaircars.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    private val _quickActionState = MutableStateFlow<QuickActionState>(QuickActionState.Idle)
    val quickActionState: StateFlow<QuickActionState> = _quickActionState.asStateFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            try {
                // Simular carregamento de dados
                delay(800)

                val mockData = HomeData(
                    userName = "Carlos",
                    todayAppointments = 4,
                    urgentTasks = 2,
                    recentActivities = listOf(
                        RecentActivity("Troca de óleo - Honda Civic", "09:30", "Concluído"),
                        RecentActivity("Alinhamento - Toyota Corolla", "11:15", "Em andamento"),
                        RecentActivity("Freios - Volkswagen Golf", "14:00", "Agendado")
                    )
                )

                _homeState.value = HomeState.Success(mockData)
            } catch (e: Exception) {
                _homeState.value = HomeState.Error("Erro ao carregar dados: ${e.message}")
            }
        }
    }

    fun startQuickAction() {
        viewModelScope.launch {
            _quickActionState.value = QuickActionState.Processing
            // Simular processamento
            delay(500)
            _quickActionState.value = QuickActionState.Completed
            // Reset após completar
            delay(1000)
            _quickActionState.value = QuickActionState.Idle
        }
    }
}

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val data: HomeData) : HomeState()
    data class Error(val message: String) : HomeState()
}

sealed class QuickActionState {
    object Idle : QuickActionState()
    object Processing : QuickActionState()
    object Completed : QuickActionState()
}

data class HomeData(
    val userName: String,
    val todayAppointments: Int,
    val urgentTasks: Int,
    val recentActivities: List<RecentActivity>
)

data class RecentActivity(
    val description: String,
    val time: String,
    val status: String
)