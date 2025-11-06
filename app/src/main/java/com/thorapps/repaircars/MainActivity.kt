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

        // Configurar a Toolbar
        setSupportActionBar(binding.toolbar)

        // Configurar navegação DEPOIS que a view estiver criada
        binding.root.post {
            setupNavigation()
        }
    }

    private fun setupNavigation() {
        // Obter o NavController
        val navController = findNavController(R.id.nav_host_fragment)

        // Configurar TODOS os destinos de nível superior para o Drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(

                R.id.nav_chats,
                R.id.nav_news,
                R.id.nav_contacts,
                R.id.nav_notifications,
                R.id.nav_settings,
                R.id.nav_help
            ),
            binding.drawerLayout
        )

        // Configurar ActionBar com NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configurar NavigationView com NavController
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}