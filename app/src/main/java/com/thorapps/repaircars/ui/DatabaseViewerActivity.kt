package com.thorapps.repaircars.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityDatabaseViewerBinding
import com.thorapps.repaircars.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseViewerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ContactsAdapter
    private lateinit var binding: ActivityDatabaseViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        loadContacts()
    }

    private fun loadContacts() {
        CoroutineScope(Dispatchers.Main).launch {
            val contactList = withContext(Dispatchers.IO) {
                dbHelper.getAllContacts()
            }

            adapter = ContactsAdapter(contactList)
            binding.recyclerViewContacts.layoutManager = LinearLayoutManager(this@DatabaseViewerActivity)
            binding.recyclerViewContacts.adapter = adapter
        }
    }
}