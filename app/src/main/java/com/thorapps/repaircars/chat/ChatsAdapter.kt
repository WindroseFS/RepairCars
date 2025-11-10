package com.thorapps.repaircars.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatsAdapter(
    private val chats: List<Chat>,
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textChatName)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.textLastMessage)
        private val timeTextView: TextView = itemView.findViewById(R.id.textTime)
        private val unreadBadge: TextView = itemView.findViewById(R.id.textUnreadCount)
        private val avatarTextView: TextView = itemView.findViewById(R.id.textAvatar)

        fun bind(chat: Chat) {
            nameTextView.text = chat.contactName
            lastMessageTextView.text = chat.lastMessage
            timeTextView.text = formatTime(chat.timestamp)

            // Configurar avatar com a primeira letra do nome
            avatarTextView.text = chat.contactName.firstOrNull()?.toString() ?: "?"

            // Mostrar badge de mensagens não lidas
            if (chat.unreadCount > 0) {
                unreadBadge.visibility = View.VISIBLE
                unreadBadge.text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString()

                // Destacar conversas com mensagens não lidas
                nameTextView.setTypeface(nameTextView.typeface, android.graphics.Typeface.BOLD)
                lastMessageTextView.setTypeface(lastMessageTextView.typeface, android.graphics.Typeface.BOLD)
            } else {
                unreadBadge.visibility = View.GONE
                nameTextView.setTypeface(nameTextView.typeface, android.graphics.Typeface.NORMAL)
                lastMessageTextView.setTypeface(lastMessageTextView.typeface, android.graphics.Typeface.NORMAL)
            }

            itemView.setOnClickListener {
                onItemClick(chat)
            }
        }

        private fun formatTime(timestamp: Long): String {
            return try {
                val date = Date(timestamp)
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                format.format(date)
            } catch (e: Exception) {
                "00:00"
            }
        }
    }
}