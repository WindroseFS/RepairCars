package com.thorapps.repaircars.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.R
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.database.DatabaseInfoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseViewerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DatabaseInfoAdapter
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_viewer)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.databaseRecyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        setupRecyclerView()
        loadDatabaseInfo()
    }

    private fun setupRecyclerView() {
        adapter = DatabaseInfoAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DatabaseViewerActivity)
            adapter = this@DatabaseViewerActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun loadDatabaseInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val databaseInfo = withContext(Dispatchers.IO) {
                    dbHelper.getDatabaseInfo()
                }

                if (databaseInfo.isEmpty()) {
                    showEmptyState(true)
                    adapter.updateData(emptyList())
                } else {
                    showEmptyState(false)
                    adapter.updateData(databaseInfo)
                }

            } catch (e: Exception) {
                showEmptyState(true)
                tvEmptyState.text = "Erro ao carregar dados do banco: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
        recyclerView.visibility = if (show) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
    }
}