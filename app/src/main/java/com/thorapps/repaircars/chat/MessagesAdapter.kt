package com.thorapps.repaircars.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.database.models.Message
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter : ListAdapter<Message, MessagesAdapter.MessageViewHolder>(DiffCallback) {

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receivedMessageContainer: View = itemView.findViewById(R.id.receivedMessageContainer)
        private val sentMessageContainer: View = itemView.findViewById(R.id.sentMessageContainer)
        private val simpleMessageContainer: View = itemView.findViewById(R.id.simpleMessageContainer)

        private val tvReceivedMessage: TextView = itemView.findViewById(R.id.tvReceivedMessage)
        private val tvSentMessage: TextView = itemView.findViewById(R.id.tvSentMessage)
        private val tvSimpleMessage: TextView = itemView.findViewById(R.id.tvSimpleMessage)

        private val tvReceivedTime: TextView = itemView.findViewById(R.id.tvReceivedTime)
        private val tvSentTime: TextView = itemView.findViewById(R.id.tvSentTime)
        private val tvSimpleTime: TextView = itemView.findViewById(R.id.tvSimpleTime)

        fun bind(message: Message) {
            // Esconder todos os containers primeiro
            receivedMessageContainer.visibility = View.GONE
            sentMessageContainer.visibility = View.GONE
            simpleMessageContainer.visibility = View.GONE

            if (message.isSentByMe) {
                // Mensagem enviada por mim
                sentMessageContainer.visibility = View.VISIBLE
                tvSentMessage.text = message.text
                tvSentTime.text = formatTime(message.timestamp)
            } else {
                // Mensagem recebida
                receivedMessageContainer.visibility = View.VISIBLE
                tvReceivedMessage.text = message.text
                tvReceivedTime.text = formatTime(message.timestamp)
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