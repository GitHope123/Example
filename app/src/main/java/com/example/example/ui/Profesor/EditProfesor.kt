package com.example.example.ui.Profesor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.NumberFormatException

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
        buttonGuardar = findViewById(R.id.buttonModificar)

        // Retrieve data from Intent
        val idProfesor = intent.getStringExtra("idProfesor") ?: ""
        val nombres = intent.getStringExtra("nombres") ?: ""
        val apellidos = intent.getStringExtra("apellidos") ?: ""
        val celular = intent.getLongExtra("celular", 0) // Use getLongExtra for Long
        val materia = intent.getStringExtra("materia") ?: ""
        val correo = intent.getStringExtra("correo") ?: ""

        // Set data to EditTexts
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular.toString()) // Convert Long to String
        editTextMateria.setText(materia)
        editTextCorreo.setText(correo)

        buttonGuardar.setOnClickListener {
            val updatedNombres = editTextNombres.text.toString()
            val updatedApellidos = editTextApellidos.text.toString()
            val updatedMateria = editTextMateria.text.toString()
            val updatedCorreo = editTextCorreo.text.toString()

            val updatedCelular: Long? = try {
                editTextCelular.text.toString().toLong()
            } catch (e: NumberFormatException) {
                null
            }

            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
                updatedCelular != null && updatedMateria.isNotEmpty() &&
                updatedCorreo.isNotEmpty()&&updatedCelular.toString().length==9) {

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
                        Toast.makeText(this, "Profesor actualizado con éxito", Toast.LENGTH_SHORT).show()
                        // Notify ProfesorFragment to refresh its data
                        notifyProfesorFragment()
                        finish() // Close the activity
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar el profesor: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun notifyProfesorFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("ProfesorFragment")
        if (fragment is ProfesorFragment) {
            fragment.refreshData()
        }
    }


}
