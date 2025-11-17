package com.thorapps.repaircars.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.MainActivity
import com.thorapps.repaircars.databinding.FragmentNewsBinding
import com.thorapps.repaircars.network.models.ApiNews
import com.thorapps.repaircars.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter
    private val newsRepository = NewsRepository()
    private var currentCategory = "Todas"
    private var currentPage = 1
    private var isLoading = false
    private var allNews = mutableListOf<ApiNews>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupCategorySpinner()
        loadNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList()) { news ->
            showNewsDetail(news)
        }

        binding.recyclerNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshNews()
        }
    }

    private fun setupCategorySpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            val categorias = newsRepository.getCategorias()

            CoroutineScope(Dispatchers.Main).launch {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categorias
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapter

                binding.spinnerCategory.onItemSelectedListener =
                    object : android.widget.AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                            val novaCategoria = parent.getItemAtPosition(position).toString()
                            if (novaCategoria != currentCategory) {
                                currentCategory = novaCategoria
                                currentPage = 1
                                allNews.clear()
                                loadNews()
                            }
                        }

                        override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                    }
            }
        }
    }

    private fun loadNews() {
        if (isLoading) return

        isLoading = true
        showLoading()

        CoroutineScope(Dispatchers.IO).launch {
            val categoria = if (currentCategory == "Todas") null else currentCategory
            val response = newsRepository.getNews(categoria, currentPage)

            CoroutineScope(Dispatchers.Main).launch {
                hideLoading()
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false

                if (response != null && response.news.isNotEmpty()) {
                    showNewsList(response.news)
                    updateToolbarTitle(response.total)
                } else {
                    showEmptyState()
                }
            }
        }
    }

    private fun showNewsList(news: List<ApiNews>) {
        binding.textEmpty.visibility = View.GONE
        binding.recyclerNews.visibility = View.VISIBLE

        allNews.clear()
        allNews.addAll(news)
        newsAdapter.updateList(allNews.toList())
    }

    private fun showNewsDetail(news: ApiNews) {
        android.widget.Toast.makeText(requireContext(), "Abrir: ${news.titulo}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun refreshNews() {
        currentPage = 1
        allNews.clear()
        loadNews()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerNews.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.textEmpty.visibility = View.VISIBLE
        binding.recyclerNews.visibility = View.GONE
        binding.textEmpty.text =
            if (currentCategory == "Todas") "Nenhuma notícia encontrada"
            else "Nenhuma notícia na categoria $currentCategory"
    }

    private fun updateToolbarTitle(total: Int) {
        (requireActivity() as? MainActivity)?.supportActionBar?.title =
            "Notícias (${total})"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}