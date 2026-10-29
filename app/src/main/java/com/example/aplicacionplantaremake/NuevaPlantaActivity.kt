package com.example.aplicacionplantaremake

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class NuevaPlantaActivity : AppCompatActivity() {

    // Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var referencia: DatabaseReference

    // Vistas
    private lateinit var btnCapturarFoto: Button
    private lateinit var btnGuardar: Button
    private lateinit var ivFoto: ImageView
    private lateinit var itNombre: EditText
    private lateinit var itDescripcion: EditText

    // Variable para la foto
    private var imagenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_planta)

        // Enlazar Vistas
        btnCapturarFoto = findViewById(R.id.btn_capturar_foto)
        btnGuardar = findViewById(R.id.btn_guardar)
        ivFoto = findViewById(R.id.iv_foto)
        itNombre = findViewById(R.id.it_nombre)
        itDescripcion = findViewById(R.id.it_descripcion)

        // Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val user = firebaseAuth.currentUser
        if (user != null) {
            referencia = database.getReference("usuarios/${user.uid}")
        } else {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnGuardar.isEnabled = false

        // Botones
        btnCapturarFoto.setOnClickListener {
            capturarImagen.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                iniciarCargarArchivo()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private val capturarImagen = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val data = resultado.data
            imagenBitmap = data?.extras?.get("data") as? Bitmap
            if (imagenBitmap != null) {
                ivFoto.setImageBitmap(imagenBitmap)
                btnGuardar.isEnabled = true
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (itNombre.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor, escribe un nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if (itDescripcion.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor, escribe una descripción", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imagenBitmap == null) {
            Toast.makeText(this, "Por favor, toma una foto", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun iniciarCargarArchivo() {
        btnGuardar.isEnabled = false
        Toast.makeText(this, "Guardando planta...", Toast.LENGTH_SHORT).show()

        // 1. Convertir la imagen (Bitmap) a un arreglo de bytes
        val bytesSalida = ByteArrayOutputStream()
        // Usamos calidad 50 para que el texto no sea tan inmenso en la base de datos
        imagenBitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bytesSalida)
        val imageBytes = bytesSalida.toByteArray()

        // 2. Convertir los bytes a texto (Base64)
        val fotoEnTexto = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        // 3. Crear el ID y el objeto Planta
        val idPlanta = referencia.child("plantas").push().key ?: "id_default"
        val planta = Planta(
            id = idPlanta,
            nombre = itNombre.text.toString().trim(),
            descripcion = itDescripcion.text.toString().trim(),
            foto = fotoEnTexto // Guardamos el texto gigante en lugar del link
        )

        // 4. Guardar en Realtime Database
        referencia.child("plantas").child(idPlanta).setValue(planta).addOnCompleteListener {
            Toast.makeText(this, "¡Planta guardada con éxito!", Toast.LENGTH_SHORT).show()
            finish() // Cierra la pantalla y regresa al Home
        }.addOnFailureListener { error ->
            btnGuardar.isEnabled = true
            Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }
}