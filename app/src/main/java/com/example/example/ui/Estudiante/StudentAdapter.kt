package com.example.example.ui.Estudiante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.example.databinding.ItemStudentBinding

class StudentAdapter(
    private val estudiantes: MutableList<Estudiante>,
    private val onEditClick: (Estudiante) -> Unit // Callback para manejar el clic en el botón de edición
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(private val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(estudiante: Estudiante) {
            binding.studentNameTextView.text = estudiante.estudiante // Cambié "estudiante" por "nombre"
            binding.studentIdTextView.text = estudiante.id
            binding.studentGradeTextView.text = estudiante.grado.toString()
            binding.studentSectionTextView.text = estudiante.seccion

            // Manejo del clic en el botón de edición
            binding.editStudents.setOnClickListener {
                onEditClick(estudiante)
            }
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

