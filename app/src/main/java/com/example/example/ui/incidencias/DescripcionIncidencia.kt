package com.example.example.ui.incidencias

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.example.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DescripcionIncidencia : AppCompatActivity() {
    private lateinit var id: String
    private lateinit var fecha: String
    private lateinit var hora: String
    private lateinit var nombreEstudiante: String
    private lateinit var apellidoEstudiante: String
    private lateinit var seccion: String
    private lateinit var tipo: String
    private lateinit var gravedad: String
    private lateinit var estado: String
    private lateinit var detalle: String
    private lateinit var imageUri: String
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    //
    private lateinit var tvFecha: TextView
    private lateinit var tvHora: TextView
    private lateinit var tvNombreCompleto: TextView
    private lateinit var tvGrado: TextView
    private lateinit var tvSeccion: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvTipo: TextView
    private lateinit var tvGravedad: TextView
    private lateinit var tvDetalle: TextView
    private lateinit var imagen: ImageView
    private lateinit var eliminar: ImageView
    private var grado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descripcion_incidencia)
        getPutExtra()
        init()
        set()
    }

    private fun getPutExtra() {
        id = intent.getStringExtra("INCIDENCIA_ID") ?: ""
        fecha = intent.getStringExtra("INCIDENCIA_FECHA") ?: ""
        hora = intent.getStringExtra("INCIDENCIA_HORA") ?: ""
        nombreEstudiante = intent.getStringExtra("INCIDENCIA_NOMBRE") ?: ""
        apellidoEstudiante = intent.getStringExtra("INCIDENCIA_APELLIDO") ?: ""
        grado = intent.getIntExtra("INCIDENCIA_GRADO", 0)
        seccion = intent.getStringExtra("INCIDENCIA_SECCION") ?: ""
        tipo = intent.getStringExtra("INCIDENCIA_TIPO") ?: ""
        gravedad = intent.getStringExtra("INCIDENCIA_GRAVEDAD") ?: ""
        estado = intent.getStringExtra("INCIDENCIA_ESTADO") ?: ""
        detalle = intent.getStringExtra("INCIDENCIA_DETALLE") ?: ""
        imageUri = intent.getStringExtra("INCIDENCIA_FOTO_URL") ?: ""

    }

    private fun init() {
        tvFecha = findViewById(R.id.tvFecha)
        tvHora = findViewById(R.id.tvHora)
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto)
        tvGrado = findViewById(R.id.tvGrado)
        tvSeccion = findViewById(R.id.tvSeccion)
        tvEstado = findViewById(R.id.tvEstado)
        tvTipo = findViewById(R.id.tvTipo)
        tvGravedad = findViewById(R.id.tvGravedad)
        tvDetalle = findViewById(R.id.tvDetalle)
        imagen = findViewById(R.id.imagen)
        eliminar = findViewById(R.id.eliminarIncidencia)
        if (estado == "Pendiente") {
            eliminar.visibility = View.VISIBLE
        }else{
            eliminar.visibility = View.GONE

        }
        imageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions().centerCrop())
                .into(imagen)
        }
    }

    private fun set() {
        tvFecha.text = fecha
        tvHora.text = hora
        tvNombreCompleto.text = "$apellidoEstudiante $nombreEstudiante"
        tvGrado.text = grado.toString()
        tvSeccion.text = seccion
        tvEstado.text = estado
        tvTipo.text = tipo
        tvGravedad.text = gravedad
        tvDetalle.text = detalle

        eliminar.setOnClickListener {
            deleteIncidencia()
        }
    }
    private fun deleteIncidencia() {
        if (id.isNotEmpty() && nombreEstudiante.isNotEmpty() && apellidoEstudiante.isNotEmpty()) {
            firestore.collection("Incidencia")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    // Actualizar el conteo de incidencias del estudiante
                    updateStudentIncidenciaCount()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar incidencia: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "ID de incidencia o datos del estudiante inválidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStudentIncidenciaCount() {
        // Buscar el estudiante por nombre y apellido
        firestore.collection("Estudiante")
            .whereEqualTo("nombres", nombreEstudiante)
            .whereEqualTo("apellidos", apellidoEstudiante)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val estudianteDocument = querySnapshot.documents.first()
                    val idEstudiante = estudianteDocument.id

                    // Reducir el conteo de incidencias en 1
                    firestore.collection("Estudiante")
                        .document(idEstudiante)
                        .update("cantidadIncidencias", FieldValue.increment(-1))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Incidencia eliminada y conteo actualizado", Toast.LENGTH_SHORT).show()
                            finish() // Cierra la actividad después de eliminar
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al actualizar el conteo de incidencias: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "No se encontró al estudiante", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar al estudiante: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}