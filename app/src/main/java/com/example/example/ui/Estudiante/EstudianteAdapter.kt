package com.example.example.ui.Estudiante

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.example.databinding.ItemStudentBinding

class EstudianteAdapter(
    private val estudiantes: MutableList<Estudiante>,
    private val context: Context
) : RecyclerView.Adapter<EstudianteAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(private val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Configura el clic en el botón de edición aquí para que esté disponible para todos los elementos
            binding.imageButtonEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val estudiante = estudiantes[position]
                    val intent = Intent(context, EditEstudiante::class.java).apply {
                        putExtra("idEstudiante", estudiante.id)
                        putExtra("nombres", estudiante.nombres)
                        putExtra("apellidos", estudiante.apellidos)
                        putExtra("celular", estudiante.celularApoderado)
                        putExtra("dni", estudiante.dni)
                        putExtra("grado", estudiante.grado)
                        putExtra("seccion", estudiante.seccion)
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(estudiante: Estudiante) {
            // Configurar los datos del estudiante en la vista
            binding.textViewNombreCompleto.text = "${estudiante.nombres} ${estudiante.apellidos}"
            binding.textViewDni.text = estudiante.dni.toString()
            binding.studentGradeTextView.text = estudiante.grado.toString()
            binding.studentSectionTextView.text = estudiante.seccion
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(estudiantes[position])
    }

    override fun getItemCount(): Int = estudiantes.size

    fun addStudents(newEstudiantes: List<Estudiante>) {
        val startPosition = estudiantes.size
        estudiantes.addAll(newEstudiantes)
        notifyItemRangeInserted(startPosition, newEstudiantes.size)
    }

    fun updateList(newEstudiantes: List<Estudiante>) {
        estudiantes.clear()
        estudiantes.addAll(newEstudiantes)
        notifyDataSetChanged()
    }
}