package com.example.example.ui.profesores

import java.io.Serializable


data class Profesor(
    var idProfesor: String? = null, // ID del profesor (documento en Firestore)
    var nombres: String = "",       // Nombre(s) del profesor
    var apellidos: String = "",     // Apellido(s) del profesor
    var celular: Long? = null,      // Número de teléfono del profesor (puede ser null)
    var materia: String = "",       // Materia que enseña el profesor
    var correo: String = "",        // Correo electrónico del profesor
    var tutor: Boolean = false,     // Indica si el profesor es tutor
    var grado: Long? = null,        // Grado del profesor (puede ser null)
    var seccion: String = "",        // Sección del profesor (puede ser null)
    var password:String=""
) :Serializable
