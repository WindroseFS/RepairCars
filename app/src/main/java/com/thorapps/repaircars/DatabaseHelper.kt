package com.thorapps.repaircars

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "RepairCarsDB"
        private const val DATABASE_VERSION = 1

        // Tabela de contatos
        const val TABLE_CONTACTS = "contacts"
        const val CONTACT_ID = "id"
        const val CONTACT_NAME = "name"
        const val CONTACT_LAST_MESSAGE = "last_message"
        const val CONTACT_TIMESTAMP = "timestamp"

        // Tabela de mensagens
        const val TABLE_MESSAGES = "messages"
        const val MESSAGE_ID = "id"
        const val MESSAGE_CONTACT_ID = "contact_id"
        const val MESSAGE_TEXT = "message"
        const val MESSAGE_SENT = "is_sent" // 1 para enviada, 0 para recebida
        const val MESSAGE_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Criar tabela de contatos
        val createContactsTable = """
            CREATE TABLE $TABLE_CONTACTS (
                $CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CONTACT_NAME TEXT NOT NULL,
                $CONTACT_LAST_MESSAGE TEXT,
                $CONTACT_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Criar tabela de mensagens (corrigido o parêntese faltante)
        val createMessagesTable = """
            CREATE TABLE $TABLE_MESSAGES (
                $MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $MESSAGE_CONTACT_ID INTEGER NOT NULL,
                $MESSAGE_TEXT TEXT NOT NULL,
                $MESSAGE_SENT INTEGER DEFAULT 1,
                $MESSAGE_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($MESSAGE_CONTACT_ID) REFERENCES $TABLE_CONTACTS($CONTACT_ID)
            )
        """.trimIndent()

        db.execSQL(createContactsTable)
        db.execSQL(createMessagesTable)

        // Inserir alguns contatos de exemplo
        insertSampleContacts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    private fun insertSampleContacts(db: SQLiteDatabase) {
        val contacts = listOf(
            "Thor Motors" to "Olá, como vai seu carro?",
            "AutoCenter Silva" to "Seu orçamento está pronto",
            "Mecânica Rápida" to "Traga seu veículo para revisão"
        )

        contacts.forEach { (name, message) ->
            val values = ContentValues().apply {
                put(CONTACT_NAME, name)
                put(CONTACT_LAST_MESSAGE, message)
            }
            val contactId = db.insert(TABLE_CONTACTS, null, values)

            // Adiciona mensagem inicial para cada contato
            if (contactId != -1L) {
                val messageValues = ContentValues().apply {
                    put(MESSAGE_CONTACT_ID, contactId)
                    put(MESSAGE_TEXT, message)
                    put(MESSAGE_SENT, 0) // Mensagem recebida
                }
                db.insert(TABLE_MESSAGES, null, messageValues)
            }
        }
    }

    // Métodos para contatos
    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CONTACTS,
            arrayOf(CONTACT_ID, CONTACT_NAME, CONTACT_LAST_MESSAGE, CONTACT_TIMESTAMP),
            null, null, null, null,
            "$CONTACT_TIMESTAMP DESC"
        )

        while (cursor.moveToNext()) {
            contacts.add(Contact(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
            )) // Fechamento de parêntese corrigido
        }
        cursor.close()
        return contacts
    }

    // Métodos para mensagens
    fun getMessagesForContact(contactId: Long): List<Message> {
        val messages = mutableListOf<Message>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MESSAGES,
            arrayOf(MESSAGE_ID, MESSAGE_TEXT, MESSAGE_SENT, MESSAGE_TIMESTAMP),
            "$MESSAGE_CONTACT_ID = ?",
            arrayOf(contactId.toString()),
            null, null,
            "$MESSAGE_TIMESTAMP ASC"
        )

        while (cursor.moveToNext()) {
            messages.add(Message(
                cursor.getLong(0),
                contactId, // Já temos esse valor
                cursor.getString(1),
                cursor.getInt(2) == 1,
                cursor.getString(3)
            ))
        }
        cursor.close()
        return messages
    }

    private fun getAutoReply(message: String): String? {
        return when {
            message.contains("orcamento", ignoreCase = true) ->
                "Podemos agendar uma avaliação para orçamento. Qual o modelo e ano do seu veículo?"

            message.contains("revisão", ignoreCase = true) ->
                "Oferecemos pacotes de revisão a partir de R$ 250. Gostaria de agendar?"

            message.contains("barulho", ignoreCase = true) ||
                    message.contains("ruído", ignoreCase = true) ->
                "Barulhos podem indicar vários problemas. Poderia descrever o tipo de barulho e quando ocorre?"

            message.contains("óleo", ignoreCase = true) ->
                "Recomendamos troca de óleo a cada 10.000 km ou 6 meses. Quando foi sua última troca?"

            message.contains("pneu", ignoreCase = true) ->
                "Temos promoção de alinhamento e balanceamento por R$ 120. Precisa de serviço nos pneus?"

            message.contains("freio", ignoreCase = true) ->
                "Sistema de freios deve ser verificado a cada 15.000 km. Nota algum ruído ou vibração?"

            message.contains("bateria", ignoreCase = true) ->
                "Baterias têm vida útil de 2-3 anos. Está com dificuldade para dar partida?"

            message.contains("agendar", ignoreCase = true) ||
                    message.contains("marcar", ignoreCase = true) ->
                "Temos horários disponíveis nesta semana. Qual dia e horário prefere?"

            message.contains("preço", ignoreCase = true) ||
                    message.contains("custo", ignoreCase = true) ->
                "Os valores variam conforme o serviço. Poderia especificar qual serviço precisa?"

            else -> null
        }
    }

    // Versão única da função addMessage (com respostas automáticas)
    fun addMessage(contactId: Long, message: String, isSent: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(MESSAGE_CONTACT_ID, contactId)
            put(MESSAGE_TEXT, message)
            put(MESSAGE_SENT, if (isSent) 1 else 0)
        }
        db.insert(TABLE_MESSAGES, null, values)

        // Se a mensagem foi enviada pelo usuário, gerar resposta automática
        if (isSent) {
            getAutoReply(message)?.let { reply ->
                val replyValues = ContentValues().apply {
                    put(MESSAGE_CONTACT_ID, contactId)
                    put(MESSAGE_TEXT, reply)
                    put(MESSAGE_SENT, 0) // 0 para mensagem recebida
                }
                db.insert(TABLE_MESSAGES, null, replyValues)

                // Atualiza a última mensagem no contato com a resposta
                val updateValues = ContentValues().apply {
                    put(CONTACT_LAST_MESSAGE, reply)
                    put(CONTACT_TIMESTAMP, System.currentTimeMillis())
                }
                db.update(
                    TABLE_CONTACTS,
                    updateValues,
                    "$CONTACT_ID = ?",
                    arrayOf(contactId.toString())
                )
                return
            }
        }

        // Atualiza a última mensagem no contato
        val updateValues = ContentValues().apply {
            put(CONTACT_LAST_MESSAGE, message)
            put(CONTACT_TIMESTAMP, System.currentTimeMillis())
        }
        db.update(
            TABLE_CONTACTS,
            updateValues,
            "$CONTACT_ID = ?",
            arrayOf(contactId.toString())
        )
    }

    data class Contact(
        val id: Long,
        val name: String,
        val lastMessage: String?,
        val timestamp: String
    )

    data class Message(
        val id: Long,
        val contactId: Long, // Adicione esta linha
        val text: String,
        val isSent: Boolean,
        val timestamp: String
    )
}