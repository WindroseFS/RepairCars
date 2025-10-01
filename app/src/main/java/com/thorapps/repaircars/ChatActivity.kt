package com.thorapps.repaircars

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thorapps.repaircars.databinding.ActivityChatBinding
import kotlinx.coroutines.launch
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.database.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var messagesAdapter: MessagesAdapter
    private var contactId: Long = -1L
    private var contactName: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    private val database = FirebaseDatabase.getInstance()
    private lateinit var messagesRef: DatabaseReference

    companion object {
        private const val CHANNEL_ID = "messages_channel"
        private const val NOTIFICATION_ID = 1001
    }

    // Registrar para resultado de permissão de localização
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLastKnownLocation()
        } else {
            Log.w("ChatActivity", "Permissão de localização negada pelo usuário")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        // Recupera dados do contato
        contactId = intent.getLongExtra("CONTACT_ID", -1L)
        if (contactId == -1L) { finish(); return }
        contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato Desconhecido"

        dbHelper = DatabaseHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        messagesRef = database.getReference("messages").child(contactId.toString())

        setupActionBar()
        setupRecyclerView()
        setupSendButton()

        // Cancelar notificação ao abrir o chat
        cancelNotification()
        requestLocationPermission()
        listenForMessages()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mensagens",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de mensagens"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = contactName
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            val messages = dbHelper.getMessagesForContact(contactId)
            messagesAdapter = MessagesAdapter(messages)
            binding.messagesRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                    stackFromEnd = true
                }
                adapter = messagesAdapter
                setHasFixedSize(true)
            }
            scrollToBottom()
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isEmpty()) return@setOnClickListener

            // Verificar se temos permissão de localização antes de obter a localização
            val lat = if (hasLocationPermission()) {
                lastKnownLocation?.latitude
            } else {
                null
            }

            val lng = if (hasLocationPermission()) {
                lastKnownLocation?.longitude
            } else {
                null
            }

            lifecycleScope.launch {
                // Firebase
                val messageId = messagesRef.push().key ?: return@launch
                val data = mapOf(
                    "text" to messageText,
                    "sender" to "me",
                    "timestamp" to System.currentTimeMillis(),
                    "latitude" to lat,
                    "longitude" to lng
                )
                messagesRef.child(messageId).setValue(data)

                // DBHelper
                dbHelper.addMessage(contactId, messageText, true, lat, lng)

                runOnUiThread {
                    binding.etMessage.text.clear()
                    refreshMessages()
                    showNotification()
                }
            }
        }
    }

    private fun showNotification() {
        try {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Nova mensagem")
                .setContentText("Para: $contactName")
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setAutoCancel(true)
                .build()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(contactId.toInt(), notification)
        } catch (e: SecurityException) {
            Log.e("ChatActivity", "Erro ao mostrar notificação: ${e.message}")
        } catch (e: Exception) {
            Log.e("ChatActivity", "Erro inesperado ao mostrar notificação: ${e.message}")
        }
    }

    private fun cancelNotification() {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(contactId.toInt())
        } catch (e: SecurityException) {
            Log.e("ChatActivity", "Erro ao cancelar notificação: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMessages()
        cancelNotification()

        // Verificar permissão antes de obter localização
        if (hasLocationPermission()) {
            getLastKnownLocation()
        }
    }

    private fun refreshMessages() {
        lifecycleScope.launch {
            try {
                val messages = withContext(Dispatchers.IO) {
                    dbHelper.getMessagesForContact(contactId)
                }
                messagesAdapter.updateMessages(messages)
                scrollToBottom()
            } catch (e: Exception) {
                Log.e("ChatActivity", "Error refreshing messages: ${e.message}")
            }
        }
    }

    private fun scrollToBottom() {
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

    // Verificar se a permissão de localização foi concedida
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (hasLocationPermission()) {
            getLastKnownLocation()
        } else {
            // Solicitar permissão apenas se não tiver sido concedida
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getLastKnownLocation() {
        // Verificar explicitamente a permissão antes de acessar a localização
        if (!hasLocationPermission()) {
            Log.w("ChatActivity", "Permissão de localização não concedida")
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lastKnownLocation = location
                    Log.d("ChatActivity", "Última localização: ${location.latitude}, ${location.longitude}")
                } else {
                    Log.d("ChatActivity", "Localização não disponível")
                }
            }.addOnFailureListener { exception ->
                Log.e("ChatActivity", "Erro ao obter localização: ${exception.message}")
            }
        } catch (e: SecurityException) {
            Log.e("ChatActivity", "Erro de segurança ao acessar localização: ${e.message}")
        }
    }

    private fun listenForMessages() {
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                snapshot.children.forEach { ds ->
                    val text = ds.child("text").getValue(String::class.java) ?: ""
                    val sender = ds.child("sender").getValue(String::class.java) ?: "unknown"
                    val timestamp = ds.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                    val lat = ds.child("latitude").getValue(Double::class.java)
                    val lng = ds.child("longitude").getValue(Double::class.java)

                    messages.add(Message(
                        text = text,
                        sender = sender,
                        timestamp = timestamp,
                        latitude = lat,
                        longitude = lng
                    ))
                }
                lifecycleScope.launch {
                    runOnUiThread {
                        messagesAdapter.updateMessages(messages)
                        scrollToBottom()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatActivity", "Erro ao ler mensagens: $error")
            }
        })
    }

    // Tratamento seguro para onRequestPermissionsResult (para versões mais antigas)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Para compatibilidade com versões mais antigas
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                getLastKnownLocation()
            }
        } else {
            Log.w("ChatActivity", "Permissão de localização negada pelo usuário")
        }
    }
}