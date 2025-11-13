package com.thorapps.repaircars.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.database.models.Message
import com.thorapps.repaircars.databinding.FragmentChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()

    private lateinit var messagesAdapter: SimpleMessagesAdapter
    private val messagesList = mutableListOf<Message>()
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        val contactId = args.contactId
        val contactName = args.contactName

        Log.d("ChatFragment", "Abrindo chat com: $contactName ($contactId)")

        setupToolbar(contactName)
        setupChat()
        loadMessages(contactId)
    }

    private fun setupToolbar(contactName: String) {
        activity?.title = contactName
    }

    private fun setupChat() {
        messagesAdapter = SimpleMessagesAdapter(messagesList)
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messagesAdapter
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.btnLocation.setOnClickListener {
            shareLocation()
        }
    }

    private fun loadMessages(contactId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = databaseHelper.getMessagesForContact(contactId)

                Log.d("ChatFragment", "Carregadas ${messages.size} mensagens para o contato $contactId")

                CoroutineScope(Dispatchers.Main).launch {
                    messagesList.clear()
                    messagesList.addAll(messages)
                    messagesAdapter.updateMessages(messagesList)
                    scrollToBottom()

                    if (messages.isEmpty()) {
                        showEmptyState()
                    } else {
                        hideEmptyState()
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatFragment", "Erro ao carregar mensagens: ${e.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    showEmptyState()
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.messagesRecyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        binding.messagesRecyclerView.visibility = View.VISIBLE
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val newMessage = Message(
                contactId = args.contactId,
                text = messageText,
                isSentByMe = true
            )

            messagesList.add(newMessage)
            messagesAdapter.updateMessages(messagesList)

            binding.etMessage.text?.clear()
            scrollToBottom()

            CoroutineScope(Dispatchers.IO).launch {
                databaseHelper.addMessage(
                    args.contactId,
                    messageText,
                    true
                )
            }

            simulateResponse()
        }
    }

    private fun simulateResponse() {
        binding.root.postDelayed({
            val responses = listOf(
                "Entendi, vou verificar isso para você",
                "Pode me dar mais detalhes sobre o barulho?",
                "Posso ajudar com isso sim!",
                "Vou consultar nossa equipe técnica sobre esse problema",
                "Podemos agendar uma avaliação para seu veículo"
            )
            val randomResponse = responses.random()

            val responseMessage = Message(
                contactId = args.contactId,
                text = randomResponse,
                isSentByMe = false
            )

            messagesList.add(responseMessage)
            messagesAdapter.updateMessages(messagesList)
            scrollToBottom()

            CoroutineScope(Dispatchers.IO).launch {
                databaseHelper.addMessage(
                    args.contactId,
                    randomResponse,
                    false
                )
            }
        }, 1500)
    }

    private fun shareLocation() {
        val locationMessage = Message(
            contactId = args.contactId,
            text = "📍 Localização compartilhada - Oficina Central",
            isSentByMe = true,
            latitude = -23.5505,
            longitude = -46.6333
        )

        messagesList.add(locationMessage)
        messagesAdapter.updateMessages(messagesList)
        scrollToBottom()

        CoroutineScope(Dispatchers.IO).launch {
            databaseHelper.addMessage(
                args.contactId,
                "📍 Localização compartilhada - Oficina Central",
                true,
                -23.5505,
                -46.6333
            )
        }
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