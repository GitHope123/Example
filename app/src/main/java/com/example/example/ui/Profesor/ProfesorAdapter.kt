package com.example.example.ui.Profesor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewApellidos: TextView = itemView.findViewById(R.id.textViewApellidos)

        fun bind(profesor: Profesor) {
            textViewNombre.text = profesor.nombres
            textViewApellidos.text = profesor.apellidos
            // Actualiza otros TextViews o Views según los datos del profesor
        }
    }
}
