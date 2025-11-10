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
        fun bind(contact: Contact) {
            // Use findViewById com os IDs corretos do seu item_contact.xml
            itemView.findViewById<TextView>(R.id.text_contact_name)?.text = contact.name
            itemView.findViewById<TextView>(R.id.text_contact_phone)?.text = contact.phone

            itemView.setOnClickListener {
                onItemClick(contact)
            }
        }
    }
}