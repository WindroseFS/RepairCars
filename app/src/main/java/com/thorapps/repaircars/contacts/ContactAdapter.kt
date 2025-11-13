package com.thorapps.repaircars.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.databinding.ItemContactBinding

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.binding.apply {
            contactLayout.visibility = View.VISIBLE
            chatContactLayout.visibility = View.GONE
            databaseInfoLayout.visibility = View.GONE

            tvContactName.text = contact.name
            tvContactPhone.text = when {
                !contact.phone.isNullOrBlank() -> contact.phone
                !contact.email.isNullOrBlank() -> contact.email
                else -> "Sem contato"
            }

            if (!contact.lastMessage.isNullOrBlank()) {
                tvLastMessage.visibility = View.VISIBLE
                tvLastMessage.text = contact.lastMessage
            } else {
                tvLastMessage.visibility = View.GONE
            }

            contactItem.setOnClickListener { onClick(contact) }
        }
    }

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
