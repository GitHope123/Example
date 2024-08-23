package com.example.example.ui.tutor

import androidx.recyclerview.widget.DiffUtil
import com.example.example.ui.profesor.Profesor

class ProfesorDiffCallback : DiffUtil.ItemCallback<Profesor>() {
    override fun areItemsTheSame(oldItem: Profesor, newItem: Profesor): Boolean =
        oldItem.idProfesor == newItem.idProfesor

    override fun areContentsTheSame(oldItem: Profesor, newItem: Profesor): Boolean =
        oldItem == newItem
}
