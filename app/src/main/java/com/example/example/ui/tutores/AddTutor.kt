package com.example.example.ui.tutores

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.profesores.Profesor
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.SearchView

class AddTutor : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var buttonAceptar: Button
    private lateinit var buttonCancelar: Button
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var tutorAdapter: TutorAdapter
    private var selectedProfesorId: String? = null
    private val db = FirebaseFirestore.getInstance()
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var grados: ArrayList<String> = arrayListOf()
    private var secciones: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutor)

        recyclerView = findViewById(R.id.recyclerViewSeleccionarTutor)
        searchView = findViewById(R.id.searchViewTutorAdd)
        buttonAceptar = findViewById(R.id.buttonAceptarTutor)
        buttonCancelar = findViewById(R.id.buttonCancelarTutor)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tutorAdapter = TutorAdapter(
            onEditClickListener = { profesor ->
                profesor.idProfesor?.let { id ->
                    selectedProfesorId = if (selectedProfesorId == id) null else id
                    tutorAdapter.notifyDataSetChanged()
                }
            },
            onRemoveClickListener = { profesor ->
            },
            isButtonVisible = true,
            isTextViewGradosSeccionVisible = false,
            isImageButtonQuitarTutor = false,
            ButtonSeleccionar = true
        )
        recyclerView.adapter = tutorAdapter
        fetchProfesores()
        getGradosYSeccionesAsignados()


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    handleSearchQuery(newText)
                }
                searchHandler.postDelayed(searchRunnable!!, 300)
                return true
            }

            private fun handleSearchQuery(query: String?) {
                val trimmedQuery = query?.trim()?.lowercase().orEmpty()

                if (trimmedQuery.isEmpty()) {
                    tutorAdapter.resetList()
                } else {
                    tutorAdapter.filterList(trimmedQuery)
                }
            }
        })

        buttonAceptar.setOnClickListener {
            updateSelectedTutor()
        }

        buttonCancelar.setOnClickListener {
            finish()
        }
    }

    private fun getGradosYSeccionesAsignados() {
        val gradosYSeccionesAsignados = mutableListOf<Pair<String, String>>()
        val gradosDisponibles = arrayListOf("1", "2", "3", "4", "5")

        db.collection("Profesor")
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val grado = document.getLong("grado").toString()
                    val seccion = document.getString("seccion") ?: ""
                    gradosYSeccionesAsignados.add(Pair(grado, seccion))
                }
                // Definir secciones disponibles por grado
                val seccionesDisponiblesPorGrado = mapOf(
                    "1" to arrayListOf("A", "B", "C", "D", "E"),
                    "2" to arrayListOf("A", "B", "C", "D"),
                    "3" to arrayListOf("A", "B", "C", "D"),
                    "4" to arrayListOf("A", "B", "C", "D"),
                    "5" to arrayListOf("A", "B", "C", "D")
                )

                // Construir lista de grados con secciones disponibles
                val gradosConSeccionesDisponibles = mutableListOf<String>()

                for (grado in gradosDisponibles) {
                    val seccionesAsignadas = gradosYSeccionesAsignados
                        .filter { it.first == grado }
                        .map { it.second }
                    val seccionesDisponibles = seccionesDisponiblesPorGrado[grado] ?: emptyList()

                    // Verificar secciones disponibles para cada grado
                    for (seccion in seccionesDisponibles) {
                        if (!seccionesAsignadas.contains(seccion)) {
                            gradosConSeccionesDisponibles.add("Grado $grado - Sección $seccion")
                        }
                    }
                }

                // Mostrar en un Toast los grados y secciones disponibles
                val mensaje = if (gradosConSeccionesDisponibles.isNotEmpty()) {
                    gradosConSeccionesDisponibles.joinToString(separator = "\n")
                } else {
                    "Todos los grados y secciones están completamente asignados."
                }

                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()


                updateGrado(gradosYSeccionesAsignados)
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreError", "Error getting documents: ", exception)
            }
    }

    private fun updateGrado(gradosYSeccionesAsignados: List<Pair<String, String>>) {
        val gradosDisponibles = arrayListOf("1", "2", "3", "4", "5")

        // Filtrar grados que aún tienen secciones disponibles
        val gradosFiltrados = gradosDisponibles.filter { grado ->
            val seccionesAsignadas = gradosYSeccionesAsignados
                .filter { it.first == grado }
                .map { it.second }
            val seccionesDisponibles = when (grado) {
                "1" -> arrayListOf("A", "B", "C", "D", "E")
                else -> arrayListOf("A", "B", "C", "D")
            }
            seccionesDisponibles.any { !seccionesAsignadas.contains(it) }
        }

        // Configurar el Spinner de grados con los grados disponibles
        val adapterGrados = ArrayAdapter(this, R.layout.spinner_item_selected, gradosFiltrados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterGrados
        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val gradoSeleccionado = spinnerGrado.selectedItem.toString()
                updateSecciones(gradoSeleccionado, gradosYSeccionesAsignados)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSecciones(gradoSeleccionado: String, gradosYSeccionesAsignados: List<Pair<String, String>>) {
        val seccionesDisponibles = when (gradoSeleccionado) {
            "1" -> arrayListOf("A", "B", "C", "D", "E")
            else -> arrayListOf("A", "B", "C", "D")
        }

        // Filtrar secciones disponibles para el grado seleccionado
        val seccionesDisponiblesParaGrado = seccionesDisponibles.filter { seccion ->
            gradosYSeccionesAsignados.none { it.first == gradoSeleccionado && it.second == seccion }
        }

        // Configurar el Spinner de secciones con las secciones disponibles
        val adapterSecciones = ArrayAdapter(this, R.layout.spinner_item_selected, seccionesDisponiblesParaGrado)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter = adapterSecciones
    }

    private fun fetchProfesores() {
        db.collection("Profesor")
            .whereEqualTo("tutor", false)
            .get()
            .addOnSuccessListener { result ->
                val listaProfesores = result.documents.mapNotNull { document ->
                    try {
                        val nombres = document.getString("nombres").orEmpty()
                        val apellidos = document.getString("apellidos").orEmpty()
                        val celular = document.getLong("celular") ?: 0L
                        val correo = document.getString("correo").orEmpty()
                        val grado = document.getLong("grado") ?: 0L
                        val seccion = document.getString("seccion").orEmpty()

                        if (nombres.isNotEmpty() && apellidos.isNotEmpty() && celular > 0 && correo.isNotEmpty()) {
                            Profesor(
                                idProfesor = document.id,
                                nombres = nombres,
                                apellidos = apellidos,
                                celular = celular,
                                correo = correo,
                                grado = grado,
                                seccion = seccion
                            )
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("FetchProfesores", "Error al mapear documento: ${e.message}", e)
                        null
                    }
                }

                if (listaProfesores.isNotEmpty()) {
                    tutorAdapter.updateList(listaProfesores)
                } else {
                    Toast.makeText(this, "No se encontraron profesores válidos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "FetchProfesores",
                    "Error al obtener profesores: ${exception.message}",
                    exception
                )
                Toast.makeText(
                    this,
                    "Error al obtener profesores: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateSelectedTutor() {
        selectedProfesorId?.let { profesorId ->
            val selectedGrado = spinnerGrado.selectedItem.toString().toLongOrNull() ?: 0L
            val selectedSeccion = spinnerSeccion.selectedItem.toString()

            db.collection("Profesor")
                .whereEqualTo("grado", selectedGrado)
                .whereEqualTo("seccion", selectedSeccion)
                .whereEqualTo("tutor", true)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        val profesorRef = db.collection("Profesor").document(profesorId)
                        val batch = db.batch()
                        batch.update(profesorRef, "tutor", true)
                        batch.update(profesorRef, "grado", selectedGrado)
                        batch.update(profesorRef, "seccion", selectedSeccion)

                        batch.commit()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Tutor actualizado exitosamente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(RESULT_OK)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Error al actualizar tutor: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "La combinación de grado y sección ya está en uso.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        "Error al verificar disponibilidad: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: Toast.makeText(this, "No hay tutor seleccionado.", Toast.LENGTH_SHORT).show()
    }


}