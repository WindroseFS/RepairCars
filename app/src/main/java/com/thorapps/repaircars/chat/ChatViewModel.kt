package com.thorapps.repaircars.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thorapps.repaircars.database.models.Message

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    fun setMessages(messages: List<Message>) {
        _messages.value = messages
    }
}