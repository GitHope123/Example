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
    private var fullList: List<Profesor>,  // Lista original de todos los profesores
    private val onEditClickListener: (Profesor) -> Unit,
    private val isButtonVisible: Boolean,
    private val istextViewGradosSeccionVisible: Boolean
) : RecyclerView.Adapter<TutorAdapter.ProfesorViewHolder>() {

    var listaProfesores: List<Profesor> = fullList // Lista actual que se muestra
    private val selectedProfesores = mutableSetOf<String>() // Track selection by ID

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewTutorNombreCompleto)
        private val textViewCelular: TextView = itemView.findViewById(R.id.textViewTutorCelular)
        private val textViewCorreo: TextView = itemView.findViewById(R.id.textViewTutorCorreo)
        private val imageButtonSeleccionar: ImageButton = itemView.findViewById(R.id.imageButtonSeleccionar)
        private val textViewGradosSeccionTutor: TextView = itemView.findViewById(R.id.textViewGradosSeccionTutor)

        fun bind(profesor: Profesor) {
            textViewNombre.text = "${profesor.nombres} ${profesor.apellidos}"
            textViewCelular.text = profesor.celular.toString()
            textViewCorreo.text = profesor.correo
            textViewGradosSeccionTutor.text = "${profesor.grado} ${profesor.seccion}"
            imageButtonSeleccionar.visibility = if (isButtonVisible) View.VISIBLE else View.GONE
            textViewGradosSeccionTutor.visibility = if (istextViewGradosSeccionVisible) View.VISIBLE else View.GONE
            val idProfesor = profesor.idProfesor ?: return
            val isSelected = selectedProfesores.contains(idProfesor)
            imageButtonSeleccionar.setColorFilter(if (isSelected) Color.YELLOW else Color.GRAY)

            imageButtonSeleccionar.setOnClickListener {
                if (isSelected) {
                    selectedProfesores.remove(idProfesor)
                } else {
                    selectedProfesores.add(idProfesor)
                }
                notifyItemChanged(adapterPosition) // Actualizar solo el ítem cambiado
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
        val queryLowerCase = query?.lowercase().orEmpty() // Evita nulos y maneja la conversión de una vez

        val filteredList = if (queryLowerCase.isNotEmpty()) {
            fullList.filter { profesor ->
                profesor.nombres.lowercase().contains(queryLowerCase) ||
                        profesor.celular.toString().contains(queryLowerCase) ||
                        profesor.correo.lowercase().contains(queryLowerCase)
            }
        } else {
            fullList // Usamos la lista completa si la consulta está vacía
        }

        updateList(filteredList)
    }

    fun resetList() {
        updateList(fullList) // Restablecemos la lista completa
    }

    fun getSelectedProfesores(): Set<String> = selectedProfesores
}
