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
        updateGrado()


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

    private fun updateGrado() {
        grados = arrayListOf("1", "2", "3", "4", "5")
        val adapterGrados = ArrayAdapter(this, R.layout.spinner_item_selected, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterGrados
        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gradoSeleccionado = spinnerGrado.selectedItem.toString()
                updateSecciones(gradoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSecciones(gradoSeleccionado: String) {
        secciones = if (gradoSeleccionado == "1") {
            arrayListOf("A", "B", "C", "D", "E")
        } else {
            arrayListOf("A", "B", "C", "D")
        }

        val adapterSecciones = ArrayAdapter(this, R.layout.spinner_item_selected, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter = adapterSecciones
        spinnerSeccion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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