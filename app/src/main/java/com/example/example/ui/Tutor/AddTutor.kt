package com.example.example.ui.Tutor

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor
import com.google.firebase.firestore.FirebaseFirestore

class AddTutor : AppCompatActivity() {

    private lateinit var buttonAceptar: Button
    private lateinit var buttonCancelar: Button
    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val profesoresCollection = firestore.collection("Profesor")
    private lateinit var tutorAdapter: TutorAdapter
    private val filteredProfesorList = mutableListOf<Profesor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutor)

        buttonAceptar = findViewById(R.id.buttonAceptar)
        buttonCancelar = findViewById(R.id.buttonCancelar)
        recyclerView = findViewById(R.id.recyclerViewSeleccionar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        tutorAdapter = TutorAdapter(filteredProfesorList) { profesor ->
            // Handle item click if needed
        }
        recyclerView.adapter = tutorAdapter

        buttonAceptar.setOnClickListener {
            val selectedProfesores = tutorAdapter.getSelectedProfesores()
            updateTutors(selectedProfesores)
        }

        buttonCancelar.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }
    }

    private fun updateTutors(selectedProfesores: List<Profesor>) {
        if (selectedProfesores.isEmpty()) {
            Toast.makeText(this, "No se seleccionaron profesores", Toast.LENGTH_SHORT).show()
            return
        }

        val batch = firestore.batch()
        for (profesor in selectedProfesores) {
            val id = profesor.idProfesor
            if (id != null) {
                val profesorRef = profesoresCollection.document(id)
                batch.update(profesorRef, "isTutor", true)
            } else {
                Log.w("AddTutor", "ID de profesor es nulo para ${profesor.nombres} ${profesor.apellidos}")
            }
        }

        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profesores actualizados exitosamente", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Log.e("AddTutor", "Error al actualizar profesores", task.exception)
                Toast.makeText(this, "Error al actualizar profesores", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
