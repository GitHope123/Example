package com.example.example.ui.Incidencia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R

class AgregarEstudianteIncidencia : AppCompatActivity() {
    private lateinit var btnIrRegistrar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_estudiante)
        init()
        initButton()
    }
    private fun init(){
        btnIrRegistrar= findViewById(R.id.btnIrRegistrar)
    }
    private fun initButton(){
        btnIrRegistrar.setOnClickListener {
            val intent= Intent(this, AgregarIncidencia::class.java)
            startActivity(intent) }
    }
}