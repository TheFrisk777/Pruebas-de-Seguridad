package com.example.aplicacionplantaremake

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    private var fireBaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference
    private var usuarioId: String? = null
    private var plantasListener: ValueEventListener? = null
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser?.uid != usuarioId) salirAlInicio()
    }

    val  plantaLista= arrayListOf<Planta>()

    private lateinit var buttonCerraSesion: FloatingActionButton
    private lateinit var button: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private var AdaptadorP: AdaptadorPlanta?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = fireBaseAuth.currentUser
        if (user == null) {
            salirAlInicio()
            return
        }
        usuarioId = user.uid
        referencia = database.getReference("usuarios/${user.uid}/plantas")
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        buttonCerraSesion=findViewById(R.id.btn_cerrar)
        recyclerView=findViewById(R.id.recycler_home)

        buttonCerraSesion.setOnClickListener{
            try {
                limpiarDatosDeSesion()
                fireBaseAuth.signOut()
                salirAlInicio()
            }catch (e:Exception){
                Toast.makeText(this, "No se pudo cerrar la sesión. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }

        button=findViewById(R.id.floatingActionButton2)
        button.setOnClickListener{
            var nuevaPlanta= Intent(this,NuevaPlantaActivity::class.java)
            startActivity(nuevaPlanta)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        plantasListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (fireBaseAuth.currentUser?.uid != usuarioId || isFinishing) return
                plantaLista.clear()
                if(snapshot.exists()){
                    for(empSnap in snapshot.children) {
                        val empData=empSnap.getValue(Planta::class.java)
                        plantaLista.add(empData!!)
                        iniciarRecycler();
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "No se pudieron cargar las plantas.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (fireBaseAuth.currentUser?.uid != usuarioId || usuarioId == null) {
            salirAlInicio()
            return
        }
        fireBaseAuth.addAuthStateListener(authListener)
        plantasListener?.let { referencia.addValueEventListener(it) }
    }

    override fun onStop() {
        fireBaseAuth.removeAuthStateListener(authListener)
        if (::referencia.isInitialized) {
            plantasListener?.let { referencia.removeEventListener(it) }
        }
        super.onStop()
    }

    private fun limpiarDatosDeSesion() {
        if (::referencia.isInitialized) {
            plantasListener?.let { referencia.removeEventListener(it) }
        }
        plantaLista.clear()
        AdaptadorP?.notifyDataSetChanged()
        if (::recyclerView.isInitialized) recyclerView.adapter = null
    }

    private fun salirAlInicio() {
        if (isFinishing) return
        limpiarDatosDeSesion()
        val actividad = Intent(this, MainActivity::class.java)
        actividad.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(actividad)
        finish()
    }

    fun iniciarRecycler(){
        recyclerView.layoutManager= LinearLayoutManager(this)
        AdaptadorP=AdaptadorPlanta(plantaLista,{planta->editarPlanta(planta)},{id->eliminarPlanta(id)})
        recyclerView.adapter=AdaptadorP;
    }

    fun eliminarPlanta(id: String){
        if (fireBaseAuth.currentUser?.uid != usuarioId) { salirAlInicio(); return }
        val plantaRef=referencia.child(id)
        val remover=plantaRef.removeValue()
        remover.addOnCompleteListener{
            Toast.makeText(this,"La planta ha sido removida", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { error->
            Toast.makeText(this,"No se pudo eliminar la planta. Inténtalo de nuevo.",
                Toast.LENGTH_SHORT).show()
        }
    }

    fun editarPlanta(planta: Planta) {
        if (fireBaseAuth.currentUser?.uid != usuarioId) { salirAlInicio(); return }
        try {
            // Crear un Intent para abrir la actividad de edición
            val intent = Intent(this, EditarPlantaActivity::class.java)

            // Pasar los datos de la planta seleccionada a través del Intent
            intent.putExtra("id", planta.id)
            intent.putExtra("nombre", planta.nombre)
            intent.putExtra("descripcion", planta.descripcion)
            intent.putExtra("imagenUrl", planta.foto)

            // Iniciar la actividad
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo completar la operación. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Error al intentar editar la planta", Toast.LENGTH_SHORT).show()
        }
    }

}