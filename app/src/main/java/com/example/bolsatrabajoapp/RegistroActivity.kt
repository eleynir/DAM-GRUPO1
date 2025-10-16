package com.example.bolsatrabajoapp


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class RegistroActivity : AppCompatActivity() {

    private lateinit var tietNombres: TextInputEditText
    private lateinit var tietApellidos: TextInputEditText
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietTelefono: TextInputEditText
    private lateinit var tietEdad: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tietClaveConfirm: TextInputEditText

    private lateinit var spRol: Spinner
    private lateinit var llPostulante: LinearLayout
    private lateinit var llEmpresa: LinearLayout

    private lateinit var tietResumenCV: TextInputEditText
    private lateinit var btnAdjuntarCV: Button

    private lateinit var tietRUC: TextInputEditText
    private lateinit var tietRazonSocial: TextInputEditText

    private lateinit var cbAcepto: android.widget.CheckBox
    private lateinit var btnEnviar: Button
    private lateinit var tvRegreso: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        tietNombres = findViewById(R.id.tietNombres)
        tietApellidos = findViewById(R.id.tietApellidos)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietTelefono = findViewById(R.id.tietTelefono)
        tietEdad = findViewById(R.id.tietEdad)
        tietClave = findViewById(R.id.tietClave)
        tietClaveConfirm = findViewById(R.id.tietClaveConfirm)

        spRol = findViewById(R.id.spRol)
        llPostulante = findViewById(R.id.llPostulante)
        llEmpresa = findViewById(R.id.llEmpresa)

        tietResumenCV = findViewById(R.id.tietResumenCV)
        btnAdjuntarCV = findViewById(R.id.btnAdjuntarCV)

        tietRUC = findViewById(R.id.tietRUC)
        tietRazonSocial = findViewById(R.id.tietRazonSocial)

        cbAcepto = findViewById(R.id.cbAcepto)
        btnEnviar = findViewById(R.id.btnEnviar)
        tvRegreso = findViewById(R.id.tvRegreso)

        val roles = listOf("Postulante", "Empresa")
        spRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        spRol.setSelection(0)
        spRol.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val rol = roles[position]
                if (rol == "Postulante") {
                    llPostulante.visibility = View.VISIBLE
                    llEmpresa.visibility = View.GONE
                } else {
                    llPostulante.visibility = View.GONE
                    llEmpresa.visibility = View.VISIBLE
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })

        // adjuntar CV: en demo
        btnAdjuntarCV.setOnClickListener {
            Toast.makeText(this, "Adjuntar CV (pendiente implementar)", Toast.LENGTH_SHORT).show()
        }

        btnEnviar.setOnClickListener {
            if (validate()) {
                val nombre    = tietNombres.text.toString().trim()
                val apellidos = tietApellidos.text.toString().trim()
                val correo    = normalizarCorreo(tietCorreo.text.toString())
                val telefono  = tietTelefono.text.toString().trim().ifEmpty { null }
                val edad      = tietEdad.text.toString().toIntOrNull()
                val clave     = tietClave.text.toString()

                val rolUi = spRol.selectedItem.toString()
                val rolDb = if (rolUi.equals("empresa", true)) "EMPRESA" else "POSTULANTE"

                val ruc       = tietRUC.text.toString().trim().ifEmpty { null }
                val razon     = tietRazonSocial.text.toString().trim().ifEmpty { null }
                val resumenCV = tietResumenCV.text.toString().trim().ifEmpty { null }

                val u = com.example.bolsatrabajoapp.entity.Usuario(
                    nombres   = nombre,
                    apellidos = apellidos,
                    correo    = correo,
                    clave     = clave,
                    edad      = edad,
                    celular   = telefono,
                    sexo      = null,
                    rol       = rolDb
                )

                val dao = com.example.bolsatrabajoapp.data.UsuarioDAO(this)
                if (dao.existeCorreo(correo)) {
                    Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val id = dao.insertar(
                    u,
                    ruc        = if (rolDb == "EMPRESA") ruc else null,
                    razonSocial= if (rolDb == "EMPRESA") razon else null,
                    resumenCV  = if (rolDb == "POSTULANTE") resumenCV else null
                )

                if (id > 0) {
                    // guarda en prefs lo básico para el header y entra a Home
                    val prefs = getSharedPreferences("user", MODE_PRIVATE)
                    prefs.edit()
                        .putString("name", "$nombre $apellidos")
                        .putString("email", correo)
                        .putString("rol", rolDb)
                        .apply()

                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "No se pudo registrar. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        tvRegreso.setOnClickListener {
            finish()
        }
    }



    private fun validate(): Boolean {
        var ok = true

        val nombre = tietNombres.text.toString().trim()
        val correo = normalizarCorreo(tietCorreo.text.toString())
        val clave = tietClave.text.toString()
        val clave2 = tietClaveConfirm.text.toString()

        if (nombre.isEmpty()) {
            tietNombres.error = "Ingrese nombres"
            ok = false
        } else tietNombres.error = null

        if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tietCorreo.error = "Ingrese un correo válido"
            ok = false
        } else tietCorreo.error = null

        if (clave.length < 6) {
            tietClave.error = "La contraseña debe tener al menos 6 caracteres"
            ok = false
        } else tietClave.error = null

        if (clave != clave2) {
            tietClaveConfirm.error = "No coincide"
            ok = false
        } else tietClaveConfirm.error = null

        if (!cbAcepto.isChecked) {
            Toast.makeText(this, "Debes aceptar términos y políticas", Toast.LENGTH_SHORT).show()
            ok = false
        }

        return ok
    }
}
