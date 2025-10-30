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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.database.Cursor
import android.provider.OpenableColumns

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

    private var cvUriString: String? = null

    private val pickPdf =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                cvUriString = uri.toString()

                findViewById<TextView>(R.id.tvCvNombre)?.text = getDisplayName(uri)
                Toast.makeText(this, "CV adjuntado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getDisplayName(uri: Uri): String {
        var name = "cv.pdf"
        contentResolver.query(uri, null, null, null, null)?.use { cursor: Cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

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

        btnAdjuntarCV.setOnClickListener {
            pickPdf.launch(arrayOf("application/pdf"))
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
                    val prefs = getSharedPreferences("user", MODE_PRIVATE).edit()
                    prefs.putLong("id_usuario", id)
                    prefs.putString("name", "$nombre $apellidos")
                    prefs.putString("email", correo)
                    prefs.putString("rol", rolDb)
                    prefs.apply()

                    if (rolDb == "POSTULANTE" && cvUriString != null) {
                        com.example.bolsatrabajoapp.data.PostulanteDAO(this)
                            .actualizarCvUrl(id, cvUriString!!)
                    }

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
