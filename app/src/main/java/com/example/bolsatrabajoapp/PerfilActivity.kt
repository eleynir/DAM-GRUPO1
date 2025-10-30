package com.example.bolsatrabajoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bolsatrabajoapp.data.PostulanteDAO
import com.example.bolsatrabajoapp.data.UsuarioDAO
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class PerfilActivity : BaseDrawerActivity() {

    override fun getLayoutResId() = R.layout.activity_perfil
    override fun navSelectedItemId() = R.id.nav_perfil
    override fun screenTitle() = "Mi perfil"

    private lateinit var usuarioDAO: UsuarioDAO
    private var cvUriString: String? = null

    private val pickPdfForPerfil =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                cvUriString = uri.toString()
                val idUsuario = getSharedPreferences("user", MODE_PRIVATE).getLong("id_usuario", -1L)
                if (idUsuario > 0) {
                    com.example.bolsatrabajoapp.data.PostulanteDAO(this).actualizarCvUrl(idUsuario, cvUriString!!)
                    android.widget.Toast.makeText(this, "CV actualizado", android.widget.Toast.LENGTH_SHORT).show()

                    val btnEditarCVRef = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditarCV)
                    if (findViewById<View>(R.id.groupPostulante).visibility == View.VISIBLE) {
                        btnEditarCVRef.text = if (cvUriString.isNullOrBlank())
                            "Adjuntar/Editar CV"
                        else
                            "CV (ver / reemplazar / resumen)"
                    }
                }
            }
        }


    private fun abrirPdf(uriString: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(android.net.Uri.parse(uriString), "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (_: Exception) {
            android.widget.Toast.makeText(this, "No se pudo abrir el PDF", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogCv(tvResumenCV: TextView) {
        val view = layoutInflater.inflate(R.layout.dialog_cv_options, null)

        val ivCerrar        = view.findViewById<android.widget.ImageView>(R.id.ivCerrar)
        val btnVerCv        = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVerCv)
        val btnReemplazarCv = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnReemplazarCv)
        val btnEditarResumen= view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditarResumen)

        btnVerCv.visibility = if (cvUriString.isNullOrBlank()) View.GONE else View.VISIBLE

        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setView(view)
            .create()

        ivCerrar.setOnClickListener { dialog.dismiss() }

        btnVerCv.setOnClickListener {
            cvUriString?.let { abrirPdf(it) }
        }

        btnReemplazarCv.setOnClickListener {
            pickPdfForPerfil.launch(arrayOf("application/pdf"))
        }

        btnEditarResumen.setOnClickListener {
            val edit = com.google.android.material.textfield.TextInputEditText(this).apply {
                setText(tvResumenCV.text.toString().takeIf { !it.startsWith("(") } ?: "")
                hint = "Resumen / Perfil"
            }
            com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Editar resumen")
                .setView(edit)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar") { _, _ ->
                    val idUsuario = getSharedPreferences("user", MODE_PRIVATE).getLong("id_usuario", -1L)
                    val nuevo = edit.text?.toString()?.trim()
                    if (idUsuario > 0) {
                        com.example.bolsatrabajoapp.data.PostulanteDAO(this).actualizarResumen(idUsuario, nuevo)
                        tvResumenCV.text = nuevo?.takeIf { it.isNotBlank() }
                            ?: "(Agrega un resumen de tu perfil y experiencia…)"
                    }
                }.show()
        }

        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usuarioDAO = UsuarioDAO(this)

        val tvNombre  = findViewById<TextView>(R.id.tvNombre)
        val tvCorreo  = findViewById<TextView>(R.id.tvCorreo)
        val chipRol   = findViewById<Chip>(R.id.chipRol)
        val groupPost = findViewById<View>(R.id.groupPostulante)
        val groupEmp  = findViewById<View>(R.id.groupEmpresa)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val idUsuario = prefs.getLong("id_usuario", -1L)

        val usr = if (idUsuario > 0) usuarioDAO.obtenerPorId(idUsuario) else null
        if (usr != null) {
            tvNombre.text = "${usr.nombres} ${usr.apellidos}"
            tvCorreo.text = usr.correo
            chipRol.text  = usr.rol
            val esPost = usr.rol.equals("POSTULANTE", ignoreCase = true)
            groupPost.visibility = if (esPost) View.VISIBLE else View.GONE
            groupEmp.visibility  = if (esPost) View.GONE else View.VISIBLE
            chipRol.setChipIconResource(if (esPost) R.drawable.ic_person else R.drawable.ic_work)
        } else {
            val rol = prefs.getString("rol", "POSTULANTE") ?: "POSTULANTE"
            tvNombre.text = prefs.getString("name", "Nombre Apellidos")
            tvCorreo.text = prefs.getString("email", "correo@dominio.com")
            chipRol.text  = rol
            val esPost = rol.equals("POSTULANTE", ignoreCase = true)
            groupPost.visibility = if (esPost) View.VISIBLE else View.GONE
            groupEmp.visibility  = if (esPost) View.GONE else View.VISIBLE
            chipRol.setChipIconResource(if (esPost) R.drawable.ic_person else R.drawable.ic_work)
        }

        val esPostulanteVisible = groupPost.visibility == View.VISIBLE
        if (idUsuario > 0 && esPostulanteVisible) {
            val postulanteDAO = PostulanteDAO(this)

            val tvResumenCV   = findViewById<TextView>(R.id.tvResumenCV)
            val btnEditarCV   = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditarCV)

            run {
                val p = postulanteDAO.obtenerPorUsuario(idUsuario)
                cvUriString = p?.cvUrl
                tvResumenCV.text = p?.resumen?.takeIf { it.isNotBlank() }
                    ?: "(Agrega un resumen de tu perfil y experiencia…)"
            }

            btnEditarCV.setOnClickListener {
                mostrarDialogCv(tvResumenCV)
            }

            val tvExpEmpty    = findViewById<TextView>(R.id.tvExperienciaEmpty)
            val btnAgregarExp = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAgregarExperiencia)

            val chipGroupSkills = findViewById<ChipGroup>(R.id.chipGroupSkills)
            val btnAgregarSkill = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAgregarSkill)

            btnEditarCV.text = if (cvUriString.isNullOrBlank())
                "Adjuntar/Editar CV"
            else
                "CV (ver / reemplazar / resumen)"

            btnAgregarExp.setOnClickListener {
                val edit = TextInputEditText(this).apply { hint = "Empresa / Rol / Periodo / Logro" }
                MaterialAlertDialogBuilder(this)
                    .setTitle("Agregar experiencia")
                    .setView(edit)
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Agregar") { _, _ ->
                        val linea = edit.text?.toString()?.trim().orEmpty()
                        if (linea.isNotBlank()) {
                            postulanteDAO.agregarExperienciaLinea(idUsuario, linea)
                            val p2 = postulanteDAO.obtenerPorUsuario(idUsuario)
                            tvExpEmpty.text = p2?.experiencia ?: "Aún no registras experiencia."
                        }
                    }.show()
            }

            fun loadSkills(): MutableList<String> {
                val csv = getSharedPreferences("user", MODE_PRIVATE).getString("skills_$idUsuario", "") ?: ""
                return csv.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableList()
            }
            fun saveSkills(list: List<String>) {
                getSharedPreferences("user", MODE_PRIVATE).edit()
                    .putString("skills_$idUsuario", list.joinToString(","))
                    .apply()
            }
            fun renderSkills() {
                chipGroupSkills.removeAllViews()
                val skills = loadSkills()
                skills.forEach { s ->
                    val chip = Chip(this).apply {
                        text = s
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            val cur = loadSkills()
                            cur.remove(s)
                            saveSkills(cur)
                            renderSkills()
                        }
                    }
                    chipGroupSkills.addView(chip)
                }
            }
            renderSkills()

            btnAgregarSkill.setOnClickListener {
                val edit = TextInputEditText(this).apply { hint = "Ej: Kotlin" }
                MaterialAlertDialogBuilder(this)
                    .setTitle("Agregar habilidad")
                    .setView(edit)
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Agregar") { _, _ ->
                        val skill = edit.text?.toString()?.trim().orEmpty()
                        if (skill.isNotBlank()) {
                            val list = loadSkills()
                            if (!list.contains(skill)) list.add(skill)
                            saveSkills(list)
                            renderSkills()
                        }
                    }.show()
            }
        }
    }
}
