package com.example.example.ui.Incidencia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.example.R
import com.example.example.databinding.ActivityAgregarIncidenciaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class AgregarIncidencia : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarIncidenciaBinding
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarIncidenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        storageRef = storage.reference

        setupUI()
        setupImagePicker()
        setupButtonListener()
    }

    private fun setupUI() {
        binding.tvEstudiante.text = "${intent.getStringExtra("EXTRA_STUDENT_LAST_NAME")} ${intent.getStringExtra("EXTRA_STUDENT_NAME")}"
        setupSpinner(binding.spinnerGravedad, arrayOf("Moderado", "Grave", "Muy grave"))
        setupSpinner(binding.spinnerTipo, arrayOf("Conductual", "Académicas", "Vestimenta", "Otros"))
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, R.layout.item_spinner, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let {
                    imageUri = it
                    Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions().centerCrop())
                        .into(binding.imageViewEvidencia)
                }
            }
        }

        binding.imageViewEvidencia.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        }
    }

    private fun setupButtonListener() {
        binding.btnRegistrarIncidencia.setOnClickListener {
            binding.btnRegistrarIncidencia.isEnabled = false
            imageUri?.let { subirImagen(it) } ?: guardarDatosIncidencia(null)
        }
    }

    private fun obtenerHoraActual(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }.format(Date())

    private fun obtenerFechaActual(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }.format(Date())

    private fun subirImagen(uri: Uri) {
        val ref = storageRef.child("incidencias/${UUID.randomUUID()}")
        ref.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception ?: Exception("Error desconocido")
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) guardarDatosIncidencia(task.result.toString())
            else mostrarError("Error al obtener la URL de la imagen")
        }.addOnFailureListener { mostrarError("Error al subir la imagen") }
    }

    private fun guardarDatosIncidencia(urlImagen: String?) {
        val userEmail = auth.currentUser?.email ?: return

        firestore.collection("Profesor")
            .whereEqualTo("correo", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    val incidencia = crearIncidencia(doc.getString("nombres"), doc.getString("apellidos"), urlImagen)
                    firestore.collection("Incidencia").add(incidencia)
                        .addOnSuccessListener { mostrarMensaje("Incidencia registrada exitosamente") }
                        .addOnFailureListener { mostrarError("Error al registrar la incidencia") }
                } else mostrarError("No se encontró el profesor con el correo proporcionado.")
            }
            .addOnFailureListener { mostrarError("Error al obtener datos del profesor: ${it.message}") }
    }

    private fun crearIncidencia(nombresProfesor: String?, apellidosProfesor: String?, urlImagen: String?) = hashMapOf(
        "fecha" to obtenerFechaActual(),
        "hora" to obtenerHoraActual(),
        "nombreEstudiante" to intent.getStringExtra("EXTRA_STUDENT_NAME"),
        "apellidoEstudiante" to intent.getStringExtra("EXTRA_STUDENT_LAST_NAME"),
        "gravedad" to binding.spinnerGravedad.selectedItem.toString(),
        "tipo" to binding.spinnerTipo.selectedItem.toString(),
        "detalle" to binding.edMultilinea.text.toString(),
        "apellidoProfesor" to apellidosProfesor,
        "nombreProfesor" to nombresProfesor,
        "urlImagen" to urlImagen
    )

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        finish()
        binding.btnRegistrarIncidencia.isEnabled = true
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        binding.btnRegistrarIncidencia.isEnabled = true
    }
}
