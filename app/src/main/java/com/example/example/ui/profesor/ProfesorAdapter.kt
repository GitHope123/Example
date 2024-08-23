package com.example.example.ui.profesor

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class ProfesorAdapter(
    private val profesores: List<Profesor>, private val onEditClickListener: (Profesor) -> Unit
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

    override fun getItemCount(): Int = profesores.size

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombreCompleto: TextView =
            itemView.findViewById(R.id.textViewNombreCompletos)
        private val textViewTelefono: TextView = itemView.findViewById(R.id.textViewTelefono)
        private val textViewCorreo: TextView = itemView.findViewById(R.id.textViewCorreo)
        private val editButton: ImageButton = itemView.findViewById(R.id.imageButtonEdit)

        fun bind(profesor: Profesor) {
            // Concatenate first and last names
            val nombreCompleto = "${profesor.apellidos} ${profesor.nombres}"
            textViewNombreCompleto.text = nombreCompleto
            textViewTelefono.text =
                profesor.celular.toString() // Ensure long is converted to string
            textViewCorreo.text = profesor.correo

            // Set the click listener for editing
            editButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditProfesor::class.java).apply {
                    putExtra("idProfesor", profesor.idProfesor)
                    putExtra("nombres", profesor.nombres)
                    putExtra("apellidos", profesor.apellidos)
                    putExtra("celular", profesor.celular)
                    putExtra("materia", profesor.materia)
                    putExtra("correo", profesor.correo)
                }
                context.startActivity(intent)
            }
            itemView.setOnClickListener {
                onEditClickListener(profesor) // Trigger the passed-in listener
            }
        }
    }
}
