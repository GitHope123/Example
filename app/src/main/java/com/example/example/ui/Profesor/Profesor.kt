package com.example.example.ui.Profesor

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

data class Profesor(
    val idProfesor: DocumentReference? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val domicilio: String = "",
    val celular: String = "",
    val materia: String = "",
    val correo: String = ""
) : Serializable
