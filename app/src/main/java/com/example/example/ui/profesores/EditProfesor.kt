    package com.example.example.ui.profesores

    import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.example.example.R
    import com.google.firebase.firestore.FirebaseFirestore

    class EditProfesor : AppCompatActivity() {
        private lateinit var firestore: FirebaseFirestore
        private lateinit var editTextNombres: EditText
        private lateinit var editTextApellidos: EditText
        private lateinit var editTextCelular: EditText
        private lateinit var editTextCargo: EditText
        private lateinit var editTextDni:EditText
        private lateinit var editTextCorreo: EditText
        private lateinit var editTextPassword: EditText
        private lateinit var buttonGuardar: Button
        private lateinit var buttonEliminar: Button
        private lateinit var idProfesor: String
        private lateinit var nombres: String
        private lateinit var apellidos: String
        private var celular: Long = 0
        private lateinit var cargo: String
        private lateinit var correo: String
        private lateinit var password: String
        private  var dni:Long=0
        private lateinit var updatedNombres: String
        private lateinit var updatedApellidos: String
        private var updatedCelular: Long = 0
        private lateinit var updatedCargo: String
        private lateinit var updatedCorreo: String
        private lateinit var updatePassword: String
        private  var updatedDni:Long=0
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_edit_profesor)
            idProfesor = intent.getStringExtra("idProfesor") ?: ""
            nombres = intent.getStringExtra("nombres") ?: ""
            apellidos = intent.getStringExtra("apellidos") ?: ""
            celular = intent.getLongExtra("celular", 0) // Use getLongExtra for Long
            cargo = intent.getStringExtra("cargo") ?: ""
            correo = intent.getStringExtra("correo") ?: ""
            password = intent.getStringExtra("password")?: ""
            dni=intent.getLongExtra("dni",0)
            init()
            loadData()
            listener()
        }

        private fun init() {

            firestore = FirebaseFirestore.getInstance()
            editTextNombres = findViewById(R.id.editTextNombres)
            editTextApellidos = findViewById(R.id.editTextApellidos)
            editTextCelular = findViewById(R.id.editTextCelular)
            editTextCargo = findViewById(R.id.editTextCargo)
            editTextCorreo = findViewById(R.id.editTextCorreo)
            editTextPassword = findViewById(R.id.editTextPassword)
            editTextDni=findViewById(R.id.editTextDni)
            buttonGuardar = findViewById(R.id.buttonModificar)
            buttonEliminar = findViewById(R.id.buttonEliminar)
        }

        private fun loadData() {
            editTextNombres.setText(nombres)
            editTextApellidos.setText(apellidos)
            editTextCelular.setText(celular.toString())
            editTextCargo.setText(cargo)
            editTextCorreo.setText(correo)
            editTextPassword.setText(password)
            editTextDni.setText(dni.toString())
        }

        private fun listener() {
            buttonGuardar.setOnClickListener {
                updatedNombres = editTextNombres.text.toString()
                updatedApellidos = editTextApellidos.text.toString()
                updatedCargo = editTextCargo.text.toString()
                updatedCorreo = editTextCorreo.text.toString()
                updatedCelular = editTextCelular.text.toString().toLong()
                updatePassword = editTextPassword.text.toString()
                updatedDni=editTextDni.text.toString().toLong()
                updateData()
            }
            buttonEliminar.setOnClickListener {
                removeTeacher()
            }

        }


        private fun updateData() {
            if (updatedNombres.isNotEmpty() && updatedApellidos.isNotEmpty() &&
                updatedCelular != null && updatedCargo.isNotEmpty() &&
                updatedCorreo.isNotEmpty() && updatedCelular.toString().length == 9 && updatePassword.isNotEmpty() && updatedDni.toString().length==8) {
                if(updatedCorreo.endsWith("@colegiosparroquiales.com")){
                    val updatedProfesor = mapOf(
                        "nombres" to updatedNombres,
                        "apellidos" to updatedApellidos,
                        "celular" to updatedCelular,
                        "cargo" to updatedCargo,
                        "correo" to updatedCorreo,
                        "password" to updatePassword,
                        "dni" to updatedDni
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

                }
                else{
                    Toast.makeText(
                        this,
                        "Su correo debe terminar con @gmail.com",
                        Toast.LENGTH_SHORT
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
            documentRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar el profesor: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }



