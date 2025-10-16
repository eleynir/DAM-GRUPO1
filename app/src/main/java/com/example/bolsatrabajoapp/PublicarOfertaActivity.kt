package com.example.bolsatrabajoapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bolsatrabajoapp.data.AppDataBaseHelper
import com.example.bolsatrabajoapp.databinding.ActivityPublicarOfertaBinding
import com.example.bolsatrabajoapp.entity.Oferta

class PublicarOfertaActivity : AppCompatActivity() {// ... (Eliminar las declaraciones individuales si usas View Binding)
private lateinit var binding: ActivityPublicarOfertaBinding

    // La vinculación de vistas debe ser:
    private lateinit var etEmpresa: AutoCompleteTextView // Usar AutoCompleteTextView
    private lateinit var etTipo: AutoCompleteTextView     // Usar AutoCompleteTextView
    //...

    // ... (código dentro de onCreate)

    private fun configurarSpinners() {
        val modalidades = listOf("Presencial", "Remoto", "Híbrido")
        val tipos = listOf("Tiempo completo", "Medio tiempo", "Prácticas")

        // 💡 1. Crear el adaptador usando el layout para Dropdown Items
        val adapterModalidad = ArrayAdapter(this, R.layout.item_oferta, modalidades)
        val adapterTipo = ArrayAdapter(this, R.layout.item_oferta, tipos)

        // 💡 2. Asignar el adaptador al AutoCompleteTextView
        binding.spModalidad.setAdapter(adapterModalidad)
        binding.spTipo.setAdapter(adapterTipo)

        // Nota: Con AutoCompleteTextView, la selección inicial no es automática como en Spinner.
        // Si necesitas una selección por defecto, puedes hacerlo:
        // binding.spModalidad.setText(modalidades[0], false)
    }

    private fun publicarOferta() {
        // ... (Validaciones)

        // 💡 3. Obtener el texto seleccionado (ya no es selectedItem)
        val modalidad = binding.spModalidad.text.toString()
        val tipo = binding.spTipo.text.toString()

        // ... (el resto del código se mantiene)
    }

    // ... (limpiarCampos)

    private fun limpiarCampos() {
        binding.apply {
            // ... otros campos
            spModalidad.setText("", false) // Limpiar texto del dropdown
            spTipo.setText("", false)
        }
    }
}