package com.thorapps.repaircars.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        // Em fragments, você pode usar requireActivity() para acessar a Activity
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                title = "Notícias de Automobilismo"
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
    }

    private fun setupRecyclerView() {
        val noticias = gerarNoticiasExemplo()

        binding.recyclerNews.apply {
            layoutManager = LinearLayoutManager(requireContext())

            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

            adapter = NewsAdapter(noticias) { news ->
                onNewsItemClicked(news)
            }
        }

        if (noticias.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
            binding.recyclerNews.visibility = View.GONE
        } else {
            binding.textEmptyState.visibility = View.GONE
            binding.recyclerNews.visibility = View.VISIBLE
        }
    }

    private fun onNewsItemClicked(news: News) {
        Toast.makeText(
            requireContext(),
            "Abrindo: ${news.titulo}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun gerarNoticiasExemplo(): List<News> {
        return listOf(
            News(
                "Max Verstappen vence GP do Japão",
                "O piloto da Red Bull conquistou mais uma vitória dominante em Suzuka, confirmando seu tetracampeonato mundial.",
                "08/10/2025"
            ),
            News(
                "Ferrari anuncia novo motor híbrido para 2026",
                "A Scuderia Ferrari revelou detalhes do seu novo propulsor híbrido, focado em maior eficiência energética.",
                "06/10/2025"
            ),
            News(
                "Toyota vence as 24 Horas de Le Mans",
                "A equipe japonesa manteve sua hegemonia no endurance mundial com o GR010 Hybrid.",
                "28/09/2025"
            ),
            News(
                "Porsche apresenta 911 GT3 RS elétrico",
                "A Porsche surpreendeu com um conceito de 911 totalmente elétrico, prometendo alto desempenho.",
                "01/10/2025"
            ),
            News(
                "McLaren expande na América do Sul",
                "A McLaren abrirá um novo centro técnico no Brasil para suporte a clientes.",
                "03/10/2025"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}