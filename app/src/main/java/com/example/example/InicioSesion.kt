package com.example.example

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.databinding.ActivityInicioSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioSesion : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityInicioSesionBinding
    private lateinit var userType: String
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val passwordEditText = binding.editTextPassword
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
        binding.buttonIniciarSesion.setOnClickListener {
            val email = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Por favor, complete todos los campos.")
            } else {
                iniciarSesionConFirebase(email, password)

            }
        }

    }

    private fun iniciarSesionConFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { task1 ->
                userType = "Administrador"
                navigateToBarraLateral(userType)
                GlobalData.datoTipoUsuario = userType
            }
            .addOnFailureListener {
                firestore.collection("Profesor")
                    .whereEqualTo("correo", email)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (!document.isEmpty) {
                                val doc = document.first()
                                val tutor = doc.getBoolean("tutor")
                                id = doc.getString("id").toString()
                                if (tutor == true) {
                                    userType = "Tutor"
                                    navigateToBarraLateral(userType)
                                } else {
                                    userType = "Profesor"
                                    navigateToBarraLateral(userType)
                                }
                                GlobalData.idUsuario = id
                                GlobalData.datoTipoUsuario = userType
                            }
                        } else {
                            Toast.makeText(this, "Este correo no se encuentra registrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Este correo no se encuentra registrado", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun navigateToBarraLateral(userType: String) {
        startActivity(Intent(this, BarraLateral::class.java).apply {
            putExtra("USER_TYPE", userType)
            if (userType == "Profesor" || userType == "Tutor") {
                putExtra("ID", id)
            }
        })
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    object GlobalData {
        var idUsuario: String=""
        var datoTipoUsuario: String=""
    }
}