package com.thorapps.repaircars.news

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityNewsBinding

class NewsFragment : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupBackPressedHandler()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Notícias de Automobilismo"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        val noticias = gerarNoticiasExemplo()

        binding.recyclerNews.apply {
            layoutManager = LinearLayoutManager(this@NewsFragment)

            addItemDecoration(
                DividerItemDecoration(
                    this@NewsFragment,
                    LinearLayoutManager.VERTICAL
                )
            )

            adapter = NewsAdapter(noticias) { news ->
                onNewsItemClicked(news)
            }
        }

        if (noticias.isEmpty()) {
            binding.textEmptyState.visibility = android.view.View.VISIBLE
            binding.recyclerNews.visibility = android.view.View.GONE
        } else {
            binding.textEmptyState.visibility = android.view.View.GONE
            binding.recyclerNews.visibility = android.view.View.VISIBLE
        }
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun onNewsItemClicked(news: News) {
        Toast.makeText(
            this,
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
}