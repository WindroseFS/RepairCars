package com.thorapps.repaircars.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPreferencesHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupPhoneField()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupPhoneField() {
        // Adicionar máscara para telefone
        binding.etPhone.addTextChangedListener(PhoneNumberTextWatcher(binding.etPhone))
    }

    private fun performRegistration() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val phone = binding.etPhone.text.toString().trim().replace(Regex("[^\\d]"), "")

        if (validateInputs(name, email, password, phone)) {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "Cadastrando..."

            // Simular chamada de API
            simulateApiRegistration(name, email, password, if (phone.isNotEmpty()) phone else null)
        }
    }

    private fun validateInputs(name: String, email: String, password: String, phone: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.etName.error = "Nome é obrigatório"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email é obrigatório"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email inválido"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Senha é obrigatória"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Senha deve ter pelo menos 6 caracteres"
            isValid = false
        }

        // Validação opcional do telefone
        if (phone.isNotEmpty() && phone.length < 10) {
            binding.etPhone.error = "Telefone inválido"
            isValid = false
        }

        return isValid
    }

    private fun simulateApiRegistration(name: String, email: String, password: String, phone: String?) {
        val passwordStrength = calculatePasswordStrength(password)

        if (passwordStrength < 2) {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.text = "Criar Conta"
            binding.etPassword.error = "Senha muito fraca. Use letras e números."
            return
        }

        // Simular delay de rede
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.btnRegister.isEnabled = true
            binding.btnRegister.text = "Criar Conta"

            // Simular registro bem-sucedido
            val userId = System.currentTimeMillis()
            val accessToken = "fake_jwt_token_${System.currentTimeMillis()}"

            // Salvar dados do registro (telefone é opcional)
            sharedPrefHelper.saveLoginData(userId, name, email, phone, accessToken)

            // CORREÇÃO: Navegar para a tela principal usando a action correta
            navigateToMainApp()

        }, 2000)
    }

    // CORREÇÃO: Método para navegação correta após registro
    private fun navigateToMainApp() {
        try {
            // Tentar usar a action específica que limpa a pilha
            findNavController().navigate(R.id.action_registerFragment_to_nav_chats)
        } catch (e: Exception) {
            try {
                // Fallback: navegação programática com limpeza de pilha
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true) // Limpa até o login
                    .build()
                findNavController().navigate(R.id.nav_chats, null, navOptions)
            } catch (e2: Exception) {
                // Fallback final: navegação simples
                findNavController().navigate(R.id.nav_chats)
            }
        }
    }

    private fun calculatePasswordStrength(password: String): Int {
        var strength = 0

        // Comprimento mínimo
        if (password.length >= 6) strength++

        // Contém letras
        if (password.any { it.isLetter() }) strength++

        // Contém números
        if (password.any { it.isDigit() }) strength++

        // Contém caracteres especiais
        if (password.any { !it.isLetterOrDigit() }) strength++

        return strength
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}