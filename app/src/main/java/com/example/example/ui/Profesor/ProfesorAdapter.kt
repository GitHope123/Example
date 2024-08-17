package com.example.example.ui.Profesor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class ProfesorAdapter(
    private val profesores: List<Profesor>
) : RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.bind(profesor)
    }

    override fun getItemCount(): Int {
        return profesores.size
    }

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombreCompletos: TextView = itemView.findViewById(R.id.textViewNombreCompletos)
        private val textViewTelefono: TextView = itemView.findViewById(R.id.textViewTelefono)
        private val textViewDomicilio: TextView = itemView.findViewById(R.id.textViewDomicilio)
        private val textViewMateria: TextView = itemView.findViewById(R.id.textViewMateria)
        private val textViewGrado: TextView = itemView.findViewById(R.id.textViewGrado)
        private val textViewSeccion: TextView = itemView.findViewById(R.id.textViewSeccion)
        private val imageButtonEdit: ImageButton = itemView.findViewById(R.id.imageButtonEdit)

        fun bind(profesor: Profesor) {
            // Concatenate first and last names
            val nombreCompleto = "${profesor.nombres} ${profesor.apellidos}"
            textViewNombreCompletos.text = nombreCompleto
            textViewTelefono.text = profesor.celular
            textViewDomicilio.text = profesor.domicilio
            textViewMateria.text = profesor.materia
            textViewGrado.text = "Grado: ${profesor.grado}"
            textViewSeccion.text = "Sección: ${profesor.seccion}"

            imageButtonEdit.setOnClickListener {
                // Handle edit action for the professor
            }
        }
    }
}

