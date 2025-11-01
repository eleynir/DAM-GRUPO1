package com.example.bolsatrabajoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.chip.Chip


class HomeActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_home
    override fun navSelectedItemId() = R.id.nav_home
    override fun screenTitle() = "Inicio"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rol = getSharedPreferences("user", MODE_PRIVATE)
            .getString("rol", "POSTULANTE") ?: "POSTULANTE"
        val esPost = rol.equals("POSTULANTE", true)

        val sectionPost = findViewById<LinearLayout?>(R.id.sectionPostulante)
        val sectionEmp  = findViewById<LinearLayout?>(R.id.sectionEmpresa)

        sectionPost?.visibility = if (esPost) View.VISIBLE else View.GONE
        sectionEmp?.visibility  = if (esPost) View.GONE else View.VISIBLE

        findViewById<Button?>(R.id.btnVerTodas)?.setOnClickListener {
            startActivity(Intent(this, ListadoOfertasActivity::class.java))
        }

        findViewById<Button?>(R.id.btnPublicar)?.setOnClickListener {
            startActivity(Intent(this, PublicarOfertaActivity::class.java))
        }

    }
}
