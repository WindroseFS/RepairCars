package com.thorapps.repaircars.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R

class ContactsAdapter(
    private val contacts: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textContactName)
        private val phoneTextView: TextView = itemView.findViewById(R.id.textContactPhone)

        fun bind(contact: Contact) {
            nameTextView.text = contact.name

            // Mostrar telefone se disponível, senão mostrar "Sem telefone"
            phoneTextView.text = contact.phone ?: "Sem telefone"

            itemView.setOnClickListener {
                onItemClick(contact)
            }
        }
    }
}