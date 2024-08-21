package com.example.example.ui.Incidencia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AgregarIncidencia : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarIncidenciaBinding
    private lateinit var studentName: String
    private lateinit var studentLastName: String
    private lateinit var spinnerGravedad: Spinner
    private lateinit var hora:TextView
    private lateinit var fecha:TextView
    private lateinit var edMultilinea:EditText
    private lateinit var spinnerTipo:Spinner
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
        init()
        set()
        val horaActual=obtenerHoraActual()
        val fechaActual= obtenerFechaActual()
        hora.text=horaActual
        fecha.text= fechaActual
        storageRef = FirebaseStorage.getInstance().reference
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
    private fun init(){
        studentName = intent.getStringExtra("EXTRA_STUDENT_NAME") ?: "N/A"
        studentLastName = intent.getStringExtra("EXTRA_STUDENT_LAST_NAME") ?: "N/A"
        studentGrade = intent.getIntExtra("EXTRA_STUDENT_GRADE", 0)
        studentSection = intent.getStringExtra("EXTRA_STUDENT_SECTION") ?: "N/A"
        estudiante=findViewById(R.id.tvEstudiante)
        spinnerGravedad=findViewById(R.id.spinnerGravedad)
        spinnerTipo=findViewById(R.id.spinnerTipo)
        hora= findViewById(R.id.tvHora)
        fecha= findViewById(R.id.tvFecha)
        edMultilinea=findViewById(R.id.edMultilinea)
        imageViewEvidencia=findViewById(R.id.imageViewEvidencia)
    }
    private fun set(){
        estudiante.text= studentLastName+" "+studentName
        val gravedad = arrayOf("Moderado", "Grave", "Muy grave")
        val adapterGravedad = ArrayAdapter(this,R.layout.item_spinner, gravedad)
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
        val adapterTipo = ArrayAdapter(this, R.layout.item_spinner, tipo)
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
    private fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        formatoHora.timeZone = TimeZone.getDefault() // Zona horaria del dispositivo
        return formatoHora.format(Date())
    }
    private fun obtenerFechaActual(): String {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatoFecha.timeZone = TimeZone.getDefault() // Asegúrate de usar la zona horaria correcta
        return formatoFecha.format(Date())
    }

}
