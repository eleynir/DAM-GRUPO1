package com.example.bolsatrabajoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.R
import com.example.bolsatrabajoapp.adapter.OfertaAdapter
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.data.OfertaDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaOfertasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OfertaAdapter
    private lateinit var db: AppDataBaseHelper
    private lateinit var ofertaDAO: OfertaDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_ofertas, container, false)

        recyclerView = view.findViewById(R.id.recyclerOfertas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        db = AppDataBaseHelper(requireContext())
        ofertaDAO = OfertaDAO(requireContext())

        cargarOfertas()

        return view
    }

    private fun cargarOfertas() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val listaOfertas = ofertaDAO.listarOfertas()

                withContext(Dispatchers.Main) {
                    if (listaOfertas.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay ofertas registradas", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter = OfertaAdapter(listaOfertas.toMutableList())
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al cargar las ofertas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
