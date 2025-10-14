package com.example.bolsatrabajoapp

import android.database.Cursor
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.adapter.HistorialAdapter
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.entity.Oferta

class ListadoOfertasActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_listado_ofertas
    override fun navSelectedItemId() = R.id.nav_listado
    override fun screenTitle() = "Ofertas disponibles"


    private lateinit var rvHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvHistorial = findViewById(R.id.tvListaOferta)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        val oferta = listOf(
            Oferta(1, "Dev Ops", "Emplea Inc.", "a",1200.20, 2400.00,"Hìbrido","Tiempo Completo","San Isidro",2 , 1,"d","url"),
        )

        historialAdapter = HistorialAdapter(oferta)
        rvHistorial.adapter = historialAdapter
    }


}