package com.thorapps.repaircars.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ChatService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Lógica do serviço de chat
        return START_STICKY
    }
}