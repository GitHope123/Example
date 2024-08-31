package com.example.example.ui.estudiantes

import java.io.Serializable

    data class Estudiante(
    val idEstudiante: String = "",
    val apellidos: String = "",
    val nombres: String = "",
    val grado: Int = 0,
    val seccion: String = "",
    val cantidadIncidencias:Int=0
):Serializable
