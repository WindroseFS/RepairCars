package com.thorapps.repaircars.news

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.news.NewsAdapter
import com.thorapps.repaircars.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar a Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Notícias de Automobilismo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupBackPressedHandler()
    }

    private fun setupRecyclerView() {
        val noticias = gerarNoticiasExemplo()

        if (noticias.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
            binding.recyclerNews.visibility = View.GONE
        } else {
            binding.textEmptyState.visibility = View.GONE
            binding.recyclerNews.visibility = View.VISIBLE

            val adapter = NewsAdapter(noticias) { news ->
                onNewsItemClicked(news)
            }
            binding.recyclerNews.layoutManager = LinearLayoutManager(this)
            binding.recyclerNews.adapter = adapter
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
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun onNewsItemClicked(news: News) {
        Toast.makeText(this, "Clicou em: ${news.titulo}", Toast.LENGTH_SHORT).show()
        // Aqui você pode abrir uma tela de detalhes da notícia se quiser
    }

    private fun gerarNoticiasExemplo(): List<News> {
        return listOf(
            News(
                "Max Verstappen vence GP do Japão e garante título antecipado",
                "O piloto da Red Bull conquistou mais uma vitória dominante em Suzuka, confirmando seu tetracampeonato mundial com algumas corridas de antecedência.",
                "08/10/2025"
            ),
            News(
                "Ferrari anuncia novo motor híbrido para 2026",
                "A Scuderia Ferrari revelou detalhes do seu novo propulsor híbrido, focado em maior eficiência energética para a próxima era da Fórmula 1.",
                "06/10/2025"
            ),
            News(
                "Toyota vence as 24 Horas de Le Mans pela quinta vez consecutiva",
                "A equipe japonesa manteve sua hegemonia no endurance mundial, com destaque para a performance do GR010 Hybrid e seus pilotos experientes.",
                "28/09/2025"
            ),
            News(
                "Porsche apresenta o novo 911 GT3 RS elétrico",
                "A Porsche surpreendeu o mercado com um conceito de 911 totalmente elétrico, prometendo desempenho digno dos modelos a combustão.",
                "01/10/2025"
            ),
            News(
                "McLaren expande operações na América do Sul",
                "A McLaren anunciou a abertura de um novo centro técnico no Brasil, voltado para o suporte a clientes e desenvolvimento de tecnologias automotivas.",
                "03/10/2025"
            )
        )
    }
}