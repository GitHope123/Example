package com.example.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class InicioSesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        auth = FirebaseAuth.getInstance()

        val iniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        iniciarSesion.setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && email.endsWith("@gmail.com")) {
                            val intent = Intent(this, BarraLateral::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            handleSignOut()
                        }
                    } else {
                        val exception = task.exception
                        val errorMessage = when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas. Verifique el correo electrónico y la contraseña."
                            is FirebaseAuthInvalidUserException -> "Usuario no registrado. Verifique el correo electrónico."
                            else -> "Error al iniciar sesión. Inténtelo de nuevo."
                        }
                        Log.e("InicioSesion", errorMessage, exception)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun handleSignOut() {
        auth.signOut()
        Toast.makeText(this, "Acceso restringido. Inténtelo de nuevo.", Toast.LENGTH_LONG).show()
    }
}
