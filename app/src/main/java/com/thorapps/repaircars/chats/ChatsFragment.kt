package com.thorapps.repaircars.chats

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.FragmentChatsBinding
import kotlinx.parcelize.Parcelize

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private val chatList = mutableListOf<Chat>()
    private lateinit var adapter: ChatsAdapter

    // Registrar para receber resultado do ChatActivity
    private val chatActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedChat = result.data?.getParcelableExtra<Chat>("UPDATED_CHAT")
            updatedChat?.let { chat ->
                // Atualizar chat existente ou adicionar novo
                val existingIndex = chatList.indexOfFirst { it.contactId == chat.contactId }
                if (existingIndex != -1) {
                    chatList[existingIndex] = chat
                    adapter.notifyItemChanged(existingIndex)
                } else {
                    chatList.add(0, chat) // Adicionar no topo
                    adapter.notifyItemInserted(0)
                }
                updateUI()
                saveChatsToSharedPrefs()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupLastChatCard()
        loadChatsFromSharedPrefs()
    }

    private fun setupRecyclerView() {
        adapter = ChatsAdapter(chatList) { chat ->
            openChat(chat)
        }
        binding.recycleViewChats.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewChats.adapter = adapter
    }

    private fun setupFab() {
        binding.fabNewChat.setOnClickListener {
            // Navegar para a lista de contatos
            findNavController().navigate(R.id.nav_contacts)
        }
    }

    private fun setupLastChatCard() {
        // Configurar clique no card da última conversa
        binding.cardLastChat?.setOnClickListener {
            getLastChat()?.let { lastChat ->
                openChat(lastChat)
            }
        }
    }

    private fun openChat(chat: Chat) {
        val intent = Intent(requireContext(), com.thorapps.repaircars.ChatActivity::class.java).apply {
            putExtra("CHAT_DATA", chat)
        }
        chatActivityLauncher.launch(intent)
    }

    private fun getLastChat(): Chat? {
        return chatList.firstOrNull()
    }

    private fun updateLastChatCard() {
        val lastChat = getLastChat()

        if (lastChat != null) {
            // Mostrar card da última conversa e esconder lista completa
            binding.cardLastChat?.visibility = View.VISIBLE
            binding.recycleViewChats.visibility = View.GONE
            binding.textEmptyState.visibility = View.GONE

            // Preencher dados da última conversa
            binding.textChatName?.text = lastChat.contactName
            binding.textLastMessage?.text = lastChat.lastMessage
            binding.textTimestamp?.text = formatTimestamp(lastChat.timestamp)

            // Mostrar contador de mensagens não lidas de forma simples
            if (lastChat.unreadCount > 0) {
                binding.textLastMessage?.text = "(${lastChat.unreadCount}) ${lastChat.lastMessage}"
            }
        } else {
            // Não há conversas, mostrar estado vazio
            binding.cardLastChat?.visibility = View.GONE
            binding.recycleViewChats.visibility = View.GONE
            binding.textEmptyState.visibility = View.VISIBLE
        }
    }

    private fun showAllChats() {
        // Mostrar lista completa de conversas
        binding.cardLastChat?.visibility = View.GONE
        binding.recycleViewChats.visibility = View.VISIBLE
        binding.textEmptyState.visibility = View.GONE
    }

    private fun updateUI() {
        if (chatList.isEmpty()) {
            // Não há conversas
            binding.cardLastChat?.visibility = View.GONE
            binding.recycleViewChats.visibility = View.GONE
            binding.textEmptyState.visibility = View.VISIBLE
        } else if (chatList.size == 1) {
            // Se há apenas 1 conversa, mostrar apenas o card da última conversa
            updateLastChatCard()
        } else {
            // Se há mais de 1 conversa, mostrar lista completa
            showAllChats()
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timestamp

        return when {
            diff < 60000 -> "Agora" // Menos de 1 minuto
            diff < 3600000 -> "${diff / 60000} min" // Menos de 1 hora
            diff < 86400000 -> "${diff / 3600000} h" // Menos de 1 dia
            else -> "${diff / 86400000} d" // Mais de 1 dia
        }
    }

    public fun addNewChat(contactName: String, contactPhone: String, contactId: String) {
        val newChat = Chat(
            contactId = contactId,
            contactName = contactName,
            contactPhone = contactPhone,
            lastMessage = "Conversa iniciada",
            timestamp = System.currentTimeMillis(),
            unreadCount = 0
        )

        // Verificar se já existe um chat com este contato
        val existingIndex = chatList.indexOfFirst { it.contactId == contactId }
        if (existingIndex != -1) {
            // Mover para o topo
            val existingChat = chatList.removeAt(existingIndex)
            chatList.add(0, existingChat)
            adapter.notifyItemMoved(existingIndex, 0)
        } else {
            // Adicionar novo chat no topo
            chatList.add(0, newChat)
            adapter.notifyItemInserted(0)
        }

        updateUI()
        saveChatsToSharedPrefs()
    }

    public fun updateChatLastMessage(contactId: String, lastMessage: String) {
        val existingIndex = chatList.indexOfFirst { it.contactId == contactId }
        if (existingIndex != -1) {
            chatList[existingIndex] = chatList[existingIndex].copy(
                lastMessage = lastMessage,
                timestamp = System.currentTimeMillis(),
                unreadCount = chatList[existingIndex].unreadCount + 1
            )

            // Mover para o topo
            if (existingIndex != 0) {
                val chat = chatList.removeAt(existingIndex)
                chatList.add(0, chat)
                adapter.notifyItemMoved(existingIndex, 0)
            } else {
                adapter.notifyItemChanged(0)
            }

            updateUI()
            saveChatsToSharedPrefs()
        }
    }

    private fun saveChatsToSharedPrefs() {
        try {
            val sharedPref = requireContext().getSharedPreferences("chats", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            // Converter lista para JSON (simplificado)
            val chatsJson = chatList.joinToString(";;") {
                "${it.contactId},${it.contactName},${it.contactPhone},${it.lastMessage},${it.timestamp},${it.unreadCount}"
            }
            editor.putString("chat_list", chatsJson)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadChatsFromSharedPrefs() {
        try {
            val sharedPref = requireContext().getSharedPreferences("chats", Context.MODE_PRIVATE)
            val chatsJson = sharedPref.getString("chat_list", "")

            if (!chatsJson.isNullOrEmpty()) {
                chatList.clear()
                chatsJson.split(";;").forEach { chatStr ->
                    val parts = chatStr.split(",")
                    if (parts.size >= 6) {
                        val chat = Chat(
                            contactId = parts[0],
                            contactName = parts[1],
                            contactPhone = parts[2],
                            lastMessage = parts[3],
                            timestamp = parts[4].toLongOrNull() ?: System.currentTimeMillis(),
                            unreadCount = parts[5].toIntOrNull() ?: 0
                        )
                        chatList.add(chat)
                    }
                }
                // Ordenar por timestamp (mais recente primeiro)
                chatList.sortByDescending { it.timestamp }
                adapter.notifyDataSetChanged()
            }
            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}