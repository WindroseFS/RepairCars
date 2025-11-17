package com.thorapps.repaircars.contacts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.thorapps.repaircars.databinding.FragmentContactDetailBinding
import kotlin.math.absoluteValue

class ContactDetailFragment : DialogFragment() {

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var contact: Contact

    companion object {
        private const val ARG_CONTACT = "contact"

        fun newInstance(contact: Contact): ContactDetailFragment {
            val fragment = ContactDetailFragment()
            fragment.arguments = bundleOf(ARG_CONTACT to contact)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // CORREÇÃO: Usando a forma não depreciada do getParcelable
            contact = it.getParcelable<Contact>(ARG_CONTACT)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()

        Log.d("ContactDetail", "Contact data - Name: ${contact.name}, Email: ${contact.email}, Phone: ${contact.phone}")
    }

    private fun setupViews() {
        // Configurar a imagem do contato
        val contactInfo = if (contact.email.isNotBlank()) contact.email else contact.phone ?: ""
        binding.contactImage.setImageBitmap(createContactImage(contact.name, contactInfo))

        binding.contactName.text = contact.name

        // Email ou telefone (um dos dois)
        val contactDetail = if (contact.email.isNotBlank()) {
            contact.email
        } else {
            contact.phone ?: "Sem informações de contato"
        }
        binding.contactEmail.text = contactDetail

        // Telefone (se disponível)
        binding.contactPhone.text = contact.phone ?: "Não informado"

        // Botões de ação - mostrar apenas se a informação estiver disponível
        binding.btnCall.visibility = if (contact.phone.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.btnMessage.visibility = if (contact.phone.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.btnEmail.visibility = if (contact.email.isBlank()) View.GONE else View.VISIBLE

        binding.btnCall.setOnClickListener {
            contact.phone?.let { phone ->
                if (phone.isNotBlank()) {
                    // Implementar chamada telefônica
                    // val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    // startActivity(intent)
                }
            }
        }

        binding.btnMessage.setOnClickListener {
            contact.phone?.let { phone ->
                if (phone.isNotBlank()) {
                    // Implementar envio de SMS
                    // val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone"))
                    // startActivity(intent)
                }
            }
        }

        binding.btnEmail.setOnClickListener {
            if (contact.email.isNotBlank()) {
                // Implementar envio de email
                // val intent = Intent(Intent.ACTION_SENDTO).apply {
                //     data = Uri.parse("mailto:${contact.email}")
                // }
                // startActivity(intent)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        // Fechar ao clicar fora do conteúdo
        binding.root.setOnClickListener {
            dismiss()
        }

        // Impedir que cliques no conteúdo fechem o dialog
        binding.dialogContent.setOnClickListener {
            // Não faz nada - impede o fechamento
        }
    }

    private fun createContactImage(name: String, contactInfo: String): Bitmap {
        val displayName = name.takeIf { it.isNotBlank() } ?: contactInfo.substringBefore("@")
        val initial = displayName.first().uppercase()

        val size = 200
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}