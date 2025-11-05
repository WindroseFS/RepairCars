package com.thorapps.repaircars.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.ActivityRegisterBinding
import com.thorapps.repaircars.models.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        // Configurar a toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Cadastro"

        // Configurar os TextInputLayouts
        setupTextInputLayouts()
    }

    private fun setupTextInputLayouts() {
        // Remover erro quando o usuário começar a digitar
        listOf(
            binding.tilName,
            binding.tilEmail,
            binding.tilPhone,
            binding.tilPassword,
            binding.tilConfirmPassword
        ).forEach { textInputLayout ->
            textInputLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    textInputLayout.error = null
                    textInputLayout.isErrorEnabled = false
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            attemptRegistration()
        }

        binding.tvLogin.setOnClickListener {
            navigateToLogin()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun attemptRegistration() {
        if (validateForm()) {
            registerUser()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar nome
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.tilName.error = "Nome é obrigatório"
            isValid = false
        }

        // Validar email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email é obrigatório"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email inválido"
            isValid = false
        }

        // Validar telefone
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Telefone é obrigatório"
            isValid = false
        } else if (phone.length < 10) {
            binding.tilPhone.error = "Telefone deve ter pelo menos 10 dígitos"
            isValid = false
        }

        // Validar senha
        val password = binding.etPassword.text.toString()
        if (password.isEmpty()) {
            binding.tilPassword.error = "Senha é obrigatória"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Senha deve ter pelo menos 6 caracteres"
            isValid = false
        }

        // Validar confirmação de senha
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Confirme sua senha"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Senhas não coincidem"
            isValid = false
        }

        return isValid
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()

        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuário criado com sucesso no Authentication
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        // Atualizar perfil com o nome
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Salvar dados adicionais no Realtime Database
                                    saveUserToDatabase(user.uid, name, email, phone)
                                } else {
                                    showLoading(false)
                                    Toast.makeText(
                                        this,
                                        "Erro ao atualizar perfil: ${profileTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Erro no cadastro: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToDatabase(userId: String, name: String, email: String, phone: String) {
        val user = User(
            id = userId,
            name = name,
            email = email,
            phone = phone,
            profileImage = "",
            createdAt = System.currentTimeMillis(),
            userType = "client" // ou "mechanic" dependendo do seu app
        )

        database.child("users").child(userId).setValue(user)
            .addOnCompleteListener { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Cadastro realizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navegar para a tela principal
                    navigateToMain()
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao salvar dados: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnRegister.isEnabled = !loading
        binding.btnRegister.text = if (loading) "" else "Cadastrar"
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        // Aqui você deve navegar para a tela principal do app
        // Por exemplo:
        // val intent = Intent(this, MainActivity::class.java)
        // startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}