package com.example.example.ui.Incidencia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.example.R
import com.example.example.databinding.ActivityAgregarIncidenciaBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class AgregarIncidencia : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarIncidenciaBinding
    private lateinit var studentName: String
    private lateinit var studentLastName: String
    private lateinit var auth: FirebaseAuth
    private lateinit var spinnerGravedad: Spinner
    private lateinit var hora: TextView
    private lateinit var fecha: TextView
    private lateinit var edMultilinea: EditText
    private lateinit var spinnerTipo: Spinner
    private var studentGrade: Int = 0
    private lateinit var studentSection: String
    private lateinit var estudiante: TextView
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var imageViewEvidencia: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarIncidenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        init()
        set()
        selectedImagen()
        selectedRegistrar()
    }

    private fun init() {
        studentName = intent.getStringExtra("EXTRA_STUDENT_NAME") ?: "N/A"
        studentLastName = intent.getStringExtra("EXTRA_STUDENT_LAST_NAME") ?: "N/A"
        studentGrade = intent.getIntExtra("EXTRA_STUDENT_GRADE", 0)
        studentSection = intent.getStringExtra("EXTRA_STUDENT_SECTION") ?: "N/A"
        estudiante = findViewById(R.id.tvEstudiante)
        spinnerGravedad = findViewById(R.id.spinnerGravedad)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        hora = findViewById(R.id.tvHora)
        fecha = findViewById(R.id.tvFecha)
        edMultilinea = findViewById(R.id.edMultilinea)
        imageViewEvidencia = findViewById(R.id.imageViewEvidencia)
    }

    private fun set() {
        estudiante.text = "$studentLastName $studentName"
        val gravedad = arrayOf("Moderado", "Grave", "Muy grave")
        val adapterGravedad = ArrayAdapter(this, R.layout.item_spinner, gravedad)
        adapterGravedad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGravedad.adapter = adapterGravedad

        val tipo = arrayOf("Conductual", "Académicas", "Vestimenta", "Otros")
        val adapterTipo = ArrayAdapter(this, R.layout.item_spinner, tipo)
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo
        fecha.text = obtenerFechaActual()
        hora.text = obtenerHoraActual()
    }

    private fun selectedImagen() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let {
                    imageUri = it
                    Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions().centerCrop())
                        .into(imageViewEvidencia)
                }
            }
        }

        imageViewEvidencia.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
    }

    private fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        formatoHora.timeZone = TimeZone.getDefault()
        return formatoHora.format(Date())
    }

    private fun selectedRegistrar() {
        binding.btnRegistrarIncidencia.setOnClickListener {
            val detalles = edMultilinea.text.toString().trim()
            if (detalles.isEmpty()) {
                Toast.makeText(this, "Incluye detalles de la incidencia", Toast.LENGTH_SHORT).show()
                binding.btnRegistrarIncidencia.isEnabled = true
                return@setOnClickListener
            }
            binding.btnRegistrarIncidencia.isEnabled = false
            if (imageUri != null) {
                subirImagen(imageUri!!)
            } else {
                guardarDatosIncidencia(null)
            }
        }
    }

    private fun obtenerFechaActual(): String {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatoFecha.timeZone = TimeZone.getDefault()
        return formatoFecha.format(Date())
    }

    private fun subirImagen(imageUri: Uri) {

        val ref = storageRef.child("incidencias/${UUID.randomUUID()}")
        ref.putFile(imageUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                guardarDatosIncidencia(downloadUri.toString())
            } else {
                Toast.makeText(this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT)
                    .show()
                guardarDatosIncidencia("")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            guardarDatosIncidencia("")
        }

    }

    private fun guardarDatosIncidencia(urlImagen: String?) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: return

        firestore.collection("Profesor")
            .whereEqualTo("correo", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    val nombresProfesor = doc.getString("nombres") ?: "Nombres no encontrados"
                    val apellidosProfesor = doc.getString("apellidos") ?: "Apellidos no encontrados"

                    val incidencia = hashMapOf(
                        "fecha" to obtenerFechaActual(),
                        "hora" to obtenerHoraActual(),
                        "nombreEstudiante" to studentName,
                        "apellidoEstudiante" to studentLastName,
                        "gravedad" to spinnerGravedad.selectedItem.toString(),
                        "tipo" to spinnerTipo.selectedItem.toString(),
                        "detalle" to edMultilinea.text.toString(),
                        "apellidoProfesor" to apellidosProfesor,
                        "nombreProfesor" to nombresProfesor,
                        "urlImagen" to urlImagen
                    )

                    firestore.collection("Incidencia")
                        .add(incidencia)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Incidencia registrada exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            binding.btnRegistrarIncidencia.isEnabled = true
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Error al registrar la incidencia",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btnRegistrarIncidencia.isEnabled = true
                        }
                } else {
                    Toast.makeText(
                        this,
                        "No se encontró el profesor con el correo proporcionado.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error al obtener datos del profesor: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}