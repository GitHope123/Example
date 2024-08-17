package com.example.example.ui.Profesor

import java.io.Serializable

data class Profesor(
    var idProfesor: String? = null, // Cambiado a String? para permitir nulos
    val nombres: String = "",
    val apellidos: String = "",
    val domicilio: String = "",
    val celular: String = "",
    val materia: String = "",
    val correo: String = ""
) : Serializable
