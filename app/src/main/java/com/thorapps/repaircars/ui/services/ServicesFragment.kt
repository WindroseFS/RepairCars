package com.thorapps.repaircars.ui.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thorapps.repaircars.databinding.FragmentServicesBinding

class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up your services UI here
        binding.textServicesTitle.text = "Our Services"
        binding.textServicesDescription.text = "Professional car repair services including:\n\n• Engine Repair\n• Brake Service\n• Oil Change\n• Tire Rotation\n• Electrical Systems"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}