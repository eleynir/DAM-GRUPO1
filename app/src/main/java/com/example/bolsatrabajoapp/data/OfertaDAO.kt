package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.bolsatrabajoapp.entity.Oferta
import kotlin.String


class OfertaDAO(context: Context) {
    private val db = AppDataBaseHelper(context)

    private val dbHelper = AppDataBaseHelper(context)

    private fun insertar(oferta : Oferta) : Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("titulo", oferta.titulo)
            put("empresa", oferta.empresa)
            put("descripcion", oferta.descripcion)
            put("salario_min", oferta.salario_min)
            put("salario_max", oferta.salario_max)
            put("modalidad", oferta.modalidad)
            put("tipo", oferta.tipo)
            put("ubicacion", oferta.ubicacion)
            put("categoria", oferta.categoria)
            put("vigente",oferta.vigente)
            put("detalle",oferta.detalle)
            put("icon_emp",oferta.iconEmpresa)
        }
        return db.insert("oferta",null, valores)
    }

    private fun obtenerOferta(idOferta: Int) : List<Oferta>{
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Oferta>()
        val cursor : Cursor = db.rawQuery(
            "SELECT * FROM oferta WHERE id_oferta = ?",
            arrayOf(idOferta.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Oferta(
                    idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    empresa = cursor.getString(cursor.getColumnIndexOrThrow("id_empresa")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                    salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                    modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                    vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                    detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                    iconEmpresa = cursor.getString(cursor.getColumnIndexOrThrow("icon_empresa")),
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
}

