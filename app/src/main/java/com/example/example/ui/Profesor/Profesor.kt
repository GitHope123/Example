package com.example.example.ui.Profesor

import java.io.Serializable

data class Profesor(
    val nombres: String = "",
    val apellidos: String = "",
    val domicilio: String = "",
    val celular: String = "",
    val materia: String = "",
    val grado: String = "",
    val seccion: String = ""
):Serializable

