package com.thorapps.repaircars.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.databinding.ItemContactBinding
import com.thorapps.repaircars.database.models.ContactDisplay

class ContactsAdapter(
    private var contactList: List<ContactDisplay>,
    private val onClick: (ContactDisplay) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contactDisplay: ContactDisplay) {
            val contact = contactDisplay.contact

            // Nome
            binding.tvContactName.text = contact.name

            // Telefone — esconde se não houver
            if (contactDisplay.phone.isNullOrBlank()) {
                binding.tvContactPhone.visibility = View.GONE
            } else {
                binding.tvContactPhone.visibility = View.VISIBLE
                binding.tvContactPhone.text = contactDisplay.phone
            }

            // Email — sempre visível
            binding.tvContactEmail.visibility = View.VISIBLE
            binding.tvContactEmail.text = contactDisplay.email ?: "Sem e-mail"

            // Última mensagem
            if (!contactDisplay.lastMessage.isNullOrBlank() &&
                contactDisplay.lastMessage != "Sem mensagens"
            ) {
                binding.tvLastMessage.visibility = View.VISIBLE
                binding.tvLastMessage.text = contactDisplay.lastMessage
            } else {
                binding.tvLastMessage.visibility = View.GONE
            }

            // Clique
            binding.root.setOnClickListener { onClick(contactDisplay) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int = contactList.size

    fun updateData(newContacts: List<ContactDisplay>) {
        contactList = newContacts
        notifyDataSetChanged()
    }
}

