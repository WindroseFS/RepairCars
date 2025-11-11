package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.thorapps.repaircars.auth.SharedPreferencesHelper
import com.thorapps.repaircars.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharedPrefHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefHelper = SharedPreferencesHelper(this)

        // Toolbar principal
        setSupportActionBar(binding.mainToolbar)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

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

        // Liga o NavigationView (menu lateral) ao NavController
        binding.navView.setupWithNavController(navController)

        // Ícone "hambúrguer" visível
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // ✅ Navegação automática + Logout funcional
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    // Fecha o Drawer e faz logout
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    logout()
                    true
                }
                else -> {
                    // Permite navegação normal via NavController
                    val handled = try {
                        navController.navigate(menuItem.itemId)
                        true
                    } catch (e: IllegalArgumentException) {
                        false // ignora itens que não tenham destino
                    }
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
        }

        // Atualiza título e estado da Toolbar conforme o destino
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    supportActionBar?.hide()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.nav_chats -> showSection("Conversas")
                R.id.nav_news -> showSection("Notícias")
                R.id.nav_contacts -> showSection("Contatos")
                R.id.nav_notifications -> showSection("Notificações")
                R.id.nav_settings -> showSection("Configurações")
                R.id.nav_help -> showSection("Ajuda")
                R.id.nav_chat -> {
                    supportActionBar?.show()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    updateNavHeader()
                }
                else -> {
                    supportActionBar?.show()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    updateNavHeader()
                }
            }
        }
    }

    private fun showSection(title: String) {
        supportActionBar?.show()
        supportActionBar?.title = title
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        updateNavHeader()
    }

    private fun updateNavHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val tvUserName = headerView.findViewById<android.widget.TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<android.widget.TextView>(R.id.tvUserEmail)
        val tvAppName = headerView.findViewById<android.widget.TextView>(R.id.tvAppName)

        if (sharedPrefHelper.isLoggedIn()) {
            tvAppName.visibility = android.view.View.GONE
            tvUserName.visibility = android.view.View.VISIBLE
            tvUserEmail.visibility = android.view.View.VISIBLE
            tvUserName.text = sharedPrefHelper.getUserName()

            val contactInfo = if (sharedPrefHelper.hasPhone()) {
                sharedPrefHelper.getUserPhone()
            } else {
                sharedPrefHelper.getUserEmail()
            }
            tvUserEmail.text = contactInfo
        } else {
            tvAppName.visibility = android.view.View.VISIBLE
            tvUserName.visibility = android.view.View.GONE
            tvUserEmail.visibility = android.view.View.GONE
        }
    }

    fun logout() {
        sharedPrefHelper.logout()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.loginFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val currentDestination = navHostFragment.navController.currentDestination?.id

                val isTopLevelDestination = currentDestination in setOf(
                    R.id.nav_chats, R.id.nav_contacts, R.id.nav_notifications,
                    R.id.nav_news, R.id.nav_settings, R.id.nav_help
                )

                if (isTopLevelDestination) {
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        binding.drawerLayout.openDrawer(GravityCompat.START)
                    }
                } else {
                    onSupportNavigateUp()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
