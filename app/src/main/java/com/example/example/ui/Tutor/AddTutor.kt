package com.example.example.ui.Tutor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor
import com.google.firebase.firestore.FirebaseFirestore

class AddTutor : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var buttonAceptar: Button
    private lateinit var buttonCancelar: Button
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerSeccion: Spinner
    private lateinit var tutorAdapter: TutorAdapter
    private val selectedProfesores = mutableSetOf<String>() // Usando IDs para el seguimiento de selección
    private val db = FirebaseFirestore.getInstance()

    private val grados = listOf("1", "2", "3", "4", "5") // Grados posibles
    private val secciones = listOf("A", "B", "C", "D", "E") // Secciones posibles

    private lateinit var allCombinations: Set<String>
    private lateinit var usedCombinations: Set<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tutor)

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewSeleccionarTutor)
        searchView = findViewById(R.id.searchViewTutorAdd)
        buttonAceptar = findViewById(R.id.buttonAceptarTutor)
        buttonCancelar = findViewById(R.id.buttonCancelarTutor)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerSeccion = findViewById(R.id.spinnerSeccion)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        tutorAdapter = TutorAdapter(emptyList(), { profesor ->
            profesor.idProfesor?.let { id ->
                if (selectedProfesores.contains(id)) {
                    selectedProfesores.remove(id)
                } else {
                    selectedProfesores.add(id)
                }
                // Actualizar solo el ítem cambiado
                tutorAdapter.notifyItemChanged(tutorAdapter.listaProfesores.indexOf(profesor))
            }
        }, isButtonVisible = true, istextViewGradosSeccionVisible = false)
        recyclerView.adapter = tutorAdapter

        // Obtener profesores
        fetchProfesores()

        // Configurar Spinners
        setupSpinners()


        // Configurar funcionalidad de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Manejar la consulta al enviar el texto
                handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Manejar la consulta al cambiar el texto
                handleSearchQuery(newText)
                return true
            }

            private fun handleSearchQuery(query: String?) {
                val trimmedQuery = query?.trim().orEmpty() // Eliminar espacios en blanco y manejar valores nulos

                if (trimmedQuery.isEmpty()) {
                    // Restablecer la lista completa si la consulta está vacía
                    tutorAdapter.resetList()
                } else {
                    // Filtrar la lista con la consulta
                    tutorAdapter.filterList(trimmedQuery)
                }

            }
        })

        // Configurar listeners de botones
        buttonAceptar.setOnClickListener {
            updateSelectedTutors()
        }

        buttonCancelar.setOnClickListener {
            finish()
        }
    }

    private fun fetchProfesores() {
        db.collection("Profesor")
            .whereEqualTo("tutor", false)
            .get()
            .addOnSuccessListener { result ->
                val listaProfesores = result.documents.mapNotNull { document ->
                    try {
                        // Convert the document to a Profesor object
                        val profesor = document.toObject(Profesor::class.java)?.apply {
                            // Get the 'grado' field from Firestore
                            val grado = document.getLong("grado")
                            // Assign the grado value to the Profesor object, defaulting to 0L if null
                            this.grado = grado ?: 0L
                        }
                        profesor
                    } catch (e: Exception) {
                        Log.e("FetchProfesores", "Error al mapear documento: ${e.message}", e)
                        null
                    }
                }

                // Update the adapter with the fetched list
                tutorAdapter.updateList(listaProfesores)
            }
            .addOnFailureListener { exception ->
                Log.e("FetchProfesores", "Error al obtener profesores: ${exception.message}", exception)
                Toast.makeText(this, "Error al obtener profesores: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun updateSelectedTutors() {
        if (selectedProfesores.isEmpty()) {
            Toast.makeText(this, "No hay tutores seleccionados.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGrado = spinnerGrado.selectedItem.toString().toLongOrNull() ?: 0L
        val selectedSeccion = spinnerSeccion.selectedItem.toString()

        // Logs en español para depuración
        Log.d("AddTutor", "Grado seleccionado: $selectedGrado")
        Log.d("AddTutor", "Sección seleccionada: $selectedSeccion")
        Log.d("AddTutor", "Profesores seleccionados: $selectedProfesores")

        // Verificar si la combinación ya está en uso
        db.collection("Profesor")
            .whereEqualTo("grado", selectedGrado)
            .whereEqualTo("seccion", selectedSeccion)
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // Si la combinación no está en uso, proceder con la actualización
                    val batch = db.batch()
                    selectedProfesores.forEach { profesorId ->
                        val profesorRef = db.collection("Profesor").document(profesorId)
                        batch.update(profesorRef, "tutor", true)
                        batch.update(profesorRef, "grado", selectedGrado)
                        batch.update(profesorRef, "seccion", selectedSeccion)
                    }

                    batch.commit()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Tutores actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK) // Establecer el resultado para indicar éxito
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error al actualizar tutores: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // La combinación ya está en uso
                    Toast.makeText(this, "La combinación de grado y sección ya está en uso.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al verificar disponibilidad: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupSpinners() {
        allCombinations = grados.flatMap { grado ->
            secciones.map { seccion ->
                "$grado$seccion"
            }
        }.toSet()

        db.collection("Profesor")
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                usedCombinations = result.documents.mapNotNull { document ->
                    val grado = document.getLong("grado")?.toString() ?: ""
                    val seccion = document.getString("seccion")?.takeIf { it.isNotEmpty() } ?: ""
                    // Combinar solo si grado y sección son válidos
                    if (grado.isNotEmpty() && seccion.isNotEmpty()) {
                        "$grado$seccion"
                    } else {
                        null
                    }
                }.toSet() // Usar Set para evitar duplicados

                // Filtrar las combinaciones disponibles
                val availableCombinations = allCombinations - usedCombinations

                // Obtener los grados y secciones disponibles
                val availableGrados = availableCombinations.map { it.first().toString() }.distinct()
                val availableSecciones = availableCombinations.map { it.drop(1) }.distinct()

                // Crear adaptadores para los spinners
                val gradoAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    availableGrados
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                spinnerGrado.adapter = gradoAdapter

                val seccionAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    availableSecciones
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                spinnerSeccion.adapter = seccionAdapter

                // Configurar el listener para verificar disponibilidad
                spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        checkAvailability()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No hacer nada si no hay selección
                    }
                }

                spinnerSeccion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        checkAvailability()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No hacer nada si no hay selección
                    }
                }

                // Logs para depuración
                Log.d("AddTutor", "Combinaciones disponibles: $availableCombinations")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener combinaciones usadas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Verificar disponibilidad del salón seleccionado
    private fun checkAvailability() {
        // Obtener los valores seleccionados en los spinners
        val selectedGrado = spinnerGrado.selectedItem?.toString() ?: ""
        val selectedSeccion = spinnerSeccion.selectedItem?.toString() ?: ""

        // Crear la combinación seleccionada
        val selectedCombination = "$selectedGrado$selectedSeccion"

        // Verificar que ambos valores estén seleccionados
        if (selectedGrado.isEmpty() || selectedSeccion.isEmpty()) {
            // Mensaje si alguno de los valores está vacío
            Toast.makeText(this, "Por favor, seleccione tanto el grado como la sección.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener una copia local de `allCombinations`
        val localAllCombinations = allCombinations

        // Verificar si la combinación seleccionada está en las combinaciones usadas
        if (localAllCombinations.contains(selectedCombination)) {
            if (usedCombinations.contains(selectedCombination)) {
                Toast.makeText(this, "El salón ya fue seleccionado.", Toast.LENGTH_SHORT).show()
            } else {
            }
        } else {
            Toast.makeText(this, "La combinación seleccionada no es válida.", Toast.LENGTH_SHORT).show()
        }

    }}

