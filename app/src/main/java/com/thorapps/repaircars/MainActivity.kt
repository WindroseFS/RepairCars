package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Botões
        binding.btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
        binding.btnLogout.setOnClickListener { finish() }
        binding.btnViewDatabase.setOnClickListener {
            startActivity(Intent(this, DatabaseViewerActivity::class.java))
        }

        // Adapter
        contactsAdapter = ContactsAdapter(dbHelper.getContactsWithLastMessage()) { contact ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("CONTACT_ID", contact.id) // Long
                putExtra("CONTACT_NAME", contact.name)
            }
            startActivity(intent)
        }


        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        contactsAdapter.updateContacts(dbHelper.getContactsWithLastMessage())
    }
}
