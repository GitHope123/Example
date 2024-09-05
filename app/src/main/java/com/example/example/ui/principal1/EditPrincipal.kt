package com.example.example.ui.principal1

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var editTextPassword: EditText
    private lateinit var btnGuardar: Button

    private lateinit var nombre: String
    private lateinit var apellido: String
    private lateinit var celular: String
    private lateinit var correo: String
    private lateinit var password: String
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
        password = intent.getStringExtra("password").toString()
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
        editTextPassword = findViewById(R.id.editTextPasswordPrincipal)
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
        editTextPassword.setText(password)
        val ocultarPassword=editTextPassword
        ocultarPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun listener() {
        val passwordEditText = editTextPassword
        val eyeIcon = findViewById<ImageView>(R.id.eyeIcon)
        var isPasswordVisible = false

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeIcon.setImageResource(R.drawable.ic_eye_on) // Cambia a un icono visible
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeIcon.setImageResource(R.drawable.ic_eye_off) // Cambia a un icono oculto
            }
            passwordEditText.setSelection(passwordEditText.text.length)

        }
        btnGuardar.setOnClickListener {
            val updatedCelularStr = edTextCelular.text.toString()
            val updatedContra=editTextPassword.text.toString();
            val updatedCelular: Long?

            // Convert celular to a number (Long)
            try {
                updatedCelular = updatedCelularStr.toLong()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Número de celular inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedProfesor = mapOf("celular" to updatedCelular,"password" to updatedContra)

            if (updatedCelularStr.isNotEmpty() && updatedCelularStr.length == 9 && updatedContra.length!=0 ) {
                if(updatedContra.length>=8){
                    firestore.collection("Profesor").document(id)
                        .update(updatedProfesor)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al actualizar datos: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                else{
                    Toast.makeText(this,"Por favor ingrese una contraseña de 8 digitos", Toast.LENGTH_SHORT).show()
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
