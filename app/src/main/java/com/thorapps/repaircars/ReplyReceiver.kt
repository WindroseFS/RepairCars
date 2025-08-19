package com.thorapps.repaircars

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput

class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.getLongExtra("CONTACT_ID", -1)
        val contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato"

        val replyText = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence("key_text_reply")
            ?.toString()

        if (!replyText.isNullOrEmpty() && contactId != -1L) {
            val dbHelper = DatabaseHelper(context)
            dbHelper.addMessage(contactId, replyText, true)

            Toast.makeText(context, "Resposta enviada: $replyText", Toast.LENGTH_SHORT).show()

            // Atualiza notificação com nova mensagem
            NotificationHelper.showMessageNotification(context, contactId, contactName)
        }
    }
}
