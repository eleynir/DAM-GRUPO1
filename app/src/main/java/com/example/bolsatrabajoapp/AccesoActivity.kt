package com.example.bolsatrabajoapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bolsatrabajoapp.entity.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.bolsatrabajoapp.data.UsuarioDAO // Importación necesaria para el DAO
// 💡 Importaciones necesarias para Coroutines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccesoActivity : AppCompatActivity() {

    // 💡 Mejora: Usar lateinit para las views que siempre estarán presentes
    private lateinit var tvRegistro: TextView
    private lateinit var btnLogin: Button
    private lateinit var ivLLamada: ImageView
    private lateinit var ivInternet: ImageView
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilClave: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acceso)

        // 1. Inicialización de Views (usando lateinit)
        tvRegistro = findViewById(R.id.tvRegistro)
        btnLogin = findViewById(R.id.btnLogin)
        ivLLamada = findViewById(R.id.ivLlamada)
        ivInternet = findViewById(R.id.ivInternet)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilClave = findViewById(R.id.tilClave)

        // 2. Listeners
        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            // Forma limpia de obtener el texto
            intent.putExtra("correo", tietCorreo.text.toString())
            startActivity(intent)
        }

        ivInternet.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            // YouTube URL
            intent.data = "https://www.youtube.com/watch?v=9hhMUT2U2L4".toUri()
            startActivity(intent)
        }

        ivLLamada.setOnClickListener {
            // Lógica de permiso de llamada (Se recomienda Activity Result API, pero se mantiene la tuya)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = "tel:+51924269099".toUri()
                startActivity(intent)
            }
        }

        btnLogin.setOnClickListener {
            // Llama a la función de validación/login
            validarYLogin()
        }

        // 3. Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    // 💡 Implementación faltante: normalizarCorreo
    private fun normalizarCorreo(usuarioInput: String): String {
        val dominio = "@cibertec.edu.pe"
        return if (usuarioInput.contains("@")) {
            usuarioInput
        } else {
            usuarioInput + dominio
        }
    }

    // 💡 FUNCIÓN CORREGIDA: Usa Coroutines para el login asíncrono
    private fun validarYLogin() {
        val usuarioInput = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()
        var error = false

        if (usuarioInput.isEmpty()) {
            error = true
            tilCorreo.error = "Ingrese un usuario"
        } else tilCorreo.error = null

        if (clave.isEmpty()) {
            error = true
            tilClave.error = "Ingrese una contraseña"
        } else tilClave.error = null

        if (error) return

        // 1. Mostrar un indicador de carga (opcional)
        // btnLogin.isEnabled = false

        val correoCompleto = normalizarCorreo(usuarioInput)

        // 2. Ejecutar la operación de base de datos en un hilo secundario (IO)
        CoroutineScope(Dispatchers.IO).launch {
            val dao = UsuarioDAO(this@AccesoActivity)
            val usuario = dao.login(correoCompleto, clave)

            // 3. Volver al hilo principal (Main) para actualizar la UI
            withContext(Dispatchers.Main) {
                // btnLogin.isEnabled = true // Restaurar botón

                if (usuario != null) {
                    val prefs = getSharedPreferences("user", MODE_PRIVATE)
                    prefs.edit()
                        .putString("name", "${usuario.nombres} ${usuario.apellidos}")
                        .putString("email", usuario.correo)
                        .putString("rol", usuario.rol)
                        .apply()
                    startActivity(Intent(this@AccesoActivity, HomeActivity::class.java))
                    Toast.makeText(this@AccesoActivity, "Bienvenido ${usuario.nombres}", Toast.LENGTH_SHORT).show()
                    finish() // Terminar AccesoActivity para que el usuario no pueda volver con el botón atrás
                } else {
                    Toast.makeText(this@AccesoActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}