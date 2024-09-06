package com.example.example

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
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
            .addOnSuccessListener {
                userType = "Administrador"
                navigateToBarraLateral(userType)
                GlobalData.datoTipoUsuario = userType
                GlobalData.idUsuario = "Administrador"
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
                                val nombres=doc.getString("nombres")?:""
                                val apellidos=doc.getString("apellidos") ?:""
                                val celular=doc.getLong("celular")?:0
                                val cargo=doc.getString("cargo")?:""
                                val correo=doc.getString("correo")?:""
                                val tutor = doc.getBoolean("tutor")?:false
                                val grado=doc.getLong("grado")?:0
                                val seccion=doc.getString("seccion")?:""
                                val password=doc.getString("password")?:""
                                val dni=doc.getLong("dni")?:0
                                id = doc.getString("id").toString()

                                if (tutor == true) {
                                    userType = "Tutor"
                                    navigateToBarraLateral(userType)
                                } else {
                                    userType = "Profesor"
                                    navigateToBarraLateral(userType)
                                }
                                GlobalData.celularUsuario=celular
                                GlobalData.correoUsuario=correo
                                GlobalData.idUsuario = id
                                GlobalData.datoTipoUsuario = userType
                                GlobalData.nombresUsuario=nombres
                                GlobalData.apellidosUsuario=apellidos
                                GlobalData.passwordUsuario=password
                                GlobalData.tutor=tutor

                            } else {
                                Toast.makeText(this, "Este correo no se encuentra registrado", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // En caso de error en la consulta
                            Toast.makeText(this, "Error al consultar la base de datos", Toast.LENGTH_SHORT).show()
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
        var datoTipoUsuario: String=""
        //infoUsuario
        var idUsuario: String=""
        var nombresUsuario:String=""
        var apellidosUsuario:String=""
        var celularUsuario:Long=0
        var cargoUsuario:String=""
        var correoUsuario:String=""
        var tutor:Boolean = false
        var gradoUsuario:Long=0
        var seccionUsuario:String=""
        var passwordUsuario:String=""
        var dniUsuario:Long=0
    }
}