package com.thorapps.repaircars.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.thorapps.repaircars.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        loadDashboardData()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collect { state ->
                when (state) {
                    is DashboardState.Loading -> showLoading(true)
                    is DashboardState.Success -> showDashboardData(state.data)
                    is DashboardState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            loadDashboardData()
        }

        binding.cardRepairs.setOnClickListener {
            // Navegar para lista de reparos
        }

        binding.cardStatistics.setOnClickListener {
            // Navegar para estatísticas
        }
    }

    private fun loadDashboardData() {
        viewModel.loadDashboardData()
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.contentGroup.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showDashboardData(data: DashboardData) {
        binding.tvActiveRepairs.text = data.activeRepairs.toString()
        binding.tvCompletedToday.text = data.completedToday.toString()
        binding.tvPendingRequests.text = data.pendingRequests.toString()
        binding.tvRevenue.text = "R$ ${data.revenue}"

        // Esconder estado vazio se houver dados
        binding.emptyState.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.errorGroup.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}