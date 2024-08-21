package com.example.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider

class InicioSesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        // Configura Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        // Registra el ActivityResultLauncher
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Log.e("InicioSesion", "Google sign-in failed", e)
                    Toast.makeText(this, "Error al iniciar sesión.", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("InicioSesion", "Result not OK")
            }
        }

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

        val iniciarSesionGoogle = findViewById<Button>(R.id.buttonGoogle)
        iniciarSesionGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val idToken = account.idToken ?: run {
            Toast.makeText(this, "Error al obtener el token de Google.", Toast.LENGTH_LONG).show()
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val email = user?.email

                    if (email != null && email.endsWith("@gmail.com")) {
                        val intent = Intent(this, BarraLateral::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        handleSignOut()
                    }
                } else {
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is ApiException -> "Error de autenticación de Google: ${exception.statusCode}"
                        else -> "Error al iniciar sesión con Firebase."
                    }
                    Log.e("InicioSesion", errorMessage, exception)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    handleSignOut()
                }
            }
    }

    private fun handleSignOut() {
        auth.signOut()
        googleSignInClient.signOut()
        Toast.makeText(this, "Acceso restringido. Inténtelo de nuevo.", Toast.LENGTH_LONG).show()
    }
}
