package com.thorapps.repaircars.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val _notificationsState = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val notificationsState: StateFlow<NotificationsState> = _notificationsState.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())

    val unreadCount = _notifications.map { notifications ->
        notifications.count { !it.isRead }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = NotificationsState.Loading

            try {
                delay(600)

                val mockNotifications = listOf(
                    Notification(
                        id = 1,
                        title = "Novo agendamento",
                        message = "Cliente João agendou troca de óleo para amanhã",
                        timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                        type = NotificationType.APPOINTMENT,
                        isRead = false
                    ),
                    Notification(
                        id = 2,
                        title = "Peça chegou",
                        message = "Pastilhas de freio para Honda Civic estão disponíveis",
                        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                        type = NotificationType.STOCK,
                        isRead = true
                    )
                )

                _notifications.value = mockNotifications
                _notificationsState.value = NotificationsState.Success(mockNotifications)
            } catch (e: Exception) {
                _notificationsState.value = NotificationsState.Error("Erro ao carregar notificações: ${e.message}")
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            _notifications.value = _notifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            _notificationsState.value = NotificationsState.Success(_notifications.value)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _notifications.value = _notifications.value.map { notification ->
                notification.copy(isRead = true)
            }
            _notificationsState.value = NotificationsState.Success(_notifications.value)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            _notifications.value = emptyList()
            _notificationsState.value = NotificationsState.Success(emptyList())
        }
    }
}

sealed class NotificationsState {
    object Loading : NotificationsState()
    data class Success(val notifications: List<Notification>) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    APPOINTMENT, STOCK, PAYMENT, SYSTEM, ALERT
}