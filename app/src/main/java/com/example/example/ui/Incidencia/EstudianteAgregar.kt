package com.example.example.ui.Incidencia
import java.io.Serializable

data class EstudianteAgregar(
    val apellidos: String = "",
    val nombres: String = "",
    val grado: Int = 0,
    val seccion: String = ""
):Serializable

