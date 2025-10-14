package com.thorapps.repaircars.ui.customers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.data.models.Customer

class CustomersAdapter(
    private val customers: List<Customer>
) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textCustomerName)
        val carInfoTextView: TextView = itemView.findViewById(R.id.textCarInfo)
        val lastServiceTextView: TextView = itemView.findViewById(R.id.textLastService)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        holder.nameTextView.text = customer.name
        holder.carInfoTextView.text = "${customer.carModel} - ${customer.carYear}"
        holder.lastServiceTextView.text = "Last service: ${customer.lastService}"
    }

    override fun getItemCount(): Int = customers.size
}