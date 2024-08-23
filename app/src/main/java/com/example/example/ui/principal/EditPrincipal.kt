package com.example.example.ui.principal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditPrincipal : AppCompatActivity() {
    private lateinit var edTxtNombre: EditText
    private lateinit var edTxtApellido: EditText
    private lateinit var edTextCelular: EditText
    private lateinit var edTextCorreo: EditText
    private lateinit var btnGuardar: Button

    private lateinit var nombre: String
    private lateinit var apellido: String
    private lateinit var celular: String
    private lateinit var correo: String
    private lateinit var id: String

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_principal)

        // Get data from intent
        nombre = intent.getStringExtra("nombre").toString()
        apellido = intent.getStringExtra("apellido").toString()
        correo = intent.getStringExtra("correo").toString()
        celular = intent.getStringExtra("celular").toString()
        id = intent.getStringExtra("id").toString()

        // Initialize UI components
        init()
        obtenerDatos()
        listener()
    }

    private fun init() {
        edTxtNombre = findViewById(R.id.edTxtNombre)
        edTxtApellido = findViewById(R.id.edTxtApellido)
        edTextCelular = findViewById(R.id.edTextCelular)
        edTextCorreo = findViewById(R.id.edTextCorreo)
        btnGuardar = findViewById(R.id.buttonModificar)

        // Make fields non-editable
        edTxtNombre.isEnabled = false
        edTxtApellido.isEnabled = false
        edTextCorreo.isEnabled = false
    }

    private fun obtenerDatos() {
        edTxtNombre.setText(nombre)
        edTxtApellido.setText(apellido)
        edTextCorreo.setText(correo)
        edTextCelular.setText(celular)
    }

    private fun listener() {
        btnGuardar.setOnClickListener {
            val updatedCelularStr = edTextCelular.text.toString()
            val updatedCelular: Long?

            // Convert celular to a number (Long)
            try {
                updatedCelular = updatedCelularStr.toLong()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Número de celular inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedProfesor = mapOf("celular" to updatedCelular)

            if (updatedCelularStr.isNotEmpty() && updatedCelularStr.length == 9) {
                firestore.collection("Profesor").document(id)
                    .update(updatedProfesor)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar datos: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshData() {
        obtenerDatos()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
