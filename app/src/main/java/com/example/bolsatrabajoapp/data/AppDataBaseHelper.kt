package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.bolsatrabajoapp.entity.Oferta

private const val DB_NAME = "emplea.db"
private const val DB_VERSION = 1

class AppDataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // ------------------------------------
        // CREACIÓN DE TABLAS (Mismo código de DDL)
        // ------------------------------------

        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                correo TEXT NOT NULL UNIQUE,
                clave TEXT NOT NULL,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                celular TEXT,
                sexo TEXT,
                edad INTEGER,
                rol TEXT NOT NULL CHECK (rol IN ('POSTULANTE','EMPRESA')),
                creado_en INTEGER DEFAULT (strftime('%s','now'))
            );
        """)

        db.execSQL("""
            CREATE TABLE empresa (
                id_empresa INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL UNIQUE,
                ruc TEXT,
                razon_social TEXT NOT NULL,
                rubro TEXT,
                sitio_web TEXT,
                descripcion TEXT,
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
            );
        """)

        db.execSQL("""
            CREATE TABLE postulante (
                id_postulante INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL UNIQUE,
                cv_url TEXT,
                resumen TEXT,
                experiencia TEXT,
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
            );
        """)

        db.execSQL("""
            CREATE TABLE categoria (
                id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            );
        """)

        db.execSQL("""
            CREATE TABLE oferta (
                id_oferta INTEGER PRIMARY KEY AUTOINCREMENT,
                empresa TEXT NOT NULL,
                titulo TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                salario_min REAL,
                salario_max REAL,
                modalidad TEXT,
                tipo TEXT,
                ubicacion TEXT,
                categoria INTEGER NOT NULL,
                vigente INTEGER NOT NULL DEFAULT 1,
                detalle TEXT,
                icon_emp TEXT,
                creado_en INTEGER DEFAULT (strftime('%s','now')),
                FOREIGN KEY (categoria) REFERENCES categoria(id_categoria) ON DELETE RESTRICT
            );
        """)

        db.execSQL("""
            CREATE TABLE postulacion (
                id_postulacion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_oferta INTEGER NOT NULL,
                id_postulante INTEGER NOT NULL,
                estado TEXT NOT NULL DEFAULT 'ENVIADA'
                       CHECK (estado IN ('ENVIADA','VISTA','DESCARTADA','ACEPTADA')),
                fecha INTEGER DEFAULT (strftime('%s','now')),
                UNIQUE (id_oferta, id_postulante),
                FOREIGN KEY (id_oferta) REFERENCES oferta(id_oferta) ON DELETE CASCADE,
                FOREIGN KEY (id_postulante) REFERENCES postulante(id_postulante) ON DELETE CASCADE
            );
        """)

        db.execSQL("""
            CREATE TABLE favorito (
                id_usuario INTEGER NOT NULL,
                id_oferta INTEGER NOT NULL,
                PRIMARY KEY (id_usuario, id_oferta),
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                FOREIGN KEY (id_oferta) REFERENCES oferta(id_oferta) ON DELETE CASCADE
            );
        """)

        db.execSQL("CREATE INDEX idx_oferta_categoria ON oferta(categoria);")
        db.execSQL("CREATE INDEX idx_oferta_vigente ON oferta(vigente);")

        // Sembrar categorías iniciales
        val categorias = listOf(
            "Desarrollo Web", "Diseño Gráfico", "Marketing Digital",
            "Administración", "Recursos Humanos"
        )
        categorias.forEach { nombre ->
            db.execSQL("INSERT INTO categoria (nombre) VALUES ('$nombre');")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS favorito")
        db.execSQL("DROP TABLE IF EXISTS postulacion")
        db.execSQL("DROP TABLE IF EXISTS oferta")
        db.execSQL("DROP TABLE IF EXISTS categoria")
        db.execSQL("DROP TABLE IF EXISTS postulante")
        db.execSQL("DROP TABLE IF EXISTS empresa")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }

    // ------------------------------------
    // CATEGORÍAS (Funciones de Categoría reubicadas)
    // ------------------------------------

//    fun insertarCategoria(nombre: String): Long {
//        val db = writableDatabase
//        val values = ContentValues().apply { put("nombre", nombre) }
//        return try {
//            db.insertOrThrow("categoria", null, values)
//        } catch (e: Exception) {
//            -1L
//        }
//    }

//    fun obtenerCategorias(): List<Pair<Int, String>> {
//        val categorias = mutableListOf<Pair<Int, String>>()
//        readableDatabase.rawQuery(
//            "SELECT id_categoria, nombre FROM categoria ORDER BY nombre ASC", null
//        ).use { cursor ->
//            while (cursor.moveToNext()) {
//                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"))
//                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
//                categorias.add(Pair(id, nombre))
//            }
//        }
//        return categorias
//    }

//    fun obtenerNombreCategoria(idCategoria: Int): String? {
//        readableDatabase.rawQuery(
//            "SELECT nombre FROM categoria WHERE id_categoria = ?",
//            arrayOf(idCategoria.toString())
//        ).use { cursor ->
//            if (cursor.moveToFirst()) {
//                return cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
//            }
//        }
//        return null
//    }

    // ------------------------------------
    // OFERTAS LABORALES (Funciones de Oferta reubicadas)
    // ------------------------------------

//    fun registrarOferta(oferta: Oferta): Boolean {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put("empresa", oferta.empresa)
//            put("titulo", oferta.titulo)
//            put("descripcion", oferta.descripcion)
//            put("salario_min", oferta.salario_min)
//            put("salario_max", oferta.salario_max)
//            put("modalidad", oferta.modalidad)
//            put("tipo", oferta.tipo)
//            put("ubicacion", oferta.ubicacion)
//            put("categoria", oferta.categoria)
//            put("vigente", oferta.vigente)
//            put("detalle", oferta.detalle)
//            put("icon_emp", oferta.icon_emp)
//        }
//
//        return try {
//            db.insert("oferta", null, values) != -1L
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

    /**
     * Función para obtener todas las ofertas de trabajo.
     * @return Una lista de objetos Oferta ordenados por fecha de creación (los más recientes primero).
     */
//    fun obtenerOfertas(): List<Oferta> {
//        val lista = mutableListOf<Oferta>()
//        readableDatabase.rawQuery(
//            "SELECT * FROM oferta ORDER BY creado_en DESC",
//            null
//        ).use { cursor ->
//            while (cursor.moveToNext()) {
//                lista.add(cursorToOferta(cursor))
//            }
//        }
//        return lista
//    }

//    fun obtenerOfertaPorId(idOferta: Int): Oferta? {
//        readableDatabase.rawQuery(
//            "SELECT * FROM oferta WHERE id_oferta = ?",
//            arrayOf(idOferta.toString())
//        ).use { cursor ->
//            if (cursor.moveToFirst()) return cursorToOferta(cursor)
//        }
//        return null
//    }

    // ------------------------------------
    // FUNCIONES AUXILIARES (Función auxiliar reubicada)
    // ------------------------------------

    private fun cursorToOferta(cursor: Cursor): Oferta {
        return Oferta(
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
}