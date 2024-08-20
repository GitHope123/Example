package com.example.example.ui.Incidencia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.R
import com.example.example.databinding.ActivityAgregarIncidenciaBinding
import com.google.firebase.firestore.FirebaseFirestore

class AgregarIncidencia : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarIncidenciaBinding
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val estudianteList = mutableListOf<EstudianteAgregar>()
    private lateinit var estudianteAdapter: EstudianteAgregarAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarIncidenciaBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val studentName = intent.getStringExtra("EXTRA_STUDENT_NAME") ?: "N/A"
        val studentLastName = intent.getStringExtra("EXTRA_STUDENT_LAST_NAME") ?: "N/A"
        val studentGrade = intent.getIntExtra("EXTRA_STUDENT_GRADE", 0)
        val studentSection = intent.getStringExtra("EXTRA_STUDENT_SECTION") ?: "N/A"

        // Mostrar los datos en un Toast
        Toast.makeText(this, "Nombre: $studentName\nApellido: $studentLastName\nGrado: $studentGrade\nSección: $studentSection", Toast.LENGTH_LONG).show()

    }


}
