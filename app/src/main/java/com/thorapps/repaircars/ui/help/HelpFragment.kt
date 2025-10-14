package com.thorapps.repaircars.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thorapps.repaircars.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textHelpContent.text = """
            Frequently Asked Questions:
            
            Q: How do I add a new contact?
            A: Go to Contacts screen and tap the add button.
            
            Q: How to schedule a service?
            A: Navigate to Services and select your preferred service.
            
            Q: How to contact support?
            A: Email us at support@repaircars.com
            
            For additional help, please contact our customer service team.
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}