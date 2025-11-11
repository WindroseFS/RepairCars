package com.thorapps.repaircars.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R

class ChatsAdapter(
    private val onChatClick: (String, String) -> Unit
) : ListAdapter<Chat, ChatsAdapter.ChatViewHolder>(DiffCallback) {

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.contactId == newItem.contactId
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ChatViewHolder(itemView: View, private val onChatClick: (String, String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val textAvatar: TextView = itemView.findViewById(R.id.textAvatar)
        private val textChatName: TextView = itemView.findViewById(R.id.textChatName)
        private val textLastMessage: TextView = itemView.findViewById(R.id.textLastMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)
        private val textUnreadCount: TextView = itemView.findViewById(R.id.textUnreadCount)

        fun bind(chat: Chat) {
            textAvatar.text = chat.getAvatarText()
            textChatName.text = chat.contactName
            textLastMessage.text = chat.lastMessage
            textTime.text = chat.getFormattedTime()

            // Configurar contador de mensagens não lidas
            if (chat.unreadCount > 0) {
                textUnreadCount.visibility = View.VISIBLE
                textUnreadCount.text = chat.unreadCount.toString()
            } else {
                textUnreadCount.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onChatClick(chat.contactId, chat.contactName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view, onChatClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)
    }
}