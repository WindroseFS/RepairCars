package com.thorapps.repaircars.contacts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.thorapps.repaircars.databinding.FragmentContactDetailBinding

class ContactDetailFragment : DialogFragment() {

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var contact: Contact

    companion object {
        private const val ARG_CONTACT = "contact"

        fun newInstance(contact: Contact): ContactDetailFragment {
            val args = Bundle().apply {
                putParcelable(ARG_CONTACT, contact)
            }
            val fragment = ContactDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.thorapps.repaircars.R.style.FullScreenDialog)
        arguments?.let {
            contact = it.getParcelable(ARG_CONTACT)!!
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
    }

    private fun setupViews() {
        // Configurar a imagem do contato usando o email
        binding.contactImage.setImageBitmap(createContactImage(contact.name, contact.email))

        binding.contactName.text = contact.name
        binding.contactEmail.text = contact.email
        binding.contactPhone.text = contact.phone ?: "Não informado"

        // Botões de ação
        binding.btnCall.setOnClickListener {
            // Implementar chamada telefônica
            // val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
            // startActivity(intent)
        }

        binding.btnMessage.setOnClickListener {
            // Implementar envio de SMS
            // val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phone}"))
            // startActivity(intent)
        }

        binding.btnEmail.setOnClickListener {
            // Implementar envio de email
            // val intent = Intent(Intent.ACTION_SENDTO).apply {
            //     data = Uri.parse("mailto:${contact.email}")
            // }
            // startActivity(intent)
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

    private fun createContactImage(name: String, email: String): Bitmap {
        // Criar uma imagem baseada no email (ou nome, se preferir)
        val displayName = name.takeIf { it.isNotBlank() } ?: email.substringBefore("@")
        val initial = displayName.first().uppercase()

        val size = 200 // tamanho da imagem
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fundo colorido baseado no email (para consistência)
        val paint = Paint().apply {
            color = generateColorFromEmail(email)
            isAntiAlias = true
        }

        // Desenhar círculo de fundo
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

    private fun generateColorFromEmail(email: String): Int {
        // Gerar cor consistente baseada no email
        val colors = listOf(
            Color.parseColor("#FF6B6B"), // Vermelho
            Color.parseColor("#4ECDC4"), // Verde água
            Color.parseColor("#45B7D1"), // Azul
            Color.parseColor("#96CEB4"), // Verde
            Color.parseColor("#FFEAA7"), // Amarelo
            Color.parseColor("#DDA0DD"), // Ameixa
            Color.parseColor("#98D8C8"), // Verde menta
            Color.parseColor("#F7DC6F")  // Amarelo claro
        )

        val index = email.hashCode().absoluteValue % colors.size
        return colors[index]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}