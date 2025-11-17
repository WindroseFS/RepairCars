package com.thorapps.repaircars.contacts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.databinding.ItemContactBinding
import kotlin.math.absoluteValue

class ContactAdapter(
    private val onContactClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            // Nome
            binding.contactName.text = contact.name

            // Email ou telefone (um dos dois é obrigatório)
            val contactInfo = if (contact.email.isNotBlank()) {
                contact.email
            } else {
                contact.phone ?: "Sem informações"
            }
            binding.contactEmail.text = contactInfo

            // Última mensagem
            binding.lastMessage.text = contact.lastMessage.takeIf { it.isNotBlank() } ?: "Sem mensagens"

            // Imagem do contato
            binding.contactImage.setImageBitmap(createContactImage(contact.name, contactInfo))

            // Clique
            binding.root.setOnClickListener {
                onContactClick(contact)
            }
        }

        private fun createContactImage(name: String, contactInfo: String): Bitmap {
            val displayName = name.takeIf { it.isNotBlank() } ?: contactInfo.substringBefore("@")
            val initial = displayName.first().uppercase()

            val size = 100
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Fundo colorido baseado no contato
            val paint = Paint().apply {
                color = generateColorFromContact(contactInfo)
                isAntiAlias = true
            }

            canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

            // Texto da inicial
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = size * 0.4f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            val yPos = (size / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText(initial, size / 2f, yPos, textPaint)

            return bitmap
        }

        private fun generateColorFromContact(contactInfo: String): Int {
            val colors = listOf(
                Color.parseColor("#FF6B6B"),
                Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"),
                Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7"),
                Color.parseColor("#DDA0DD"),
                Color.parseColor("#98D8C8"),
                Color.parseColor("#F7DC6F")
            )

            val index = contactInfo.hashCode().absoluteValue % colors.size
            return colors[index]
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }

    fun updateContacts(newContacts: List<Contact>) {
        submitList(newContacts)
    }
}