package com.thorapps.repaircars.database.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.databinding.ItemContactBinding

data class DatabaseInfoItem(
    val tableName: String,
    val rowCount: Int,
    val columns: List<String>
)

class DatabaseInfoAdapter(private var data: List<DatabaseInfoItem>) :
    RecyclerView.Adapter<DatabaseInfoAdapter.DatabaseInfoViewHolder>() {

    class DatabaseInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemContactBinding.bind(itemView)

        fun bind(item: DatabaseInfoItem) {
            binding.contactLayout.visibility = View.GONE
            binding.chatContactLayout.visibility = View.GONE
            binding.databaseInfoLayout.visibility = View.VISIBLE

            binding.tvTableName.text = "Tabela: ${item.tableName}"
            binding.tvRowCount.text = "Registros: ${item.rowCount}"
            binding.tvColumns.text = "Colunas: ${item.columns.joinToString(", ")}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return DatabaseInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatabaseInfoViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<DatabaseInfoItem>) {
        data = newData
        notifyDataSetChanged()
    }
}
