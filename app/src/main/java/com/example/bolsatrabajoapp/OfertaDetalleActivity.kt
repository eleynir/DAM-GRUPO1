// OfertaDetalleActivity.kt
package com.example.bolsatrabajoapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.bolsatrabajoapp.data.OfertaDAO
import com.example.bolsatrabajoapp.data.PostulanteDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class OfertaDetalleActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_detalle_oferta
    override fun navSelectedItemId() = R.id.nav_listado
    override fun screenTitle() = "Detalle de la oferta"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getIntExtra("id_oferta", -1)
        if (id <= 0) { finish(); return }

        val dao = OfertaDAO(this)
        val o = dao.obtenerOferta(id) ?: run { finish(); return }

        findViewById<TextView>(R.id.tvTitulo).text = o.titulo
        findViewById<TextView>(R.id.tvEmpresa).text = o.empresa
        findViewById<TextView>(R.id.tvDescripcion).text = o.descripcion

        val salarioTxt = when {
            o.salario_min > 0 && o.salario_max > 0 ->
                "S/ ${"%.2f".format(o.salario_min)} - ${"%.2f".format(o.salario_max)}"
            o.salario_min > 0 -> "S/ ${"%.2f".format(o.salario_min)}"
            o.salario_max > 0 -> "S/ ${"%.2f".format(o.salario_max)}"
            else -> "A convenir"
        }
        findViewById<Chip>(R.id.chipModalidad).text = o.modalidad
        findViewById<Chip>(R.id.chipSalario).text   = salarioTxt

        findViewById<MaterialButton>(R.id.btnPostular).setOnClickListener {
            val prefs = getSharedPreferences("user", MODE_PRIVATE)
            val rol = prefs.getString("rol", "POSTULANTE") ?: "POSTULANTE"
            if (!rol.equals("POSTULANTE", true)) {
                Toast.makeText(this, "Solo postulantes pueden postular", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idUsuario = prefs.getLong("id_usuario", -1L)
            if (idUsuario <= 0L) {
                Toast.makeText(this, "Vuelve a iniciar sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pDao = PostulanteDAO(this)
            val idPostulante = pDao.obtenerIdPostulantePorUsuario(idUsuario)
            if (idPostulante == null) {
                Toast.makeText(this, "Completa tu perfil de postulante primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pDao.existePostulacion(id, idPostulante)) {
                Toast.makeText(this, "Ya postulaste a esta oferta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newId = pDao.crearPostulacion(id, idPostulante)
            if (newId > 0) {
                Toast.makeText(this, "¡Postulación enviada!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "No se pudo postular", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
