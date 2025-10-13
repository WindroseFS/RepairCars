package com.thorapps.repaircars.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.thorapps.repaircars.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        loadHomeData()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeState.collect { state ->
                when (state) {
                    is HomeState.Loading -> showLoading(true)
                    is HomeState.Success -> showHomeData(state.data)
                    is HomeState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnQuickAction.setOnClickListener {
            // Ação rápida - iniciar novo serviço
            viewModel.startQuickAction()
        }

        binding.swipeRefresh.setOnRefreshListener {
            loadHomeData()
        }
    }

    private fun loadHomeData() {
        viewModel.loadHomeData()
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = loading
        binding.contentGroup.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showHomeData(data: HomeData) {
        binding.tvWelcome.text = "Bem-vindo, ${data.userName}!"
        binding.tvTodayAppointments.text = data.todayAppointments.toString()
        binding.tvUrgentTasks.text = data.urgentTasks.toString()

        // Atualizar lista de atividades recentes
        val adapter = RecentActivityAdapter(data.recentActivities)
        binding.recyclerRecentActivities.adapter = adapter

        binding.swipeRefresh.isRefreshing = false
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.errorGroup.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}