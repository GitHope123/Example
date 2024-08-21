package com.example.example.ui.Estudiante

import android.os.Bundle
import android.util.Log

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
    private lateinit var idEstudiante: String
    private lateinit var nombres: String
    private lateinit var apellidos: String
    private var celular: Long = 0
    private var dni: Long = 0
    private var grado: Int = 0
    private lateinit var seccion: String
    private lateinit var updatedNombres: String
    private lateinit var updatedApellidos: String
    private var updatedCelular: Long = 0
    private var updatedDni: Long = 0
    private var updatedGrado: Int = 0
    private lateinit var updatedSeccion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_estudiante)
        firestore = FirebaseFirestore.getInstance()
        editTextNombres = findViewById(R.id.editTextNombres)
        editTextApellidos = findViewById(R.id.editTextApellidos)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextDni = findViewById(R.id.editTextDni)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSection)
        buttonModificar = findViewById(R.id.buttonModificar)
        buttonEliminar = findViewById(R.id.buttonEliminar)

        // Obtener datos del Intent
        idEstudiante = intent.getStringExtra("idEstudiante") ?: ""
        nombres = intent.getStringExtra("nombres") ?: ""
        apellidos = intent.getStringExtra("apellidos") ?: ""
        celular = intent.getLongExtra("celular", 0)
        dni = intent.getLongExtra("dni", 0)
        grado = intent.getIntExtra("grado", 0)
        seccion = intent.getStringExtra("seccion") ?: ""
        Log.d("EditEstudiante", "Grado recibido: $grado")

        // Rellenar los campos con los datos recibidos
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular.toString())
        editTextDni.setText(dni.toString())

        // Configurar los adaptadores y establecer el valor del spinner
        initButton()

        // Establecer el valor seleccionado en el Spinner de grado
        setSpinnerValue(spinnerGrado, grado.toString())

        // Establecer el valor seleccionado en el Spinner de sección
        setSpinnerValue(spinnerSeccion, seccion)

        buttonModificar.setOnClickListener {
            updatedNombres = editTextNombres.text.toString().trim()
            updatedApellidos = editTextApellidos.text.toString().trim()
            updatedCelular = editTextCelular.text.toString().trim().toLong()
            updatedDni = editTextDni.text.toString().trim().toLong()
            updatedGrado = spinnerGrado.selectedItemPosition + 1 // Ajusta el grado según tu lógica
            updatedSeccion = spinnerSeccion.selectedItem.toString().trim()

            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
                updatedCelular.toString().length == 9 && updatedDni.toString().length == 8 &&
                updatedSeccion.isNotEmpty()&&updatedGrado.toString().isNotEmpty()) {

                firestore.collection("Aula").get()
                    .addOnSuccessListener { snapshot ->
                        val batch = firestore.batch()

                        snapshot.documents.forEach { document ->
                            val estudiantesList = document.get("estudiantes") as? List<Map<String, Any>> ?: emptyList()
                            estudiantesList.find { it["idEstudiante"] == idEstudiante }?.let { estudiante ->
                                val updatedEstudiante = estudiante.toMutableMap().apply {
                                    put("nombres", updatedNombres)
                                    put("apellidos", updatedApellidos)
                                    put("celular", updatedCelular)
                                    put("dni", updatedDni)
                                    put("grado", updatedGrado)
                                    put("seccion", updatedSeccion)
                                }

                                val estudiantesRef = document.reference.collection("estudiantes")
                                estudiantesRef.document(idEstudiante).set(updatedEstudiante)
                            }
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Estudiante actualizado con éxito", Toast.LENGTH_SHORT).show()
                                notifyEstudianteFragment()
                                finish() // Cierra la actividad
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar el estudiante: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }


        buttonEliminar.setOnClickListener {
            // Encontrar el documento de Aula que contiene el estudiante
            firestore.collection("Aula").get()
                .addOnSuccessListener { snapshot ->
                    val batch = firestore.batch()

                    snapshot.documents.forEach { document ->
                        val estudiantesList = document.get("estudiantes") as? List<Map<String, Any>> ?: emptyList()
                        estudiantesList.find { it["idEstudiante"] == idEstudiante }?.let { estudiante ->
                            // Eliminar el estudiante del documento de Aula
                            val estudiantesRef = document.reference.collection("estudiantes")
                            estudiantesRef.document(idEstudiante).delete()
                        }
                    }

                    batch.commit()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Estudiante eliminado con éxito", Toast.LENGTH_SHORT).show()
                            notifyEstudianteFragment()
                            finish() // Cierra la actividad
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al eliminar el estudiante: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun initButton() {
        val grados = arrayOf("1", "2", "3", "4", "5")
        val adapterGrados = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterGrados

        val secciones = arrayOf("A", "B", "C", "D", "E")
        val adapterSecciones = ArrayAdapter(this, android.R.layout.simple_spinner_item, secciones)
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


