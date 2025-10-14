package com.thorapps.repaircars.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "repaircars.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_CONTACTS = "contacts"
        const val TABLE_MESSAGES = "messages"

        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(
                "CREATE TABLE $TABLE_CONTACTS(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "email TEXT)"
            )
            db.execSQL(
                "CREATE TABLE $TABLE_MESSAGES(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "contactId INTEGER," +
                        "text TEXT," +
                        "sender TEXT," +
                        "timestamp INTEGER," +
                        "latitude REAL," +
                        "longitude REAL)"
            )
            Log.d(TAG, "Banco de dados criado com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar banco de dados: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE $TABLE_MESSAGES ADD COLUMN latitude REAL")
                db.execSQL("ALTER TABLE $TABLE_MESSAGES ADD COLUMN longitude REAL")
                Log.d(TAG, "Banco de dados atualizado da versão $oldVersion para $newVersion")
            } else {
                db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
                db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
                onCreate(db)
                Log.d(TAG, "Banco de dados recriado na versão $newVersion")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar banco de dados: ${e.message}")
        }
    }

    suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        try {
            readableDatabase.rawQuery("SELECT id, name, email FROM $TABLE_CONTACTS", null).use { cursor ->
                while (cursor.moveToNext()) {
                    contacts.add(Contact(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter contatos: ${e.message}")
        }
        contacts
    }

    suspend fun getContactsWithLastMessage(): List<ContactDisplay> = withContext(Dispatchers.IO) {
        val list = mutableListOf<ContactDisplay>()
        try {
            readableDatabase.rawQuery(
                """
                SELECT c.id, c.name, c.email, m.text
                FROM $TABLE_CONTACTS c
                LEFT JOIN $TABLE_MESSAGES m ON m.id = (
                    SELECT id FROM $TABLE_MESSAGES 
                    WHERE contactId = c.id
                    ORDER BY timestamp DESC LIMIT 1
                )
                ORDER BY m.timestamp DESC
                """.trimIndent(), null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(0)
                    val name = cursor.getString(1)
                    val email = cursor.getString(2)
                    val lastMessage = if (cursor.isNull(3)) "Sem mensagens" else cursor.getString(3)
                    list.add(ContactDisplay(id, name, email, lastMessage))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter contatos com última mensagem: ${e.message}")
        }
        list
    }

    suspend fun getMessagesForContact(contactId: Long): List<Message> =
        withContext(Dispatchers.IO) {
            val messages = mutableListOf<Message>()
            try {
                readableDatabase.rawQuery(
                    "SELECT id, contactId, text, sender, timestamp, latitude, longitude FROM $TABLE_MESSAGES WHERE contactId = ? ORDER BY timestamp ASC",
                    arrayOf(contactId.toString())
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        messages.add(
                            Message(
                                id = cursor.getLong(0),
                                contactId = cursor.getLong(1),
                                text = cursor.getString(2),
                                sender = cursor.getString(3),
                                timestamp = cursor.getLong(4),
                                latitude = if (!cursor.isNull(5)) cursor.getDouble(5) else null,
                                longitude = if (!cursor.isNull(6)) cursor.getDouble(6) else null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter mensagens para contato $contactId: ${e.message}")
            }
            messages
        }

    suspend fun addMessage(contactId: Long, text: String, isSentByMe: Boolean, lat: Double? = null, lng: Double? = null): Long =
        withContext(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put("contactId", contactId)
                    put("text", text)
                    put("sender", if (isSentByMe) "me" else "other")
                    put("timestamp", System.currentTimeMillis())
                    if (lat != null) put("latitude", lat)
                    if (lng != null) put("longitude", lng)
                }
                val result = writableDatabase.insert(TABLE_MESSAGES, null, values)
                if (result == -1L) {
                    Log.e(TAG, "Erro ao inserir mensagem no banco")
                }
                result
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao adicionar mensagem: ${e.message}")
                -1L
            }
        }

    suspend fun addContact(name: String, email: String): Long = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put("name", name)
                put("email", email)
            }
            val result = writableDatabase.insert(TABLE_CONTACTS, null, values)
            if (result == -1L) {
                Log.e(TAG, "Erro ao inserir contato no banco")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar contato: ${e.message}")
            -1L
        }
    }

    suspend fun addSampleMessages(contactId: Long) = withContext(Dispatchers.IO) {
        try {
            val sampleMessages = listOf(
                MessageData(contactId, "Olá, preciso fazer uma revisão no meu carro", false),
                MessageData(contactId, "É um Honda Civic 2020", false),
                MessageData(contactId, "Estou sentindo uns barulhos estranhos na suspensão", false),
                MessageData(contactId, "E também o freio está meio mole", false),
                MessageData(contactId, "Bom dia! Pode trazer o veículo para avaliação?", true),
                MessageData(contactId, "Vamos fazer uma inspeção completa na suspensão e no sistema de freios", true),
                MessageData(contactId, "Você sente os barulhos mais em ruas esburacadas ou em qualquer tipo de piso?", true),
                MessageData(contactId, "Principalmente em ruas esburacadas e quando passo em lombadas", false),
                MessageData(contactId, "O barulho parece vir da frente do carro", false),
                MessageData(contactId, "Provavelmente são as buchas da suspensão ou os amortecedores", true),
                MessageData(contactId, "Vou verificar também os coxins e as bandejas", true),
                MessageData(contactId, "Em relação aos freios, quando foi a última vez que trocou as pastilhas?", true),
                MessageData(contactId, "Faz uns 20.000 km que não troco as pastilhas", false),
                MessageData(contactId, "Devo ter rodado uns 35.000 km com as pastilhas atuais", false),
                MessageData(contactId, "Já está na hora da troca então! A vida útil média é de 30.000 km", true),
                MessageData(contactId, "Vou verificar também os discos de freio e o fluido", true),
                MessageData(contactId, "Acabei de fazer a inspeção no seu Civic", true),
                MessageData(contactId, "✅ Amortecedores dianteiros: Necessitam substituição", true),
                MessageData(contactId, "✅ Buchas da bandeja: Desgastadas - precisa trocar", true),
                MessageData(contactId, "✅ Pastilhas de freio: Lascadas - troca urgente", true),
                MessageData(contactId, "✅ Discos de freio: Estão em bom estado", true),
                MessageData(contactId, "✅ Fluido de freio: Precisa ser substituído", true),
                MessageData(contactId, "Nossa, então precisa trocar bastante coisa né?", false),
                MessageData(contactId, "Quanto vai ficar mais ou menos o orçamento?", false),
                MessageData(contactId, "Vou preparar um orçamento detalhado para você", true),
                MessageData(contactId, "📋 ORÇAMENTO ESTIMADO:", true),
                MessageData(contactId, "• Par de amortecedores dianteiros: R$ 450,00", true),
                MessageData(contactId, "• Jogo de buchas da suspensão: R$ 180,00", true),
                MessageData(contactId, "• Pastilhas de freio dianteiras: R$ 120,00", true),
                MessageData(contactId, "• Fluido de freio: R$ 40,00", true),
                MessageData(contactId, "• Mão de obra: R$ 200,00", true),
                MessageData(contactId, "💰 TOTAL ESTIMADO: R$ 990,00", true),
                MessageData(contactId, "Entendi, e quanto tempo vai levar o serviço?", false),
                MessageData(contactId, "Precisamos de 1 dia útil para concluir todos os serviços", true),
                MessageData(contactId, "Pode deixar o carro pela manhã que entrego no final da tarde", true),
                MessageData(contactId, "Perfeito! Posso levar amanhã às 8h?", false),
                MessageData(contactId, "Pode sim! Temos vaga disponível", true),
                MessageData(contactId, "Só confirmando: Honda Civic 2020, placa ABC-1234, certo?", true),
                MessageData(contactId, "Isso mesmo! Placa ABC-1234", false),
                MessageData(contactId, "Vou estar lá às 8h então. Obrigado!", false),
                MessageData(contactId, "Combinado! Até amanhã 👍", true)
            )

            sampleMessages.forEach { messageData ->
                val values = ContentValues().apply {
                    put("contactId", messageData.contactId)
                    put("text", messageData.text)
                    put("sender", if (messageData.isFromMe) "me" else "other")
                    put("timestamp", System.currentTimeMillis() - (Math.random() * 86400000 * 7).toLong())
                }
                writableDatabase.insert(TABLE_MESSAGES, null, values)
            }

            Log.d(TAG, "Mensagens de exemplo adicionadas para contato $contactId")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar mensagens de exemplo: ${e.message}")
        }
    }
}

private data class MessageData(
    val contactId: Long,
    val text: String,
    val isFromMe: Boolean
)