package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context

data class PostulanteMini(
    val resumen: String?,
    val experiencia: String?,
    val cvUrl: String?
)

class PostulanteDAO(ctx: Context) {
    private val helper = AppDataBaseHelper(ctx)

    fun obtenerPorUsuario(idUsuario: Long): PostulanteMini? {
        val db = helper.readableDatabase
        db.rawQuery(
            """
            SELECT resumen, experiencia, cv_url
            FROM postulante
            WHERE id_usuario = ?
            """.trimIndent(),
            arrayOf(idUsuario.toString())
        ).use { c ->
            if (c.moveToFirst()) {
                return PostulanteMini(
                    resumen     = if (c.isNull(0)) null else c.getString(0),
                    experiencia = if (c.isNull(1)) null else c.getString(1),
                    cvUrl       = if (c.isNull(2)) null else c.getString(2)
                )
            }
        }
        return null
    }

    fun obtenerIdPostulantePorUsuario(idUsuario: Long): Long? {
        val c = helper.readableDatabase.rawQuery(
            "SELECT id_postulante FROM postulante WHERE id_usuario=?",
            arrayOf(idUsuario.toString())
        )
        c.use { if (it.moveToFirst()) return it.getLong(0) }
        return null
    }

    fun existePostulacion(idOferta: Int, idPostulante: Long): Boolean {
        val c = helper.readableDatabase.rawQuery(
            "SELECT 1 FROM postulacion WHERE id_oferta=? AND id_postulante=?",
            arrayOf(idOferta.toString(), idPostulante.toString())
        )
        c.use { return it.moveToFirst() }
    }

    fun crearPostulacion(idOferta: Int, idPostulante: Long): Long {
        val cv = ContentValues().apply {
            put("id_oferta", idOferta)
            put("id_postulante", idPostulante)
            put("estado", "ENVIADA")
        }
        return helper.writableDatabase.insert("postulacion", null, cv)
    }



    fun actualizarResumen(idUsuario: Long, resumen: String?): Int {
        val db = helper.writableDatabase
        val cv = ContentValues().apply { put("resumen", resumen) }
        return db.update("postulante", cv, "id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    fun agregarExperienciaLinea(idUsuario: Long, nuevaLinea: String): Int {
        val actual = obtenerPorUsuario(idUsuario)?.experiencia ?: ""
        val nuevo = (if (actual.isBlank()) "" else "$actual\n") + "• $nuevaLinea"
        val db = helper.writableDatabase
        val cv = ContentValues().apply { put("experiencia", nuevo) }
        return db.update("postulante", cv, "id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    fun actualizarCvUrl(idUsuario: Long, uriString: String): Int {
        val db = helper.writableDatabase
        val cv = ContentValues().apply { put("cv_url", uriString) }
        return db.update("postulante", cv, "id_usuario = ?", arrayOf(idUsuario.toString()))
    }
}
