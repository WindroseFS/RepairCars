package com.thorapps.repaircars.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.MainActivity
import com.thorapps.repaircars.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter

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
        loadNews()
    }

    private fun setupRecyclerView() {
        // CORREÇÃO: Configurar o adapter com uma lista vazia inicialmente
        newsAdapter = NewsAdapter(emptyList()) { news ->
            // Handle item click - você pode adicionar ação ao clicar em uma notícia
            showNewsDetail(news)
        }

        binding.recyclerNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun loadNews() {
        // Carregar notícias (dados mock por enquanto)
        val news = listOf(
            News(
                titulo = "Novo Sistema de Diagnóstico Eletrônico",
                descricao = "A Repair Cars acaba de implementar um sistema de diagnóstico eletrônico de última geração que permite identificar problemas com precisão milimétrica. Agilize o atendimento e tenha diagnósticos mais precisos.",
                data = "25/11/2023"
            ),
            News(
                titulo = "Promoção: Revisão Completa 30% Off",
                descricao = "Aproveite nossa promoção especial de revisão completa com 30% de desconto. Inclui troca de óleo, filtros, verificação de freios e mais. Válida até o final do mês.",
                data = "20/11/2023"
            ),
            News(
                titulo = "Workshop: Mecânica Básica para Mulheres",
                descricao = "Participe do nosso workshop gratuito de mecânica básica voltado para mulheres. Aprenda a trocar pneus, verificar óleo, identificar problemas básicos e muito mais. Inscrições abertas!",
                data = "15/11/2023"
            ),
            News(
                titulo = "Dicas para Manutenção Preventiva",
                descricao = "Confira nossas dicas essenciais para manter seu veículo em perfeito estado. A manutenção preventiva pode evitar grandes prejuízos no futuro.",
                data = "10/11/2023"
            ),
            News(
                titulo = "Nova Unidade Inaugurada",
                descricao = "Temos o prazer de anunciar a inauguração de nossa nova unidade no centro da cidade. Agora com mais espaço e equipamentos modernos para melhor atendê-lo.",
                data = "05/11/2023"
            )
        )

        setupNewsList(news)
    }

    private fun setupNewsList(news: List<News>) {
        if (news.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
            binding.recyclerNews.visibility = View.GONE
        } else {
            binding.textEmptyState.visibility = View.GONE
            binding.recyclerNews.visibility = View.VISIBLE

            // CORREÇÃO: Atualizar o adapter com a lista de notícias
            newsAdapter = NewsAdapter(news) { selectedNews ->
                showNewsDetail(selectedNews)
            }
            binding.recyclerNews.adapter = newsAdapter

            // Atualizar título com quantidade de notícias na toolbar principal
            (requireActivity() as? MainActivity)?.supportActionBar?.title = "Notícias (${news.size})"
        }
    }

    private fun showNewsDetail(news: News) {
        // Mostrar detalhes da notícia (pode ser um dialog ou nova tela)
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(news.titulo)
            .setMessage("${news.descricao}\n\nData: ${news.data}")
            .setPositiveButton("Fechar", null)
            .show()
    }

    private fun refreshNews() {
        // Simular atualização de notícias
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            loadNews()
            android.widget.Toast.makeText(
                requireContext(),
                "Notícias atualizadas",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }, 1500)
    }

    private fun showFilterOptions() {
        val categories = arrayOf("Todas", "Promoções", "Workshops", "Dicas", "Novidades")

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Filtrar Notícias")
            .setItems(categories) { _, which ->
                val selectedCategory = categories[which]
                filterNewsByCategory(selectedCategory)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun filterNewsByCategory(category: String) {
        // Implementar filtro por categoria
        android.widget.Toast.makeText(
            requireContext(),
            "Filtrando por: $category",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}