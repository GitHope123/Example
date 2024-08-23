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


        setupGoogleSignIn()
        auth = FirebaseAuth.getInstance()
        setupSignInLauncher()
        setupButtons()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupSignInLauncher() {
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                handleGoogleSignInResult(result.data)
            } else {
                Log.e("InicioSesion", "Google sign-in failed: result not OK")
            }
        }
    }
    private fun setupButtons() {
        val iniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        val iniciarSesionGoogle = findViewById<Button>(R.id.buttonGoogle)
        iniciarSesion.setOnClickListener { loginWithEmailAndPassword() }
        iniciarSesionGoogle.setOnClickListener { signInWithGoogle() }
    }
    private fun loginWithEmailAndPassword() {
        val email = findViewById<EditText>(R.id.editTextUsername).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

        if (email.isBlank() || password.isBlank()) {
            showToast("Por favor, ingrese todos los campos.")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    handleSuccessfulLogin(email)
                } else {
                    handleSignInError(task.exception)
                }
            }
    }

    private fun handleSuccessfulLogin(email: String) {
        val user = auth.currentUser
        if (user != null && email.endsWith("@gmail.com")) {
            navigateToBarraLateral()
        } else {
            handleSignOut()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e("InicioSesion", "Google sign-in failed", e)
            showToast("Error al iniciar sesión.")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val idToken = account.idToken ?: run {
            showToast("Error al obtener el token de Google.")
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val email = auth.currentUser?.email
                    if (email != null && email.endsWith("@gmail.com")) {
                        navigateToBarraLateral()
                    } else {
                        handleSignOut()
                    }
                } else {
                    handleSignInError(task.exception)
                    handleSignOut()
                }
            }
    }

    private fun handleSignInError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas. Verifique el correo electrónico y la contraseña."
            is FirebaseAuthInvalidUserException -> "Usuario no registrado. Verifique el correo electrónico."
            is ApiException -> "Error de autenticación de Google: ${exception.statusCode}"
            else -> "Error al iniciar sesión. Inténtelo de nuevo."
        }
        Log.e("InicioSesion", errorMessage, exception)
        showToast(errorMessage)
    }

    private fun handleSignOut() {
        auth.signOut()
        googleSignInClient.signOut()
        showToast("Acceso restringido. Inténtelo de nuevo.")
    }

    private fun navigateToBarraLateral() {
        val intent = Intent(this, BarraLateral::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
