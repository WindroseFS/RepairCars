package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thorapps.repaircars.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginButton()
        setupRegisterLink()

        // Preencher com dados de teste para facilitar
        binding.etEmail.setText("usuario@exemplo.com")
        binding.etPassword.setText("123456")
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }
    }

    private fun setupRegisterLink() {
        binding.tvRegister.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de cadastro será implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Digite seu email"
            binding.etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Digite um email válido"
            binding.etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Digite sua senha"
            binding.etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "Senha deve ter pelo menos 6 caracteres"
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        // Verificar se o progressBar existe no layout
        if (::binding.isInitialized) {
            // Tentar acessar o progressBar se existir
            try {
                binding.progressBar?.visibility = android.view.View.VISIBLE
            } catch (e: Exception) {
                // Se progressBar não existir, criar um temporário
                showTemporaryProgress()
            }
        }

        binding.btnLogin.isEnabled = false

        // Simular delay de rede
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Ocultar progressBar se existir
            try {
                binding.progressBar?.visibility = android.view.View.GONE
            } catch (e: Exception) {
                // Ignorar se não existir
            }

            binding.btnLogin.isEnabled = true

            // Login bem-sucedido (para teste, aceita qualquer credencial válida)
            if (email.isNotEmpty() && password.length >= 6) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }, 1500)
    }

    private fun showTemporaryProgress() {
        // Mostrar Toast como fallback se não houver progressBar
        Toast.makeText(this, "Fazendo login...", Toast.LENGTH_SHORT).show()
    }
}