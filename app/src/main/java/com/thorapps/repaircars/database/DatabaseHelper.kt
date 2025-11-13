package com.thorapps.repaircars.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.thorapps.repaircars.contacts.Contact
import com.thorapps.repaircars.database.models.ContactData
import com.thorapps.repaircars.database.models.ContactDisplay
import com.thorapps.repaircars.database.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "repaircars.db"
        private const val DATABASE_VERSION = 8

        const val TABLE_CONTACTS = "contacts"
        const val TABLE_MESSAGES = "messages"
        const val TABLE_MESSAGE_OPTIONS = "message_options"

        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(
                """
                CREATE TABLE $TABLE_CONTACTS (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    phone TEXT,
                    email TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE $TABLE_MESSAGES (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    contactId TEXT,
                    text TEXT,
                    isSentByMe INTEGER,
                    timestamp INTEGER,
                    latitude REAL,
                    longitude REAL,
                    has_options INTEGER DEFAULT 0
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE $TABLE_MESSAGE_OPTIONS (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    messageId INTEGER,
                    option_text TEXT,
                    FOREIGN KEY(messageId) REFERENCES $TABLE_MESSAGES(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )

            Log.d(TAG, "Banco de dados criado com sucesso.")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar banco de dados: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGE_OPTIONS")
            onCreate(db)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar banco de dados: ${e.message}")
        }
    }

    // 🔹 Gera ID único
    fun generateContactId(): String {
        return "contact_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    // 🔹 Adiciona contato (telefone opcional, e-mail obrigatório)
    suspend fun addContact(id: String, name: String, phone: String?, email: String): Long =
        withContext(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put("id", id)
                    put("name", name)
                    put("phone", phone)
                    put("email", email)
                }
                writableDatabase.insert(TABLE_CONTACTS, null, values)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao adicionar contato: ${e.message}")
                -1L
            }
        }

    // 🔹 Obtém todos os contatos
    suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        try {
            readableDatabase.rawQuery(
                "SELECT id, name, phone, email FROM $TABLE_CONTACTS",
                null
            ).use { cursor ->
                val idxId = cursor.getColumnIndexOrThrow("id")
                val idxName = cursor.getColumnIndexOrThrow("name")
                val idxPhone = cursor.getColumnIndexOrThrow("phone")
                val idxEmail = cursor.getColumnIndexOrThrow("email")

                while (cursor.moveToNext()) {
                    val id = cursor.getString(idxId)
                    val name = cursor.getString(idxName)
                    val phone = if (!cursor.isNull(idxPhone)) cursor.getString(idxPhone) else null
                    val email = cursor.getString(idxEmail) // obrigatório

                    contacts.add(Contact(id, name, phone, email))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter contatos: ${e.message}")
        }
        contacts
    }

    // 🔹 Retorna contatos com última mensagem e e-mail
    suspend fun getContactsWithLastMessage(): List<ContactDisplay> =
        withContext(Dispatchers.IO) {
            val list = mutableListOf<ContactDisplay>()
            try {
                readableDatabase.rawQuery(
                    """
                    SELECT c.id, c.name, c.phone, c.email,
                           (SELECT text FROM $TABLE_MESSAGES 
                            WHERE contactId = c.id 
                            ORDER BY timestamp DESC LIMIT 1) AS last_message,
                           (SELECT timestamp FROM $TABLE_MESSAGES 
                            WHERE contactId = c.id 
                            ORDER BY timestamp DESC LIMIT 1) AS last_timestamp
                    FROM $TABLE_CONTACTS c
                    ORDER BY last_timestamp DESC
                    """.trimIndent(),
                    null
                ).use { cursor ->
                    val idxId = cursor.getColumnIndexOrThrow("id")
                    val idxName = cursor.getColumnIndexOrThrow("name")
                    val idxPhone = cursor.getColumnIndexOrThrow("phone")
                    val idxEmail = cursor.getColumnIndexOrThrow("email")
                    val idxLastMessage = cursor.getColumnIndexOrThrow("last_message")

                    while (cursor.moveToNext()) {
                        val id = cursor.getString(idxId)
                        val name = cursor.getString(idxName)

                        val phone = if (!cursor.isNull(idxPhone)) {
                            cursor.getString(idxPhone)
                        } else {
                            null
                        }

                        val email = cursor.getString(idxEmail)
                        val lastMessage = if (!cursor.isNull(idxLastMessage)) {
                            cursor.getString(idxLastMessage)
                        } else {
                            "Sem mensagens"
                        }

                        val contact = Contact(id, name, phone, email, lastMessage)
                        list.add(ContactDisplay(contact, lastMessage, phone, email))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter contatos com última mensagem: ${e.message}")
            }
            list
        }

    // 🔹 Obtém mensagens de um contato
    suspend fun getMessagesForContact(contactId: String): List<Message> =
        withContext(Dispatchers.IO) {
            val messages = mutableListOf<Message>()
            try {
                readableDatabase.rawQuery(
                    """
                    SELECT id, contactId, text, isSentByMe, timestamp, latitude, longitude 
                    FROM $TABLE_MESSAGES 
                    WHERE contactId = ? 
                    ORDER BY timestamp ASC
                    """,
                    arrayOf(contactId)
                ).use { cursor ->
                    val idxId = cursor.getColumnIndexOrThrow("id")
                    val idxContactId = cursor.getColumnIndexOrThrow("contactId")
                    val idxText = cursor.getColumnIndexOrThrow("text")
                    val idxIsSentByMe = cursor.getColumnIndexOrThrow("isSentByMe")
                    val idxTimestamp = cursor.getColumnIndexOrThrow("timestamp")
                    val idxLatitude = cursor.getColumnIndexOrThrow("latitude")
                    val idxLongitude = cursor.getColumnIndexOrThrow("longitude")

                    while (cursor.moveToNext()) {
                        val latitude = if (!cursor.isNull(idxLatitude)) cursor.getDouble(idxLatitude) else null
                        val longitude = if (!cursor.isNull(idxLongitude)) cursor.getDouble(idxLongitude) else null

                        messages.add(
                            Message(
                                id = cursor.getLong(idxId),
                                contactId = cursor.getString(idxContactId),
                                text = cursor.getString(idxText),
                                isSentByMe = cursor.getInt(idxIsSentByMe) == 1,
                                timestamp = cursor.getLong(idxTimestamp),
                                latitude = latitude,
                                longitude = longitude
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao obter mensagens: ${e.message}")
            }
            messages
        }

    // 🔹 Adiciona mensagem
    suspend fun addMessage(
        contactId: String,
        text: String,
        isSentByMe: Boolean,
        lat: Double? = null,
        lng: Double? = null
    ): Long = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put("contactId", contactId)
                put("text", text)
                put("isSentByMe", if (isSentByMe) 1 else 0)
                put("timestamp", System.currentTimeMillis())
                if (lat != null) put("latitude", lat)
                if (lng != null) put("longitude", lng)
            }
            writableDatabase.insert(TABLE_MESSAGES, null, values)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar mensagem: ${e.message}")
            -1L
        }
    }

    // 🔹 Inicializa dados de exemplo
    suspend fun initializeSampleData() = withContext(Dispatchers.IO) {
        try {
            val existing = getAllContacts()
            if (existing.isEmpty()) {
                val samples = listOf(
                    ContactData("1", "Oficina Central", "(21) 99999-1111", "oficina@repaircars.com"),
                    ContactData("2", "Suporte Técnico", "(21) 98888-2222", "suporte@repaircars.com"),
                    ContactData("3", "Mecânico João", "(21) 97777-3333", "joao@repaircars.com"),
                    ContactData("4", "Atendimento", "(21) 96666-4444", "atendimento@repaircars.com"),
                    ContactData("5", "Gerente Carlos", "(21) 95555-5555", "carlos@repaircars.com")
                )
                samples.forEach {
                    addContact(it.id, it.name, it.phone, it.email)
                }
                Log.d(TAG, "Contatos de exemplo adicionados com sucesso.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inicializar contatos: ${e.message}")
        }
    }
}
