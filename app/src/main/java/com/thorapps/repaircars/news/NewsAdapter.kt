package com.thorapps.repaircars.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.databinding.ItemNewsBinding
import com.thorapps.repaircars.news.News

class NewsAdapter(
    private val newsList: List<News>,
    private val onItemClick: (News) -> Unit = {}
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.tvTitulo.text = news.titulo
            binding.tvDescricao.text = news.descricao
            binding.tvData.text = news.data

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