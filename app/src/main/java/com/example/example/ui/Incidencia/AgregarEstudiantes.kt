package com.example.example.ui.Incidencia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class AgregarEstudiantes : AppCompatActivity() {
    private lateinit var btnIrRegistrar: Button
    private lateinit var recyclerViewEstudiantes: RecyclerView
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val estudianteList = mutableListOf<EstudianteAgregar>()
    private val fullEstudianteList = mutableListOf<EstudianteAgregar>()
    private lateinit var estudianteAdapter: EstudianteAgregarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_estudiantes)
        init()
        initButton()
        setupRecyclerView()
        fetchEstudiantes()
    }

    private fun init() {
        btnIrRegistrar = findViewById(R.id.btnIrRegistrar)
        recyclerViewEstudiantes = findViewById(R.id.recyclerViewEstudiantes)
    }

    private fun initButton() {
        btnIrRegistrar.setOnClickListener {
            val intent = Intent(this, AgregarIncidencia::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        estudianteAdapter = EstudianteAgregarAdapter(estudianteList)
        recyclerViewEstudiantes.layoutManager = LinearLayoutManager(this)
        recyclerViewEstudiantes.adapter = estudianteAdapter
    }
    private fun fetchEstudiantes() {
        firestore.collection("Aula")
            .get()
            .addOnSuccessListener { result ->
                estudianteList.clear()
                for (document in result) {
                    val estudiantes = document["estudiantes"] as? List<Map<String, Any>> ?: continue
                    estudiantes.forEach { estudiante ->
                        val nombres = estudiante["nombres"] as? String ?: ""
                        val apellidos = estudiante["apellidos"] as? String ?: ""
                        val grado = (estudiante["grado"] as? Long)?.toInt() ?: 0
                        val seccion = estudiante["seccion"] as? String ?: ""
                        estudianteList.add(EstudianteAgregar(nombres, apellidos, grado, seccion))
                    }
                }
                estudianteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Manejo de errores
            }
    }

    fun refreshData() {
        fetchEstudiantes()
    }

    override fun onResume() {
        super.onResume()
        refreshData() // Refresh data when the fragment becomes visible
    }

}