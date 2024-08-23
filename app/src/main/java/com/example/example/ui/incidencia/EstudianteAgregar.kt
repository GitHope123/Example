package com.example.example.ui.incidencia
import java.io.Serializable

data class EstudianteAgregar(
    val id: String="",
    val nombres: String = "",
    val apellidos: String = "",
    val grado: Int = 0,
    val seccion: String = ""
):Serializable

