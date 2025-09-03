package com.thorapps.repaircars

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat

object NotificationHelper {
    private const val CHANNEL_ID = "chat_channel"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mensagens",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showMessageNotification(context: Context, contactId: Long, contactName: String) {
        val db = DatabaseHelper(context)
        val messages = db.getMessagesForContact(contactId)

        val user = Person.Builder().setName("Você").build()
        val contact = Person.Builder().setName(contactName).build()

        val style = NotificationCompat.MessagingStyle(user).also { style ->
            style.setConversationTitle(contactName)
            messages.forEach {
                style.addMessage(it.text, System.currentTimeMillis(), if (it.isSent) user else contact)
            }
        }

        val openChatIntent = Intent(context, ChatActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("CONTACT_NAME", contactName)
        }
        val openChatPendingIntent = PendingIntent.getActivity(
            context, contactId.toInt(), openChatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val remoteInput = RemoteInput.Builder("key_text_reply")
            .setLabel("Digite sua resposta...")
            .build()

        val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("CONTACT_NAME", contactName)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            context, contactId.toInt(), replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_send, "Responder", replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(contactName)
            .setContentText(messages.lastOrNull()?.text ?: "")
            .setStyle(style)
            .setContentIntent(openChatPendingIntent)
            .setAutoCancel(true)
            .addAction(replyAction)
            .build()

        // Checagem de permissão no Android 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                NotificationManagerCompat.from(context).notify(contactId.toInt(), notification)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}
