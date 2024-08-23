package com.example.example.ui.estudiante

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class EstudianteAdapter(
    private val estudiantes: List<Estudiante>,
    private val onEditClickListenerEstudiante: (Estudiante) -> Unit
) : RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstudianteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return EstudianteViewHolder(view)
    }

    override fun getItemCount(): Int = estudiantes.size

    override fun onBindViewHolder(
        holder: EstudianteViewHolder,
        position: Int
    ) {
        val estudiante = estudiantes[position]
        holder.bind(estudiante)
    }

    inner class EstudianteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtViewNombres: TextView = itemView.findViewById(R.id.textViewNombreCompleto)
        private val textViewCelular: TextView = itemView.findViewById(R.id.textViewCelularStudent)
        private val textViewGradoandSection: TextView =
            itemView.findViewById(R.id.studentGradeTextViewItem)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.imageButtonEditStudent)
        private lateinit var completeName: String
        private lateinit var degreeAndSection: String
        fun bind(estudiante: Estudiante) {
            completeName = "${estudiante.apellidos} ${estudiante.nombres}"
            degreeAndSection = "${estudiante.grado} ${estudiante.seccion}"
            txtViewNombres.text=completeName
            textViewCelular.text=estudiante.celularApoderado.toString()
            textViewGradoandSection.text=degreeAndSection
            btnEdit.setOnClickListener{
                val context= itemView.context
                val intent= Intent(context,EditEstudiante::class.java).apply{
                    putExtra("idEstudiante",estudiante.idEstudiante)
                    putExtra("nombres",estudiante.nombres)
                    putExtra("apellidos",estudiante.apellidos)
                    putExtra("celularApoderado",estudiante.celularApoderado)
                    putExtra("dni",estudiante.dni)
                    putExtra("grado",estudiante.grado)
                    putExtra("seccion",estudiante.seccion)
                    putExtra("cantidadIncidencias",estudiante.cantidadIncidencias)
                }
                context.startActivity(intent)
            }
            itemView.setOnClickListener {
                onEditClickListenerEstudiante(estudiante) // Trigger the passed-in listener
            }
        }

    }

}