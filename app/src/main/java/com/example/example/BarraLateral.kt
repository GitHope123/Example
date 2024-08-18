package com.example.example

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.webauthn.Cbor
import com.example.example.databinding.ActivityBarraLateralBinding
import com.google.firebase.auth.FirebaseAuth

class BarraLateral : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBarraLateralBinding
    private lateinit var auth: FirebaseAuth

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Obtén el nombre del usuario si está disponible
            val userName = user.displayName ?: "Usuario"
            val email = user.email ?: "No disponible"

            // Registra la información en el log
            Log.d("BarraLateral", "Correo electrónico del usuario: $email")

            // Configura las vistas del header en el NavigationView
            val navView: NavigationView = binding.navView
            val headerView = navView.getHeaderView(0)
            val userWelcome = headerView.findViewById<TextView>(R.id.textViewUser)
            val emailTextView = headerView.findViewById<TextView>(R.id.textViewEmail)
            val formattedText = userName
                .toLowerCase()
                .split(" ")
                .take(2)
                .joinToString(" ") { it.capitalize() }
            // Actualiza los textos en el header
            userWelcome.text = "Bienvenido, $formattedText!"
            emailTextView.text = email
        } else {
            Log.d("BarraLateral", "No hay usuario autenticado")
            // Manejo cuando no hay usuario autenticado, posiblemente redirigir a pantalla de login
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarraLateralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configura el AuthStateListener
        auth.addAuthStateListener(authStateListener)

        // Configura la barra de acción
        setSupportActionBar(binding.appBarBarraLateral.toolbar)


        // Configura el Navigation Drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navController = findNavController(R.id.nav_host_fragment_content_barra_lateral)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_principal,
                R.id.nav_profesor,
                R.id.nav_estudiantes,
                R.id.nav_tutor,
                R.id.nav_incidencia,
                R.id.nav_tutoria,
                R.id.nav_reporte
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.barra_lateral, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_barra_lateral)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Elimina el AuthStateListener
        auth.removeAuthStateListener(authStateListener)
    }
}
