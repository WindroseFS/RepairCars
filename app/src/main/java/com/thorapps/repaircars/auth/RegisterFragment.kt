package com.thorapps.repaircars.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(name, email, password)) {
                registerUser(name, email, password)
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Nome é obrigatório"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email é obrigatório"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email inválido"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Senha é obrigatória"
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "Senha deve ter pelo menos 6 caracteres"
            return false
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String) {
        // Simular processo de registro
        Toast.makeText(
            requireContext(),
            "Registro bem-sucedido! Bem-vindo, $name",
            Toast.LENGTH_SHORT
        ).show()

        // Navegar para ChatsFragment (conforme seu navigation graph)
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}