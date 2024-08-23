package com.example.example.ui.Estudiante
import android.os.Bundle
import android.util.Log
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
    private var originalDni: Long = 0
    private lateinit var gradoSeccionActual: String
    private lateinit var gradoSeccionNuevo: String
    private lateinit var updatedNombres: String
    private lateinit var updatedApellidos: String
    private  var updatedCelular:Long=0
    private var updatedDni: Long =0
    private  var updatedGrado: Int=0
    private lateinit var updatedSeccion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_estudiante)
        findButtons()
        listener()


    }

    private fun listener() {
        buttonModificar.setOnClickListener {
            updatedNombres = editTextNombres.text.toString().trim()
            updatedApellidos = editTextApellidos.text.toString().trim()
            updatedCelular = editTextCelular.text.toString().trim().toLongOrNull()!!
            updatedDni = editTextDni.text.toString().trim().toLongOrNull()!!
            updatedGrado = spinnerGrado.selectedItem.toString().trim().toIntOrNull()!!
            updatedSeccion = spinnerSeccion.selectedItem.toString().trim()
            gradoSeccionNuevo = "${updatedGrado}${updatedSeccion}"

            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
               updatedCelular.toString().length == 9 && updatedDni.toString().length == 8 &&
                updatedSeccion.isNotEmpty()) {

            } else {
                Toast.makeText(
                    this,
                    "Por favor complete todos los campos correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonEliminar.setOnClickListener {
            firestore.collection("Aula").get()
                .addOnSuccessListener { snapshot ->
                }
        }
    }

    private fun getData() {
        val nombres = intent.getStringExtra("nombres") ?: ""
        val apellidos = intent.getStringExtra("apellidos") ?: ""
        val celular = intent.getLongExtra("celularApoderado", 0L)
        val dni = intent.getLongExtra("dni", 0L)
        val grado = intent.getIntExtra("grado", 0)
        val seccion = intent.getStringExtra("seccion") ?: ""
        Log.d("EditEstudiante", "Grado recibido: $grado")
        originalDni = dni
        gradoSeccionActual = "${grado}${seccion}"
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular.toString())
        editTextDni.setText(dni.toString())
        initButton()
        setSpinnerValue(spinnerGrado, grado.toString())
        setSpinnerValue(spinnerSeccion, seccion)
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

    private fun initButton() {
        val grados = arrayOf("1", "2", "3", "4", "5")
        val adapterGrados = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterGrados

        val secciones = arrayOf("A", "B", "C", "D", "E")
        val adapterSecciones =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeccion.adapter = adapterSecciones
    }

    private fun setSpinnerValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String>
        val position = adapter?.getPosition(value) ?: 0
        spinner.setSelection(position)
    }

    private fun notifyEstudianteFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("EstudianteFragment")
        if (fragment is EstudianteFragment) {
            fragment.refreshData()
        }
    }
}
