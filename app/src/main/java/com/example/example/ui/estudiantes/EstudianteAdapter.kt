package com.example.example.ui.estudiantes

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class EstudianteAdapter(
    private val estudiantes: List<Estudiante>,
    private val onEditClickListenerEstudiante: (Estudiante) -> Unit,
    var isEditButtonVisible: Boolean // Cambio a booleano
) : RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstudianteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return EstudianteViewHolder(view)
    }

    override fun getItemCount(): Int = estudiantes.size

    override fun onBindViewHolder(holder: EstudianteViewHolder, position: Int) {
        val estudiante = estudiantes[position]
        holder.bind(estudiante)
    }

    inner class EstudianteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtViewNombres: TextView = itemView.findViewById(R.id.textViewNombreCompleto)
        private val textViewCelular: TextView = itemView.findViewById(R.id.textViewCelularStudent)
        private val textViewGradoAndSection: TextView = itemView.findViewById(R.id.studentGradeTextViewItem)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.imageButtonEditStudent)

        fun bind(estudiante: Estudiante) {
            val completeName = "${estudiante.apellidos} ${estudiante.nombres}"
            val degreeAndSection = "${estudiante.grado} ${estudiante.seccion}"

            txtViewNombres.text = completeName
            textViewCelular.text = estudiante.celularApoderado.toString()
            textViewGradoAndSection.text = degreeAndSection

            // Establece la visibilidad del botón de edición según el parámetro `isEditButtonVisible`
            btnEdit.visibility = if (isEditButtonVisible) View.VISIBLE else View.INVISIBLE

            btnEdit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditEstudiante::class.java).apply {
                    putExtra("idEstudiante", estudiante.idEstudiante)
                    putExtra("nombres", estudiante.nombres)
                    putExtra("apellidos", estudiante.apellidos)
                    putExtra("celularApoderado", estudiante.celularApoderado)
                    putExtra("dni", estudiante.dni)
                    putExtra("grado", estudiante.grado)
                    putExtra("seccion", estudiante.seccion)
                    putExtra("cantidadIncidencias", estudiante.cantidadIncidencias)
                }
                context.startActivity(intent)
            }

            itemView.setOnClickListener {
                onEditClickListenerEstudiante(estudiante)
            }
        }
    }
}
