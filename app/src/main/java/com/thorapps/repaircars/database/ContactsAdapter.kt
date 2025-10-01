package com.thorapps.repaircars.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R

class ContactsAdapter(
    private var contacts: List<ContactDisplay>, // Should be ContactDisplay
    private val onContactClick: (ContactDisplay) -> Unit = {} // Should be ContactDisplay
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.tvName)
        val contactEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val lastMessage: TextView = itemView.findViewById(R.id.tvLastMessage) // Keep this for ContactDisplay
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.contactName.text = contact.name
        holder.contactEmail.text = contact.email
        holder.lastMessage.text = contact.lastMessage ?: "Nenhuma mensagem"

        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<ContactDisplay>) { // Should be ContactDisplay
        this.contacts = newContacts
        notifyDataSetChanged()
    }
}