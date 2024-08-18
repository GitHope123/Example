package com.example.example.ui.Profesor

import android.content.Intent
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
    private var isSaving = false  // Flag to prevent duplicate saves

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profesor)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Bind the views
        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextMateria = findViewById(R.id.editTextMateria)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        buttonAgregar = findViewById(R.id.button2)

        // Set click listener for the button
        buttonAgregar.setOnClickListener {
            if (!isSaving) {
                isSaving = true
                saveProfesorToFirebase()
            }
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

        // Validate inputs
        if (nombres.isEmpty() || apellidos.isEmpty() || domicilio.isEmpty() ||
            celular.isEmpty() || materia.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            isSaving = false
            return
        }

        // Create a Profesor object without ID
        val profesor = Profesor(
            idProfesor = "", // Temporary empty ID
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
            .addOnSuccessListener { documentReference ->
                // Update the profesor object with the document ID
                val updatedProfesor = profesor.copy(idProfesor = documentReference.id)

                // Optionally, update the document with the ID (if needed)
                documentReference.update("idProfesor", updatedProfesor.idProfesor)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profesor agregado con éxito", Toast.LENGTH_SHORT).show()
                        clearFields()

                        // Navigate to ProfesorFragment
                        navigateToProfesorFragment()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar el ID del profesor: ${e.message}", Toast.LENGTH_SHORT).show()
                        isSaving = false
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al agregar profesor: ${e.message}", Toast.LENGTH_SHORT).show()
                isSaving = false
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

    private fun navigateToProfesorFragment() {
        val intent = Intent(this,ProfesorFragment::class.java) // Replace MainActivity with your activity
        intent.putExtra("navigateToFragment", "ProfesorFragment")
        startActivity(intent)
        finish()
    }
}
