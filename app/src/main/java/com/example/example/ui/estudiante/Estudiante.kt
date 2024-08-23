package com.example.example.ui.estudiante

import java.io.Serializable

    data class Estudiante(
    val idEstudiante: String = "",
    val apellidos: String = "",
    val nombres: String = "",
    val celularApoderado: Long = 0,
    val dni: Long = 0,
    val grado: Int = 0,
    val seccion: String = "",
    val cantidadIncidencias:Int=0
):Serializable
