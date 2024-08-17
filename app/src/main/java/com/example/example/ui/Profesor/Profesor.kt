package com.example.example.ui.Profesor

import java.io.Serializable

// Definición de la clase Profesor con propiedades
data class Profesor(
    val apellidos: String,
    val nombres: String,
    val domicilio: String,
    val celular: String,
    val materia: String,
    val seccion: String,
    val grado: String
):Serializable
