package com.thorapps.repaircars.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thorapps.repaircars.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // REMOVIDA referência à toolbar
        setupSettings()
    }

    private fun setupSettings() {
        // Configurar as opções de configurações aqui
        // Exemplo: listeners para switches, botões, etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}