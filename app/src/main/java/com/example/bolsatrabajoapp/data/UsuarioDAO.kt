package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import com.example.bolsatrabajoapp.entity.Usuario





class UsuarioDAO(context: Context) {
    private val helper = AppDataBaseHelper(context)



    // Acá se registrará al usuario + sus datos extra según rol. Devolverá id del usuario o -1 si falló.
    fun insertar(
        u: Usuario,
        ruc: String? = null,
        razonSocial: String? = null,
        resumenCV: String? = null
    ): Long {
        val db = helper.writableDatabase
        db.beginTransaction()
        return try {
            // 1) usuario
            val values = ContentValues().apply {
                put("correo", u.correo)
                put("clave", u.clave)
                put("nombres", u.nombres)
                put("apellidos", u.apellidos)
                put("dni", "")
                put("celular", u.celular)
                put("sexo", u.sexo)
                put("edad", u.edad)
                put("rol", u.rol)
            }
            val idUsuario = db.insertOrThrow("usuario", null, values)
            // 2) datos extra por rol
            if (u.rol == "EMPRESA") {
                val v = ContentValues().apply {
                    put("id_usuario", idUsuario)
                    put("ruc", ruc)
                    put("razon_social", razonSocial ?: "${u.nombres} ${u.apellidos}")
                    put("rubro", "")
                    put("sitio_web", "")
                    put("descripcion", "")
                }
                db.insertOrThrow("empresa", null, v)
            } else { // POSTULANTE
                val v = ContentValues().apply {
                    put("id_usuario", idUsuario)
                    put("cv_url", null as String?)
                    put("resumen", resumenCV)
                    put("experiencia", "")
                }
                db.insertOrThrow("postulante", null, v)
            }
            db.setTransactionSuccessful()
            idUsuario
        } catch (e: Exception) {
            -1
        } finally {
            db.endTransaction()
        }
    }

    // Login: devolverá el usuario si coincide correo/clave, o null si no existe.
    fun login(correo: String, clave: String): Usuario? {
        val db = helper.readableDatabase
        db.rawQuery(
            """
            SELECT id_usuario, nombres, apellidos, correo, clave, edad, celular, sexo, rol
            FROM usuario
            WHERE correo = ? AND clave = ?
            """.trimIndent(),
            arrayOf(correo, clave)
        ).use { c ->
            if (c.moveToFirst()) {
                return Usuario(
                    idUsuario = c.getLong(0),
                    nombres   = c.getString(1),
                    apellidos = c.getString(2),
                    correo    = c.getString(3),
                    clave     = c.getString(4),
                    edad      = if (c.isNull(5)) null else c.getInt(5),
                    celular   = if (c.isNull(6)) null else c.getString(6),
                    sexo      = if (c.isNull(7)) null else c.getString(7),
                    rol       = c.getString(8)
                )
            }
        }
        return null
    }

    // validación si ya existe el correo en la bd
    fun existeCorreo(correo: String): Boolean {
        val db = helper.readableDatabase
        db.rawQuery(
            "SELECT 1 FROM usuario WHERE correo = ? LIMIT 1",
            arrayOf(correo)
        ).use { c -> return c.moveToFirst() }
    }


}