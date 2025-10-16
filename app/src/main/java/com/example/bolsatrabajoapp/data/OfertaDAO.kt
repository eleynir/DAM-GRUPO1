package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.bolsatrabajoapp.entity.Oferta

class OfertaDAO(context: Context) {

    private val dbHelper = AppDataBaseHelper(context)

    fun insertar(oferta: Oferta): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_empresa", 1) // ⚠️ Cambia esto si manejas empresas reales con sesión
            put("empresa_nombre", oferta.empresa)
            put("titulo", oferta.titulo)
            put("descripcion", oferta.descripcion)
            put("salario_min", oferta.salario_min)
            put("salario_max", oferta.salario_max)
            put("modalidad", oferta.modalidad.uppercase())
            put("tipo", oferta.tipo.uppercase())
            put("ubicacion", oferta.ubicacion)
            put("categoria", oferta.categoria)
            put("vigente", oferta.vigente)
            put("detalle", oferta.detalle)
            put("icon_empresa", oferta.icon_emp)
        }

        val id = db.insert("oferta", null, values)
        db.close()
        return id
    }

    fun listar(): List<Oferta> {
        val lista = mutableListOf<Oferta>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM oferta", null)

        if (cursor.moveToFirst()) {
            do {
                val oferta = Oferta(
                    idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    empresa = cursor.getString(cursor.getColumnIndexOrThrow("empresa_nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                    salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                    modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                    vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                    detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                    icon_emp = cursor.getString(cursor.getColumnIndexOrThrow("icon_empresa"))
                )
                lista.add(oferta)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete("oferta", "id_oferta = ?", arrayOf(id.toString()))
        db.close()
        return filas
    }
}
