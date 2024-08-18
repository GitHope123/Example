package com.example.example.ui.Profesor

import java.io.Serializable

data class Profesor(
    var idProfesor: String? = null, // ID del profesor (documento en Firestore)
    val nombres: String = "",       // Nombre(s) del profesor
    val apellidos: String = "",     // Apellido(s) del profesor
    val celular: String = "",       // Número de teléfono del profesor
    val materia: String = "",       // Materia que enseña el profesor
    val correo: String = "",        // Correo electrónico del profesor
    val tutor: Boolean = false      // Indica si el profesor es tutor
) : Serializable
