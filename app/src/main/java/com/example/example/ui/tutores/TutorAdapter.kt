package com.example.example.ui.tutores

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.profesores.Profesor
import com.google.firebase.firestore.FirebaseFirestore

class TutorAdapter(
    private val onEditClickListener: (Profesor) -> Unit,
    private val onRemoveClickListener: (Profesor) -> Unit, // Callback for removing a tutor
    private val isButtonVisible: Boolean,
    private val isTextViewGradosSeccionVisible: Boolean,
    private val isImageButtonQuitarTutor: Boolean,
    private val ButtonSeleccionar:Boolean
) : ListAdapter<Profesor, TutorAdapter.ProfesorViewHolder>(ProfesorDiffCallback()) {

    private var selectedProfesorId: String? = null
    private var fullList: List<Profesor> = emptyList()
    private var filteredList: List<Profesor> = emptyList()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewTutorNombreCompleto)
        private val textViewCelular: TextView = itemView.findViewById(R.id.textViewTutorCelular)
        private val textViewCorreo: TextView = itemView.findViewById(R.id.textViewTutorCorreo)
        private val imageButtonSeleccionar: ImageButton = itemView.findViewById(R.id.imageButtonSeleccionar)
        private val textViewGradosSeccionTutor: TextView = itemView.findViewById(R.id.textViewGradosSeccionTutor)
        private val imageButtonQuitarTutor: ImageButton = itemView.findViewById(R.id.imageButtonQuitarTutor)
        private val imageButtonLlamadaTutor: ImageView = itemView.findViewById(R.id.imageViewTutorLlamada)

        fun bind(profesor: Profesor) {
            textViewNombre.text = "${profesor.nombres} ${profesor.apellidos}"
            textViewCelular.text = profesor.celular.toString()
            textViewCorreo.text = profesor.correo
            textViewGradosSeccionTutor.text = "${profesor.grado} ${profesor.seccion}"

            imageButtonSeleccionar.visibility = if (isButtonVisible) View.VISIBLE else View.GONE
            textViewGradosSeccionTutor.visibility = if (isTextViewGradosSeccionVisible) View.VISIBLE else View.GONE
            imageButtonQuitarTutor.visibility = if (isImageButtonQuitarTutor) View.VISIBLE else View.GONE
            imageButtonSeleccionar.visibility= if (ButtonSeleccionar) View.VISIBLE else View.GONE


            val idProfesor = profesor.idProfesor ?: return
            val isSelected = idProfesor == selectedProfesorId
            imageButtonSeleccionar.setColorFilter(if (isSelected) Color.YELLOW else Color.GRAY)

            itemView.setOnClickListener {
                toggleSelection(idProfesor)
                onEditClickListener(profesor)
            }

            imageButtonQuitarTutor.setOnClickListener {
                onRemoveClickListener(profesor)
            }


            imageButtonSeleccionar.setOnClickListener {
                toggleSelection(idProfesor)
                onEditClickListener(profesor)
            }
            imageButtonLlamadaTutor.setOnClickListener {
                val phoneNumber = profesor.celular.toString()
                if (phoneNumber.isNotBlank()) {
                    try {
                        val callIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                        }
                        // Usamos el contexto desde itemView
                        itemView.context.startActivity(callIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(itemView.context, "Error al intentar realizar la llamada", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(itemView.context, "Número de teléfono no válido", Toast.LENGTH_SHORT).show()
                }
            }

        }

        private fun toggleSelection(idProfesor: String) {
            selectedProfesorId = if (selectedProfesorId == idProfesor) null else idProfesor
            notifyItemChanged(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateList(newList: List<Profesor>) {
        fullList = newList
        filteredList = newList
        submitList(filteredList)
    }

    fun resetList() {
        filteredList = fullList
        submitList(filteredList)
    }

    fun filterList(query: String) {
        filteredList = fullList.filter { profesor ->
            val fullName = "${profesor.nombres} ${profesor.apellidos}".lowercase()
            fullName.contains(query.lowercase())
        }
        submitList(filteredList)
    }
}
