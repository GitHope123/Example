package com.example.example.ui.Incidencia

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.R
import com.example.example.databinding.ActivityAgregarIncidenciaBinding
import com.google.firebase.firestore.FirebaseFirestore

class AgregarIncidencia : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarIncidenciaBinding
    private lateinit var studentName: String
    private lateinit var studentLastName: String
    private lateinit var spinnerGravedad: Spinner
    private lateinit var spinnerTipo:Spinner
    private var studentGrade: Int = 0
    private lateinit var studentSection: String
    private lateinit var estudiante: TextView
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarIncidenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        set()

    }
    private fun init(){
        studentName = intent.getStringExtra("EXTRA_STUDENT_NAME") ?: "N/A"
        studentLastName = intent.getStringExtra("EXTRA_STUDENT_LAST_NAME") ?: "N/A"
        studentGrade = intent.getIntExtra("EXTRA_STUDENT_GRADE", 0)
        studentSection = intent.getStringExtra("EXTRA_STUDENT_SECTION") ?: "N/A"
        estudiante=findViewById(R.id.tvEstudiante)
        spinnerGravedad=findViewById(R.id.spinnerGravedad)
        spinnerTipo=findViewById(R.id.spinnerTipo)
    }
    private fun set(){
        estudiante.text= studentLastName+" "+studentName
        val gravedad = arrayOf("Moderado", "Grave", "Muy grave")
        val adapterGravedad = ArrayAdapter(this, android.R.layout.simple_spinner_item, gravedad)
        adapterGravedad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGravedad.adapter = adapterGravedad

        spinnerGravedad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gravedadSeleccionado = spinnerGravedad.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected
            }
        }

        val tipo = arrayOf("Conductual", "Academicas", "Vestimenta", "Otros")
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipo)
        adapterGravedad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val TipoSeleccionado = spinnerTipo.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected
            }
        }

    }

}
