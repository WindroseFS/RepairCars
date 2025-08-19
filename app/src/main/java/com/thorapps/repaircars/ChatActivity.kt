package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var messagesAdapter: MessagesAdapter
    private var contactId: Long = -1
    private var contactName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Criar canal de notificações (garante que exista)
        NotificationHelper.createChannel(this)

        // Inicialização segura com verificação de intent
        contactId = intent.getLongExtra("CONTACT_ID", -1).takeIf { it != -1L }
            ?: run {
                finish() // Encerra se não houver ID válido
                return
            }

        contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato Desconhecido"

        dbHelper = DatabaseHelper(this)

        setupActionBar()
        setupRecyclerView()
        setupSendButton()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = contactName
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(dbHelper.getMessagesForContact(contactId))
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true // Melhora a rolagem automática
            }
            adapter = messagesAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                // Adiciona a mensagem no banco
                dbHelper.addMessage(contactId, message, true)

                // Limpa o campo de texto
                binding.etMessage.text.clear()

                // Atualiza RecyclerView
                refreshMessages()

                // Exibe a notificação interativa
                NotificationHelper.showMessageNotification(this, contactId, contactName)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMessages()
    }

    private fun refreshMessages() {
        val messages = dbHelper.getMessagesForContact(contactId)
        messagesAdapter.updateMessages(messages)

        // Rolagem suave para o final
        binding.messagesRecyclerView.postDelayed({
            binding.messagesRecyclerView.smoothScrollToPosition(messagesAdapter.itemCount - 1)
        }, 100)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
