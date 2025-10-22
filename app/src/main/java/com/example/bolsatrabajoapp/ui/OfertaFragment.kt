package com.example.bolsatrabajoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.data.CategoriaDAO
import com.example.bolsatrabajoapp.data.OfertaDAO
import com.example.bolsatrabajoapp.entity.Oferta
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfertaFragment : Fragment() {

    private lateinit var db: AppDataBaseHelper
    private lateinit var categoriaDAO: CategoriaDAO
    private lateinit var ofertaDAO: OfertaDAO
    private var categoriasMap = mapOf<String, Int>() // (Nombre -> ID)

    // Campos del formulario
    private lateinit var etEmpresa: TextInputEditText
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var etSalarioMin: TextInputEditText
    private lateinit var etSalarioMax: TextInputEditText
    private lateinit var etUbicacion: TextInputEditText
    private lateinit var spModalidad: MaterialAutoCompleteTextView
    private lateinit var spTipo: MaterialAutoCompleteTextView
    private lateinit var spCategoria: MaterialAutoCompleteTextView
    private lateinit var etDetalle: TextInputEditText
    private lateinit var btnPublicar: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_oferta, container, false)

        db = AppDataBaseHelper(requireContext())
        categoriaDAO = CategoriaDAO(requireContext())
        ofertaDAO = OfertaDAO(requireContext())

        // Inicializar vistas
        etEmpresa = view.findViewById(R.id.etEmpresa)
        etTitulo = view.findViewById(R.id.etTitulo)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etSalarioMin = view.findViewById(R.id.etSalarioMin)
        etSalarioMax = view.findViewById(R.id.etSalarioMax)
        etUbicacion = view.findViewById(R.id.etUbicacion)
        spModalidad = view.findViewById(R.id.spModalidad)
        spTipo = view.findViewById(R.id.spTipo)
        spCategoria = view.findViewById(R.id.spCategoria)
        etDetalle = view.findViewById(R.id.etDetalle)
        btnPublicar = view.findViewById(R.id.btnPublicar)

        configurarSpinners()
        configurarBoton()

        return view
    }

    private fun configurarSpinners() {
        val modalidades = listOf("Remoto", "Híbrido", "Presencial")
        val tipos = listOf("Full-time", "Part-time", "Freelance")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val categoriasDB = categoriaDAO.obtenerCategoriasConId()

                categoriasMap = categoriasDB.associate { it.second to it.first }
                val nombresCategorias = categoriasDB.map { it.second }

                withContext(Dispatchers.Main) {
                    spCategoria.setAdapter(
                        ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresCategorias)
                    )

                    spModalidad.setAdapter(
                        ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modalidades)
                    )

                    spTipo.setAdapter(
                        ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al cargar las categorías", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarBoton() {
        btnPublicar.setOnClickListener {
            val empresa = etEmpresa.text.toString().trim()
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val salarioMin = etSalarioMin.text.toString().toDoubleOrNull() ?: 0.0
            val salarioMax = etSalarioMax.text.toString().toDoubleOrNull() ?: 0.0
            val ubicacion = etUbicacion.text.toString().trim()
            val modalidad = spModalidad.text.toString()
            val tipo = spTipo.text.toString()
            val nombreCategoria = spCategoria.text.toString()
            val detalle = etDetalle.text.toString().trim()

            // Validación
            when {
                empresa.isEmpty() || titulo.isEmpty() || descripcion.isEmpty() ||
                        modalidad.isEmpty() || tipo.isEmpty() || nombreCategoria.isEmpty() -> {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                salarioMin > salarioMax -> {
                    Toast.makeText(requireContext(), "El salario mínimo no puede ser mayor que el máximo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val categoriaId = categoriasMap[nombreCategoria]
            if (categoriaId == null) {
                Toast.makeText(requireContext(), "Categoría no válida. Selecciona una de la lista.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val oferta = Oferta(
                empresa = empresa,
                titulo = titulo,
                descripcion = descripcion,
                salario_min = salarioMin,
                salario_max = salarioMax,
                modalidad = modalidad,
                tipo = tipo,
                ubicacion = ubicacion,
                categoria = categoriaId,
                detalle = detalle
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val result = ofertaDAO.insertarOferta(oferta)
                withContext(Dispatchers.Main) {
                    if (result != -1L) {
                        Toast.makeText(requireContext(), "✅ Oferta publicada con éxito", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(requireContext(), "❌ Error al publicar la oferta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun limpiarCampos() {
        etEmpresa.text?.clear()
        etTitulo.text?.clear()
        etDescripcion.text?.clear()
        etSalarioMin.text?.clear()
        etSalarioMax.text?.clear()
        etUbicacion.text?.clear()
        spModalidad.setText("", false)
        spTipo.setText("", false)
        spCategoria.setText("", false)
        etDetalle.text?.clear()
    }
}