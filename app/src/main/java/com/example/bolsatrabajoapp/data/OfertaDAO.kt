package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.bolsatrabajoapp.entity.Oferta

class OfertaDAO(context: Context) {
    private val dbHelper = AppDataBaseHelper(context)


    fun insertar(oferta: Oferta, idEmpresa: Long): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("id_empresa", idEmpresa)
            put("empresa_nombre", oferta.empresa)
            put("titulo", oferta.titulo)
            put("descripcion", oferta.descripcion)
            put("salario_min", oferta.salario_min)
            put("salario_max", oferta.salario_max)
            put("modalidad", oferta.modalidad)
            put("tipo", oferta.tipo)
            put("ubicacion", oferta.ubicacion)
            put("categoria", oferta.categoria)
            put("vigente", oferta.vigente)
            put("detalle", oferta.detalle)
            put("icon_empresa", oferta.iconEmpresa)
        }
        return db.insert("oferta", null, valores)
    }


    fun obtenerIdEmpresaPorUsuario(idUsuario: Long): Long? {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT id_empresa FROM empresa WHERE id_usuario=?",
            arrayOf(idUsuario.toString())
        )
        var id: Long? = null
        c.use { if (it.moveToFirst()) id = it.getLong(0) }
        return id
    }


    fun listarActivas(): List<Oferta> {
        val sql = """
            SELECT id_oferta, empresa_nombre, titulo, descripcion, salario_min, salario_max,
                   modalidad, tipo, ubicacion, categoria, vigente, detalle, icon_empresa
            FROM oferta
            WHERE vigente=1
            ORDER BY id_oferta DESC
        """.trimIndent()
        return mapearListado(sql, null)
    }


    fun listarPorEmpresa(idEmpresa: Long): List<Oferta> {
        val sql = """
            SELECT id_oferta, empresa_nombre, titulo, descripcion, salario_min, salario_max,
                   modalidad, tipo, ubicacion, categoria, vigente, detalle, icon_empresa
            FROM oferta
            WHERE id_empresa=?
            ORDER BY id_oferta DESC
        """.trimIndent()
        return mapearListado(sql, arrayOf(idEmpresa.toString()))
    }

    fun obtenerOferta(idOferta: Int): Oferta? {
        val db = dbHelper.readableDatabase
        val c: Cursor = db.rawQuery(
            """
            SELECT id_oferta, empresa_nombre, titulo, descripcion, salario_min, salario_max,
                   modalidad, tipo, ubicacion, categoria, vigente, detalle, icon_empresa
            FROM oferta
            WHERE id_oferta=?
            """.trimIndent(),
            arrayOf(idOferta.toString())
        )
        c.use {
            return if (it.moveToFirst()) {
                Oferta(
                    idOferta     = it.getInt(0),
                    empresa      = it.getString(1),
                    titulo       = it.getString(2),
                    descripcion  = it.getString(3),
                    salario_min  = it.getDouble(4),
                    salario_max  = it.getDouble(5),
                    modalidad    = it.getString(6),
                    tipo         = it.getString(7),
                    ubicacion    = it.getString(8),
                    categoria    = it.getInt(9),
                    vigente      = it.getInt(10),
                    detalle      = it.getString(11),
                    iconEmpresa  = it.getString(12)
                )
            } else null
        }
    }

    private fun mapearListado(sql: String, args: Array<String>?): List<Oferta> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Oferta>()
        val c = db.rawQuery(sql, args)
        c.use {
            while (it.moveToNext()) {
                lista.add(
                    Oferta(
                        idOferta     = it.getInt(0),
                        empresa      = it.getString(1),
                        titulo       = it.getString(2),
                        descripcion  = it.getString(3),
                        salario_min  = it.getDouble(4),
                        salario_max  = it.getDouble(5),
                        modalidad    = it.getString(6),
                        tipo         = it.getString(7),
                        ubicacion    = it.getString(8),
                        categoria    = it.getInt(9),
                        vigente      = it.getInt(10),
                        detalle      = it.getString(11),
                        iconEmpresa  = it.getString(12)
                    )
                )
            }
        }
        return lista
    }
}
