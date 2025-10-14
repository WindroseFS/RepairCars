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

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            try {
                delay(800)

                val mockData = HomeData(
                    userName = "Carlos",
                    todayAppointments = 4,
                    urgentTasks = 2
                )

                _homeState.value = HomeState.Success(mockData)
            } catch (e: Exception) {
                _homeState.value = HomeState.Error("Erro ao carregar dados: ${e.message}")
            }
        }
    }
}

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val data: HomeData) : HomeState()
    data class Error(val message: String) : HomeState()
}

data class HomeData(
    val userName: String,
    val todayAppointments: Int,
    val urgentTasks: Int
)