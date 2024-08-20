package com.example.example.ui.Incidencia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class AgregarEstudiantes : AppCompatActivity() {
    private lateinit var btnIrRegistrar: Button
    private lateinit var searchViewEstudiante: SearchView
    private lateinit var recyclerViewEstudiantes: RecyclerView
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val estudianteList = mutableListOf<EstudianteAgregar>()
    private val filterEstudianteList = mutableListOf<EstudianteAgregar>()
    private lateinit var estudianteAdapter: EstudianteAgregarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_estudiantes)
        init()
        initButton()
        setupRecyclerView()
        fetchEstudiantes()
        setupSearchView()
    }

    private fun init() {
        btnIrRegistrar = findViewById(R.id.btnIrRegistrar)
        recyclerViewEstudiantes = findViewById(R.id.recyclerViewEstudiantes)
        searchViewEstudiante = findViewById(R.id.searchViewEstudiante)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)

    }

    private fun initButton() {
        btnIrRegistrar.setOnClickListener {
            val intent = Intent(this, AgregarIncidencia::class.java)
            startActivity(intent)
        }
        val grados = arrayOf(1, 2, 3, 4, 5)
        val adapterGrados = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterGrados

        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateSecciones(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected
            }
        }

    }

    private fun updateSecciones(gradoPosition: Int){
        val secciones= when(gradoPosition){
            0-> arrayOf("A","B", "C", "D","E")
            else-> arrayOf("A","B", "C", "D")
        }
        val adapterSecciones = ArrayAdapter(this, android.R.layout.simple_spinner_item, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter = adapterSecciones
    }
    private fun setupRecyclerView() {
        estudianteAdapter = EstudianteAgregarAdapter(filterEstudianteList)
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
                filterEstudianteList.addAll(estudianteList)
                estudianteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Manejo de errores
            }
    }

    private fun setupSearchView() {
        searchViewEstudiante.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterEstudiante(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterEstudiante(it) }
                return true
            }
        })
    }

    private fun filterEstudiante(query: String) {
        val queryLower = query.toLowerCase()
        val queryWords = queryLower.split("\\s+".toRegex())

        val filteredList = estudianteList.filter { estudiante ->
            val nombreCompleto = "${estudiante.nombres} ${estudiante.apellidos}".toLowerCase()
            queryWords.all { queryWord -> nombreCompleto.contains(queryWord) }
        }

        filterEstudianteList.clear()
        filterEstudianteList.addAll(filteredList)
        estudianteAdapter.notifyDataSetChanged()
    }

    fun refreshData() {
        fetchEstudiantes()
    }

    override fun onResume() {
        super.onResume()
        refreshData() // Refresh data when the fragment becomes visible
    }

}