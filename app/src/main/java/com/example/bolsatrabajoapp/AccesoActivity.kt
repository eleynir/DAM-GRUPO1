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

        private val listaUsuarios = mutableListOf(
            Usuario(
                idUsuario = 1,
                nombres = "José Vidal",
                apellidos = "Aquije Quintero",
                correo = "joseaquije2@cibertec.edu.pe",
                clave = "01234",
                edad = 21,
                rol = "POSTULANTE"
            ),
            Usuario(
                idUsuario = 2,
                nombres = "Test",
                apellidos = "Ape Test",
                correo = "testmail@cibertec.edu.pe",
                clave = "5678",
                edad = 19,
                rol = "EMPRESA"
            )
        )

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
            val correo = tietCorreo.text.toString().trim()
            val clave = tietClave.text.toString().trim()
            var error : Boolean = false
            if (correo.isEmpty()) {
                error = true
                tilCorreo.error = "Ingrese un correo"
            } else {
                tilCorreo.error = null
            }
            if (clave.isEmpty()) {
                error = true
                tilClave.error="Ingrese una contraseña"
            } else {
                tilClave.error=null
            }
            if (!error)  {
                var usuario : Usuario?= null
      //          for (i in 0 until listaUsuarios.size) {
       //             if (listaUsuarios[i].correo == (correo + "@cibertec.edu.pe") && listaUsuarios[i].clave == clave) {
       //               usuario = listaUsuarios[i]
      //         }
      //    }

                for (u in listaUsuarios) {
                    if (u.correo == (correo + "@cibertec.edu.pe") && u.clave == clave){
                        usuario = u
                    }
                }
                if (usuario != null) {
                    val prefs = getSharedPreferences("user", MODE_PRIVATE)
                    prefs.edit()
                        .putString("name", usuario?.nombre ?: "Usuario")
                        .putString("email", usuario?.correo ?: "correo@dominio.com")
                        .apply()
                    startActivity(Intent(this, HomeActivity::class.java))
                    Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

