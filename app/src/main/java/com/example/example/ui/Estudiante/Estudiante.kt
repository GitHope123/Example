package com.example.example.ui.Estudiante

import java.io.Serializable

data class Estudiante(
    val id: String = "",
    val apellidos: String = "",
    val celularApoderado: Long = 0,
    val dni: Long = 0,
    val grado: Int = 0,
    val nombres: String = "",
    val seccion: String = ""
):Serializable

