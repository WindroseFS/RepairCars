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
        val contactId = intent.getStringExtra("CONTACT_ID") ?: ""
        val contactName = intent.getStringExtra("CONTACT_NAME") ?: "Contato"

        val replyText = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence("key_text_reply")
            ?.toString()

        if (!replyText.isNullOrEmpty() && contactId.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dbHelper = DatabaseHelper(context)
                    dbHelper.addMessage(contactId, replyText, true)

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Resposta enviada: $replyText", Toast.LENGTH_SHORT).show()
                    }

                    val updateIntent = Intent("ACTION_MESSAGE_SENT")
                    updateIntent.putExtra("CONTACT_ID", contactId)
                    context.sendBroadcast(updateIntent)

                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Erro ao enviar resposta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}