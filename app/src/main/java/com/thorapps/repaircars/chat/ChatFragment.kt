package com.thorapps.repaircars.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.database.Message
import com.thorapps.repaircars.databinding.FragmentChatBinding
import java.util.*

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()

    private lateinit var messagesAdapter: SimpleMessagesAdapter
    private val messagesList = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactId = args.contactId
        val contactName = args.contactName

        setupToolbar(contactName)
        setupChat()
        loadSampleMessages(contactId)
    }

    private fun setupToolbar(contactName: String) {
        // ✅ CORREÇÃO: Apenas define o título, a navegação é gerenciada pelo MainActivity
        // O MainActivity já configura a seta de voltar automaticamente para este fragment
    }

    private fun setupChat() {
        // Configurar RecyclerView com o adapter simples
        messagesAdapter = SimpleMessagesAdapter(messagesList)
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messagesAdapter
        }

        // Configurar botão de enviar
        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        // Configurar botão de localização
        binding.btnLocation.setOnClickListener {
            shareLocation()
        }
    }

    private fun loadSampleMessages(contactId: String) {
        // Mensagens de exemplo
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -5)

        messagesList.clear()
        messagesList.addAll(listOf(
            Message(
                id = 1,
                contactId = contactId,
                text = "Olá! Como posso ajudar com seu veículo?",
                isSentByMe = false,
                timestamp = calendar.timeInMillis
            ),
            Message(
                id = 2,
                contactId = contactId,
                text = "Preciso de ajuda com o motor do meu carro",
                isSentByMe = true,
                timestamp = calendar.apply { add(Calendar.MINUTE, 1) }.timeInMillis
            ),
            Message(
                id = 3,
                contactId = contactId,
                text = "Claro! Qual modelo e qual problema específico?",
                isSentByMe = false,
                timestamp = calendar.apply { add(Calendar.MINUTE, 1) }.timeInMillis
            ),
            Message(
                id = 4,
                contactId = contactId,
                text = "É um Honda Civic 2020, está fazendo um barulho estranho ao acelerar",
                isSentByMe = true,
                timestamp = calendar.apply { add(Calendar.MINUTE, 1) }.timeInMillis
            ),
            Message(
                id = 5,
                contactId = contactId,
                text = "Pode ser problema na correia dentada. Traga para uma avaliação gratuita",
                isSentByMe = false,
                timestamp = calendar.apply { add(Calendar.MINUTE, 1) }.timeInMillis
            )
        ))

        messagesAdapter.updateMessages(messagesList)
        scrollToBottom()
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val newMessage = Message(
                id = System.currentTimeMillis(),
                contactId = args.contactId,
                text = messageText,
                isSentByMe = true,
                timestamp = System.currentTimeMillis()
            )

            messagesList.add(newMessage)
            messagesAdapter.updateMessages(messagesList)

            binding.etMessage.text?.clear()
            scrollToBottom()

            simulateResponse()
        }
    }

    private fun simulateResponse() {
        binding.root.postDelayed({
            val responses = listOf(
                "Entendi, vou verificar isso para você",
                "Pode me dar mais detalhes sobre o barulho?",
                "Posso ajudar com isso sim!",
                "Vou consultar nossa equipe técnica sobre esse problema"
            )
            val randomResponse = responses.random()

            val responseMessage = Message(
                id = System.currentTimeMillis() + 1,
                contactId = args.contactId,
                text = randomResponse,
                isSentByMe = false,
                timestamp = System.currentTimeMillis()
            )

            messagesList.add(responseMessage)
            messagesAdapter.updateMessages(messagesList)
            scrollToBottom()
        }, 1500)
    }

    private fun shareLocation() {
        val locationMessage = Message(
            id = System.currentTimeMillis(),
            contactId = args.contactId,
            text = "📍 Localização compartilhada - Oficina Central",
            isSentByMe = true,
            timestamp = System.currentTimeMillis(),
            latitude = -23.5505, // Exemplo: latitude de São Paulo
            longitude = -46.6333 // Exemplo: longitude de São Paulo
        )

        messagesList.add(locationMessage)
        messagesAdapter.updateMessages(messagesList)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        binding.messagesRecyclerView.post {
            if (messagesList.isNotEmpty()) {
                binding.messagesRecyclerView.smoothScrollToPosition(messagesList.size - 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}