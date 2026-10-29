package com.example.aplicacionplantaremake

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorPlanta(
    private var plantaLista: List<Planta>,
    private val alEditar: (planta: Planta) -> Unit,
    private val alEliminar: (id: String) -> Unit
) : RecyclerView.Adapter<AdaptadorPlanta.ContenidoViewHolder>() {

    fun setData(nuevaLista: List<Planta>) {
        this.plantaLista = nuevaLista
        notifyDataSetChanged()
    }

    class ContenidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombre)
        val descripcion: TextView = view.findViewById(R.id.descripcion)
        val id: TextView = view.findViewById(R.id.id)
        val foto: ImageView = view.findViewById(R.id.foto)
        val btnEditar: Button = view.findViewById(R.id.btn_editar_R)
        val btnEliminar: Button = view.findViewById(R.id.btn_eliminar_R)

        fun render(item: Planta, alEditar: (planta: Planta) -> Unit, alEliminar: (id: String) -> Unit) {
            nombre.text = item.nombre
            descripcion.text = item.descripcion
            id.text = item.id

            // Aquí hacemos la magia: Convertir el texto Base64 de vuelta a Imagen
            try {
                if (!item.foto.isNullOrEmpty()) {
                    // Decodificamos el texto a bytes
                    val imageBytes = Base64.decode(item.foto, Base64.DEFAULT)
                    // Reconstruimos la imagen con los bytes
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    foto.setImageBitmap(decodedImage)
                } else {
                    // Si no hay foto, pone el icono de Android
                    foto.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } catch (e: Exception) {
                // Si ocurre algún error al decodificar, pone el icono por defecto para que no crashee
                foto.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Asignamos clics
            btnEditar.setOnClickListener { alEditar(item) }
            btnEliminar.setOnClickListener {
                item.id?.let { idPlanta -> alEliminar(idPlanta) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContenidoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ContenidoViewHolder(layoutInflater.inflate(R.layout.recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: ContenidoViewHolder, position: Int) {
        val item = plantaLista[position]
        holder.render(item, alEditar, alEliminar)
    }

    override fun getItemCount(): Int {
        return plantaLista.size
    }
}