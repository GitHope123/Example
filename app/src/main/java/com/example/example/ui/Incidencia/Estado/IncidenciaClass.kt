package com.example.example.ui.Incidencia.Estado

import java.io.Serializable

data class IncidenciaClass(
    val id: String = "",
    val fecha: String = "",
    val hora: String = "",
    val nombreEstudiante: String = "",
    val apellidoEstudiante: String = "",
    val tipo: String = "",
    val gravedad: String = "",
    val estado: String = "",
    val detalle: String = ""
) : Serializable

