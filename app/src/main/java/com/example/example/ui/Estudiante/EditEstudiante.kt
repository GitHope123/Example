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
        val nombres = intent.getStringExtra("nombres") ?: ""
        val apellidos = intent.getStringExtra("apellidos") ?: ""
        val celular = intent.getLongExtra("celular", 0L)
        val dni = intent.getLongExtra("dni", 0L)
        val grado = intent.getIntExtra("grado", 0)
        val seccion = intent.getStringExtra("seccion") ?: ""
        Log.d("EditEstudiante", "Grado recibido: $grado")

        originalDni = dni

        // Rellenar los campos con los datos recibidos
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular.toString())
        editTextDni.setText(dni.toString())
        initButton()
        setSpinnerValue(spinnerGrado, grado.toString())
        setSpinnerValue(spinnerSeccion, seccion)

        buttonModificar.setOnClickListener {
            val updatedNombres = editTextNombres.text.toString().trim()
            val updatedApellidos = editTextApellidos.text.toString().trim()
            val updatedCelular = editTextCelular.text.toString().trim().toLongOrNull()
            val updatedDni = editTextDni.text.toString().trim().toLongOrNull()
            val updatedGrado = spinnerGrado.selectedItem.toString().trim().toIntOrNull()
            val updatedSeccion = spinnerSeccion.selectedItem.toString().trim()

            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
                updatedCelular != null && updatedCelular.toString().length == 9 &&
                updatedDni != null && updatedDni.toString().length == 8 &&
                updatedSeccion.isNotEmpty() && updatedGrado != null) {

                val documentPathOld = "${grado}${seccion}" // Sección antigua
                val documentPathNew = "${updatedGrado}${updatedSeccion}" // Nueva sección

                val docRefOld = firestore.collection("Aula").document(documentPathOld)
                val docRefNew = firestore.collection("Aula").document(documentPathNew)

                firestore.runTransaction { transaction ->
                    val oldDocument = transaction.get(docRefOld)
                    val newDocument = transaction.get(docRefNew)

                    val estudiantesListOld = oldDocument.get("estudiantes") as? MutableList<Map<String, Any>> ?: mutableListOf()
                    val estudiantesListNew = newDocument.get("estudiantes") as? MutableList<Map<String, Any>> ?: mutableListOf()

                    // Actualizar el estudiante solo si cambia el grado o la sección
                    if (grado != updatedGrado || seccion != updatedSeccion) {
                        // Eliminar al estudiante de la sección antigua
                        estudiantesListOld.removeIf { estudiante -> (estudiante["dni"] as? Long) == originalDni }

                        // Agregar al estudiante actualizado a la nueva sección
                        val updatedEstudiante = mapOf(
                            "nombres" to updatedNombres,
                            "apellidos" to updatedApellidos,
                            "celularApoderado" to updatedCelular,
                            "dni" to updatedDni,
                            "grado" to updatedGrado,
                            "seccion" to updatedSeccion
                        )
                        estudiantesListNew.add(updatedEstudiante)

                        transaction.update(docRefOld, "estudiantes", estudiantesListOld)
                        transaction.update(docRefNew, "estudiantes", estudiantesListNew)
                    } else {
                        // Solo actualizar los datos del estudiante en la sección actual
                        val updatedEstudiante = mapOf(
                            "nombres" to updatedNombres,
                            "apellidos" to updatedApellidos,
                            "celularApoderado" to updatedCelular,
                            "dni" to updatedDni,
                            "grado" to updatedGrado,
                            "seccion" to updatedSeccion
                        )
                        val updatedEstudiantesListOld = estudiantesListOld.map {
                            if ((it["dni"] as? Long) == originalDni) updatedEstudiante else it
                        }
                        transaction.update(docRefOld, "estudiantes", updatedEstudiantesListOld)
                    }

                    null
                }.addOnSuccessListener {
                    Toast.makeText(this, "Estudiante actualizado con éxito", Toast.LENGTH_SHORT).show()
                    notifyEstudianteFragment()
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar el estudiante: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirestoreError", "Error en la transacción", e)
                }

            } else {
                Toast.makeText(this, "Por favor complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        buttonEliminar.setOnClickListener {
            firestore.collection("Aula").get()
                .addOnSuccessListener { snapshot ->
                    val batch = firestore.batch()
                    var documentFound = false

                    snapshot.documents.forEach { document ->
                        val estudiantesList = document.get("estudiantes") as? MutableList<Map<String, Any>> ?: mutableListOf()
                        val estudianteIndex = estudiantesList.indexOfFirst {
                            (it["dni"] as? Long) == originalDni
                        }

                        if (estudianteIndex != -1) {
                            estudiantesList.removeAt(estudianteIndex)
                            batch.update(document.reference, "estudiantes", estudiantesList)
                            documentFound = true
                        }
                    }

                    if (documentFound) {
                        batch.commit()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Estudiante eliminado con éxito", Toast.LENGTH_SHORT).show()
                                notifyEstudianteFragment()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al eliminar el estudiante: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Estudiante no encontrado", Toast.LENGTH_SHORT).show()
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
