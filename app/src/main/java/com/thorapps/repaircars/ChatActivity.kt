package com.thorapps.repaircars

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.thorapps.repaircars.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var messagesAdapter: MessagesAdapter
    private var contactId: Long = -1
    private var contactName: String = ""

    // Localização
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    // Firebase
    private val database = FirebaseDatabase.getInstance()
    private lateinit var messagesRef: DatabaseReference

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cria canal de notificações
        NotificationHelper.createChannel(this)

        // Recupera dados do contato
        contactId = intent.getLongExtra("CONTACT_ID", -1).takeIf { it != -1L }
            ?: run { finish(); return }
        contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato Desconhecido"

        dbHelper = DatabaseHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        messagesRef = database.getReference("messages").child(contactId.toString())

        setupActionBar()
        setupRecyclerView()
        setupSendButton()

        // Cancela notificações existentes do contato
        NotificationManagerCompat.from(this).cancel(contactId.toInt())

        // Pede permissão de localização
        requestLocationPermission()

        // Começa a escutar mensagens em tempo real
        listenForMessages()
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
                stackFromEnd = true
            }
            adapter = messagesAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {

                val lat = lastKnownLocation?.latitude
                val lng = lastKnownLocation?.longitude

                // Cria ID único para a mensagem
                val messageId = messagesRef.push().key ?: return@setOnClickListener
                val data = mapOf(
                    "text" to messageText,
                    "sender" to "me",
                    "timestamp" to System.currentTimeMillis(),
                    "latitude" to lat,
                    "longitude" to lng
                )

                // Salva no Firebase
                messagesRef.child(messageId).setValue(data)

                // Salva localmente (opcional)
                dbHelper.addMessage(contactId, "$messageText\n📍 [$lat, $lng]", true)

                // Limpa campo de texto
                binding.etMessage.text.clear()

                // Atualiza RecyclerView
                refreshMessages()

                // Notificação
                NotificationHelper.showMessageNotification(this, contactId, contactName)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMessages()
        NotificationManagerCompat.from(this).cancel(contactId.toInt())
        getLastKnownLocation()
    }

    private fun refreshMessages() {
        val messages = dbHelper.getMessagesForContact(contactId)
        messagesAdapter.updateMessages(messages)

        if (messagesAdapter.itemCount > 0) {
            binding.messagesRecyclerView.postDelayed({
                binding.messagesRecyclerView.smoothScrollToPosition(messagesAdapter.itemCount - 1)
            }, 100)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // ==============================
    // LOCALIZAÇÃO
    // ==============================

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLocation = location
                Log.d("ChatActivity", "Última localização: ${location.latitude}, ${location.longitude}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLastKnownLocation()
        }
    }

    // ==============================
    // FIREBASE - RECEBENDO MENSAGENS
    // ==============================

    private fun listenForMessages() {
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                snapshot.children.forEach { ds ->
                    val text = ds.child("text").getValue(String::class.java) ?: ""
                    val sender = ds.child("sender").getValue(String::class.java) ?: "unknown"
                    val lat = ds.child("latitude").getValue(Double::class.java)
                    val lng = ds.child("longitude").getValue(Double::class.java)
                    messages.add(Message(text, sender, lat, lng))
                }

                // Atualiza adapter
                messagesAdapter.updateMessages(messages)
                if (messagesAdapter.itemCount > 0) {
                    binding.messagesRecyclerView.postDelayed({
                        binding.messagesRecyclerView.smoothScrollToPosition(messagesAdapter.itemCount - 1)
                    }, 100)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatActivity", "Erro ao ler mensagens: $error")
            }
        })
    }
}
