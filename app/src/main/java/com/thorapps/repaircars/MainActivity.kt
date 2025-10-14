package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

        // Configurar a Toolbar como ActionBar (agora funciona com tema NoActionBar)
        setSupportActionBar(binding.toolbar)

        // Configurar navegação
        setupNavigation()

        // Configurar botão voltar moderno
        setupBackPressedCallback()

        Toast.makeText(this, "Bem-vindo ao Repair Cars!", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        try {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(
                setOf(R.id.nav_contacts, R.id.nav_services, R.id.nav_customers),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            binding.navView.setupWithNavController(navController)

        } catch (e: Exception) {
            createFallbackInterface()
        }
    }

    private fun setupBackPressedCallback() {
        // Configurar o comportamento do botão voltar
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Verificar se o drawer está aberto
                if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
                    binding.drawerLayout.closeDrawer(binding.navView)
                } else {
                    // Verificar se podemos voltar no nav controller
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    if (!navController.popBackStack()) {
                        // Se não há mais fragments para voltar, finalize a activity
                        finish()
                    }
                }
            }
        })
    }

    private fun createFallbackInterface() {
        // Interface de fallback caso a navegação falhe
        Toast.makeText(this, "Erro na navegação. Carregando interface básica...", Toast.LENGTH_LONG).show()

        // Aqui você pode adicionar lógica alternativa
        // Por exemplo, carregar um fragment diretamente ou mostrar uma tela de erro
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_contacts -> {
                    Toast.makeText(this, "Contatos", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_services -> {
                    Toast.makeText(this, "Serviços", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_customers -> {
                    Toast.makeText(this, "Clientes", Toast.LENGTH_SHORT).show()
                }
            }
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}