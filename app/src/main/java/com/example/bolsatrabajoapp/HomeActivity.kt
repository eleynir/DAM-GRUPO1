package com.example.bolsatrabajoapp

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.bolsatrabajoapp.ui.CategoriaFragment
import com.example.bolsatrabajoapp.ui.ListaOfertasFragment
import com.example.bolsatrabajoapp.ui.OfertaFragment
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var dlayMenu: DrawerLayout
    private lateinit var nvMenu: NavigationView
    private lateinit var ivMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Asumiendo que el layout activity_home tiene un FrameLayout/FragmentContainerView con id=R.id.contenedorFragment
        // y el DrawerLayout con id=R.id.dlayMenu
        setContentView(R.layout.activity_home)

        dlayMenu = findViewById(R.id.dlayMenu)
        nvMenu = findViewById(R.id.nvMenu)
        ivMenu = findViewById(R.id.ivMenu)

        // Ajuste: Listener para padding del ícono del menú (para evitar que la barra de estado lo cubra)
        ViewCompat.setOnApplyWindowInsetsListener(ivMenu) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        // 1. Abre el menú lateral al tocar el ícono
        ivMenu.setOnClickListener {
            dlayMenu.open()
        }

        // 2. Maneja la selección de ítems del Navigation View
        nvMenu.setNavigationItemSelectedListener { menuItem ->
            // Primero cierra el menú y marca el ítem como seleccionado
            menuItem.isChecked = true
            dlayMenu.closeDrawers()

            when(menuItem.itemId){
                // ✅ CORRECCIÓN 1: El inicio debe mostrar la lista de ofertas
                R.id.itInicio -> replaceFragment(ListaOfertasFragment())
                R.id.nav_Ofertas -> replaceFragment(ListaOfertasFragment())

                // ✅ CORRECCIÓN 2: nav_publicar debe llevar al formulario de publicación
                R.id.nav_publicar -> replaceFragment(OfertaFragment())

                // ✅ CORRECCIÓN 3: nav_Categoria debe llevar al formulario de categorías
                R.id.nav_Categoria -> replaceFragment(CategoriaFragment())

                // ✅ CORRECCIÓN 4: nav_perfil debe llevar al Perfil (se asume que es la intención real, no la lista)
                // Usamos ListaOfertaFragment temporalmente si no existe PerfilFragment
                R.id.nav_perfil -> {
                    // Aquí deberías usar PerfilFragment(), si existe.
                    replaceFragment(ListaOfertasFragment())
                }
            }
            true
        }

        // 3. Fragment inicial (al abrir la app por primera vez)
        if (savedInstanceState == null) {
            // ✅ CORRECCIÓN 5: El inicio siempre debe mostrar la lista de ofertas.
            replaceFragment(ListaOfertasFragment())
            nvMenu.setCheckedItem(R.id.itInicio)
        }
    }

    private fun replaceFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            // Opcional: .addToBackStack(null) para permitir la navegación hacia atrás
            .commit()
    }
}