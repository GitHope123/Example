package com.example.example.ui.Estudiante

import java.io.Serializable

data class Estudiante(
    val estudiante: String = "",
    val grado: Int = 0,
    val id: String = "",
    val seccion: String = ""
):Serializable

