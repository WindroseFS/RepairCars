package com.thorapps.repaircars.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        fun bind(chat: Chat) {
            nameTextView.text = chat.contactName
            lastMessageTextView.text = chat.lastMessage
            timeTextView.text = formatTime(chat.timestamp)

            // Mostrar badge de mensagens não lidas
            if (chat.unreadCount > 0) {
                unreadBadge.visibility = View.VISIBLE
                unreadBadge.text = if (chat.unreadCount > 9) "9+" else chat.unreadCount.toString()
            } else {
                unreadBadge.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(chat)
            }
        }

        private fun formatTime(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }
}