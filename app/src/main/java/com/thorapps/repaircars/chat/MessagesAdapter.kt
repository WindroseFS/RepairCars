package com.thorapps.repaircars.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.database.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesAdapter : ListAdapter<Message, MessagesAdapter.MessageViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageLayoutReceived: View = itemView.findViewById(R.id.messageLayoutReceived)
        private val messageLayoutSent: View = itemView.findViewById(R.id.messageLayoutSent)
        private val messageTextReceived: TextView = itemView.findViewById(R.id.tvMessageTextReceived)
        private val messageTextSent: TextView = itemView.findViewById(R.id.tvMessageTextSent)
        private val messageTimeReceived: TextView = itemView.findViewById(R.id.tvMessageTimeReceived)
        private val messageTimeSent: TextView = itemView.findViewById(R.id.tvMessageTimeSent)

        fun bind(message: Message) {
            val isSentByMe = message.isSentByMe

            if (isSentByMe) {
                messageLayoutSent.visibility = View.VISIBLE
                messageLayoutReceived.visibility = View.GONE
                messageTimeSent.visibility = View.VISIBLE
                messageTimeReceived.visibility = View.GONE

                messageTextSent.text = message.text
                messageTimeSent.text = formatTime(message.timestamp)
            } else {
                messageLayoutSent.visibility = View.GONE
                messageLayoutReceived.visibility = View.VISIBLE
                messageTimeSent.visibility = View.GONE
                messageTimeReceived.visibility = View.VISIBLE

                messageTextReceived.text = message.text
                messageTimeReceived.text = formatTime(message.timestamp)
            }
        }

        private fun formatTime(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }
}