package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import com.example.bolsatrabajoapp.entity.Usuario

class UsuarioDAO (context : Context) {
    private val db = AppDataBaseHelper(context)

    private val dbHelper = AppDataBaseHelper(context)

    private fun insertar(usuario: Usuario) : Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("empresa", usuario.nombre)
            put("descripcion", usuario.correo)
            put("salario_min", usuario.clave)
        }
        return db.insert("usuario",null, valores)
    }
}