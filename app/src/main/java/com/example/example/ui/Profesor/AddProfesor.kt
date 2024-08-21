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
    private lateinit var editTextCelular: EditText
    private lateinit var editTextMateria: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var buttonAgregar: Button

    private lateinit var db: FirebaseFirestore
    private var isSaving = false // Flag to prevent duplicate saves

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profesor)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Bind the views
        bindViews()

        // Set click listener for the button
        buttonAgregar.setOnClickListener {
            if (!isSaving) {
                isSaving = true
                saveProfesorToFirebase()
            }
        }
    }

    private fun bindViews() {
        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextMateria = findViewById(R.id.editTextMateria)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        buttonAgregar = findViewById(R.id.button2)
    }

    private fun saveProfesorToFirebase() {
        // Collect the data from input fields
        val nombres = editTextNombres.text.toString().trim()
        val apellidos = editTextApellidos.text.toString().trim()
        val celularStr = editTextCelular.text.toString().trim()
        val materia = editTextMateria.text.toString().trim()
        val correo = editTextCorreo.text.toString().trim()

        // Validate inputs
        if (nombres.isEmpty() || apellidos.isEmpty() || celularStr.isEmpty() ||
            materia.isEmpty() || correo.isEmpty()||celularStr.length!=9) {
            showToast("Por favor, complete todos los campos")
            isSaving = false
            return
        }

        // Parse celular as a Long, handle potential errors
        val celular: Long = try {
            celularStr.toLong()
        } catch (e: NumberFormatException) {
            showToast("Número de celular inválido")
            isSaving = false
            return
        }

        // Create a Profesor object with tutor set to false by default
        val profesor = Profesor(
            idProfesor = "", // Temporary empty ID
            nombres = nombres,
            apellidos = apellidos,
            celular = celular,
            materia = materia,
            correo = correo,
            tutor = false // Default value for tutor
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
                        showToast("Profesor agregado con éxito")
                        clearFields()
                        navigateToProfesorFragment() // Navigate to the ProfesorFragment
                    }
                    .addOnFailureListener { e ->
                        showToast("Error al actualizar el ID del profesor: ${e.message}")
                        isSaving = false
                    }
            }
            .addOnFailureListener { e ->
                showToast("Error al agregar profesor: ${e.message}")
                isSaving = false
            }
    }

    private fun clearFields() {
        editTextNombres.text.clear()
        editTextApellidos.text.clear()
        editTextCelular.text.clear()
        editTextMateria.text.clear()
        editTextCorreo.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToProfesorFragment() {
        // Assuming that ProfesorFragment is part of the navigation system
        // Use proper fragment navigation or intent as per your app's architecture
        val intent = Intent(this, ProfesorFragment::class.java) // Replace with correct destination
        startActivity(intent)
        finish()
    }
}
