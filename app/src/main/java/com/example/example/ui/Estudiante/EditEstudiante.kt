package com.example.example.ui.Estudiante

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.NumberFormatException

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
        val idEstudiante = intent.getStringExtra("idEstudiante") ?: ""
        val nombres = intent.getStringExtra("nombres") ?: ""
        val apellidos = intent.getStringExtra("apellidos") ?: ""
        val celular = intent.getLongExtra("celular",0)
        val dni = intent.getLongExtra("dni", 0 )
        val grado = intent.getIntExtra("grado", 0)
        val seccion = intent.getStringExtra("seccion") ?: ""

        // Rellenar los campos con los datos recibidos
        editTextNombres.setText(nombres)
        editTextApellidos.setText(apellidos)
        editTextCelular.setText(celular.toString())
        editTextDni.setText(dni.toString())
        setSpinnerValue(spinnerGrado, grado)
        setSpinnerValue(spinnerSeccion, seccion)

        buttonModificar.setOnClickListener {
            val updatedNombres = editTextNombres.text.toString().trim()
            val updatedApellidos = editTextApellidos.text.toString().trim()
            val updatedCelular: Long? = try {
                editTextCelular.text.toString().trim().toLong()
            } catch (e: NumberFormatException) {
                null
            }
            val updatedDni = editTextDni.text.toString().trim()
            val updatedGrado = spinnerGrado.selectedItemPosition
            val updatedSeccion = spinnerSeccion.selectedItem.toString().trim()

            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
                updatedCelular != null && updatedDni.isNotEmpty() &&
                updatedSeccion.isNotEmpty()&& updatedCelular.toString().length==9) {

                // Encontrar el documento de Aula que contiene el estudiante
                firestore.collection("Aula").get()
                    .addOnSuccessListener { snapshot ->
                        val batch = firestore.batch()

                        snapshot.documents.forEach { document ->
                            val estudiantesList = document.get("estudiantes") as? List<Map<String, Any>> ?: emptyList()
                            estudiantesList.find { it["idEstudiante"] == idEstudiante }?.let { estudiante ->
                                // Crear el nuevo objeto estudiante con los datos actualizados
                                val updatedEstudiante = estudiante.toMutableMap().apply {
                                    put("nombres", updatedNombres)
                                    put("apellidos", updatedApellidos)
                                    put("celular", updatedCelular)
                                    put("dni", updatedDni)
                                    put("grado", updatedGrado)
                                    put("seccion", updatedSeccion)
                                }

                                // Actualizar el estudiante en el documento de Aula
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

    private fun setSpinnerValue(spinner: Spinner, value: Any) {
        // Implementa la lógica para establecer el valor del Spinner
        // Esto puede variar según cómo configures tus adaptadores y datos
    }

    private fun notifyEstudianteFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("EstudianteFragment")
        if (fragment is EstudianteFragment) {
            fragment.refreshData()
        }
    }
}
