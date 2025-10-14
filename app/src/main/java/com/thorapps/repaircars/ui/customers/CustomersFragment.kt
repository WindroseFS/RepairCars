package com.thorapps.repaircars.ui.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.data.models.Customer
import com.thorapps.repaircars.databinding.FragmentCustomersBinding

class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private val customerList = mutableListOf<Customer>()
    private lateinit var customersAdapter: CustomersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCustomers()
    }

    private fun setupRecyclerView() {
        customersAdapter = CustomersAdapter(customerList)
        binding.recyclerViewCustomers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customersAdapter
        }
    }

    private fun loadCustomers() {
        // Sample data
        val sampleCustomers = listOf(
            Customer("Alice Johnson", "Toyota Camry", "2020", "Regular maintenance"),
            Customer("Bob Wilson", "Honda Civic", "2019", "Brake replacement"),
            Customer("Carol Davis", "Ford F-150", "2021", "Oil change")
        )

        customerList.clear()
        customerList.addAll(sampleCustomers)
        customersAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}