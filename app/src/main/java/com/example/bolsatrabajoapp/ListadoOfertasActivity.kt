package com.example.bolsatrabajoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.databinding.ActivityListadoOfertasBinding // Suponiendo este nombre
import com.example.bolsatrabajoapp.entity.Oferta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListadoOfertasActivity : AppCompatActivity(), OfertasAdapter.OnItemClickListener {

    private lateinit var binding: ActivityListadoOfertasBinding
    private lateinit var dbHelper: AppDataBaseHelper
    private lateinit var ofertasAdapter: OfertasAdapter

    // Lista para almacenar las ofertas
    private val ofertasList: MutableList<Oferta> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuración de View Binding
        binding = ActivityListadoOfertasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inicialización del Helper de la Base de Datos
        dbHelper = AppDataBaseHelper(this)

        // 3. Configuración del RecyclerView
        configurarRecyclerView()

        // 4. Cargar datos
        cargarOfertas()
    }

    private fun configurarRecyclerView() {
        // Inicializar el adaptador y el RecyclerView
        ofertasAdapter = OfertasAdapter(ofertasList, this)
        binding.rvOfertas.apply {
            layoutManager = LinearLayoutManager(this@ListadoOfertasActivity)
            adapter = ofertasAdapter
        }
    }

    /**
     * Carga las ofertas desde la base de datos de forma asíncrona.
     */
    private fun cargarOfertas() {
        // Usamos Coroutines para evitar bloquear el hilo principal (UI)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Aquí debes implementar la función 'obtenerTodasOfertas' en tu AppDataBaseHelper
                // o en tu DAO para SQLite nativo, que devuelva List<Oferta>.
                val ofertas = dbHelper.obtenerOfertas()

                withContext(Dispatchers.Main) {
                    if (ofertas.isNotEmpty()) {
                        // Actualiza la lista y notifica al adaptador en el hilo principal
                        ofertasList.clear()
                        ofertasList.addAll(ofertas)
                        ofertasAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@ListadoOfertasActivity,
                            "No hay ofertas disponibles.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListadoOfertasActivity,
                        "Error al cargar la lista de ofertas.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 5. Implementación del click en el item de la lista
    override fun onItemClick(oferta: Oferta) {
        // Navegar a DetalleOfertaActivity
        val intent = Intent(this, DetalleOfertaActivity::class.java).apply {
            // Asumiendo que Oferta tiene un campo 'idOferta' (Int)
            // Necesitas el ID de la oferta para cargar el detalle
            putExtra("id_oferta", oferta.idOferta)
        }
        startActivity(intent)
    }

    // 6. Refrescar la lista si volvemos a esta Activity
    override fun onResume() {
        super.onResume()
        cargarOfertas()
    }
}