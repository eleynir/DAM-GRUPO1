package com.example.bolsatrabajoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolsatrabajoapp.adapter.FeedAdapter
import com.example.bolsatrabajoapp.data.HomeFeedDAO

class HomeActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_home
    override fun navSelectedItemId() = R.id.nav_home
    override fun screenTitle() = "Inicio"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val rol = prefs.getString("rol", "POSTULANTE") ?: "POSTULANTE"
        val idUsuario = prefs.getLong("id_usuario", -1)

        val esPost = rol.equals("POSTULANTE", true)

        findViewById<LinearLayout?>(R.id.sectionPostulante)?.visibility = if (esPost) View.VISIBLE else View.GONE
        findViewById<LinearLayout?>(R.id.sectionEmpresa)?.visibility  = if (esPost) View.GONE else View.VISIBLE

        findViewById<Button?>(R.id.btnVerTodas)?.setOnClickListener {
            startActivity(Intent(this, ListadoOfertasActivity::class.java))
        }

        findViewById<Button?>(R.id.btnPublicar)?.setOnClickListener {
            startActivity(Intent(this, PublicarOfertaActivity::class.java))
        }

        cargarFeed(esPost, idUsuario)
    }

    private fun cargarFeed(esPost: Boolean, idUsuario: Long) {
        val rv = findViewById<RecyclerView>(R.id.rvFeed)
        val tvEmpty = findViewById<TextView>(R.id.tvFeedEmpty)

        rv.layoutManager = LinearLayoutManager(this)

        val dao = HomeFeedDAO(this)
        val feed = if (esPost) dao.feedPostulante(idUsuario) else dao.feedEmpresa(idUsuario)

        if (feed.isEmpty()) {
            rv.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE

            rv.adapter = FeedAdapter(
                feed,
                onVerOferta = { item ->
                    startActivity(
                        Intent(this, OfertaDetalleActivity::class.java)
                            .putExtra("id_oferta", item.idOferta)
                    )
                },
                onVerUsuario = { item ->
                    startActivity(
                        Intent(this, PerfilActivity::class.java)
                            .putExtra("id_usuario", item.idUsuarioPostulante)
                    )
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val esPost = prefs.getString("rol", "POSTULANTE") == "POSTULANTE"
        cargarFeed(esPost, prefs.getLong("id_usuario", -1))
    }
}
