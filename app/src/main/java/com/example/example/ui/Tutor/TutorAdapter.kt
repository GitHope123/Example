package com.example.example.ui.Tutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor

class TutorAdapter(
    private val profesores: List<Profesor>,
    private val onItemClick: (Profesor) -> Unit
) : RecyclerView.Adapter<TutorAdapter.TutorViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private val selectedProfesores = mutableSetOf<Profesor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return TutorViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.bind(profesor, position)
    }

    override fun getItemCount(): Int = profesores.size

    inner class TutorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombreCompleto: TextView = itemView.findViewById(R.id.textViewNombreCompletos)
        private val textViewTelefono: TextView = itemView.findViewById(R.id.textViewTelefono)
        private val textViewCorreo: TextView = itemView.findViewById(R.id.textViewCorreo)
        private val imageButtonSeleccionar: ImageButton = itemView.findViewById(R.id.imageButtonSeleccionar)

        fun bind(profesor: Profesor, position: Int) {
            val nombreCompleto = "${profesor.nombres} ${profesor.apellidos}"
            textViewNombreCompleto.text = nombreCompleto
            textViewTelefono.text = profesor.celular
            textViewCorreo.text = profesor.correo

            // Update item view based on selection
            val isSelected = selectedProfesores.contains(profesor)
            itemView.setBackgroundColor(
                if (isSelected) ContextCompat.getColor(itemView.context, R.color.Primary_blue_sky)
                else ContextCompat.getColor(itemView.context, R.color.Transparent_color)
            )
            imageButtonSeleccionar.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                if (selectedPosition == position) {
                    // Deselect item if it's already selected
                    selectedProfesores.remove(profesor)
                    selectedPosition = RecyclerView.NO_POSITION
                } else {
                    // Select new item
                    selectedProfesores.add(profesor)
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                }
                notifyItemChanged(selectedPosition)
                onItemClick(profesor)
            }

            imageButtonSeleccionar.setOnClickListener {
                // Handle select button click if needed
                // For example, open details or perform other actions
            }
        }
    }

    fun getSelectedProfesores(): List<Profesor> = selectedProfesores.toList()
}
