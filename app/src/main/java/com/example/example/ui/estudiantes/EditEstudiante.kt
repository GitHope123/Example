package com.example.example.ui.estudiantes

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
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var buttonModificar: Button
    private lateinit var buttonEliminar: Button
    private lateinit var updatedNombres: String
    private lateinit var updatedApellidos: String
    private var updatedGrado: Int = 0
    private lateinit var updatedSeccion: String
    private lateinit var idEstudiante: String
    private lateinit var nombres: String
    private lateinit var apellidos: String
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
            modifyStudent(updatedGrado,updatedSeccion)
        }
        buttonEliminar.setOnClickListener {
            removeStudent()
        }
    }

    private fun getData() {
        idEstudiante = intent.getStringExtra("idEstudiante") ?: ""
        nombres = intent.getStringExtra("nombres") ?: ""
        apellidos = intent.getStringExtra("apellidos") ?: ""
        grado = intent.getIntExtra("grado", 0)
        seccion = intent.getStringExtra("seccion") ?: ""
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        updatedGrado()
    }

    private fun findButtons() {
        firestore = FirebaseFirestore.getInstance()
        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSection)
        buttonModificar = findViewById(R.id.buttonModificar)
        buttonEliminar = findViewById(R.id.buttonEliminar)
        getData()
    }
    private fun updatedGrado(){
        val grados=arrayOf("1","2","3","4","5")
        val adapterGrados=ArrayAdapter(this,R.layout.spinner_item_selected,grados)
        adapterGrados.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        spinnerGrado.adapter=adapterGrados
        setSpinnerValue(spinnerGrado, grado.toString())
              spinnerGrado.onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updatedGrado=spinnerGrado.selectedItem.toString().trim().toIntOrNull()!!
                updatedSeccion(updatedGrado)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }
    private fun updatedSeccion(gradoSeleccionado:Int){
        val secciones= if(gradoSeleccionado.toString()=="1"){
                arrayOf("A","B","C","D","E")
            }
            else{
                arrayOf("A","B","C","D")
            }

        val adapterSecciones=ArrayAdapter(this,R.layout.spinner_item_selected,secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter=adapterSecciones
        setSpinnerValue(spinnerSeccion, seccion)
        spinnerSeccion.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updatedSeccion=spinnerSeccion.selectedItem.toString().trim()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }




    private fun modifyStudent(updatedGrado:Int,updatedSeccion:String) {
        updatedNombres = editTextNombres.text.toString().trim()
        updatedApellidos = editTextApellidos.text.toString().trim()
        val updatedEstudiante = mapOf(
            "nombres" to updatedNombres,
            "apellidos" to updatedApellidos,
            "grado" to updatedGrado,
            "seccion" to updatedSeccion)
        if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
            updatedSeccion.isNotEmpty()
        ) {
            firestore.collection("Estudiante").document(idEstudiante)
                .update(updatedEstudiante)
                .addOnSuccessListener {
                    Toast.makeText(this,"Estudiante actualizado con éxito",Toast.LENGTH_SHORT).show()
                    notifyEstudianteFragment()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error al actualizar el estudiante",Toast.LENGTH_SHORT).show()
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
        documentRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this,"Estudiante Eliminado",Toast.LENGTH_SHORT).show()
                notifyEstudianteFragment()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error al agregar",Toast.LENGTH_SHORT).show()
            }
    }
    private fun setSpinnerValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String>
        val position = adapter?.getPosition(value) ?: 0
        spinner.setSelection(position)
    }

}
