package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.thorapps.repaircars.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Configuração da navegação
        setupNavigation()
    }

    private fun setupNavigation() {
        // Obtém o NavController do FragmentContainerView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Define os destinos de top-level para o Drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_dashboard,
                R.id.nav_services,
                R.id.nav_customers,
                R.id.nav_chats,
                R.id.nav_contacts,
                R.id.nav_notifications,
                R.id.nav_settings,
                R.id.nav_help
            ),
            binding.drawerLayout
        )

        // Configura a ActionBar com o NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configura o NavigationView com o NavController
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}