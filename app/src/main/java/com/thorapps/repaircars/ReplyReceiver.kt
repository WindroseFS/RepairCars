package com.thorapps.repaircars

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.thorapps.repaircars.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.getLongExtra("CONTACT_ID", -1)
        val contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato"

        val replyText = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence("key_text_reply")
            ?.toString()

        if (!replyText.isNullOrEmpty() && contactId != -1L) {
            // Use CoroutineScope to call suspend function
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dbHelper = DatabaseHelper(context)
                    dbHelper.addMessage(contactId, replyText, true)

                    // Show toast on main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Resposta enviada: $replyText", Toast.LENGTH_SHORT).show()
                    }

                    // Atualiza notificação com nova mensagem
                    NotificationHelper.showMessageNotification(context, contactId, contactName)

                    // Optional: Send broadcast to update UI if ChatActivity is open
                    val updateIntent = Intent("ACTION_MESSAGE_SENT")
                    updateIntent.putExtra("CONTACT_ID", contactId)
                    context.sendBroadcast(updateIntent)

                } catch (e: Exception) {
                    // Handle error - show error toast
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Erro ao enviar resposta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}