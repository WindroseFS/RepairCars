package com.thorapps.repaircars.notifications

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import java.text.SimpleDateFormat
import java.util.*

class NotificationsAdapter(
    private val onNotificationClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationsAdapter.ViewHolder>(NotificationDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = getItem(position)
        holder.bind(notification)
        holder.itemView.setOnClickListener {
            onNotificationClick(notification)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
        private val dotUnread: View = itemView.findViewById(R.id.dotUnread)

        fun bind(notification: Notification) {
            tvTitle.text = notification.title
            tvMessage.text = notification.message
            tvTime.text = formatTime(notification.timestamp)

            // Marcar como lido/não lido
            dotUnread.visibility = if (notification.isRead) View.GONE else View.VISIBLE

            // Cor baseada no tipo
            val color = when (notification.type) {
                NotificationType.APPOINTMENT -> Color.parseColor("#2196F3")
                NotificationType.STOCK -> Color.parseColor("#4CAF50")
                NotificationType.PAYMENT -> Color.parseColor("#FF9800")
                NotificationType.SYSTEM -> Color.parseColor("#666666")
                NotificationType.ALERT -> Color.parseColor("#F44336")
            }

            dotUnread.setBackgroundColor(color)
        }

        private fun formatTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60000 -> "Agora" // Menos de 1 minuto
                diff < 3600000 -> "${diff / 60000} min atrás" // Menos de 1 hora
                diff < 86400000 -> "${diff / 3600000} h atrás" // Menos de 1 dia
                else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    .format(Date(timestamp))
            }
        }
    }

    // Método para obter notificação por posição (se necessário)
    fun getNotificationAt(position: Int): Notification {
        return getItem(position)
    }

    // Método para atualizar uma notificação específica
    fun updateNotification(notification: Notification) {
        val currentList = currentList.toMutableList()
        val position = currentList.indexOfFirst { it.id == notification.id }
        if (position != -1) {
            currentList[position] = notification
            submitList(currentList)
        }
    }

    // Método para marcar todas como lidas
    fun markAllAsRead() {
        val updatedList = currentList.map { it.copy(isRead = true) }
        submitList(updatedList)
    }

    companion object NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}