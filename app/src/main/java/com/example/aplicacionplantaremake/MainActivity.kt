package com.example.aplicacionplantaremake

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var ivlogo: ImageView
    private lateinit var ivlogo2: ImageView
    private lateinit var  btnRegistro: Button
    private lateinit var  btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ivlogo = findViewById(R.id.foto)
        //ivlogo2 = findViewById(R.id.patata)

        btnRegistro = findViewById(R.id.btn_registro);
        btnRegistro.setOnClickListener{
            val actividad = Intent (this, RegistroActivity::class.java)
            startActivity(actividad)
        }

        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            val actividad = Intent(this, LoginActivity::class.java)
            startActivity(actividad)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}