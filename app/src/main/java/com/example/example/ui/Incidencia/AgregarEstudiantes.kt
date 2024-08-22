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
        updateGrado()
        setupRecyclerView()
        fetchEstudiantes()
        setupSearchView()
    }

    private fun init() {
        recyclerViewEstudiantes = findViewById(R.id.recyclerViewEstudiantes)
        searchViewEstudiante = findViewById(R.id.searchViewEstudiante)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)

    }

    private fun updateGrado() {
        val grados = arrayOf("Todas", "1", "2", "3", "4", "5")
        val adapterGrados = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterGrados

        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val gradoSeleccionado = spinnerGrado.selectedItem.toString()
                updateSecciones(gradoSeleccionado)
                filterEstudiante(searchViewEstudiante.query.toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun updateSecciones(gradoSeleccionado: String) {
        val secciones = if (gradoSeleccionado == "Todas") {
            arrayOf("Todas")
        } else {
            arrayOf("Todas", "A", "B", "C", "D", "E")
        }
        val adapterSecciones = ArrayAdapter(this, android.R.layout.simple_spinner_item, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter = adapterSecciones
        spinnerSeccion.isEnabled = gradoSeleccionado != "Todas"
        spinnerSeccion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                filterEstudiante(searchViewEstudiante.query.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun setupRecyclerView() {
        estudianteAdapter =
            EstudianteAgregarAdapter(filterEstudianteList) { estudianteSeleccionado ->
                val intent = Intent(this, AgregarIncidencia::class.java)
                intent.putExtra("EXTRA_STUDENT_ID", estudianteSeleccionado.id)
                intent.putExtra("EXTRA_STUDENT_NAME", estudianteSeleccionado.nombres)
                intent.putExtra("EXTRA_STUDENT_LAST_NAME", estudianteSeleccionado.apellidos)
                intent.putExtra("EXTRA_STUDENT_GRADE", estudianteSeleccionado.grado)
                intent.putExtra("EXTRA_STUDENT_SECTION", estudianteSeleccionado.seccion)
                startActivity(intent)
            }
        recyclerViewEstudiantes.layoutManager = LinearLayoutManager(this)
        recyclerViewEstudiantes.adapter = estudianteAdapter
    }

    private fun fetchEstudiantes() {
        firestore.collection("Estudiante").get().addOnSuccessListener { result ->
            estudianteList.clear()
            for (document in result) {
                val id = document.id  // Obtener el ID del documento
                val nombres = document.getString("nombres") ?: ""
                val apellidos = document.getString("apellidos") ?: ""
                val grado = document.getLong("grado")?.toInt() ?: 0
                val seccion = document.getString("seccion") ?: ""
                estudianteList.add(EstudianteAgregar(id, nombres, apellidos, grado, seccion))
            }
            filterEstudianteList.clear()
            filterEstudianteList.addAll(estudianteList)
            estudianteAdapter.notifyDataSetChanged()
        }
    }


    private fun setupSearchView() {
        searchViewEstudiante.setOnClickListener {
            searchViewEstudiante.isIconified = false
            searchViewEstudiante.requestFocus()
        }

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
        val gradoSeleccionado = spinnerGrado.selectedItem.toString()
        val seccionSeleccionada = spinnerSeccion.selectedItem.toString()
        val queryWords = query.lowercase().split("\\s+".toRegex())

        filterEstudianteList.clear()
        estudianteList.filterTo(filterEstudianteList) { estudiante ->
            val coincideGrado =
                gradoSeleccionado == "Todas" || estudiante.grado.toString() == gradoSeleccionado
            val coincideSeccion =
                seccionSeleccionada == "Todas" || estudiante.seccion == seccionSeleccionada
            val nombreCompleto = "${estudiante.nombres} ${estudiante.apellidos}".lowercase()
            val coincideNombre = queryWords.all { nombreCompleto.contains(it) }
            coincideNombre && coincideGrado && coincideSeccion
        }

        estudianteAdapter.notifyDataSetChanged()
    }

    fun refreshData() {
        fetchEstudiantes()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

}