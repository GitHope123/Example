package com.example.example.ui.Profesor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class EditProfesor : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var editTextNombres: EditText
    private lateinit var editTextApellidos: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var editTextMateria: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var buttonGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profesor)

        firestore = FirebaseFirestore.getInstance()

        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextMateria = findViewById(R.id.editTextMateria)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        buttonGuardar = findViewById(R.id.buttonGuardar)

        // Get the data from the intent
        val idProfesor = intent.getStringExtra("idProfesor") ?: ""
        val nombres = intent.getStringExtra("nombres") ?: ""
        val apellidos = intent.getStringExtra("apellidos") ?: ""
        val celular = intent.getStringExtra("celular") ?: ""
        val materia = intent.getStringExtra("materia") ?: ""
        val correo = intent.getStringExtra("correo") ?: ""

        // Set the data to the EditText fields
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular)
        editTextMateria.setText(materia)
        editTextCorreo.setText(correo)

        buttonGuardar.setOnClickListener {
            val updatedNombres = editTextNombres.text.toString()
            val updatedApellidos = editTextApellidos.text.toString()
            val updatedCelular = editTextCelular.text.toString()
            val updatedMateria = editTextMateria.text.toString()
            val updatedCorreo = editTextCorreo.text.toString()

            val updatedProfesor = mapOf(
                "nombres" to updatedNombres,
                "apellidos" to updatedApellidos,
                "celular" to updatedCelular,
                "materia" to updatedMateria,
                "correo" to updatedCorreo
            )

            firestore.collection("Profesor").document(idProfesor)
                .update(updatedProfesor)
                .addOnSuccessListener {
                    finish() // Close the activity
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }
}
