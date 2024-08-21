package com.example.example.ui.Profesor

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Profesor(
    @DocumentId var idProfesor: String? = null, // ID del profesor (documento en Firestore)
    var nombres: String = "",       // Nombre(s) del profesor
    var apellidos: String = "",     // Apellido(s) del profesor
    var celular: Long = 0,          // Número de teléfono del profesor
    var materia: String = "",       // Materia que enseña el profesor
    var correo: String = "",        // Correo electrónico del profesor
    var tutor: Boolean = false,     // Indica si el profesor es tutor
    var grado: Long = 0,
    var seccion: String = ""        // Asegúrate de que todos los campos tengan valores predeterminados
) : Serializable
