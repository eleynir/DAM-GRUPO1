package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.bolsatrabajoapp.entity.Oferta

class OfertaDAO(context: Context) {

    private val dbHelper = AppDataBaseHelper(context)

    /**
     * Inserta una nueva oferta laboral.
     * @return ID insertado o -1 si falla.
     */
    fun insertarOferta(oferta: Oferta): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("empresa", oferta.empresa)
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
            put("icon_emp", oferta.icon_emp)
        }

        val result = db.insert("oferta", null, values)
        db.close()
        return result
    }

    /**
     * Actualiza una oferta laboral existente.
     * @return Número de filas afectadas.
     */
    fun actualizarOferta(oferta: Oferta): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("empresa", oferta.empresa)
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
            put("icon_emp", oferta.icon_emp)
        }
        val result = db.update(
            "oferta",
            valores,
            "id_oferta = ?",
            arrayOf(oferta.idOferta.toString())
        )
        db.close()
        return result
    }

    /**
     * Obtiene una oferta laboral por su ID.
     * @param id ID de la oferta a buscar.
     * @return Objeto Oferta o null si no se encuentra.
     */
    fun obtenerPorId(id: Int): Oferta? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            "oferta",
            null, // columns
            "id_oferta = ?", // selection
            arrayOf(id.toString()), // selectionArgs
            null, // groupBy
            null, // having
            null // orderBy
        )

        var oferta: Oferta? = null
        if (cursor.moveToFirst()) {
            oferta = Oferta(
                idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                empresa = cursor.getString(cursor.getColumnIndexOrThrow("empresa")),
                titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                icon_emp = cursor.getString(cursor.getColumnIndexOrThrow("icon_emp"))
            )
        }

        cursor.close()
        db.close()
        return oferta
    }


    fun obtenerPorLista(idLista: Int): List<Oferta> {
        val lista = mutableListOf<Oferta>()
        val db = dbHelper.readableDatabase
        val cursor : Cursor = db.rawQuery(
            "SELECT * FROM oferta WHERE id_oferta IN (SELECT id_oferta FROM lista_oferta WHERE id_lista = ?)",
            arrayOf(idLista.toString())
        )
        while (cursor.moveToNext()){
            lista.add(
                Oferta(
                    idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                    empresa = cursor.getString(cursor.getColumnIndexOrThrow("empresa")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                    salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                    modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                    vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                    detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                    icon_emp = cursor.getString(cursor.getColumnIndexOrThrow("icon_emp"))
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }


    fun listarOfertas(): List<Oferta> {
        val lista = mutableListOf<Oferta>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM oferta ORDER BY id_oferta DESC", null)

        while (cursor.moveToNext()) {
            lista.add(
                Oferta(
                    idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                    empresa = cursor.getString(cursor.getColumnIndexOrThrow("empresa")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                    salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                    modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                    vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                    detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                    icon_emp = cursor.getString(cursor.getColumnIndexOrThrow("icon_emp"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }



    /** Devuelve todas las ofertas registradas */
    fun obtenerOfertas(): List<Oferta> {
        val lista = mutableListOf<Oferta>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM oferta ORDER BY id_oferta DESC", null)

        while (cursor.moveToNext()) {
            lista.add(
                Oferta(
                    idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                    empresa = cursor.getString(cursor.getColumnIndexOrThrow("empresa")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                    salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                    modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                    vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                    detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                    icon_emp = cursor.getString(cursor.getColumnIndexOrThrow("icon_emp"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }

    /**
     * Devuelve todas las ofertas registradas en la base de datos.
     */
    fun listar(): List<Oferta> {
        val lista = mutableListOf<Oferta>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM oferta ORDER BY creado_en DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val oferta = Oferta(
                    idOferta = cursor.getInt(cursor.getColumnIndexOrThrow("id_oferta")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    empresa = cursor.getString(cursor.getColumnIndexOrThrow("empresa")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    salario_min = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_min")),
                    salario_max = cursor.getDouble(cursor.getColumnIndexOrThrow("salario_max")),
                    modalidad = cursor.getString(cursor.getColumnIndexOrThrow("modalidad")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    categoria = cursor.getInt(cursor.getColumnIndexOrThrow("categoria")),
                    vigente = cursor.getInt(cursor.getColumnIndexOrThrow("vigente")),
                    detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                    icon_emp = cursor.getString(cursor.getColumnIndexOrThrow("icon_emp"))
                )
                lista.add(oferta)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    /**
     * Elimina una oferta por su ID.
     * @return número de filas afectadas.
     */
    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete("oferta", "id_oferta = ?", arrayOf(id.toString()))
        db.close()
        return filas
    }
}