package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.bolsatrabajoapp.entity.Categoria

class CategoriaDAO(context: Context) {
    private val dbHelper = AppDataBaseHelper(context)

    // Inserta una nueva categoría
    fun insertarCategoria(nombre: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
        }
        return try {
            val result = db.insertOrThrow("categoria", null, values)
            db.close()
            result
        } catch (e: Exception) {
            db.close()
            -1L
        }
    }

    // Obtiene todas las categorías (id + nombre)
    fun obtenerTodasLasCategorias(): List<Categoria> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Categoria>()

        val cursor = db.rawQuery(
            "SELECT id_categoria, nombre FROM categoria ORDER BY nombre ASC",
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            lista.add(Categoria(idCategoria = id, descripcion = nombre))
        }

        cursor.close()
        db.close()
        return lista
    }



    // Obtiene lista (ID, Nombre)
    fun obtenerCategoriasConId(): List<Pair<Int, String>> {
        val categorias = mutableListOf<Pair<Int, String>>()
        val db = dbHelper.readableDatabase

        db.rawQuery(
            "SELECT id_categoria, nombre FROM categoria ORDER BY nombre ASC", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                categorias.add(Pair(id, nombre))
            }
        }
        db.close()
        return categorias
    }

    // Obtiene solo los nombres
    fun obtenerNombresCategorias(): List<String> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        val cursor = db.rawQuery(
            "SELECT nombre FROM categoria ORDER BY nombre ASC", null
        )

        while (cursor.moveToNext()) {
            lista.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
        }

        cursor.close()
        db.close()
        return lista
    }

    // Obtiene el ID a partir del nombre
    fun obtenerIdCategoriaPorNombre(nombreCat: String): Int {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id_categoria FROM categoria WHERE nombre = ?",
            arrayOf(nombreCat)
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"))
        }
        cursor.close()
        db.close()
        return id
    }

    // Verifica si existe una categoría
    fun existeCategoria(nombre: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM categoria WHERE nombre = ?",
            arrayOf(nombre)
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count > 0
    }

    // Actualiza el nombre de una categoría por ID
    fun actualizarCategoria(id: Int, nuevoNombre: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nuevoNombre)
        }
        val result = db.update(
            "categoria",
            values,
            "id_categoria = ?",
            arrayOf(id.toString())
        )
        db.close()
        return result
    }


    // Elimina una categoría por ID
    fun eliminarCategoria(id: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete("categoria", "id_categoria = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}
