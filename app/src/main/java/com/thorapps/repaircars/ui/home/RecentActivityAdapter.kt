package com.thorapps.repaircars.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.data.models.RecentActivity

class RecentActivityAdapter : ListAdapter<RecentActivity, RecentActivityAdapter.RecentActivityViewHolder>(
    RecentActivityDiffCallback()
) {

    inner class RecentActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvActivityDescription)
        private val timeTextView: TextView = itemView.findViewById(R.id.tvActivityTime)
        private val statusTextView: TextView = itemView.findViewById(R.id.tvActivityStatus)

        fun bind(activity: RecentActivity) {
            descriptionTextView.text = activity.description
            timeTextView.text = activity.time
            statusTextView.text = activity.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return RecentActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RecentActivityDiffCallback : DiffUtil.ItemCallback<RecentActivity>() {
    override fun areItemsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentActivity, newItem: RecentActivity): Boolean {
        return oldItem == newItem
    }
}