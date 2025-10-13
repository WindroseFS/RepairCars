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

        // Constantes para nomes de tabelas
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

    // ====== MÉTODO PARA VISUALIZAÇÃO DO BANCO ======
    suspend fun getDatabaseInfo(): List<DatabaseInfoItem> = withContext(Dispatchers.IO) {
        val databaseInfo = mutableListOf<DatabaseInfoItem>()

        try {
            // Informações da tabela de contatos
            val contactsCursor = readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_CONTACTS", null
            )
            val contactsCount = if (contactsCursor.moveToFirst()) contactsCursor.getInt(0) else 0
            contactsCursor.close()

            // Obter colunas da tabela contacts
            val contactsColumnsCursor = readableDatabase.rawQuery(
                "PRAGMA table_info($TABLE_CONTACTS)", null
            )
            val contactsColumns = mutableListOf<String>()
            while (contactsColumnsCursor.moveToNext()) {
                contactsColumns.add(contactsColumnsCursor.getString(1)) // nome da coluna
            }
            contactsColumnsCursor.close()

            databaseInfo.add(
                DatabaseInfoItem(
                    tableName = TABLE_CONTACTS,
                    rowCount = contactsCount,
                    columns = contactsColumns
                )
            )

            // Informações da tabela de mensagens
            val messagesCursor = readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM $TABLE_MESSAGES", null
            )
            val messagesCount = if (messagesCursor.moveToFirst()) messagesCursor.getInt(0) else 0
            messagesCursor.close()

            // Obter colunas da tabela messages
            val messagesColumnsCursor = readableDatabase.rawQuery(
                "PRAGMA table_info($TABLE_MESSAGES)", null
            )
            val messagesColumns = mutableListOf<String>()
            while (messagesColumnsCursor.moveToNext()) {
                messagesColumns.add(messagesColumnsCursor.getString(1)) // nome da coluna
            }
            messagesColumnsCursor.close()

            databaseInfo.add(
                DatabaseInfoItem(
                    tableName = TABLE_MESSAGES,
                    rowCount = messagesCount,
                    columns = messagesColumns
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter informações do banco: ${e.message}")
        }

        return@withContext databaseInfo
    }

    // ====== CONTACTS ======
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

    // ====== MESSAGES ======
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

    // Método auxiliar para compatibilidade (opcional)
    suspend fun getMessages(contactId: Long): List<Message> = getMessagesForContact(contactId)

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
}