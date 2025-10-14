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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

abstract class BaseDrawerActivity : AppCompatActivity() {

    abstract fun getLayoutResId(): Int

    abstract fun navSelectedItemId(): Int
    abstract fun screenTitle(): String


    //aca se configurarán los roles
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

        val header = nav.getHeaderView(0)
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        header.findViewById<TextView>(R.id.tvNombreHeader).text = prefs.getString("name", "Usuario")
        header.findViewById<TextView>(R.id.tvCorreoHeader).text = prefs.getString("email", "correo@dominio.com")

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        nav.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_perfil   -> goIfNotCurrent(PerfilActivity::class.java)
                R.id.nav_listado  -> goIfNotCurrent(ListadoOfertasActivity::class.java)
                R.id.nav_publicar -> goIfNotCurrent(PublicarOfertaActivity::class.java)
                R.id.nav_logout   -> {
                    getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, AccesoActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
                else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        findViewById<NavigationView>(R.id.navView).setCheckedItem(navSelectedItemId())
        findViewById<MaterialToolbar>(R.id.toolbar).title = screenTitle()
    }

    private fun goIfNotCurrent(target: Class<out AppCompatActivity>) {
        if (this::class.java != target) startActivity(Intent(this, target))
    }
}