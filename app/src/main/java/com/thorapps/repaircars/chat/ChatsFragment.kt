package com.thorapps.repaircars.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.databinding.FragmentChatsBinding
import com.thorapps.repaircars.R

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViews()
    }

    private fun setupRecyclerView() {
        binding.chatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter temporário para demonstração
        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.setOnClickListener {
                    val contactId = (position + 1).toLong()
                    val contactName = "Contato ${position + 1}"

                    // SAFE ARGS - Navegando com argumentos
                    val action = ChatsFragmentDirections.actionChatsFragmentToChatFragment(
                        contactId = contactId,
                        contactName = contactName
                    )
                    findNavController().navigate(action)
                }
            }

            override fun getItemCount(): Int = 5 // 5 itens de exemplo
        }

        binding.chatsRecyclerView.adapter = adapter
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            // Abrir drawer navigation se necessário
        }

        binding.fabNewChat.setOnClickListener {
            // Navegar para contatos para criar novo chat
            val action = ChatsFragmentDirections.actionChatsFragmentToContactsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}