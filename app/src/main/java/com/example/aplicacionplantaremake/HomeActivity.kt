package com.example.aplicacionplantaremake

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    private var fireBaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var referencia = database.getReference("usuarios/"+fireBaseAuth.currentUser!!.uid.toString()
            +"/plantas");

    val  plantaLista= arrayListOf<Planta>()

    private lateinit var buttonCerraSesion: FloatingActionButton
    private lateinit var button: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private var AdaptadorP: AdaptadorPlanta?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        buttonCerraSesion=findViewById(R.id.btn_cerrar)
        recyclerView=findViewById(R.id.recycler_home)

        buttonCerraSesion.setOnClickListener{
            try {
                FirebaseAuth.getInstance().signOut()
                val actividad = Intent(this,MainActivity::class.java)
                startActivity(actividad)
            }catch (e:Exception){
                e.printStackTrace();
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

        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
                val errorMessage=error.message
                Log.e("FirebaseErorr","DatabaseError: $errorMessage")
            }
        })
    }

    fun iniciarRecycler(){
        recyclerView.layoutManager= LinearLayoutManager(this)
        AdaptadorP=AdaptadorPlanta(plantaLista,{planta->editarPlanta(planta)},{id->eliminarPlanta(id)})
        recyclerView.adapter=AdaptadorP;
    }

    fun eliminarPlanta(id: String){
        val plantaRef=referencia.child(id)
        val remover=plantaRef.removeValue()
        remover.addOnCompleteListener{
            Toast.makeText(this,"La planta ha sido removida", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { error->
            Toast.makeText(this,"Error al intentar eliminar ${error.message} ",
                Toast.LENGTH_SHORT).show()
        }
    }

    fun editarPlanta(planta: Planta) {
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
            e.printStackTrace()
            Toast.makeText(this, "Error al intentar editar la planta", Toast.LENGTH_SHORT).show()
        }
    }

}