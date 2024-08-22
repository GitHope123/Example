package com.example.example.ui.Tutor

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor

class TutorAdapter(
    private val onEditClickListener: (Profesor) -> Unit,
    private val isButtonVisible: Boolean,
    private val isTextViewGradosSeccionVisible: Boolean
) : ListAdapter<Profesor, TutorAdapter.ProfesorViewHolder>(ProfesorDiffCallback()) {

    private var selectedProfesorId: String? = null
    private var fullList: List<Profesor> = emptyList() // List that stores the complete list of tutors
    private var filteredList: List<Profesor> = emptyList() // List that stores the filtered results

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
            textViewGradosSeccionTutor.visibility = if (isTextViewGradosSeccionVisible) View.VISIBLE else View.GONE

            val idProfesor = profesor.idProfesor ?: return
            val isSelected = idProfesor == selectedProfesorId
            imageButtonSeleccionar.setColorFilter(if (isSelected) Color.YELLOW else Color.GRAY)

            itemView.setOnClickListener {
                toggleSelection(idProfesor)
                onEditClickListener(profesor)
            }

            imageButtonSeleccionar.setOnClickListener {
                toggleSelection(idProfesor)
                onEditClickListener(profesor)
            }
        }

        private fun toggleSelection(idProfesor: String) {
            selectedProfesorId = if (selectedProfesorId == idProfesor) null else idProfesor
            notifyItemChanged(adapterPosition) // Update only the current item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Updates the complete list of tutors and the filtered list
    fun updateList(newList: List<Profesor>) {
        fullList = newList
        filteredList = newList
        submitList(filteredList)
    }

    // Resets the filtered list to the complete list
    fun resetList() {
        filteredList = fullList
        submitList(filteredList)
    }

    // Filters the list based on the search query
    fun filterList(query: String) {
        filteredList = fullList.filter { profesor ->
            val fullName = "${profesor.nombres} ${profesor.apellidos}".lowercase()
            fullName.contains(query.lowercase())
        }
        submitList(filteredList)
    }
}
