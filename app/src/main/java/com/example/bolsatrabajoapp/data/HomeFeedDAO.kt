package com.example.bolsatrabajoapp.data

import android.content.Context

data class FeedItem(
    val postulanteNombre: String,
    val tituloOferta: String,
    val fechaUnix: Long,
    val idUsuarioPostulante: Long,
    val idOferta: Int
)

class HomeFeedDAO(ctx: Context) {
    private val db = AppDataBaseHelper(ctx).readableDatabase

    fun feedEmpresa(idUsuarioEmpresa: Long, limit: Int = 20): List<FeedItem> {
        val idEmpresa = db.rawQuery(
            "SELECT id_empresa FROM empresa WHERE id_usuario=?",
            arrayOf(idUsuarioEmpresa.toString())
        ).use { c -> if (c.moveToFirst()) c.getLong(0) else return emptyList() }

        val sql = """
            SELECT u.nombres || ' ' || u.apellidos AS postulante,
                   o.titulo,
                   p.fecha,
                   u.id_usuario,
                   o.id_oferta
            FROM postulacion p
            JOIN postulante po ON p.id_postulante = po.id_postulante
            JOIN usuario u     ON po.id_usuario   = u.id_usuario
            JOIN oferta  o     ON p.id_oferta     = o.id_oferta
            WHERE o.id_empresa = ?
            ORDER BY p.fecha DESC
            LIMIT $limit
        """.trimIndent()

        val items = mutableListOf<FeedItem>()
        db.rawQuery(sql, arrayOf(idEmpresa.toString())).use { c ->
            while (c.moveToNext()) {
                items += FeedItem(
                    postulanteNombre   = c.getString(0),
                    tituloOferta       = c.getString(1),
                    fechaUnix          = c.getLong(2),
                    idUsuarioPostulante= c.getLong(3),
                    idOferta           = c.getInt(4)
                )
            }
        }
        return items
    }

    fun feedPostulante(idUsuarioPost: Long, limit: Int = 20): List<FeedItem> {
        val sql = """
            SELECT u.nombres || ' ' || u.apellidos AS postulante,
                   o.titulo,
                   p.fecha,
                   u.id_usuario,
                   o.id_oferta
            FROM postulacion p
            JOIN postulante po ON p.id_postulante = po.id_postulante
            JOIN usuario u     ON po.id_usuario   = u.id_usuario
            JOIN oferta  o     ON p.id_oferta     = o.id_oferta
            WHERE u.id_usuario = ?
            ORDER BY p.fecha DESC
            LIMIT $limit
        """.trimIndent()

        val items = mutableListOf<FeedItem>()
        db.rawQuery(sql, arrayOf(idUsuarioPost.toString())).use { c ->
            while (c.moveToNext()) {
                items += FeedItem(
                    postulanteNombre   = c.getString(0),
                    tituloOferta       = c.getString(1),
                    fechaUnix          = c.getLong(2),
                    idUsuarioPostulante= c.getLong(3),
                    idOferta           = c.getInt(4)
                )
            }
        }
        return items
    }
}
