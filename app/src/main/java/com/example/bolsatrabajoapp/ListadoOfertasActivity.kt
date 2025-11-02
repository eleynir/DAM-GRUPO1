package com.example.bolsatrabajoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.adapter.HistorialAdapter
import com.example.bolsatrabajoapp.data.OfertaDAO
import com.example.bolsatrabajoapp.entity.Oferta

class ListadoOfertasActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_listado_ofertas
    override fun navSelectedItemId() = R.id.nav_listado
    override fun screenTitle() = "Ofertas disponibles"

    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rv = findViewById(R.id.tvListaOferta)
        tvEmpty = findViewById(R.id.tvEmpty)

        rv.layoutManager = LinearLayoutManager(this)

        val dao = OfertaDAO(this)
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val rol = prefs.getString("rol", "POSTULANTE") ?: "POSTULANTE"

        val data: List<Oferta> = if (rol.equals("EMPRESA", true)) {
            val idUsuario = prefs.getLong("id_usuario", -1L)
            val idEmp = dao.obtenerIdEmpresaPorUsuario(idUsuario)
            if (idEmp != null) dao.listarPorEmpresa(idEmp) else emptyList()
        } else {
            dao.listarActivas()
        }

        tvEmpty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE

        val adapter = HistorialAdapter(data) { oferta ->
            val i = Intent(this, OfertaDetalleActivity::class.java)
            i.putExtra("id_oferta", oferta.idOferta)
            startActivity(i)
        }
        rv.adapter = adapter
    }
}
