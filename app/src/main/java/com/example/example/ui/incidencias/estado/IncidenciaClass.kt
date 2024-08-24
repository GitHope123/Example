package com.example.example.ui.incidencias.estado

import java.io.Serializable

data class IncidenciaClass(
    val id: String = "",
    val fecha: String = "",
    val hora: String = "",
    val nombreEstudiante: String = "",
    val apellidoEstudiante: String = "",
    val grado: Int = 0,
    val seccion: String = "",
    val tipo: String = "",
    val gravedad: String = "",
    val estado: String = "",
    val detalle: String = "",
    val imageUri: String=""
) : Serializable

