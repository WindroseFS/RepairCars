package com.thorapps.repaircars

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.thorapps.repaircars.auth.SharedPreferencesHelper
import com.thorapps.repaircars.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharedPrefHelper: SharedPreferencesHelper
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefHelper = SharedPreferencesHelper(this)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // ✅ CONFIGURAÇÃO DA TOOLBAR
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ✅ CONFIGURAÇÃO DO DRAWER
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_chats,
                R.id.nav_contacts,
                R.id.nav_notifications,
                R.id.nav_news,
                R.id.nav_settings,
                R.id.nav_help
            ),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // ✅ CONFIGURAÇÃO SIMPLES DO LISTENER
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    // Esconder toolbar e drawer na autenticação
                    supportActionBar?.hide()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    // Mostrar toolbar e drawer nas outras telas
                    supportActionBar?.show()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    updateNavHeader()
                }
            }
        }

        // ✅ LISTENER DO MENU
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // Fecha o drawer quando um item é selecionado
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> {
                    // Navegação normal
                    try {
                        navController.navigate(menuItem.itemId)
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }
            }
        }
    }

    private fun updateNavHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val tvUserName = headerView.findViewById<android.widget.TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<android.widget.TextView>(R.id.tvUserEmail)
        val tvAppName = headerView.findViewById<android.widget.TextView>(R.id.tvAppName)

        if (sharedPrefHelper.isLoggedIn()) {
            tvAppName.visibility = View.GONE
            tvUserName.visibility = View.VISIBLE
            tvUserEmail.visibility = View.VISIBLE
            tvUserName.text = sharedPrefHelper.getUserName() ?: "Usuário"
            tvUserEmail.text = sharedPrefHelper.getUserEmail() ?: "email@exemplo.com"
        } else {
            tvAppName.visibility = View.VISIBLE
            tvUserName.visibility = View.GONE
            tvUserEmail.visibility = View.GONE
        }
    }

    fun logout() {
        sharedPrefHelper.logout()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()
        navController.navigate(R.id.loginFragment, null, navOptions)
    }

    override fun onSupportNavigateUp(): Boolean {
        // ✅ COMPORTAMENTO CORRETO DO BOTÃO DE NAVEGAÇÃO
        return if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        } else {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}