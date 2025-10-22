package com.example.bolsatrabajoapp.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.data.CategoriaDAO
import com.example.bolsatrabajoapp.data.OfertaDAO
import com.example.bolsatrabajoapp.databinding.FragmentEditarOfertaBinding
import com.example.bolsatrabajoapp.entity.Oferta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarOfertaFragment : Fragment() {

    private var _binding: FragmentEditarOfertaBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: AppDataBaseHelper
    private lateinit var categoriaDAO: CategoriaDAO
    private lateinit var ofertaDAO: OfertaDAO

    private var idOferta: Int = 0
    private var categoriaMap = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarOfertaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = AppDataBaseHelper(requireContext())
        categoriaDAO = CategoriaDAO(requireContext())
        ofertaDAO = OfertaDAO(requireContext())

        // Obtener el ID de la oferta que se quiere editar
        idOferta = arguments?.getInt("id_oferta") ?: 0

        if (idOferta > 0) {
            cargarCategorias()
            cargarDatosOferta()
        } else {
            Toast.makeText(requireContext(), "Error: ID de oferta no válido", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnActualizarOferta.setOnClickListener {
            actualizarOferta()
        }
    }

    private fun cargarCategorias() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categorias = categoriaDAO.obtenerCategoriasConId()
            withContext(Dispatchers.Main) {
                val nombres = categorias.map { it.second }
                // Mapea (Nombre -> ID)
                categoriaMap = categorias.associate { it.second to it.first }.toMutableMap()

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, // Uso R.layout de Android para Spinner
                    nombres
                )
                binding.spnCategoria.adapter = adapter
            }
        }
    }

    private fun cargarDatosOferta() {
        lifecycleScope.launch(Dispatchers.IO) {
            val oferta = ofertaDAO.obtenerPorId(idOferta)

            withContext(Dispatchers.Main) {
                if (oferta != null) {
                    binding.etEmpresa.setText(oferta.empresa)
                    binding.etTitulo.setText(oferta.titulo)
                    binding.etDescripcion.setText(oferta.descripcion)
                    binding.etSalarioMin.setText(oferta.salario_min.toString())
                    binding.etSalarioMax.setText(oferta.salario_max.toString())
                    binding.etModalidad.setText(oferta.modalidad)
                    binding.etTipo.setText(oferta.tipo)
                    binding.etUbicacion.setText(oferta.ubicacion)
                    binding.etDetalle.setText(oferta.detalle)

                    // Seleccionar la categoría correcta en el Spinner
                    val categoriaNombre = categoriaMap.entries.find { it.value == oferta.categoria }?.key
                    if (categoriaNombre != null) {
                        val adapter = binding.spnCategoria.adapter as ArrayAdapter<String>
                        val position = adapter.getPosition(categoriaNombre)
                        if (position >= 0) {
                            binding.spnCategoria.setSelection(position)
                        }
                    }

                } else {
                    Toast.makeText(requireContext(), "Oferta no encontrada.", Toast.LENGTH_LONG).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun actualizarOferta() {
        val empresa = binding.etEmpresa.text.toString().trim()
        val titulo = binding.etTitulo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val salarioMin = binding.etSalarioMin.text.toString().toDoubleOrNull() ?: 0.0
        val salarioMax = binding.etSalarioMax.text.toString().toDoubleOrNull() ?: 0.0
        val modalidad = binding.etModalidad.text.toString().trim()
        val tipo = binding.etTipo.text.toString().trim()
        val ubicacion = binding.etUbicacion.text.toString().trim()
        val detalle = binding.etDetalle.text.toString().trim()
        val categoriaNombre = binding.spnCategoria.selectedItem?.toString()
        val categoriaId = categoriaMap[categoriaNombre] ?: -1

        if (empresa.isEmpty() || titulo.isEmpty() || descripcion.isEmpty() || categoriaId == -1) {
            Toast.makeText(requireContext(), "Complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Obtener los datos originales para no perder 'vigente' ni 'icon_emp'
        lifecycleScope.launch(Dispatchers.IO) {
            val ofertaOriginal = ofertaDAO.obtenerPorId(idOferta)

            if (ofertaOriginal == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "❌ Error: Oferta original no encontrada para actualizar.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // 2. Crear el objeto Oferta combinando los datos nuevos con los originales
            val ofertaActualizada = Oferta(
                idOferta = idOferta, // ¡Esto es clave para el UPDATE!
                empresa = empresa,
                titulo = titulo,
                descripcion = descripcion,
                salario_min = salarioMin,
                salario_max = salarioMax,
                modalidad = modalidad,
                tipo = tipo,
                ubicacion = ubicacion,
                categoria = categoriaId,
                detalle = detalle,
                // Preservar los valores originales:
                vigente = ofertaOriginal.vigente,
                icon_emp = ofertaOriginal.icon_emp
            )

            // 3. USANDO OfertaDAO.actualizarOferta()
            val filasAfectadas = ofertaDAO.actualizarOferta(ofertaActualizada)

            withContext(Dispatchers.Main) {
                if (filasAfectadas > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Oferta actualizada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "❌ Error al actualizar", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}