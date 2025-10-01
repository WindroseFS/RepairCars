package com.thorapps.repaircars

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.database.Message


class MessagesAdapter(private var messages: List<Message> = emptyList()) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.message_text)
        val senderView: TextView = itemView.findViewById(R.id.sender_name)
        val timeView: TextView = itemView.findViewById(R.id.message_time)
        val locationView: TextView = itemView.findViewById(R.id.location_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.textView.text = message.text
        holder.senderView.text = if (message.sender == "me") "Eu" else "Contato"
        holder.timeView.text = formatTime(message.timestamp)

        if (message.latitude != null && message.longitude != null) {
            holder.locationView.visibility = View.VISIBLE
            holder.locationView.text = "📍 ${message.latitude}, ${message.longitude}"
        } else {
            holder.locationView.visibility = View.GONE
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: Long): String {
        return android.text.format.DateFormat.format("HH:mm", timestamp).toString()
    }
}