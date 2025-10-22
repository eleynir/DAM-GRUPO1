package com.example.bolsatrabajoapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.adapter.CategoriaAdapter
import com.example.bolsatrabajoapp.data.CategoriaDAO
import com.example.bolsatrabajoapp.databinding.FragmentCategoriaBinding
import com.example.bolsatrabajoapp.entity.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaFragment : Fragment() {
    private var _binding: FragmentCategoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriaDAO: CategoriaDAO
    private lateinit var categoriaAdapter: CategoriaAdapter
    private val categoriasList = mutableListOf<Categoria>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriaDAO = CategoriaDAO(requireContext())

        inicializarRecyclerView()
        binding.btnGuardarCategoria.setOnClickListener { guardarCategoria() }
        cargarYMostrarCategorias()
    }

    private fun inicializarRecyclerView() {
        categoriaAdapter = CategoriaAdapter(categoriasList)
        binding.rvCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriaAdapter
        }

        categoriaAdapter.setOnItemActionListener(object : CategoriaAdapter.OnItemActionListener {
            override fun onEditClick(categoria: Categoria) {
                mostrarDialogoEditar(categoria)
            }

            override fun onDeleteClick(categoria: Categoria) {
                mostrarDialogoEliminar(categoria)
            }
        })
    }

    private fun guardarCategoria() {
        val nombre = binding.etNombreCategoria.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.tilNombreCategoria.error = "El nombre de la categoría es obligatorio"
            return
        } else {
            binding.tilNombreCategoria.error = null
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val id = categoriaDAO.insertarCategoria(nombre)

            withContext(Dispatchers.Main) {
                if (id > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Categoría '$nombre' agregada con éxito (ID: $id)",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etNombreCategoria.text?.clear()
                    cargarYMostrarCategorias()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "❌ Error al agregar la categoría. (Puede que ya exista)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun cargarYMostrarCategorias() {
        lifecycleScope.launch {
            binding.txtMensaje.visibility = View.VISIBLE
            binding.rvCategorias.visibility = View.GONE

            val lista = withContext(Dispatchers.IO) {
                try {
                    categoriaDAO.obtenerTodasLasCategorias()
                } catch (e: Exception) {
                    Log.e("CategoriaFragment", "Error al cargar categorías", e)
                    emptyList<Categoria>()
                }
            }

            withContext(Dispatchers.Main) {
                categoriaAdapter.updateData(lista)
                if (lista.isEmpty()) {
                    binding.txtMensaje.text = "No hay categorías registradas."
                    binding.txtMensaje.visibility = View.VISIBLE
                    binding.rvCategorias.visibility = View.GONE
                } else {
                    binding.txtMensaje.visibility = View.GONE
                    binding.rvCategorias.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun mostrarDialogoEliminar(categoria: Categoria) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Deseas eliminar la categoría '${categoria.descripcion}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCategoria(categoria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCategoria(categoria: Categoria) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filasAfectadas = categoriaDAO.eliminarCategoria(categoria.idCategoria)
            withContext(Dispatchers.Main) {
                if (filasAfectadas > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Categoría '${categoria.descripcion}' eliminada.",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarYMostrarCategorias()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "❌ Error al eliminar la categoría.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // 🔹 NUEVO: diálogo para editar categoría
    private fun mostrarDialogoEditar(categoria: Categoria) {
        val editText = EditText(requireContext())
        editText.setText(categoria.descripcion)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Categoría")
            .setView(editText)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nuevoNombre = editText.text.toString().trim()
                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    val filas = categoriaDAO.actualizarCategoria(categoria.idCategoria, nuevoNombre)
                    withContext(Dispatchers.Main) {
                        if (filas > 0) {
                            Toast.makeText(
                                requireContext(),
                                "✅ Categoría actualizada a '$nuevoNombre'",
                                Toast.LENGTH_SHORT
                            ).show()
                            cargarYMostrarCategorias()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "❌ Error al actualizar la categoría.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
