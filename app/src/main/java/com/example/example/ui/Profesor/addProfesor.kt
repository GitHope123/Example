package com.example.example.ui.Profesor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class addProfesor : AppCompatActivity() {

    private lateinit var editTextNombres: EditText
    private lateinit var editTextApellidos: EditText
    private lateinit var editTextDomicilio: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var editTextMateria: EditText
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var buttonAgregar: Button

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profesor)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Bind the views
        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        editTextDomicilio = findViewById(R.id.editTextDomicilio)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextMateria = findViewById(R.id.editTextMateria)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)
        buttonAgregar = findViewById(R.id.button2)

        // Set click listener for the button
        buttonAgregar.setOnClickListener {
            saveProfesorToFirebase()
        }
    }

    private fun saveProfesorToFirebase() {
        // Collect the data from input fields
        val nombres = editTextNombres.text.toString()
        val apellidos = editTextApellidos.text.toString()
        val domicilio = editTextDomicilio.text.toString()
        val celular = editTextCelular.text.toString()
        val materia = editTextMateria.text.toString()


        val grado = spinnerGrado.selectedItem.toString()
        val seccion = spinnerSeccion.selectedItem.toString()

        // Create a Profesor object
        val profesor = Profesor(nombres, apellidos, domicilio, celular, materia, grado, seccion)

        // Save the data to Firebase Firestore
        db.collection("Profesor")
            .add(profesor)
            .addOnSuccessListener {
                Toast.makeText(this, "Profesor agregado con éxito", Toast.LENGTH_SHORT).show()
                // Optionally, clear the input fields after saving
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al agregar profesor: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        editTextNombres.text.clear()
        editTextApellidos.text.clear()
        editTextDomicilio.text.clear()
        editTextCelular.text.clear()
        editTextMateria.text.clear()
        spinnerGrado.setSelection(0)
        spinnerSeccion.setSelection(0)
    }
}
