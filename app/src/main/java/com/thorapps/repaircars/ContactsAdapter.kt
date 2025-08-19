package com.thorapps.repaircars

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(
    private var contacts: List<DatabaseHelper.Contact>,
    private val onItemClick: (DatabaseHelper.Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.contactName)
        val messageTextView: TextView = view.findViewById(R.id.lastMessage)
        val timeTextView: TextView = view.findViewById(R.id.messageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.messageTextView.text = contact.lastMessage ?: ""
        holder.timeTextView.text = contact.timestamp

        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newContacts: List<DatabaseHelper.Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}