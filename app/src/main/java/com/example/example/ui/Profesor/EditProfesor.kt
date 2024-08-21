    package com.example.example.ui.Profesor

    import android.content.Context
    import android.os.Bundle
    import android.util.Log
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.example.example.R
    import com.google.firebase.firestore.FirebaseFirestore
    import java.lang.NumberFormatException

    class EditProfesor : AppCompatActivity() {
        private lateinit var firestore: FirebaseFirestore
        private lateinit var editTextNombres: EditText
        private lateinit var editTextApellidos: EditText
        private lateinit var editTextCelular: EditText
        private lateinit var editTextMateria: EditText
        private lateinit var editTextCorreo: EditText
        private lateinit var buttonGuardar: Button
        private lateinit var buttonEliminar: Button
        private lateinit var idProfesor: String
        private lateinit var nombres: String
        private lateinit var apellidos: String
        private var celular: Long = 0
        private lateinit var materia: String
        private lateinit var correo: String
        private lateinit var updatedNombres: String
        private lateinit var updatedApellidos: String
        private lateinit var updatedMateria: String
        private lateinit var updatedCorreo: String
        private var updatedCelular: Long = 0
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_edit_profesor)
            idProfesor = intent.getStringExtra("idProfesor") ?: ""
            nombres = intent.getStringExtra("nombres") ?: ""
            apellidos = intent.getStringExtra("apellidos") ?: ""
            celular = intent.getLongExtra("celular", 0) // Use getLongExtra for Long
            materia = intent.getStringExtra("materia") ?: ""
            correo = intent.getStringExtra("correo") ?: ""
            init()
            loadData()
            listener()
        }

        private fun init() {

            firestore = FirebaseFirestore.getInstance()
            editTextNombres = findViewById(R.id.editTextNombres)
            editTextApellidos = findViewById(R.id.editTextApellidos)
            editTextCelular = findViewById(R.id.editTextCelular)
            editTextMateria = findViewById(R.id.editTextMateria)
            editTextCorreo = findViewById(R.id.editTextCorreo)
            buttonGuardar = findViewById(R.id.buttonModificar)
            buttonEliminar = findViewById(R.id.buttonEliminar)


        }

        private fun loadData() {
            editTextNombres.setText(nombres)
            editTextApellidos.setText(apellidos)
            editTextCelular.setText(celular.toString())
            editTextMateria.setText(materia)
            editTextCorreo.setText(correo)


        }

        private fun listener() {
            buttonGuardar.setOnClickListener {
                updatedNombres = editTextNombres.text.toString()
                updatedApellidos = editTextApellidos.text.toString()
                updatedMateria = editTextMateria.text.toString()
                updatedCorreo = editTextCorreo.text.toString()
                updatedCelular = editTextCelular.text.toString().toLong()
                updateData()
            }
            buttonEliminar.setOnClickListener {
                removeTeacher()
            }

        }


        private fun updateData() {
            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
                updatedCelular != null && updatedMateria.isNotEmpty() &&
                updatedCorreo.isNotEmpty() && updatedCelular.toString().length == 9
            ) {

                val updatedProfesor = mapOf(
                    "nombres" to updatedNombres,
                    "apellidos" to updatedApellidos,
                    "celular" to updatedCelular,
                    "materia" to updatedMateria,
                    "correo" to updatedCorreo
                )

                firestore.collection("Profesor").document(idProfesor)
                    .update(updatedProfesor)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profesor actualizado con éxito", Toast.LENGTH_SHORT)
                            .show()
                        // Notify ProfesorFragment to refresh its data
                        notifyProfesorFragment()
                        finish() // Close the activity
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error al actualizar el profesor: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "Por favor complete todos los campos correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        private fun notifyProfesorFragment() {
            val fragment = supportFragmentManager.findFragmentByTag("ProfesorFragment")
            if (fragment is ProfesorFragment) {
                fragment.refreshData()
            }
        }

        private fun removeTeacher() {

            val documentRef = firestore.collection("Profesor").document(idProfesor)

            // Eliminar el documento
            documentRef.delete()
                .addOnSuccessListener {
                    // Código que se ejecuta si la eliminación fue exitosa
                    Log.d("Firestore", "Documento eliminado exitosamente")
                    Toast.makeText(this, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    // Código que se ejecuta si ocurrió un error al eliminar el documento
                    Log.w("Firestore", "Error al eliminar el documento", e)
                    Toast.makeText(this, "Error al eliminar el profesor: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }



