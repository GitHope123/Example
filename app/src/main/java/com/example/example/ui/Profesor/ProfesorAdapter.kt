package com.example.example.ui.Profesor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import org.w3c.dom.Text

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
        private val textViewNombreCompleto: TextView = itemView.findViewById(R.id.textViewNombreCompletos)
        private val textViewTelefono: TextView = itemView.findViewById(R.id.textViewTelefono)
        private val textViewDomicilio: TextView = itemView.findViewById(R.id.textViewDomicilio)

        fun bind(profesor: Profesor) {
            textViewNombreCompleto.text = profesor.nombres
            textViewTelefono.text= profesor.celular
            textViewDomicilio.text=profesor.domicilio
        }
    }
}