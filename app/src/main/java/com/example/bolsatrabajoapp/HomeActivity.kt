package com.example.bolsatrabajoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class HomeActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_home
    override fun navSelectedItemId() = R.id.nav_home
    override fun screenTitle() = "Inicio"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val rol = prefs.getString("rol", "POSTULANTE") // "POSTULANTE" o "EMPRESA"
        val nombre = prefs.getString("name", "Usuario")

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val tvMensajeRol = findViewById<TextView>(R.id.tvMensajeRol)
        val secPost = findViewById<View>(R.id.sectionPostulante)
        val secEmp  = findViewById<View>(R.id.sectionEmpresa)

        tvBienvenida.text = "Hola, $nombre "
        if (rol == "EMPRESA") {
            tvMensajeRol.text = "Gestiona tus vacantes y encuentra talento."
            secPost.visibility = View.GONE
            secEmp.visibility = View.VISIBLE
        } else {
            tvMensajeRol.text = "Explora ofertas y mejora tu perfil."
            secPost.visibility = View.VISIBLE
            secEmp.visibility = View.GONE
        }

        // Navegaciones
        findViewById<Button>(R.id.btnVerTodas).setOnClickListener {
            startActivity(Intent(this, ListadoOfertasActivity::class.java))
        }
        findViewById<Button>(R.id.btnIrPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        findViewById<Button>(R.id.btnIrMisOfertas).setOnClickListener {
            // reutilizar ListadoOfertasActivity con un filtro por empresa más adelante
            startActivity(Intent(this, ListadoOfertasActivity::class.java))
        }
        findViewById<Button>(R.id.btnPublicarRapido).setOnClickListener {
            startActivity(Intent(this, PublicarOfertaActivity::class.java))
        }
    }
}