package com.example.example.ui.Tutor

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor
import com.google.firebase.firestore.FirebaseFirestore

class AddTutor : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var buttonAceptar: Button
    private lateinit var buttonCancelar: Button
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var tutorAdapter: TutorAdapter
    private val selectedProfesores = mutableSetOf<String>() // Using IDs to track selection
    private val db = FirebaseFirestore.getInstance()

    private val grados = listOf("1", "2", "3", "4", "5") // Grados posibles
    private val secciones = listOf("A", "B", "C", "D", "E") // Secciones posibles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutor)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSeleccionarTutor)
        searchView = findViewById(R.id.searchViewTutorAdd)
        buttonAceptar = findViewById(R.id.buttonAceptarTutor)
        buttonCancelar = findViewById(R.id.buttonCancelarTutor)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        tutorAdapter = TutorAdapter(emptyList(), { profesor ->
            profesor.idProfesor?.let { id ->
                if (selectedProfesores.contains(id)) {
                    selectedProfesores.remove(id)
                } else {
                    selectedProfesores.add(id)
                }
                // Update only the changed item
                tutorAdapter.notifyItemChanged(tutorAdapter.listaProfesores.indexOf(profesor))
            }
        }, isButtonVisible = true)
        recyclerView.adapter = tutorAdapter

        // Fetch professors
        fetchProfesores()

        // Set up Spinners
        setupSpinners()

        // Set up search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                tutorAdapter.filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tutorAdapter.filterList(newText)
                return true
            }
        })

        // Set up button click listeners
        buttonAceptar.setOnClickListener {
            updateSelectedTutors()
        }

        buttonCancelar.setOnClickListener {
            finish()
        }
    }

    private fun fetchProfesores() {
        db.collection("Profesor")
            .whereEqualTo("tutor", false)
            .get()
            .addOnSuccessListener { result ->
                val listaProfesores = result.documents.mapNotNull { document ->
                    document.toObject(Profesor::class.java)
                }
                tutorAdapter.updateList(listaProfesores)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching professors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateSelectedTutors() {
        if (selectedProfesores.isEmpty()) {
            Toast.makeText(this, "No hay tutores seleccionados.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGrado = spinnerGrado.selectedItem.toString().toLong()
        val selectedSeccion = spinnerSeccion.selectedItem.toString()

        // Debug logs
        Log.d("AddTutor", "Selected Grado: $selectedGrado")
        Log.d("AddTutor", "Selected Seccion: $selectedSeccion")
        Log.d("AddTutor", "Selected Profesores: $selectedProfesores")

        val batch = db.batch()
        selectedProfesores.forEach { profesorId ->
            val profesorRef = db.collection("Profesor").document(profesorId)
            batch.update(profesorRef, "tutor", true)
            batch.update(profesorRef, "grado", selectedGrado)
            batch.update(profesorRef, "seccion", selectedSeccion)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Tutores actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Set result to indicate success
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error updating tutors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSpinners() {
        // Generate all combinations of grado and sección
        val allCombinations = grados.flatMap { grado ->
            secciones.map { seccion ->
                "$grado$seccion"
            }
        }

        // Fetch used combinations from Firestore
        db.collection("Profesor")
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                val usedCombinations = result.documents.mapNotNull { document ->
                    // Fetch grado and seccion as appropriate type
                    val grado = document.get("grado")?.toString() ?: ""
                    val seccion = document.get("seccion")?.toString() ?: ""
                    "$grado$seccion"
                }.toSet()

                val availableCombinations = allCombinations - usedCombinations

                // Update spinners with available combinations
                val gradoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
                gradoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = gradoAdapter

                val seccionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, secciones)
                seccionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSeccion.adapter = seccionAdapter

                // Log available combinations
                Log.d("AddTutor", "Available Combinations: $availableCombinations")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching used combinations: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
