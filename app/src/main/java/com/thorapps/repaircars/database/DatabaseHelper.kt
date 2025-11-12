package com.thorapps.repaircars.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.thorapps.repaircars.contacts.Contact

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "repaircars.db"
        private const val DATABASE_VERSION = 4

        const val TABLE_CONTACTS = "contacts"
        const val TABLE_MESSAGES = "messages"
        const val TABLE_MESSAGE_OPTIONS = "message_options"

        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(
                "CREATE TABLE $TABLE_CONTACTS(" +
                        "id TEXT PRIMARY KEY," +
                        "name TEXT," +
                        "phone TEXT," +
                        "email TEXT)"
            )

            db.execSQL(
                "CREATE TABLE $TABLE_MESSAGES(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "contactId TEXT," +
                        "text TEXT," +
                        "isSentByMe INTEGER," +
                        "timestamp INTEGER," +
                        "latitude REAL," +
                        "longitude REAL," +
                        "has_options INTEGER DEFAULT 0)"
            )

            db.execSQL(
                "CREATE TABLE $TABLE_MESSAGE_OPTIONS(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "messageId INTEGER," +
                        "option_text TEXT," +
                        "FOREIGN KEY(messageId) REFERENCES $TABLE_MESSAGES(id) ON DELETE CASCADE)"
            )

            Log.d(TAG, "Banco de dados criado com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar banco de dados: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            var currentVersion = oldVersion

            while (currentVersion < newVersion) {
                when (currentVersion) {
                    1, 2, 3 -> {
                        try {
                            db.execSQL("CREATE TABLE contacts_new (id TEXT PRIMARY KEY, name TEXT, phone TEXT, email TEXT)")
                            db.execSQL("CREATE TABLE messages_new (id INTEGER PRIMARY KEY AUTOINCREMENT, contactId TEXT, text TEXT, isSentByMe INTEGER, timestamp INTEGER, latitude REAL, longitude REAL, has_options INTEGER DEFAULT 0)")

                            val contactsCursor = db.rawQuery("SELECT id, name, phone, email FROM $TABLE_CONTACTS", null)
                            contactsCursor.use { cursor ->
                                while (cursor.moveToNext()) {
                                    val oldId = cursor.getLong(0)
                                    val name = cursor.getString(1)
                                    val phone = if (!cursor.isNull(2)) cursor.getString(2) else null
                                    val email = if (!cursor.isNull(3)) cursor.getString(3) else null

                                    val newId = oldId.toString()
                                    val values = ContentValues().apply {
                                        put("id", newId)
                                        put("name", name)
                                        put("phone", phone)
                                        put("email", email)
                                    }
                                    db.insert("contacts_new", null, values)
                                }
                            }

                            val messagesCursor = db.rawQuery("SELECT id, contactId, text, isSentByMe, timestamp, latitude, longitude, has_options FROM $TABLE_MESSAGES", null)
                            messagesCursor.use { cursor ->
                                while (cursor.moveToNext()) {
                                    val id = cursor.getLong(0)
                                    val oldContactId = cursor.getLong(1)
                                    val text = cursor.getString(2)
                                    val isSentByMe = cursor.getInt(3)
                                    val timestamp = cursor.getLong(4)
                                    val latitude = if (!cursor.isNull(5)) cursor.getDouble(5) else null
                                    val longitude = if (!cursor.isNull(6)) cursor.getDouble(6) else null
                                    val hasOptions = cursor.getInt(7)

                                    val newContactId = oldContactId.toString()
                                    val values = ContentValues().apply {
                                        put("id", id)
                                        put("contactId", newContactId)
                                        put("text", text)
                                        put("isSentByMe", isSentByMe)
                                        put("timestamp", timestamp)
                                        if (latitude != null) put("latitude", latitude)
                                        if (longitude != null) put("longitude", longitude)
                                        put("has_options", hasOptions)
                                    }
                                    db.insert("messages_new", null, values)
                                }
                            }

                            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGE_OPTIONS")
                            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
                            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")

                            db.execSQL("ALTER TABLE contacts_new RENAME TO $TABLE_CONTACTS")
                            db.execSQL("ALTER TABLE messages_new RENAME TO $TABLE_MESSAGES")

                            db.execSQL(
                                "CREATE TABLE $TABLE_MESSAGE_OPTIONS(" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "messageId INTEGER," +
                                        "option_text TEXT," +
                                        "FOREIGN KEY(messageId) REFERENCES $TABLE_MESSAGES(id) ON DELETE CASCADE)"
                            )

                            Log.d(TAG, "Migração para String IDs concluída com sucesso")
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro na migração: ${e.message}")
                            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
                            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
                            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGE_OPTIONS")
                            onCreate(db)
                        }
                        currentVersion++
                    }
                    else -> {
                        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
                        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
                        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGE_OPTIONS")
                        onCreate(db)
                        Log.d(TAG, "Banco de dados recriado na versão $newVersion")
                        currentVersion = newVersion
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar banco de dados: ${e.message}")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGE_OPTIONS")
            onCreate(db)
        }
    }

    suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        try {
            readableDatabase.rawQuery("SELECT id, name, phone, email FROM $TABLE_CONTACTS", null).use { cursor ->
                while (cursor.moveToNext()) {
                    contacts.add(Contact(
                        id = cursor.getString(0),
                        name = cursor.getString(1),
                        phone = if (!cursor.isNull(2)) cursor.getString(2) else null,
                        email = if (!cursor.isNull(3)) cursor.getString(3) else null
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
                SELECT c.id, c.name, c.email, c.phone, 
                       (SELECT text FROM $TABLE_MESSAGES 
                        WHERE contactId = c.id 
                        ORDER BY timestamp DESC LIMIT 1) as last_message,
                       (SELECT timestamp FROM $TABLE_MESSAGES 
                        WHERE contactId = c.id 
                        ORDER BY timestamp DESC LIMIT 1) as last_timestamp
                FROM $TABLE_CONTACTS c
                WHERE EXISTS (SELECT 1 FROM $TABLE_MESSAGES m WHERE m.contactId = c.id)
                ORDER BY last_timestamp DESC
                """.trimIndent(), null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val name = cursor.getString(1)
                    val email = cursor.getString(2)
                    val phone = if (!cursor.isNull(3)) cursor.getString(3) else ""
                    val lastMessage = if (cursor.isNull(4)) "Sem mensagens" else cursor.getString(4)

                    val contact = Contact(id = id, name = name, email = email, phone = phone, lastMessage = lastMessage)
                    list.add(ContactDisplay(contact, lastMessage))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter contatos com última mensagem: ${e.message}")
        }
        list
    }

    suspend fun getMessagesForContact(contactId: String): List<Message> =
        withContext(Dispatchers.IO) {
            val messages = mutableListOf<Message>()
            try {
                readableDatabase.rawQuery(
                    "SELECT id, contactId, text, isSentByMe, timestamp, latitude, longitude FROM $TABLE_MESSAGES WHERE contactId = ? ORDER BY timestamp ASC",
                    arrayOf(contactId)
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        messages.add(
                            Message(
                                id = cursor.getLong(0),
                                contactId = cursor.getString(1),
                                text = cursor.getString(2),
                                isSentByMe = cursor.getInt(3) == 1,
                                timestamp = cursor.getLong(4),
                                latitude = if (!cursor.isNull(5)) cursor.getDouble(5) else null,
                                longitude = if (!cursor.isNull(6)) cursor.getDouble(6) else null
                            )
                        )
                    }
                }
                Log.d(TAG, "Carregadas ${messages.size} mensagens para contato $contactId")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter mensagens para contato $contactId: ${e.message}")
            }
            messages
        }

    suspend fun addMessage(contactId: String, text: String, isSentByMe: Boolean, lat: Double? = null, lng: Double? = null): Long =
        withContext(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put("contactId", contactId)
                    put("text", text)
                    put("isSentByMe", if (isSentByMe) 1 else 0)
                    put("timestamp", System.currentTimeMillis())
                    if (lat != null) put("latitude", lat)
                    if (lng != null) put("longitude", lng)
                }
                val result = writableDatabase.insert(TABLE_MESSAGES, null, values)
                if (result == -1L) {
                    Log.e(TAG, "Erro ao inserir mensagem no banco")
                } else {
                    Log.d(TAG, "Mensagem inserida com sucesso para contato $contactId")
                }
                result
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao adicionar mensagem: ${e.message}")
                -1L
            }
        }

    suspend fun contactExists(contactId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            readableDatabase.rawQuery(
                "SELECT 1 FROM $TABLE_CONTACTS WHERE id = ?",
                arrayOf(contactId)
            ).use { cursor ->
                return@use cursor.moveToFirst()
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addContact(id: String, name: String, phone: String?, email: String?): Long = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put("id", id)
                put("name", name)
                put("phone", phone)
                put("email", email)
            }
            val result = writableDatabase.insert(TABLE_CONTACTS, null, values)
            if (result == -1L) {
                Log.e(TAG, "Erro ao inserir contato no banco")
            } else {
                Log.d(TAG, "Contato $name inserido com sucesso")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar contato: ${e.message}")
            -1L
        }
    }

    fun generateContactId(): String {
        return "contact_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    suspend fun initializeSampleData() = withContext(Dispatchers.IO) {
        try {
            // Verifica se já existem contatos para não duplicar
            val existingContacts = getAllContacts()
            if (existingContacts.isEmpty()) {
                Log.d(TAG, "Inicializando dados de exemplo...")

                val sampleContacts = listOf(
                    ContactData("1", "Oficina Central", "(21) 99999-1111", "oficina@repaircars.com"),
                    ContactData("2", "Suporte Técnico", "(21) 98888-2222", "suporte@repaircars.com"),
                    ContactData("3", "Mecânico João", "(21) 97777-3333", "joao@repaircars.com"),
                    ContactData("4", "Atendimento", "(21) 96666-4444", "atendimento@repaircars.com"),
                    ContactData("5", "Gerente Carlos", "(21) 95555-5555", "carlos@repaircars.com")
                )

                // Adiciona contatos
                sampleContacts.forEach { contactData ->
                    addContact(contactData.id, contactData.name, contactData.phone, contactData.email)
                }

                // Adiciona mensagens de exemplo para cada contato
                sampleContacts.forEach { contactData ->
                    addSampleMessages(contactData.id, contactData.name)
                }

                Log.d(TAG, "Dados de exemplo inicializados com sucesso")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inicializar dados de exemplo: ${e.message}")
        }
    }

    private suspend fun addSampleMessages(contactId: String, contactName: String) = withContext(Dispatchers.IO) {
        try {
            val sampleMessages = listOf(
                MessageData(contactId, "Olá! Como posso ajudar com seu veículo?", false),
                MessageData(contactId, "Preciso de ajuda com o motor do meu carro", true),
                MessageData(contactId, "Claro! Qual modelo e qual problema específico?", false),
                MessageData(contactId, "É um Honda Civic 2020, está fazendo um barulho estranho", true),
                MessageData(contactId, "Pode ser problema na correia dentada. Traga para uma avaliação", false)
            )

            sampleMessages.forEach { messageData ->
                addMessage(
                    messageData.contactId,
                    messageData.text,
                    messageData.isFromMe
                )
            }

            Log.d(TAG, "Mensagens de exemplo adicionadas para $contactName")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar mensagens de exemplo para $contactName: ${e.message}")
        }
    }

    // Métodos existentes para opções de mensagens
    suspend fun addMessageWithOptions(
        contactId: String,
        text: String,
        isSentByMe: Boolean,
        options: List<String>,
        lat: Double? = null,
        lng: Double? = null
    ): Long = withContext(Dispatchers.IO) {
        var messageId = -1L
        try {
            writableDatabase.beginTransaction()

            val values = ContentValues().apply {
                put("contactId", contactId)
                put("text", text)
                put("isSentByMe", if (isSentByMe) 1 else 0)
                put("timestamp", System.currentTimeMillis())
                put("has_options", if (options.isNotEmpty()) 1 else 0)
                if (lat != null) put("latitude", lat)
                if (lng != null) put("longitude", lng)
            }

            messageId = writableDatabase.insert(TABLE_MESSAGES, null, values)

            if (messageId != -1L && options.isNotEmpty()) {
                for (option in options) {
                    val optionValues = ContentValues().apply {
                        put("messageId", messageId)
                        put("option_text", option)
                    }
                    writableDatabase.insert(TABLE_MESSAGE_OPTIONS, null, optionValues)
                }
            }

            writableDatabase.setTransactionSuccessful()
            Log.d(TAG, "Mensagem com opções adicionada com sucesso, ID: $messageId")

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar mensagem com opções: ${e.message}")
            messageId = -1L
        } finally {
            writableDatabase.endTransaction()
        }
        messageId
    }

    suspend fun getMessageOptions(messageId: Long): List<String> = withContext(Dispatchers.IO) {
        val options = mutableListOf<String>()
        try {
            readableDatabase.rawQuery(
                "SELECT option_text FROM $TABLE_MESSAGE_OPTIONS WHERE messageId = ? ORDER BY id ASC",
                arrayOf(messageId.toString())
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    options.add(cursor.getString(0))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter opções da mensagem $messageId: ${e.message}")
        }
        options
    }

    suspend fun getMessageWithOptions(messageId: Long): MessageWithOptions? =
        withContext(Dispatchers.IO) {
            try {
                var message: Message? = null
                var options = emptyList<String>()

                readableDatabase.rawQuery(
                    "SELECT id, contactId, text, isSentByMe, timestamp, latitude, longitude, has_options FROM $TABLE_MESSAGES WHERE id = ?",
                    arrayOf(messageId.toString())
                ).use { cursor ->
                    if (cursor.moveToFirst()) {
                        message = Message(
                            id = cursor.getLong(0),
                            contactId = cursor.getString(1),
                            text = cursor.getString(2),
                            isSentByMe = cursor.getInt(3) == 1,
                            timestamp = cursor.getLong(4),
                            latitude = if (!cursor.isNull(5)) cursor.getDouble(5) else null,
                            longitude = if (!cursor.isNull(6)) cursor.getDouble(6) else null
                        )

                        val hasOptions = cursor.getInt(7) == 1
                        if (hasOptions) {
                            options = getMessageOptions(messageId)
                        }
                    }
                }

                return@withContext message?.let { msg ->
                    MessageWithOptions(msg, options)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter mensagem com opções $messageId: ${e.message}")
                null
            }
        }

    // Método original para adicionar mensagens de exemplo detalhadas
    suspend fun addDetailedSampleMessages(contactId: String) = withContext(Dispatchers.IO) {
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
                    put("isSentByMe", if (messageData.isFromMe) 1 else 0)
                    put("timestamp", System.currentTimeMillis() - (Math.random() * 86400000 * 7).toLong())
                    put("has_options", 0)
                }
                writableDatabase.insert(TABLE_MESSAGES, null, values)
            }

            Log.d(TAG, "Mensagens detalhadas de exemplo adicionadas para contato $contactId")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar mensagens detalhadas de exemplo: ${e.message}")
        }
    }
}

data class MessageWithOptions(
    val message: Message,
    val options: List<String>
)

data class ContactDisplay(
    val contact: Contact,
    val lastMessage: String
)

private data class MessageData(
    val contactId: String,
    val text: String,
    val isFromMe: Boolean
)

private data class ContactData(
    val id: String,
    val name: String,
    val phone: String,
    val email: String
)