package com.thorapps.repaircars

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.thorapps.repaircars.contacts.Contact
import com.thorapps.repaircars.databinding.ActivityNewChatBinding

class NewChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupBackPressedHandler()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupViews() {
        binding.btnSaveContact.setOnClickListener {
            saveContact()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnBackToList.setOnClickListener {
            finish()
        }
    }

    private fun saveContact() {
        val name = binding.etContactName.text.toString().trim()
        val phone = binding.etContactPhone.text.toString().trim()
        val email = binding.etContactEmail.text.toString().trim()

        if (!validateInputs(name, phone)) {
            return
        }

        // Criar o contato
        val newContact = Contact(
            id = System.currentTimeMillis().toString(),
            name = name,
            phone = if (phone.isNotEmpty()) phone else null,
            email = email
        )

        // Retornar o contato para o ContactsFragment
        val resultIntent = Intent().apply {
            putExtra("NEW_CONTACT", newContact)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()

        showSuccess("Contato adicionado com sucesso!")
    }

    private fun validateInputs(name: String, phone: String): Boolean {
        var isValid = true

        // Validar nome
        if (name.isEmpty()) {
            binding.etContactName.error = "Digite o nome do contato"
            isValid = false
        } else {
            binding.etContactName.error = null
        }

        // Validar telefone
        if (phone.isEmpty()) {
            binding.etContactPhone.error = "Digite o telefone do contato"
            isValid = false
        } else {
            binding.etContactPhone.error = null
        }

        return isValid
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}