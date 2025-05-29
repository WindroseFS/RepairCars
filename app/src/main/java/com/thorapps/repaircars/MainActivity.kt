package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.thorapps.repaircars.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Defina o login e senha válidos (pode vir de banco ou API em apps reais)
    private val usuarioValido = "Almir"
    private val senhaValida = "pass"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonEntrar.setOnClickListener {
            val usuario = binding.edTextUsuario.text.toString().trim()
            val senha = binding.edTextSenha.text.toString().trim()

            if (usuario.isEmpty() || senha.isEmpty()) {
                Toast.makeText(applicationContext, "Digite o usuário e/ou a senha.", Toast.LENGTH_SHORT).show()
            } else if (usuario == usuarioValido && senha == senhaValida) {
                Toast.makeText(applicationContext, "Obrigado por testar, $usuario.", Toast.LENGTH_LONG).show()
                val i = Intent(this, LoginOkActivity::class.java)
                i.putExtra("Almir", usuario)
                i.putExtra("pass", senha)
                startActivity(i)
            } else {
                Toast.makeText(applicationContext, "Usuário e/ou senha inválidos.", Toast.LENGTH_SHORT).show()
                val j = Intent(this, LoginFailActivity::class.java)
                startActivity(j)
            }

            binding.edTextUsuario.setText("")
            binding.edTextSenha.setText("")
        }
    }
}