package com.thorapps.repaircars

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thorapps.repaircars.databinding.ActivityChatBinding
import com.thorapps.repaircars.database.DatabaseHelper
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var messagesRef: DatabaseReference
    private var contactId: Long = 0

    companion object {
        private const val CHANNEL_ID = "messages_channel"
        private const val TAG = "ChatActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        contactId = intent.getLongExtra("CONTACT_ID", 0)
        val contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato"

        supportActionBar?.title = contactName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database = FirebaseDatabase.getInstance()
        messagesRef = database.getReference("messages/$contactId")

        setupRecyclerView()
        setupSendButton()
        setupLocationButton()
        createNotificationChannel()
        setupBackPressedHandler()
        loadMessages()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        refreshMessages()
    }

    private fun refreshMessages() {
        lifecycleScope.launch {
            try {
                val messages = dbHelper.getMessagesForContact(contactId)
                val adapter = MessagesAdapter()
                adapter.submitList(messages)
                binding.messagesRecyclerView.adapter = adapter
                if (messages.isNotEmpty()) {
                    binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar mensagens: ${e.message}")
                Toast.makeText(this@ChatActivity, "Erro ao carregar mensagens", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isEmpty()) return@setOnClickListener

            lifecycleScope.launch {
                try {
                    val messageId = messagesRef.push().key ?: return@launch
                    val data = mapOf(
                        "text" to messageText,
                        "sender" to "me",
                        "timestamp" to System.currentTimeMillis()
                    )

                    messagesRef.child(messageId).setValue(data)
                    dbHelper.addMessage(contactId, messageText, true, null, null)

                    runOnUiThread {
                        binding.etMessage.text.clear()
                        refreshMessages()
                        showNotification()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao enviar mensagem: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(this@ChatActivity, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupLocationButton() {
        binding.btnLocation?.setOnClickListener {
            getLastKnownLocationAndSend()
        } ?: run {
            Log.d(TAG, "Botão de localização não encontrado no layout")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocationAndSend() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                sendLocationMessage(location)
            } else {
                Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Erro de permissão de localização: ${e.message}")
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter localização: ${e.message}")
            Toast.makeText(this, "Erro ao obter localização", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastKnownLocationAndSend()
                } else {
                    Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendLocationMessage(location: Location) {
        val messageText = "Minha localização: https://maps.google.com/?q=${location.latitude},${location.longitude}"

        lifecycleScope.launch {
            try {
                val messageId = messagesRef.push().key ?: return@launch
                val data = mapOf(
                    "text" to messageText,
                    "sender" to "me",
                    "timestamp" to System.currentTimeMillis(),
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                )

                messagesRef.child(messageId).setValue(data)
                dbHelper.addMessage(contactId, messageText, true, location.latitude, location.longitude)

                runOnUiThread {
                    refreshMessages()
                    showNotification()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao enviar localização: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@ChatActivity, "Erro ao enviar localização", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mensagens",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações de novas mensagens"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Nova mensagem")
                .setContentText("Mensagem enviada com sucesso")
                .setAutoCancel(true)
                .build()

            notificationManager.notify(contactId.toInt(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao mostrar notificação: ${e.message}")
        }
    }

    private fun loadMessages() {
        refreshMessages()
    }
}