package com.thorapps.repaircars.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.ItemNewsBinding
import com.thorapps.repaircars.network.models.ApiNews

class NewsAdapter(
    private var newsList: List<ApiNews>,
    private val onItemClick: (ApiNews) -> Unit = {}
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    fun updateList(newList: List<ApiNews>) {
        newsList = newList
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: ApiNews) {
            binding.tvTitulo.text = news.titulo
            binding.tvDescricao.text = news.descricao
            binding.tvData.text = news.data

            // Usar safe calls para views que podem ser nulas
            binding.tvCategoria?.text = news.categoria
            binding.tvVisualizacoes?.text = "${news.visualizacoes} visualizações"

            // Carregar imagem se disponível
            news.imageUrl?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_news)
                    .error(R.drawable.ic_news)
                    .into(binding.ivNewsImage)
            } ?: run {
                binding.ivNewsImage.setImageResource(R.drawable.ic_news)
            }

            binding.root.setOnClickListener {
                onItemClick(news)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size
}