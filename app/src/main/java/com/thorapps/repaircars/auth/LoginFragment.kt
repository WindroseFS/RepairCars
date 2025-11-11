package com.thorapps.repaircars.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefHelper: SharedPreferencesHelper
    private var isUsingPhone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPreferencesHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupPhoneField()
        checkAutoLogin()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnForgotPassword.setOnClickListener {
            // TODO: Implementar recuperação de senha
            showForgotPasswordDialog()
        }

        binding.btnToggleLogin.setOnClickListener {
            toggleLoginMethod()
        }
    }

    private fun setupPhoneField() {
        // Adicionar máscara para telefone
        binding.etPhone.addTextChangedListener(PhoneNumberTextWatcher(binding.etPhone))
    }

    private fun toggleLoginMethod() {
        isUsingPhone = !isUsingPhone

        if (isUsingPhone) {
            // Mudar para telefone
            binding.phoneContainer.visibility = View.VISIBLE
            binding.etEmail.visibility = View.GONE
            binding.btnToggleLogin.text = "Usar email"
            binding.etPhone.requestFocus()
        } else {
            // Mudar para email
            binding.phoneContainer.visibility = View.GONE
            binding.etEmail.visibility = View.VISIBLE
            binding.btnToggleLogin.text = "Usar telefone"
            binding.etEmail.requestFocus()
        }

        // Limpar erros
        binding.etEmail.error = null
        binding.etPhone.error = null
    }

    private fun checkAutoLogin() {
        if (sharedPrefHelper.isLoggedIn()) {
            findNavController().navigate(R.id.nav_chats)
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val phone = binding.etPhone.text.toString().trim().replace(Regex("[^\\d]"), "")

        if (validateInputs(email, password, phone)) {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Entrando..."

            simulateApiLogin(email, password, if (isUsingPhone) phone else null)
        }
    }

    private fun validateInputs(email: String, password: String, phone: String): Boolean {
        var isValid = true

        if (isUsingPhone) {
            if (phone.isEmpty()) {
                binding.etPhone.error = "Telefone é obrigatório"
                isValid = false
            } else if (phone.length < 10) {
                binding.etPhone.error = "Telefone inválido"
                isValid = false
            }
        } else {
            if (email.isEmpty()) {
                binding.etEmail.error = "Email é obrigatório"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email inválido"
                isValid = false
            }
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Senha é obrigatória"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Senha deve ter pelo menos 6 caracteres"
            isValid = false
        }

        return isValid
    }

    private fun simulateApiLogin(email: String, password: String, phone: String?) {
        // CORREÇÃO: Usar o parâmetro password para simular validação
        val isValidPassword = validatePasswordStrength(password)

        if (!isValidPassword) {
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Entrar"
            binding.etPassword.error = "Senha muito fraca"
            return
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Entrar"

            // Simular login bem-sucedido
            val userId = System.currentTimeMillis()
            val userName = if (phone != null) "Usuário ${phone.takeLast(4)}" else email.substringBefore("@")
            val userEmail = if (isUsingPhone) "$phone@temp.com" else email
            val accessToken = "fake_jwt_token_${System.currentTimeMillis()}"

            sharedPrefHelper.saveLoginData(userId, userName, userEmail, phone, accessToken)

            findNavController().navigate(R.id.nav_chats)

        }, 1500)
    }

    // CORREÇÃO: Método para usar o parâmetro password
    private fun validatePasswordStrength(password: String): Boolean {
        // Verificar se a senha tem pelo menos 6 caracteres, uma letra e um número
        if (password.length < 6) return false
        if (!password.any { it.isLetter() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    // CORREÇÃO: Método adicional para usar password
    private fun showForgotPasswordDialog() {
        val email = binding.etEmail.text.toString().trim()
        val message = if (email.isNotEmpty()) {
            "Enviaremos instruções para redefinir sua senha para: $email"
        } else {
            "Digite seu email para redefinir a senha"
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Esqueci minha senha")
            .setMessage(message)
            .setPositiveButton("Enviar") { dialog, _ ->
                // Simular envio de email de recuperação
                simulatePasswordRecovery(email)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun simulatePasswordRecovery(email: String) {
        // Simular envio de email de recuperação
        android.widget.Toast.makeText(
            requireContext(),
            "Instruções enviadas para $email",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}