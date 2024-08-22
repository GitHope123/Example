package com.example.example.ui.Tutor

import androidx.recyclerview.widget.DiffUtil
import com.example.example.ui.Profesor.Profesor

class ProfesorDiffCallback : DiffUtil.ItemCallback<Profesor>() {
    override fun areItemsTheSame(oldItem: Profesor, newItem: Profesor): Boolean =
        oldItem.idProfesor == newItem.idProfesor

    override fun areContentsTheSame(oldItem: Profesor, newItem: Profesor): Boolean =
        oldItem == newItem
}
