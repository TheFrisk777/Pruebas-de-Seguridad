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

class RegistroActivity : AppCompatActivity() {

    private lateinit var  btnCancelar: Button
    private lateinit var  btnRegistrar: Button
    private lateinit var  firebaseAuth: FirebaseAuth

    private lateinit var  Correo: EditText
    private lateinit var  Password: EditText

    private lateinit var  ConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        Correo = findViewById(R.id.ti_Correo)

        Password = findViewById(R.id.ti_Pass)
        btnCancelar = findViewById(R.id.btn_Cancelar)
        ConfirmPassword = findViewById(R.id.ti_ConfirmPass)

        btnCancelar.setOnClickListener {
            val actividad = Intent(this, MainActivity::class.java)
            startActivity(actividad)
        }

        firebaseAuth = FirebaseAuth.getInstance() //Conectamos con firebase

        btnRegistrar = findViewById(R.id.btn_Registrar)
        btnRegistrar.setOnClickListener {
            var email = Correo.text.toString()
            var pass1 = Password.text.toString()
            var pass2 = ConfirmPassword.text.toString()
            try {
                if(email.isNotEmpty() and pass1.isNotEmpty() and pass2.isNotEmpty()){
                    if(pass1 == pass2){
                        firebaseAuth.createUserWithEmailAndPassword(email,pass1)
                            .addOnCompleteListener{
                                if(it.isSuccessful){
                                    val login = Intent(this, LoginActivity::class.java)
                                    startActivity(login)
                                } else{
                                    Toast.makeText(this, it.exception.toString(),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else{
                        Toast.makeText(this, "La contraseña no concuerada", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this, "Faltan valores requeridos", Toast.LENGTH_SHORT).show()
                }
            } catch (e:Exception){
                e.printStackTrace()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}