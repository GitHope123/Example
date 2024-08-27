package com.example.example.ui.Tutorias

import java.io.Serializable

data class TutoriaClass(
    val id: String = "",
    val apellidoEstudiante: String = "",
    val apellidoProfesor: String = "",
    val detalle: String = "",
    val estado: String = "",
    val fecha: String = "",
    val grado: Int = 0,
    val gravedad: String = "",
    val hora: String = "",
    val nombreEstudiante: String = "",
    val nombreProfesor: String = "",
    val seccion: String = "",
    val tipo: String = "",
    val urlImagen: String = ""
):Serializable
