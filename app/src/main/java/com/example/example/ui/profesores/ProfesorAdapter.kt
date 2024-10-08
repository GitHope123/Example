package com.example.example.ui.profesores

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class ProfesorAdapter(
    private val context: Context,
    private val profesores: List<Profesor>,
    private val onEditClickListener: (Profesor) -> Unit,
    private val isEditButtonVisible: Boolean // Parámetro para visibilidad del editButton
) : RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.bind(profesor)
    }

    override fun getItemCount(): Int = profesores.size

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombreCompleto: TextView = itemView.findViewById(R.id.textViewNombreCompletos)
        private val textViewTelefono: TextView = itemView.findViewById(R.id.textViewTelefono)
        private val textViewCorreo: TextView = itemView.findViewById(R.id.textViewCargo)
        private val editButton: ImageButton = itemView.findViewById(R.id.imageButtonEdit)
        private val buttonLlamada: ImageButton = itemView.findViewById(R.id.imageButtonLlamada)

        fun bind(profesor: Profesor) {
            val nombreCompleto = "${profesor.apellidos} ${profesor.nombres}"
            textViewNombreCompleto.text = nombreCompleto
            textViewTelefono.text = profesor.celular.toString()
            textViewCorreo.text = profesor.cargo

            // Ajusta la visibilidad del editButton
            editButton.visibility = if (isEditButtonVisible) View.VISIBLE else View.GONE

            // Configura el click listener del editButton
            editButton.setOnClickListener {
                val intent = Intent(context, EditProfesor::class.java).apply {
                    putExtra("idProfesor", profesor.idProfesor)
                    putExtra("nombres", profesor.nombres)
                    putExtra("apellidos", profesor.apellidos)
                    putExtra("celular", profesor.celular)
                    putExtra("cargo", profesor.cargo)
                    putExtra("correo", profesor.correo)
                    putExtra("password", profesor.password)
                    putExtra("dni", profesor.dni)
                }
                context.startActivity(intent)
            }

            // Configura el click listener para hacer una llamada
            buttonLlamada.setOnClickListener {
                val phoneNumber = profesor.celular.toString()
                if (phoneNumber.isNotBlank()) {
                    try {
                        val callIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                        }
                        context.startActivity(callIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error al intentar realizar la llamada", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Número de teléfono no válido", Toast.LENGTH_SHORT).show()
                }
            }

            itemView.setOnClickListener {
                onEditClickListener(profesor)
            }
        }
    }
}
