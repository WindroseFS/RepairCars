package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thorapps.repaircars.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        val contacts = loadSampleContacts()

        contactsAdapter = ContactsAdapter(contacts) { contact ->
            Snackbar.make(
                requireView(),
                "Contato selecionado: ${contact.name}",
                Snackbar.LENGTH_SHORT
            ).show()
            // Aqui você pode navegar para o ChatFragment, por exemplo
        }

        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            Snackbar.make(it, "Adicionar novo contato", Snackbar.LENGTH_SHORT).show()
            // Aqui você pode navegar para NewContactFragment
        }
    }

    /** 🔹 Carrega contatos fictícios (mesmo estilo de loadSampleChats) */
    private fun loadSampleContacts(): List<Contact> {
        return listOf(
            Contact(
                id = "1",
                name = "Oficina Central",
                phone = "(21) 99999-1111",
                email = "oficina@repaircars.com",
                lastMessage = "Olá! Como posso ajudar com seu veículo?"
            ),
            Contact(
                id = "2",
                name = "Suporte Técnico",
                phone = "(21) 98888-2222",
                email = "suporte@repaircars.com",
                lastMessage = "Seu orçamento está pronto para revisão"
            ),
            Contact(
                id = "3",
                name = "Mecânico João",
                phone = "(21) 97777-3333",
                email = "joao@repaircars.com",
                lastMessage = "As peças chegaram, podemos agendar?"
            ),
            Contact(
                id = "4",
                name = "Atendimento",
                phone = "(21) 96666-4444",
                email = "atendimento@repaircars.com",
                lastMessage = "Lembramos que sua revisão está agendada"
            ),
            Contact(
                id = "5",
                name = "Gerente Carlos",
                phone = "(21) 95555-5555",
                email = "carlos@repaircars.com",
                lastMessage = "Temos uma promoção especial para clientes"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
