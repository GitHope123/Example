package com.example.example.ui.Tutor

import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
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
    private lateinit var tutorAdapter: TutorAdapter
    private val selectedProfesores = mutableSetOf<String>() // Using IDs to track selection
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutor)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSeleccionarTutor)
        searchView = findViewById(R.id.searchViewTutorAdd)
        buttonAceptar = findViewById(R.id.buttonAceptarTutor)
        buttonCancelar = findViewById(R.id.buttonCancelarTutor)

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

        val batch = db.batch()
        selectedProfesores.forEach { profesorId ->
            val profesorRef = db.collection("Profesor").document(profesorId)
            batch.update(profesorRef, "tutor", true)
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
}
