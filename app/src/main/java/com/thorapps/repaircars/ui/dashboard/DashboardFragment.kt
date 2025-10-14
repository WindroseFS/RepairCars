package com.thorapps.repaircars.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thorapps.repaircars.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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
        loadDashboardData()
    }

    private fun loadDashboardData() {
        showLoading(true)

        // Simulate data loading
        binding.root.postDelayed({
            try {
                binding.tvActiveRepairs.text = "Active Repairs: 8"
                binding.tvCompletedToday.text = "Completed Today: 12"
                binding.tvPendingRequests.text = "Pending Requests: 5"
                showError(false)
            } catch (e: Exception) {
                showError(true)
            }
            showLoading(false)
        }, 1000)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        binding.errorGroup.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            binding.tvActiveRepairs.visibility = View.GONE
            binding.tvCompletedToday.visibility = View.GONE
            binding.tvPendingRequests.visibility = View.GONE
        } else {
            binding.tvActiveRepairs.visibility = View.VISIBLE
            binding.tvCompletedToday.visibility = View.VISIBLE
            binding.tvPendingRequests.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}