package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityNewsBinding

data class News(
    val titulo: String,
    val descricao: String,
    val data: String
)

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notícias de Automobilismo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val noticias = gerarNoticiasExemplo()

        val adapter = NewsAdapter(noticias)
        binding.recyclerNews.layoutManager = LinearLayoutManager(this)
        binding.recyclerNews.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
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
