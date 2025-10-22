package com.example.bolsatrabajoapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bolsatrabajoapp.ui.CategoriaFragment
import com.example.bolsatrabajoapp.ui.ListaOfertasFragment
import com.example.bolsatrabajoapp.ui.OfertaFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

abstract class BaseDrawerActivity : AppCompatActivity() {

    abstract fun getLayoutResId(): Int
    abstract fun navSelectedItemId(): Int
    abstract fun screenTitle(): String

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_drawer)

        val content = findViewById<android.widget.FrameLayout>(R.id.contentFrame)
        layoutInflater.inflate(getLayoutResId(), content, true)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerLayout)
        val nav: NavigationView = findViewById(R.id.navView)

        // 📋 Obtener datos del usuario
        val header = nav.getHeaderView(0)
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val name = prefs.getString("name", "Usuario")
        val email = prefs.getString("email", "correo@dominio.com")
        val rol = prefs.getString("rol", "POSTULANTE") ?: "POSTULANTE"

        header.findViewById<TextView>(R.id.tvNombreHeader).text = name
        header.findViewById<TextView>(R.id.tvCorreoHeader).text = email
        header.findViewById<TextView>(R.id.tvRolHeader).text =
            if (rol == "EMPRESA") "Empresa" else "Postulante"

        // 🔒 Configurar visibilidad del menú según el rol
        val menu = nav.menu
        menu.findItem(R.id.nav_publicar)?.isVisible = (rol == "EMPRESA")
        menu.findItem(R.id.nav_Categoria)?.isVisible = true

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // 🧭 Configurar navegación del menú lateral
        nav.setNavigationItemSelectedListener { item: MenuItem ->
            val fragment = when (item.itemId) {
                R.id.nav_Ofertas -> ListaOfertasFragment()
                R.id.nav_publicar -> OfertaFragment()
                R.id.nav_Categoria -> CategoriaFragment()
                else -> null
            }

            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.contentFrame, fragment)
                    .addToBackStack(null)
                    .commit()
            } else if (item.itemId == R.id.nav_perfil) {
                startActivity(Intent(this, PerfilActivity::class.java))
            } else if (item.itemId == R.id.nav_logout) {
                getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
                startActivity(
                    Intent(this, AccesoActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }

            drawer.closeDrawer(GravityCompat.START)
            true
        }

        // 🔙 Manejar el botón atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        findViewById<NavigationView>(R.id.navView).setCheckedItem(navSelectedItemId())
        findViewById<MaterialToolbar>(R.id.toolbar).title = screenTitle()
    }
}
