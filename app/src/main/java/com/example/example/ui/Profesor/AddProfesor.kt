package com.example.example.ui.Profesor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class AddProfesor : AppCompatActivity() {

    private lateinit var editTextNombres: EditText
    private lateinit var editTextApellidos: EditText
    private lateinit var editTextDomicilio: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var editTextMateria: EditText
    private lateinit var editTextCorreo: EditText
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
        editTextCorreo = findViewById(R.id.editTextCorreo)
        buttonAgregar = findViewById(R.id.button2)

        // Set click listener for the button
        buttonAgregar.setOnClickListener {
            saveProfesorToFirebase()
        }
    }

    private fun saveProfesorToFirebase() {
        // Collect the data from input fields
        val nombres = editTextNombres.text.toString().trim()
        val apellidos = editTextApellidos.text.toString().trim()
        val domicilio = editTextDomicilio.text.toString().trim()
        val celular = editTextCelular.text.toString().trim()
        val materia = editTextMateria.text.toString().trim()
        val correo = editTextCorreo.text.toString().trim()

        // Validate input fields
        if (nombres.isEmpty() || apellidos.isEmpty() || domicilio.isEmpty() ||
            celular.isEmpty() || materia.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a Profesor object
        val profesor = Profesor(
            nombres = nombres,
            apellidos = apellidos,
            domicilio = domicilio,
            celular = celular,
            materia = materia,
            correo = correo
        )

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
        editTextCorreo.text.clear()
    }
}
