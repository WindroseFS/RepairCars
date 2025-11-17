package com.thorapps.repaircars.database.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R

data class DatabaseInfoItem(
    val tableName: String,
    val rowCount: Int,
    val columns: List<String>
)

class DatabaseInfoAdapter(private var data: List<DatabaseInfoItem>) :
    RecyclerView.Adapter<DatabaseInfoAdapter.DatabaseInfoViewHolder>() {

    class DatabaseInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTableName: TextView = itemView.findViewById(R.id.tvTableName)
        private val tvRowCount: TextView = itemView.findViewById(R.id.tvRowCount)
        private val tvColumns: TextView = itemView.findViewById(R.id.tvColumns)

        fun bind(item: DatabaseInfoItem) {
            tvTableName.text = "Tabela: ${item.tableName}"
            tvRowCount.text = "Registros: ${item.rowCount}"
            tvColumns.text = "Colunas: ${item.columns.joinToString(", ")}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_database_info, parent, false)
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