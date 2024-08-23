package com.example.example.ui.estudiante

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class EditEstudiante : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editTextNombres: EditText
    private lateinit var editTextApellidos: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var editTextDni: EditText
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var buttonModificar: Button
    private lateinit var buttonEliminar: Button
    private lateinit var updatedNombres: String
    private lateinit var updatedApellidos: String
    private var updatedCelular: Long = 0
    private var updatedDni: Long = 0
    private var updatedGrado: Int = 0
    private lateinit var updatedSeccion: String
    private lateinit var idEstudiante: String
    private lateinit var nombres: String
    private lateinit var apellidos: String
    private var celular: Long = 0
    private var dni: Long = 0
    private var grado: Int = 0
    private lateinit var seccion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_estudiante)
        findButtons()
        listener()
    }

    private fun listener() {
        buttonModificar.setOnClickListener {
            modifyStudent()
        }

        buttonEliminar.setOnClickListener {
            removeStudent()
        }
    }

    private fun getData() {
        idEstudiante = intent.getStringExtra("idEstudiante") ?: ""
        nombres = intent.getStringExtra("nombres") ?: ""
        apellidos = intent.getStringExtra("apellidos") ?: ""
        celular = intent.getLongExtra("celularApoderado", 0L)
        dni = intent.getLongExtra("dni", 0L)
        grado = intent.getIntExtra("grado", 0)
        seccion = intent.getStringExtra("seccion") ?: ""
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular.toString())
        editTextDni.setText(dni.toString())
        updatedGrado()
    }

    private fun findButtons() {
        firestore = FirebaseFirestore.getInstance()
        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextDni = findViewById(R.id.editTextDni)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSection)
        buttonModificar = findViewById(R.id.buttonModificar)
        buttonEliminar = findViewById(R.id.buttonEliminar)
        getData()
    }
    private fun updatedGrado(){
        val grados=arrayOf("Todas","1","2","3","4","5")
        val adapterGrados=ArrayAdapter(this,android.R.layout.simple_spinner_item,grados)
        adapterGrados.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        spinnerGrado.adapter=adapterGrados
        spinnerGrado.onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gradoSeleccionado=spinnerGrado.selectedItem.toString()
                updatedSeccion(gradoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }
    private fun updatedSeccion(gradoSeleccionado:String){
        val secciones= if(gradoSeleccionado=="Todas"){
            arrayOf("Todas")
        }else{
            if(gradoSeleccionado=="1"){
                arrayOf("Todas","A","B","C","D","E")
            }
            else{
                arrayOf("Todas","A","B","C","D")
            }
        }
        val adapterSecciones=ArrayAdapter(this,android.R.layout.simple_spinner_item,secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter=adapterSecciones
        spinnerSeccion.isEnabled=gradoSeleccionado != "Todas"
    }


    private fun setSpinnerValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String>
        val position = adapter?.getPosition(value) ?: 0
        spinner.setSelection(position)
    }



    private fun modifyStudent() {
        updatedNombres = editTextNombres.text.toString().trim()
        updatedApellidos = editTextApellidos.text.toString().trim()
        updatedCelular = editTextCelular.text.toString().trim().toLongOrNull()!!
        updatedDni = editTextDni.text.toString().trim().toLongOrNull()!!
        updatedGrado = spinnerGrado.selectedItem.toString().trim().toIntOrNull()!!
        updatedSeccion = spinnerSeccion.selectedItem.toString().trim()
        val updatedEstudiante = mapOf(
            "nombres" to updatedNombres,
            "apellidos" to updatedApellidos,
            "celularApoderado" to updatedCelular,
            "dni" to updatedDni,
            "grado" to updatedGrado,
            "seccion" to updatedSeccion,
        )
        if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
            updatedCelular.toString().length == 9 && updatedDni.toString().length == 8 &&
            updatedSeccion.isNotEmpty()
        ) {
            firestore.collection("Estudiante").document(idEstudiante)
                .update(updatedEstudiante)
                .addOnSuccessListener {
                    Toast.makeText(this,"Estudiante actualizado con éxito",Toast.LENGTH_SHORT).show()
                    notifyEstudianteFragment()
                    finish()
                }
                .addOnFailureListener { e->
                    Toast.makeText(this,"Error al actualizar el estudiante: ${e}",Toast.LENGTH_SHORT).show()
                }

        } else {
            Toast.makeText(
                this,
                "Por favor complete todos los campos correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    private fun notifyEstudianteFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("EstudianteFragment")
        if (fragment is EstudianteFragment) {
            fragment.refreshData()
        }
    }
    private fun removeStudent(){
        val documentRef=firestore.collection("Estudiante").document(idEstudiante)

    }

}
