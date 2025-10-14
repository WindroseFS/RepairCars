package com.thorapps.repaircars.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.data.models.Contact
import com.thorapps.repaircars.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactsAdapter: ContactsAdapter
    private val contactList = mutableListOf<Contact>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadContacts()
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter(contactList)
        binding.recycleViewContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadContacts() {
        showLoading(true)

        // Simulate loading data
        binding.recycleViewContacts.postDelayed({
            val sampleContacts = listOf(
                Contact(
                    name = "John Doe",
                    email = "john@example.com",
                    phone = "123-456-7890",
                    lastMessage = "Last service: Oil change"
                ),
                Contact(
                    name = "Jane Smith",
                    email = "jane@example.com",
                    phone = "123-456-7891",
                    lastMessage = "Last service: Brake repair"
                ),
                Contact(
                    name = "Mike Johnson",
                    email = "mike@example.com",
                    phone = "123-456-7892",
                    lastMessage = "Last service: Tire rotation"
                )
            )

            contactList.clear()
            contactList.addAll(sampleContacts)
            contactsAdapter.notifyDataSetChanged()

            if (contactList.isEmpty()) {
                showEmptyState(true)
            } else {
                showEmptyState(false)
            }
            showLoading(false)
        }, 1000)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBarMain.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(show: Boolean) {
        binding.textEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.recycleViewContacts.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}