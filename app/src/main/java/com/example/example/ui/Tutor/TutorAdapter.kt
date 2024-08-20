package com.example.example.ui.Tutor

import ProfesorDiffCallback
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor

class TutorAdapter(
    var listaProfesores: List<Profesor>,
    private val onEditClickListener: (Profesor) -> Unit,
    private val isButtonVisible: Boolean
) : RecyclerView.Adapter<TutorAdapter.ProfesorViewHolder>() {

    private val selectedProfesores = mutableSetOf<String>() // Track selection by ID

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewTutorNombreCompleto)
        private val textViewCelular: TextView = itemView.findViewById(R.id.textViewTutorCelular)
        private val textViewCorreo: TextView = itemView.findViewById(R.id.textViewTutorCorreo)
        private val imageButtonSeleccionar: ImageButton = itemView.findViewById(R.id.imageButtonSeleccionar)

        fun bind(profesor: Profesor) {
            textViewNombre.text = "${profesor.nombres} ${profesor.apellidos}"
            textViewCelular.text = profesor.celular.toString()
            textViewCorreo.text = profesor.correo
            imageButtonSeleccionar.visibility = if (isButtonVisible) View.VISIBLE else View.GONE

            val idProfesor = profesor.idProfesor ?: return
            val isSelected = selectedProfesores.contains(idProfesor)
            imageButtonSeleccionar.setColorFilter(if (isSelected) Color.YELLOW else Color.GRAY)

            imageButtonSeleccionar.setOnClickListener {
                if (isSelected) {
                    selectedProfesores.remove(idProfesor)
                } else {
                    selectedProfesores.add(idProfesor)
                }
                notifyItemChanged(adapterPosition) // Update only the changed item
                onEditClickListener(profesor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        holder.bind(listaProfesores[position])
    }

    override fun getItemCount(): Int = listaProfesores.size

    fun updateList(newList: List<Profesor>) {
        val diffCallback = ProfesorDiffCallback(listaProfesores, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listaProfesores = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterList(query: String?) {
        val filteredList = listaProfesores.filter { profesor ->
            val queryLowerCase = query?.lowercase() ?: ""
            profesor.nombres.lowercase().contains(queryLowerCase) ||
                    profesor.celular.toString().contains(queryLowerCase) ||
                    profesor.correo.lowercase().contains(queryLowerCase)
        }
        updateList(filteredList)
    }

    fun getSelectedProfesores(): Set<String> = selectedProfesores
}
