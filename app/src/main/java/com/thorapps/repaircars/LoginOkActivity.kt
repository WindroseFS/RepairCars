package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.thorapps.repaircars.databinding.ActivityLoginOkBinding

class LoginOkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOkBinding
    private lateinit var redirectHandler: Handler
    private lateinit var redirectRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplica padding nos insets da view raiz (precisa do ID "main" no layout XML)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Redirecionamento automático para a tela de perfil após 5 segundos
        redirectHandler = Handler(Looper.getMainLooper())
        redirectRunnable = Runnable {
            val i = Intent(this, ProfileActivity::class.java)
            startActivity(i)
            finish()
        }
        redirectHandler.postDelayed(redirectRunnable, 5000)

        // Botão de logout cancela o redirecionamento e encerra a tela
        binding.buttonLogout.setOnClickListener {
            redirectHandler.removeCallbacks(redirectRunnable)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Garante que o redirecionamento não ocorra se a Activity for destruída antes dos 5 segundos
        redirectHandler.removeCallbacks(redirectRunnable)
    }
}