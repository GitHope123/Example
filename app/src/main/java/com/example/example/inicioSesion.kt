package com.example.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.google.firebase.auth.GoogleAuthProvider

class inicioSesion : AppCompatActivity() {
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
            // Verifica si el usuario está autenticado antes de iniciar la nueva actividad
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val intent = Intent(this, barraLateral::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Debes iniciar sesión primero.", Toast.LENGTH_SHORT).show()
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
        val idToken = account.idToken
        if (idToken == null) {
            // Maneja el caso en el que el token es nulo
            Toast.makeText(this, "Error al obtener el token de Google.", Toast.LENGTH_LONG).show()
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // La autenticación con Firebase fue exitosa
                    val user = auth.currentUser
                    val email = user?.email

                    if (email != null && email.endsWith("@undc.edu.pe")) {
                        // El correo electrónico tiene el dominio correcto, permitir acceso
                        val intent = Intent(this, barraLateral::class.java)
                        startActivity(intent)
                        auth.signOut()
                        googleSignInClient.signOut()
                        finish()
                    } else {
                        // El correo electrónico no tiene el dominio correcto, mostrar mensaje de error
                        handleSignOut()
                    }
                } else {
                    // Manejo de errores específicos de Firebase
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
        // Cierra sesión del usuario y muestra un mensaje
        auth.signOut()
        googleSignInClient.signOut()
        Toast.makeText(this, "Acceso restringido o error al iniciar sesión. Inténtelo de nuevo.", Toast.LENGTH_LONG).show()
    }

}
