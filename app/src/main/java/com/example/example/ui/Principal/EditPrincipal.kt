package com.example.example.ui.Principal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditPrincipal : AppCompatActivity() {
    private lateinit var edTxtNombre: EditText
    private lateinit var edTxtApellido: EditText
    private lateinit var edTextCelular: EditText
    private lateinit var edTextCorreo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var nombre:String
    private lateinit var apellido:String
    private lateinit var celular:String
    private lateinit var correo:String
    private lateinit var id:String
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_principal)
        nombre= intent.getStringExtra("nombre").toString()
        apellido=intent.getStringExtra("apellido").toString()
        correo=intent.getStringExtra("correo").toString()
        celular=intent.getStringExtra("celular").toString()
        id=intent.getStringExtra("id").toString()
            init()
        obtenerDatos()
        listener()
    }



    fun init(){

        edTxtNombre=findViewById(R.id.edTxtNombre)
        edTxtApellido=findViewById(R.id.edTxtApellido)
        edTextCelular=findViewById(R.id.edTextCelular)
        edTextCorreo=findViewById(R.id.edTextCorreo)
        btnGuardar=findViewById(R.id.buttonModificar)
        edTxtApellido.isEnabled = false
        edTxtNombre.isEnabled=false
        edTextCorreo.isEnabled=false
    }
    private fun obtenerDatos() {
      edTxtNombre.setText(nombre)
        edTxtApellido.setText(apellido)
        edTextCorreo.setText(correo)
        edTextCelular.setText(celular)
    }

    private fun listener() {
        btnGuardar.setOnClickListener{
            val updatedCelular=edTextCelular.text.toString()
            val updatedProfesor= mapOf(
                "celular" to updatedCelular
            )
            if(updatedCelular.isNotEmpty() && updatedCelular.length==9){
            firestore.collection("Profesor").document(id)
                .update(updatedProfesor)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos actualizado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun refreshData() {
        obtenerDatos()
    }
    override fun onResume() {
        super.onResume()
        refreshData()
    }


}
