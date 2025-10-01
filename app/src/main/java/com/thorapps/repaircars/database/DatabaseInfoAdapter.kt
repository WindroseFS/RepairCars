package com.thorapps.repaircars.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R // Importação do R

data class DatabaseInfoItem(
    val tableName: String,
    val rowCount: Int,
    val columns: List<String>
)

class DatabaseInfoAdapter(private var data: List<DatabaseInfoItem>) :
    RecyclerView.Adapter<DatabaseInfoAdapter.DatabaseInfoViewHolder>() {

    class DatabaseInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableName: TextView = itemView.findViewById(R.id.tvTableName)
        val rowCount: TextView = itemView.findViewById(R.id.tvRowCount)
        val columns: TextView = itemView.findViewById(R.id.tvColumns)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabaseInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_database_info, parent, false)
        return DatabaseInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatabaseInfoViewHolder, position: Int) {
        val item = data[position]

        holder.tableName.text = "Tabela: ${item.tableName}"
        holder.rowCount.text = "Registros: ${item.rowCount}"
        holder.columns.text = "Colunas: ${item.columns.joinToString(", ")}"
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<DatabaseInfoItem>) {
        data = newData
        notifyDataSetChanged()
    }
}