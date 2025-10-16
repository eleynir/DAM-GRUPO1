package com.example.bolsatrabajoapp

import android.database.Cursor
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ReturnThis
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.adapter.HistorialAdapter
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.data.OfertaDAO
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

        val dbHelper = AppDataBaseHelper(this)
        val db = dbHelper.writableDatabase

        val OfertaDAO = OfertaDAO(this)
        val listaOferta = OfertaDAO.obtenerTodas()

        historialAdapter = HistorialAdapter(listaOferta)
        rvHistorial.adapter = historialAdapter


//        fun DetalleOferta (idLista : Int){
//            val dbHelper = AppDataBaseHelper(this)
//            val db = dbHelper.readableDatabase
//             val detalles = mutableListOf<Oferta>()
//            val cursor = db.rawQuery(
//                "SELECT * FROM Oferta WHERE  id_oferta = ?", arrayOf(idLista)
//            )



        }



}