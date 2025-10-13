package com.thorapps.repaircars.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()

    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadNotifications()
    }

    private fun setupRecyclerView() {
        notificationsAdapter = NotificationsAdapter { notification ->
            viewModel.markAsRead(notification.id)
        }

        binding.recyclerNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notificationsState.collect { state ->
                when (state) {
                    is NotificationsState.Loading -> showLoading(true)
                    is NotificationsState.Success -> showNotifications(state.notifications)
                    is NotificationsState.Error -> showError(state.message)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.unreadCount.collect { count ->
                updateBadge(count)
            }
        }
    }

    private fun setupClickListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadNotifications()
        }

        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllNotifications()
        }

        binding.btnMarkAllRead.setOnClickListener {
            viewModel.markAllAsRead()
        }

        binding.btnRetry.setOnClickListener {
            loadNotifications()
        }
    }

    private fun loadNotifications() {
        viewModel.loadNotifications()
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.swipeRefreshLayout.isRefreshing = loading

        if (loading) {
            binding.layoutContent.visibility = View.GONE
            binding.layoutError.visibility = View.GONE
            binding.layoutEmpty.visibility = View.GONE
        }
    }

    private fun showNotifications(notifications: List<Notification>) {
        notificationsAdapter.submitList(notifications)

        if (notifications.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.GONE
            binding.recyclerNotifications.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
            binding.recyclerNotifications.visibility = View.VISIBLE
        }

        binding.layoutError.visibility = View.GONE
        binding.swipeRefreshLayout.isRefreshing = false
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.layoutError.visibility = View.VISIBLE
        binding.layoutContent.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.recyclerNotifications.visibility = View.GONE
        binding.swipeRefreshLayout.isRefreshing = false
        binding.progressBar.visibility = View.GONE
    }

    private fun updateBadge(count: Int) {
        binding.tvBadgeCount.text = if (count > 0) count.toString() else ""
        binding.badgeContainer.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}