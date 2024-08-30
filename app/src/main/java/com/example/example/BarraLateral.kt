package com.example.example

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.example.databinding.ActivityBarraLateralBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class BarraLateral : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBarraLateralBinding
    private lateinit var auth: FirebaseAuth

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        val userName = user?.displayName ?: "Usuario"
        val email = user?.email ?: "No disponible"

        Log.d("BarraLateral", "Correo electrónico del usuario: $email")
        updateHeader(userName, email)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarraLateralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authStateListener)

        setSupportActionBar(binding.appBarBarraLateral.toolbar)

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

        // Manejo del tipo de usuario después de configurar el NavController
        configureMenuBasedOnUserType(intent.getStringExtra("USER_TYPE"))
    }

    private fun updateHeader(userName: String, email: String) {
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val userWelcome = headerView.findViewById<TextView>(R.id.textViewUser)
        val emailTextView = headerView.findViewById<TextView>(R.id.textViewEmail)

        val formattedText = userName.split(" ").joinToString(" ") { it.capitalize() }
        userWelcome.text = "Bienvenido, $formattedText!"
        emailTextView.text = email
    }

    private fun configureMenuBasedOnUserType(userType: String?) {
        val navMenu = binding.navView.menu
        when (userType) {
            "Administrador" -> {
                showAllMenuItems(navMenu)
            }
            "Tutor" -> {
                showTutorMenuItems(navMenu)
            }
            "Profesor" -> {
                showProfesorMenuItems(navMenu)
            }
            else -> {
                showDefaultMenuItems(navMenu)
            }
        }
    }

    private fun showAllMenuItems(navMenu: Menu) {
        navMenu.findItem(R.id.nav_principal).isVisible = false
        navMenu.findItem(R.id.nav_profesor).isVisible = true
        navMenu.findItem(R.id.nav_estudiantes).isVisible = true
        navMenu.findItem(R.id.nav_tutor).isVisible = true
        navMenu.findItem(R.id.nav_incidencia).isVisible = true
        navMenu.findItem(R.id.nav_tutoria).isVisible = false
        navMenu.findItem(R.id.nav_reporte).isVisible = true
    }

    private fun showTutorMenuItems(navMenu: Menu) {
        navMenu.findItem(R.id.nav_principal).isVisible = true
        navMenu.findItem(R.id.nav_profesor).isVisible = true
        navMenu.findItem(R.id.nav_estudiantes).isVisible = true
        navMenu.findItem(R.id.nav_tutor).isVisible = true
        navMenu.findItem(R.id.nav_incidencia).isVisible = true
        navMenu.findItem(R.id.nav_tutoria).isVisible = true
        navMenu.findItem(R.id.nav_reporte).isVisible = false
    }

    private fun showProfesorMenuItems(navMenu: Menu) {
        navMenu.findItem(R.id.nav_principal).isVisible = true
        navMenu.findItem(R.id.nav_profesor).isVisible = true
        navMenu.findItem(R.id.nav_estudiantes).isVisible = true
        navMenu.findItem(R.id.nav_tutor).isVisible = true
        navMenu.findItem(R.id.nav_incidencia).isVisible = true
        navMenu.findItem(R.id.nav_tutoria).isVisible = false
        navMenu.findItem(R.id.nav_reporte).isVisible = false
    }

    private fun showDefaultMenuItems(navMenu: Menu) {
        navMenu.findItem(R.id.nav_principal).isVisible = true
        navMenu.findItem(R.id.nav_profesor).isVisible = false
        navMenu.findItem(R.id.nav_estudiantes).isVisible = false
        navMenu.findItem(R.id.nav_tutor).isVisible = false
        navMenu.findItem(R.id.nav_incidencia).isVisible = false
        navMenu.findItem(R.id.nav_tutoria).isVisible = false
        navMenu.findItem(R.id.nav_reporte).isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_barra_lateral)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
