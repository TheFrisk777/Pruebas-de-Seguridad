package com.example.aplicacionplantaremake

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btnRegistrar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        btnRegistrar = findViewById<Button>(R.id.btn_registrar);
        btnCancelar = findViewById<Button>(R.id.btn_cancelar);
        btnCancelar.setOnClickListener {
            val actividad = Intent(this, MainActivity::class.java)
            startActivity(actividad)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        btnRegistrar.setOnClickListener {
            var tiCorreo = findViewById<EditText>(R.id.ti_Correo)
            var tiPass = findViewById<EditText>(R.id.ti_Pass)

            var email = tiCorreo.text.toString()
            var pass1 = tiPass.text.toString()

            if(email.isNotEmpty() and pass1.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,pass1).addOnCompleteListener{
                    if(it.isSuccessful){
                        var home = Intent (this, HomeActivity::class.java)
                        startActivity(home)
                    } else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}