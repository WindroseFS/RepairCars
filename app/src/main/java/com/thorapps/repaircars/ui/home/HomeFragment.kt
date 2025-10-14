package com.thorapps.repaircars.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thorapps.repaircars.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        loadHomeData()
    }

    private fun loadHomeData() {
        showLoading(true)

        // Simulate data loading
        binding.root.postDelayed({
            try {
                binding.tvWelcome.text = "Welcome to Repair Cars!"
                binding.tvTodayAppointments.text = "Today's Appointments: 5"
                binding.tvUrgentTasks.text = "Urgent Tasks: 2"
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
            binding.tvWelcome.visibility = View.GONE
            binding.tvTodayAppointments.visibility = View.GONE
            binding.tvUrgentTasks.visibility = View.GONE
        } else {
            binding.tvWelcome.visibility = View.VISIBLE
            binding.tvTodayAppointments.visibility = View.VISIBLE
            binding.tvUrgentTasks.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}