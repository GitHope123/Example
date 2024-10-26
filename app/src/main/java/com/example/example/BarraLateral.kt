package com.example.example

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.example.databinding.ActivityBarraLateralBinding
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalTime

class BarraLateral : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBarraLateralBinding
    private lateinit var datoId:String
    private lateinit var datoTipoUsuario:String
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarraLateralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        var nombres=InicioSesion.GlobalData.nombresUsuario
        var correo=InicioSesion.GlobalData.correoUsuario
        datoId= intent.getStringExtra("ID").toString()
        datoTipoUsuario=intent.getStringExtra("USER_TYPE").toString()

        if(datoTipoUsuario=="Administrador"){
            var username1="Director"
            var correo1="wguzman@colegiosparroquiales.com"
            updateHeader(username1,correo1)
        }
        else{
            updateHeader(nombres,correo)
                }
      //  auth.addAuthStateListener(authStateListener)
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.barra_lateral, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cerrarSesionaction_cerrarSesion -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                val intent = Intent(this, InicioSesion::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun updateHeader(userName: String, email: String) {
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val userWelcome = headerView.findViewById<TextView>(R.id.textViewUser)
        val emailTextView = headerView.findViewById<TextView>(R.id.textViewEmail)

        val formattedText = userName.split(" ").joinToString(" ") { it.capitalize() }
        val saludo = saludoSegunHora()
        userWelcome.text = "$saludo, $formattedText!"
        emailTextView.text = email
    }
    fun saludoSegunHora(): String {
        val horaActual = LocalTime.now()
        return when {
            horaActual.isBefore(LocalTime.NOON) -> "Buenos días"
            horaActual.isBefore(LocalTime.of(18, 0)) -> "Buenas tardes"
            else -> "Buenas noches"
        }
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
        navMenu.findItem(R.id.nav_estudiantes).isVisible = false
        navMenu.findItem(R.id.nav_tutor).isVisible = true
        navMenu.findItem(R.id.nav_incidencia).isVisible = true
        navMenu.findItem(R.id.nav_tutoria).isVisible = true
        navMenu.findItem(R.id.nav_reporte).isVisible = true
    }

    private fun showProfesorMenuItems(navMenu: Menu) {
        navMenu.findItem(R.id.nav_principal).isVisible = true
        navMenu.findItem(R.id.nav_profesor).isVisible = true
        navMenu.findItem(R.id.nav_estudiantes).isVisible = false
        navMenu.findItem(R.id.nav_tutor).isVisible = true
        navMenu.findItem(R.id.nav_incidencia).isVisible = true
        navMenu.findItem(R.id.nav_tutoria).isVisible = false
        navMenu.findItem(R.id.nav_reporte).isVisible = true
    }

    private fun showDefaultMenuItems(navMenu: Menu) {
        navMenu.findItem(R.id.nav_principal).isVisible = true
        navMenu.findItem(R.id.nav_profesor).isVisible = false
        navMenu.findItem(R.id.nav_estudiantes).isVisible = false
        navMenu.findItem(R.id.nav_tutor).isVisible = false
        navMenu.findItem(R.id.nav_incidencia).isVisible = false
        navMenu.findItem(R.id.nav_tutoria).isVisible = false
        navMenu.findItem(R.id.nav_reporte).isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_barra_lateral)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}

