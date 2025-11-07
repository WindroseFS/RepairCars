package com.thorapps.repaircars.chat

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.FragmentChatBinding
import com.thorapps.repaircars.database.DatabaseHelper
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private val viewModel: ChatViewModel by viewModels()

    // SAFE ARGS - Recebendo argumentos
    private val args: ChatFragmentArgs by navArgs()

    companion object {
        private const val CHANNEL_ID = "messages_channel"
        private const val TAG = "ChatFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        setupToolbar()
        setupRecyclerView()
        setupSendButton()
        setupLocationButton()
        createNotificationChannel()
        loadMessages()
        observeMessages()
    }

    private fun setupToolbar() {
        binding.toolbar.title = args.contactName
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeMessages() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            val adapter = MessagesAdapter()
            adapter.submitList(messages)
            binding.messagesRecyclerView.adapter = adapter
            if (messages.isNotEmpty()) {
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun refreshMessages() {
        lifecycleScope.launch {
            try {
                val messages = dbHelper.getMessagesForContact(args.contactId)
                viewModel.setMessages(messages)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar mensagens: ${e.message}")
                Toast.makeText(requireContext(), "Erro ao carregar mensagens", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isEmpty()) return@setOnClickListener

            lifecycleScope.launch {
                try {
                    val messagesRef = FirebaseDatabase.getInstance().getReference("messages/${args.contactId}")
                    val messageId = messagesRef.push().key ?: return@launch
                    val data = mapOf(
                        "text" to messageText,
                        "sender" to "me",
                        "timestamp" to System.currentTimeMillis()
                    )

                    messagesRef.child(messageId).setValue(data)
                    dbHelper.addMessage(args.contactId, messageText, true, null, null)

                    requireActivity().runOnUiThread {
                        binding.etMessage.text.clear()
                        refreshMessages()
                        showNotification()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao enviar mensagem: ${e.message}")
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupLocationButton() {
        binding.btnLocation.setOnClickListener {
            getLastKnownLocationAndSend()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocationAndSend() {
        if (!hasLocationPermission()) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                sendLocationMessage(location)
            } else {
                Toast.makeText(requireContext(), "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Erro de permissão de localização: ${e.message}")
            Toast.makeText(requireContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter localização: ${e.message}")
            Toast.makeText(requireContext(), "Erro ao obter localização", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
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
                    Toast.makeText(requireContext(), "Permissão de localização necessária", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendLocationMessage(location: Location) {
        val messageText = "Minha localização: https://maps.google.com/?q=${location.latitude},${location.longitude}"

        lifecycleScope.launch {
            try {
                val messagesRef = FirebaseDatabase.getInstance().getReference("messages/${args.contactId}")
                val messageId = messagesRef.push().key ?: return@launch
                val data = mapOf(
                    "text" to messageText,
                    "sender" to "me",
                    "timestamp" to System.currentTimeMillis(),
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                )

                messagesRef.child(messageId).setValue(data)
                dbHelper.addMessage(args.contactId, messageText, true, location.latitude, location.longitude)

                requireActivity().runOnUiThread {
                    refreshMessages()
                    showNotification()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao enviar localização: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Erro ao enviar localização", Toast.LENGTH_SHORT).show()
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
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        try {
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_send)
                .setContentTitle("Nova mensagem")
                .setContentText("Mensagem enviada com sucesso")
                .setAutoCancel(true)
                .build()

            notificationManager.notify(args.contactId.toInt(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao mostrar notificação: ${e.message}")
        }
    }

    private fun loadMessages() {
        refreshMessages()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}