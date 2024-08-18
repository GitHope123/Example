package com.example.example.ui.Tutor

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor
import com.example.example.ui.Profesor.ProfesorAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AddTutor : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var buttonAgregar: Button
    private lateinit var recyclerViewProfesores: RecyclerView

    private val profesores = mutableListOf<Profesor>()  // This should be populated with data
    private lateinit var profesorAdapter: ProfesorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutor)

        firestore = FirebaseFirestore.getInstance()

        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)
        buttonAgregar = findViewById(R.id.buttonAgregar)
        recyclerViewProfesores = findViewById(R.id.recyclerViewProfesores)

        // Initialize the adapter with the list of profesores
        profesorAdapter = ProfesorAdapter(profesores) { profesor ->
            // Handle item selection if needed
        }

        recyclerViewProfesores.layoutManager = LinearLayoutManager(this)
        recyclerViewProfesores.adapter = profesorAdapter

        buttonAgregar.setOnClickListener {
            assignTutor()
        }
    }

    private fun assignTutor() {
        val grado = spinnerGrado.selectedItem.toString()
        val seccion = spinnerSeccion.selectedItem.toString()

        // Filter the selected profesores
        val selectedProfesores = profesores.filter { it.tutor }

        for (profesor in selectedProfesores) {
            val tutorAssignment = mapOf(
                "grado" to grado,
                "seccion" to seccion,
                "profesorId" to profesor.idProfesor // Ensure this matches the actual property name
            )

            firestore.collection("Tutors").add(tutorAssignment)
                .addOnSuccessListener {
                    Toast.makeText(this, "TutorFragment asignado exitosamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al asignar tutor: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
