package com.example.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.databinding.ActivityInicioSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioSesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityInicioSesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToBarraLateral(email, "administrador")
                } else {
                    verificarUsuarioEnFirestore(email, password)
                }
            }
    }

    private fun verificarUsuarioEnFirestore(email: String, password: String) {
        firestore.collection("Profesor")
            .whereEqualTo("correo", email)
            .whereEqualTo("password", password)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    val document = task.result.documents.firstOrNull()
                    document?.let {
                        val isTutor = it.getBoolean("tutor") ?: false
                        val userType = if (isTutor) "tutor" else "profesor"
                        showToast("Inicio de sesión exitoso.")
                        navigateToBarraLateral(email, userType)
                    }
                } else {
                    showToast("Error al iniciar sesión. Verifica tus datos.")
                }
            }
            .addOnFailureListener { exception ->
                showToast("Error al verificar usuario: ${exception.localizedMessage}")
            }
    }

    private fun navigateToBarraLateral(email: String, userType: String) {
        startActivity(Intent(this, BarraLateral::class.java).apply {
            putExtra("USER_PROFESOR", email)
            putExtra("USER_TYPE", userType)
        })
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}