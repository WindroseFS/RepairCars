package com.thorapps.repaircars.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R

class RecentActivityAdapter : ListAdapter<RecentActivity, RecentActivityAdapter.ViewHolder>(RecentActivityDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(activity: RecentActivity) {
            tvDescription.text = activity.description
            tvTime.text = activity.time
            tvStatus.text = activity.status

            // Cor do status baseada no estado
            val backgroundRes = when (activity.status.toLowerCase()) {
                "concluído" -> R.drawable.status_background_green
                "em andamento" -> R.drawable.status_background_blue
                "agendado" -> R.drawable.status_background_orange
                else -> R.drawable.status_background_gray
            }
            tvStatus.setBackgroundResource(backgroundRes)
        }
    }

    companion object RecentActivityDiffCallback : DiffUtil.ItemCallback<RecentActivity>() {
        override fun areItemsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
            return oldItem.description == newItem.description && oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
            return oldItem == newItem
        }
    }
}