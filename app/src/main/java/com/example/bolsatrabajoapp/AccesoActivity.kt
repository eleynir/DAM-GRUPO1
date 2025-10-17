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


    class AccesoActivity : AppCompatActivity() {

        private var tvRegistro: TextView? = null
        private var btnLogin: Button? = null

        private lateinit var ivLLamada : ImageView

        private lateinit var tietCorreo : TextInputEditText

        private lateinit var tilClave : TextInputLayout

        private lateinit var tilCorreo : TextInputLayout

        private lateinit var tietClave : TextInputEditText
        private lateinit var ivInternet : ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_acceso)

            tvRegistro = findViewById(R.id.tvRegistro)
            btnLogin   = findViewById(R.id.btnLogin)
            ivLLamada = findViewById(R.id.ivLlamada)
            ivInternet = findViewById(R.id.ivInternet)
            tietCorreo = findViewById(R.id.tietCorreo)
            tietClave = findViewById(R.id.tietClave)
            tilCorreo = findViewById(R.id.tilCorreo)
            tilClave = findViewById(R.id.tilClave)

            tvRegistro?.setOnClickListener {
                var intent = Intent(this, RegistroActivity::class.java)
                val correo : String = "" + tietCorreo.text
                intent.putExtra("correo", correo)
                startActivity(intent)
            }

            ivInternet.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData("https://www.youtube.com/watch?v=9hhMUT2U2L4".toUri())
                startActivity(intent)
            }

            ivLLamada.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                } else {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = "tel:+51924269099".toUri()
                    startActivity(intent)
                }
            }

            btnLogin?.setOnClickListener {
                validarCampos()
            }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                insets
            }
        }


        fun validarCampos() {
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

            val correoCompleto = normalizarCorreo(usuarioInput)
            val dao = com.example.bolsatrabajoapp.data.UsuarioDAO(this)
            val usuario = dao.login(correoCompleto, clave)

            if (usuario != null) {
                val prefs = getSharedPreferences("user", MODE_PRIVATE)
                prefs.edit()
                    .putString("name", "${usuario.nombres} ${usuario.apellidos}")
                    .putString("email", usuario.correo)
                    .putString("rol", usuario.rol)
                    .apply()
                startActivity(Intent(this, HomeActivity::class.java))
                Toast.makeText(this, "Bienvenido ${usuario.nombres}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

