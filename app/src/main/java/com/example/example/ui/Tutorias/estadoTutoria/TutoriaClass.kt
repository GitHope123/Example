package com.example.example.ui.Tutorias.estadotutoria

import java.io.Serializable

data class TutoriaClass(
    val id: String="",
    val fecha:String="",
    val hora:String="",
    val nombreEstudiante:String="",
    val apellidoEstudiante:String="",
    val grado:Int=0,
    val seccion:String="",
    val tipo:String="",
    val gravedad:String="",
    val estado:String="",
    val detalle:String="",
    val imageUri:String="",
    val apellidoProfesor:String="",
    val nombreProfesor:String=""
):Serializable
