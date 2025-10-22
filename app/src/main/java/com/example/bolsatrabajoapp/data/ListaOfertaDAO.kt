package com.example.bolsatrabajoapp.data

import android.content.Context
import com.example.bolsatrabajoapp.entity.Oferta

class ListaOfertaDAO (context: Context){
    private val dbHelper = AppDataBaseHelper(context)
    fun obtenerPorLista(idOferta: Oferta) : List<Oferta> {
        val lista = mutableListOf<Oferta>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM oferta WHERE id_oferta IN (SELECT id_oferta FROM lista_oferta WHERE id_lista = ?)",
            arrayOf(idOferta.toString())
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

}