package com.example.bolsatrabajoapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class PerfilActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_perfil
    override fun navSelectedItemId() = R.id.nav_perfil
    override fun screenTitle() = "Mi perfil"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rol = getSharedPreferences("user", MODE_PRIVATE)
            .getString("rol", "POSTULANTE") ?: "POSTULANTE"

        val groupPost = findViewById<View>(R.id.groupPostulante)
        val groupEmp  = findViewById<View>(R.id.groupEmpresa)
        val chipRol   = findViewById<com.google.android.material.chip.Chip>(R.id.chipRol)

        val esPostulante = rol.equals("POSTULANTE", ignoreCase = true)
        groupPost.visibility = if (esPostulante) View.VISIBLE else View.GONE
        groupEmp.visibility  = if (esPostulante) View.GONE else View.VISIBLE

        chipRol.text = rol.uppercase()
        chipRol.setChipIconResource(if (esPostulante) R.drawable.ic_person else R.drawable.ic_work)
    }
}
