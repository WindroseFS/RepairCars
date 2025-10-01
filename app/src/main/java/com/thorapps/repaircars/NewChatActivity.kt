package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thorapps.repaircars.databinding.ActivityNewChatBinding
import com.thorapps.repaircars.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChatBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        setupViews()
    }

    private fun setupViews() {
        binding.btnSaveContact.setOnClickListener {
            saveContact()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnBackToList.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun saveContact() {
        val name = binding.etContactName.text.toString().trim()
        val email = binding.etContactEmail.text.toString().trim()

        if (!validateInputs(name, email)) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                showLoading(true)

                val contactId = withContext(Dispatchers.IO) {
                    dbHelper.addContact(name, email)
                }

                if (contactId != -1L) {
                    showSuccess("Contato criado com sucesso!")
                    openChatDirectly(contactId, name)
                } else {
                    showError("Erro ao criar contato. Tente novamente.")
                }

            } catch (e: Exception) {
                showError("Erro: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun validateInputs(name: String, email: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.etContactName.error = "Digite o nome do contato"
            binding.etContactName.requestFocus()
            isValid = false
        } else {
            binding.etContactName.error = null
        }

        if (email.isEmpty()) {
            binding.etContactEmail.error = "Digite o email do contato"
            binding.etContactEmail.requestFocus()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etContactEmail.error = "Digite um email válido"
            binding.etContactEmail.requestFocus()
            isValid = false
        } else {
            binding.etContactEmail.error = null
        }

        return isValid
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnSaveContact.isEnabled = !show
        binding.btnCancel.isEnabled = !show
        binding.btnBackToList.isEnabled = !show
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun openChatDirectly(contactId: Long, contactName: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("CONTACT_NAME", contactName)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        navigateToMainActivity()
    }
}