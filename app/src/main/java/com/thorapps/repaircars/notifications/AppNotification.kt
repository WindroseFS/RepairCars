package com.thorapps.repaircars.notifications

import java.util.*

/**
 * Data class representing a notification in the app
 */
data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: NotificationType,
    val isRead: Boolean = false,
    val actionUrl: String? = null
) {

    companion object {
        fun generateId(): String {
            return "notif_${UUID.randomUUID()}"
        }

        fun createSampleNotifications(): List<AppNotification> {
            val currentTime = System.currentTimeMillis()
            return listOf(
                AppNotification(
                    id = generateId(),
                    title = "Nova Mensagem",
                    message = "Você recebeu uma nova mensagem no chat",
                    timestamp = currentTime - 300000,
                    type = NotificationType.APPOINTMENT,
                    isRead = false
                ),
                AppNotification(
                    id = generateId(),
                    title = "Estoque Baixo",
                    message = "Peças de reposição estão com estoque baixo",
                    timestamp = currentTime - 3600000,
                    type = NotificationType.STOCK,
                    isRead = true
                ),
                AppNotification(
                    id = generateId(),
                    title = "Pagamento Confirmado",
                    message = "Seu pagamento foi processado com sucesso",
                    timestamp = currentTime - 86400000,
                    type = NotificationType.PAYMENT,
                    isRead = false
                )
            )
        }
    }
}
