package com.thorapps.repaircars.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.database.models.Message
import java.text.SimpleDateFormat
import java.util.*

class SimpleMessagesAdapter(private var messages: List<Message>) :
    RecyclerView.Adapter<SimpleMessagesAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tvSimpleMessage)
        private val messageTime: TextView = itemView.findViewById(R.id.tvSimpleTime)
        private val messageContainer: View = itemView.findViewById(R.id.simpleMessageContainer)

        fun bind(message: Message) {
            // Torna o container visível
            messageContainer.visibility = View.VISIBLE

            // Esconde outros containers
            itemView.findViewById<View>(R.id.receivedMessageContainer).visibility = View.GONE
            itemView.findViewById<View>(R.id.sentMessageContainer).visibility = View.GONE

            messageText.text = message.text
            messageTime.text = formatTime(message.timestamp)

            // Define o alinhamento baseado em quem enviou a mensagem
            val layoutParams = messageContainer.layoutParams as ViewGroup.MarginLayoutParams
            if (message.isSentByMe) {
                // Mensagem enviada por mim - alinha à direita
                layoutParams.setMargins(100, 4, 16, 4)
                messageContainer.setBackgroundResource(R.drawable.message_sent_background)
                messageText.setTextColor(itemView.context.getColor(android.R.color.white))
                messageTime.setTextColor(itemView.context.getColor(android.R.color.white))
            } else {
                // Mensagem recebida - alinha à esquerda
                layoutParams.setMargins(16, 4, 100, 4)
                messageContainer.setBackgroundResource(R.drawable.message_received_background)
                messageText.setTextColor(itemView.context.getColor(android.R.color.black))
                messageTime.setTextColor(itemView.context.getColor(R.color.gray))
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
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}