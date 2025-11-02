package com.example.bolsatrabajoapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.example.bolsatrabajoapp.data.OfertaDAO
import com.example.bolsatrabajoapp.entity.Oferta
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class PublicarOfertaActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_publicar_oferta
    override fun navSelectedItemId() = R.id.nav_publicar
    override fun screenTitle() = "Publicar oferta"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val rol = prefs.getString("rol", "POSTULANTE") ?: "POSTULANTE"
        if (!rol.equals("EMPRESA", true)) {
            toast("Solo las empresas pueden publicar")
            finish()
            return
        }

        val etTitulo      = findViewById<TextInputEditText>(R.id.etTitulo)
        val etEmpresa     = findViewById<TextInputEditText>(R.id.etEmpresa)
        val etSalarioMin  = findViewById<TextInputEditText>(R.id.etSalarioMin)
        val etSalarioMax  = findViewById<TextInputEditText>(R.id.etSalarioMax)
        val etUbicacion   = findViewById<TextInputEditText>(R.id.etUbicacion)
        val etDescripcion = findViewById<TextInputEditText>(R.id.etDescripcion)
        val etTipo        = findViewById<MaterialAutoCompleteTextView>(R.id.etTipo)
        val etCategoria   = findViewById<MaterialAutoCompleteTextView>(R.id.etCategoria)
        val chipGroup     = findViewById<ChipGroup>(R.id.chipGroupModalidad)
        val btnPublicar   = findViewById<Button>(R.id.btnPublicar)

        val tiposMap = linkedMapOf(
            "Tiempo completo" to "TIEMPO_COMPLETO",
            "Medio tiempo"    to "MEDIO_TIEMPO",
            "Prácticas"       to "PRACTICAS"
        )
        etTipo.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, tiposMap.keys.toList()))


        val categoriasUi = listOf("Tecnología", "Diseño", "Ventas", "Operaciones")
        etCategoria.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, categoriasUi))

        btnPublicar.setOnClickListener {
            val titulo   = etTitulo.text?.toString()?.trim().orEmpty()
            val empresaN = etEmpresa.text?.toString()?.trim().orEmpty()
            val salMin   = etSalarioMin.text?.toString()?.trim()?.toDoubleOrNull()
            val salMax   = etSalarioMax.text?.toString()?.trim()?.toDoubleOrNull()
            val ubic     = etUbicacion.text?.toString()?.trim().orEmpty()
            val desc     = etDescripcion.text?.toString()?.trim().orEmpty()
            val tipoUi   = etTipo.text?.toString()?.trim().orEmpty()
            val tipo     = tiposMap[tipoUi] ?: "TIEMPO_COMPLETO"
            val categoriaUi = etCategoria.text?.toString()?.trim().orEmpty()
            val categoriaId = 1

            val modalidad = when (chipGroup.checkedChipId) {
                R.id.chipRemoto     -> "REMOTO"
                R.id.chipHibrido    -> "HIBRIDO"
                R.id.chipPresencial -> "PRESENCIAL"
                else -> ""
            }

            if (titulo.isEmpty())     return@setOnClickListener toast("Ingresa título")
            if (empresaN.isEmpty())   return@setOnClickListener toast("Ingresa empresa")
            if (salMin == null || salMin <= 0.0) return@setOnClickListener toast("Salario mínimo inválido")
            if (salMax == null || salMax < salMin) return@setOnClickListener toast("Salario máximo inválido")
            if (ubic.isEmpty())       return@setOnClickListener toast("Ingresa ubicación")
            if (tipoUi.isEmpty())     return@setOnClickListener toast("Selecciona tipo")
            if (categoriaUi.isEmpty())return@setOnClickListener toast("Selecciona categoría")
            if (modalidad.isEmpty())  return@setOnClickListener toast("Selecciona modalidad")
            if (desc.isEmpty())       return@setOnClickListener toast("Ingresa descripción")

            val idUsuario = prefs.getLong("id_usuario", -1L)
            val dao = OfertaDAO(this)
            val idEmpresa = dao.obtenerIdEmpresaPorUsuario(idUsuario)
            if (idEmpresa == null) {
                toast("No se encontró empresa asociada al usuario")
                return@setOnClickListener
            }

            val oferta = Oferta(
                idOferta     = 0,
                titulo       = titulo,
                empresa      = empresaN,
                descripcion  = desc,
                salario_min  = salMin,
                salario_max  = salMax,
                modalidad    = modalidad,
                tipo         = tipo,
                ubicacion    = ubic,
                categoria    = categoriaId,
                vigente      = 1,
                detalle      = desc,
                iconEmpresa  = ""
            )

            val newId = dao.insertar(oferta, idEmpresa)
            if (newId > 0) {
                toast("Oferta publicada")
                finish()
            } else {
                toast("No se pudo publicar")
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
