package com.example.example.ui.reportes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.example.R

class Reporte : Fragment() {

    companion object {
        fun newInstance() = Reporte()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_reporte, container, false)

        // Obtén referencias a los elementos del layout
        val button = view.findViewById<ImageButton>(R.id.buttonOpenLink)
        val editText = view.findViewById<EditText>(R.id.editTextLink)
        val compartir = view.findViewById<ImageButton>(R.id.imageButtonCompartir)

        // URL que se utilizará
        val url = "https://lookerstudio.google.com/reporting/55c2d7b2-0d63-4e02-895a-21ab9c7d018c"

        // Mostrar la URL en el EditText y hacerlo no editable
        editText.setText(url)
        editText.isFocusable = false
        editText.isClickable = false

        // Configuración para copiar la URL al portapapeles
        button.setOnClickListener {
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Reporte URL", url)
            clipboardManager.setPrimaryClip(clipData)

            // Mostrar un mensaje de confirmación
            Toast.makeText(requireContext(), "URL copiada al portapapeles", Toast.LENGTH_SHORT).show()
        }

        // Configuración para compartir la URL a otras aplicaciones
        compartir.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Revisa este reporte: $url")
                type = "text/plain"
            }

            // Crear el Intent Chooser para que el usuario elija la app
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        return view
    }
}

