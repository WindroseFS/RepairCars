package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            MaterialTheme {
                ChatScreen()
            }
        }
    }
}

data class Message(
    val sender: String,
    val content: String
)

@Composable
fun ChatScreen() {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Digite sua mensagem") }
            )
            Button(onClick = {
                if (messageText.isNotBlank()) {
                    messages.add(Message("Você", messageText))
                    messages.add(Message("Bot", "Você disse: $messageText"))
                    messageText = ""
                }
            }) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    Column {
        Text(text = "${message.sender}:", style = MaterialTheme.typography.labelSmall)
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}