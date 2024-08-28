package com.example.example.ui.Tutorias

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.example.R
import com.google.android.gms.fido.fido2.api.common.RequestOptions

class DescripcionRevisar : AppCompatActivity() {
    private lateinit var tutoria: TutoriaClass
    private lateinit var tvNombreEstudiante: TextView
    private lateinit var tvRevisado: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvHora: TextView
    private lateinit var tvGrado: TextView
    private lateinit var tvSeccion: TextView
    private lateinit var tvProfesor:TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvTipo: TextView
    private lateinit var tvGravedad: TextView
    private lateinit var tvDetalle: TextView
    private lateinit var imagen: ImageView
    private lateinit var checkBoxRevisado: CheckBox
    private lateinit var btnEnviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descripcion_revisar)
        // Inicializar views
        initViews()
        getIntentData()
        setData()
        ocultar()
    }

    private fun initViews() {
        tvNombreEstudiante = findViewById(R.id.tvNombreEstudiante)
        tvFecha = findViewById(R.id.tvFecha)
        tvHora = findViewById(R.id.tvHora)
        checkBoxRevisado = findViewById(R.id.checkBoxRevisado)
        tvGrado = findViewById(R.id.tvGrado)
        tvSeccion = findViewById(R.id.tvSeccion)
        tvEstado = findViewById(R.id.tvEstado)
        tvTipo = findViewById(R.id.tvTipo)
        tvGravedad = findViewById(R.id.tvGravedad)
        tvDetalle = findViewById(R.id.tvDetalle)
        imagen = findViewById(R.id.imagen)
        tvProfesor= findViewById(R.id.tvProfesor)
        tvRevisado=findViewById(R.id.tvRevisado)
        btnEnviar= findViewById(R.id.btnEnviar)
    }

    private fun getIntentData() {
        tutoria = intent.getSerializableExtra("TUTORIA_EXTRA") as TutoriaClass
    }

    private fun setData() {
        // Configurar datos en las views
        tvNombreEstudiante.text = "${tutoria.nombreEstudiante} ${tutoria.apellidoEstudiante}"
        tvFecha.text = tutoria.fecha
        tvHora.text = tutoria.hora
        tvGrado.text = tutoria.grado.toString()
        tvSeccion.text = tutoria.seccion
        tvEstado.text = tutoria.estado
        tvTipo.text = tutoria.tipo
        tvProfesor.text = "${tutoria.nombreProfesor} ${tutoria.apellidoProfesor}"
        tvGravedad.text = tutoria.gravedad
        tvDetalle.text = tutoria.detalle

        tutoria.urlImagen?.let { uri ->
            Glide.with(this)
                .load(uri)
                .apply(com.bumptech.glide.request.RequestOptions().centerCrop())
                .into(imagen)
        }
    }
    private fun ocultar(){
        if (tutoria.estado=="Revisado"){
            checkBoxRevisado.visibility= View.GONE
            tvRevisado.visibility=View.GONE
            btnEnviar.visibility=View.GONE
        }
    }
}