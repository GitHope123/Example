package com.example.example.ui.tutores

import androidx.recyclerview.widget.DiffUtil
import com.example.example.ui.profesores.Profesor

class ProfesorDiffCallback : DiffUtil.ItemCallback<Profesor>() {
    override fun areItemsTheSame(oldItem: Profesor, newItem: Profesor): Boolean =
        oldItem.idProfesor == newItem.idProfesor

    override fun areContentsTheSame(oldItem: Profesor, newItem: Profesor): Boolean =
        oldItem == newItem
}
