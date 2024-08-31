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

class AddEstudiante : AppCompatActivity() {
    private lateinit var edTxtAddName: EditText
    private lateinit var edTxtAddLastName: EditText
    private lateinit var spinnerAddGrado: Spinner
    private lateinit var spinnerAddSection: Spinner
    private lateinit var addName: String
    private lateinit var addLastName: String
    private var addGrado: Int = 0
    private lateinit var addSection: String
    private lateinit var db: FirebaseFirestore
    private lateinit var btnAdd: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_estudiantes)
        db = FirebaseFirestore.getInstance()
        initComponents()
        listener()

    }

    private fun initComponents() {
        edTxtAddName = findViewById(R.id.addNameStudent)
        edTxtAddLastName = findViewById(R.id.addLastNameStudent)
        spinnerAddGrado = findViewById(R.id.addSpinnerGradoStudent)
        spinnerAddSection = findViewById(R.id.addSpinnerSectionStudent)
        btnAdd = findViewById(R.id.buttonAddStudent)
        updateGrado()
    }

    private fun updateGrado() {
        val grados = arrayOf("1", "2", "3", "4", "5")
        val adapterGrados = ArrayAdapter(this, R.layout.spinner_item_selected, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddGrado.adapter = adapterGrados
        spinnerAddGrado.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gradoSeleccionado=spinnerAddGrado.selectedItem.toString()
                updateSecciones(gradoSeleccionado)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
    private fun updateSecciones(gradoSeleccionado:String){
        val secciones= if(gradoSeleccionado=="1"){
                arrayOf("A","B","C","D","E")
            }
            else{
                arrayOf("A","B","C","D")
            }

        val adapterSecciones = ArrayAdapter(this, R.layout.spinner_item_selected, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddSection.adapter = adapterSecciones
        spinnerAddSection.isEnabled = gradoSeleccionado != "Todas"

    }

    private fun listener() {
        btnAdd.setOnClickListener {
            saveStudentToFirebase()
        }
    }

    private fun saveStudentToFirebase() {
        addName = edTxtAddName.text.toString().trim()
        addLastName = edTxtAddLastName.text.toString().trim()
        addGrado = spinnerAddGrado.selectedItem.toString().trim().toIntOrNull()?:0
        addSection = spinnerAddSection.selectedItem.toString().trim()
        if (addName.isNotEmpty() && addLastName.isNotEmpty() &&
           addSection.isNotEmpty()
            ) {
                val student = Estudiante(
                    idEstudiante = "",
                    nombres = addName,
                    apellidos = addLastName,
                    grado = addGrado,
                    seccion = addSection,
                    cantidadIncidencias = 0,
                )
                db.collection("Estudiante")
                    .add(student)
                    .addOnSuccessListener { documentReference->
                        val updatedStudent = student.copy(idEstudiante = documentReference.id)
                        documentReference.update("idEstudiante",updatedStudent.idEstudiante)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Estudiante agregado con éxito", Toast.LENGTH_SHORT).show()
                                clearFields()
                                finish()// Navigate to the ProfesorFragment
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al actualizar ID del estudiante", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Error al agregar al estudiante",Toast.LENGTH_SHORT).show()
                    }

        } else {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        edTxtAddName.text.clear()
        edTxtAddLastName.text.clear()
    }


}