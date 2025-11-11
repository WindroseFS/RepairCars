package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
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
        setSupportActionBar(binding.mainToolbar)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_chats, R.id.nav_contacts, R.id.nav_notifications,
                R.id.nav_news, R.id.nav_settings, R.id.nav_help
            ),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_logout) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                logout()
                true
            } else {
                false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    supportActionBar?.hide()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    supportActionBar?.show()
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    updateNavHeader()
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
            tvAppName.visibility = android.view.View.GONE
            tvUserName.visibility = android.view.View.VISIBLE
            tvUserEmail.visibility = android.view.View.VISIBLE
            tvUserName.text = sharedPrefHelper.getUserName()
            tvUserEmail.text = sharedPrefHelper.getUserEmail()
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
        navHostFragment.navController.navigate(R.id.loginFragment)
    }

    // ✅ CORREÇÃO: Método navigateUp sem parâmetros
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}