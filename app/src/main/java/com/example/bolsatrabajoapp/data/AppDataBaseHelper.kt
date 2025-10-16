package com.example.bolsatrabajoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.bolsatrabajoapp.entity.Oferta

private const val DB_NAME = "emplea.db"
private const val DB_VERSION = 1

class AppDataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    // ... (onConfigure, onCreate, onUpgrade se mantienen sin cambios)

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1) Usuarios
        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                correo TEXT NOT NULL UNIQUE,
                clave TEXT NOT NULL,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                dni TEXT,
                celular TEXT,
                sexo TEXT,
                edad INTEGER,
                rol TEXT NOT NULL CHECK (rol IN ('POSTULANTE','EMPRESA')),
                creado_en INTEGER DEFAULT (strftime('%s','now'))
            );
        """)

        // 2) Empresa
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

        // 3) Postulante
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

        // 4) Categoría
        db.execSQL("""
            CREATE TABLE categoria (
                id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            );
        """)

        // 5) Oferta (adaptada para coincidir con OfertaDAO y data class)
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

        // 6) Postulaciones
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

        // 7) Favoritos
        db.execSQL("""
            CREATE TABLE favorito (
                id_usuario INTEGER NOT NULL,
                id_oferta INTEGER NOT NULL,
                PRIMARY KEY (id_usuario, id_oferta),
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                FOREIGN KEY (id_oferta) REFERENCES oferta(id_oferta) ON DELETE CASCADE
            );
        """)

        // Índices
        db.execSQL("CREATE INDEX idx_oferta_categoria ON oferta(categoria);")
        db.execSQL("CREATE INDEX idx_oferta_vigente ON oferta(vigente);")
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

    //---------------------
    // CATEGORÍA
    //---------------------

    // 💡 CORRECCIÓN: El campo en la tabla es 'nombre', no 'descripcion'.
    fun registrarCategorias(nombre: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
        }
        // Usar 'use' no es necesario aquí, pero se cierra al final.
        val result = db.insert("categoria", null, values)
        db.close()
        return result != -1L
    }

    //---------------------
    // OFERTAS LABORALES (MEJORADO)
    //---------------------

    /**
     * Inserta una nueva oferta laboral en la tabla 'oferta'.
     * @param oferta El objeto Oferta a registrar.
     * @return true si la inserción fue exitosa (ID > -1), false en caso contrario.
     */
    fun registrarOferta(oferta: Oferta): Boolean {
        // 💡 Usamos use() para asegurar que la base de datos se cierre automáticamente
        this.writableDatabase.use { db ->
            val values = ContentValues().apply {
                // NOTA: 'id_oferta' se autoincrementa, no se incluye
                put("titulo", oferta.titulo)
                put("empresa", oferta.empresa)
                put("descripcion", oferta.descripcion)
                put("salario_min", oferta.salario_min)
                put("salario_max", oferta.salario_max)
                put("modalidad", oferta.modalidad)
                put("tipo", oferta.tipo)
                put("ubicacion", oferta.ubicacion)
                // Asumo que 'categoria' es el ID (Int) de la categoría
                put("categoria", oferta.categoria)
                // Asumo que 'vigente' es un Int (1 o 0)
                put("vigente", oferta.vigente)
                put("detalle", oferta.detalle)
                put("icon_emp", oferta.icon_emp)
                // 'creado_en' usa el valor DEFAULT de la tabla (timestamp), no se incluye
            }
            val result = db.insert("oferta", null, values)
            return result != -1L
        }
    }
    fun obtenerOfertas(): List<Oferta> {
        val ofertasList = mutableListOf<Oferta>()
        // Usamos readableDatabase para solo leer datos
        this.readableDatabase.use { db ->
            // Consulta SQL para obtener todos los campos de la tabla 'oferta'
            val cursor = db.rawQuery("SELECT * FROM oferta ORDER BY creado_en DESC", null)

            // Usamos use para asegurar que el Cursor se cierre automáticamente
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        // Mapeo manual de columnas a propiedades del data class Oferta
                        val idOferta = it.getInt(it.getColumnIndexOrThrow("id_oferta"))
                        val titulo = it.getString(it.getColumnIndexOrThrow("titulo"))
                        val empresa = it.getString(it.getColumnIndexOrThrow("empresa"))
                        val descripcion = it.getString(it.getColumnIndexOrThrow("descripcion"))
                        val salarioMin = it.getDouble(it.getColumnIndexOrThrow("salario_min"))
                        val salarioMax = it.getDouble(it.getColumnIndexOrThrow("salario_max"))
                        val modalidad = it.getString(it.getColumnIndexOrThrow("modalidad"))
                        val tipo = it.getString(it.getColumnIndexOrThrow("tipo"))
                        val ubicacion = it.getString(it.getColumnIndexOrThrow("ubicacion"))
                        val categoria = it.getInt(it.getColumnIndexOrThrow("categoria"))
                        val vigente = it.getInt(it.getColumnIndexOrThrow("vigente"))
                        val detalle = it.getString(it.getColumnIndexOrThrow("detalle"))
                        val iconEmpresa = it.getString(it.getColumnIndexOrThrow("icon_emp"))
                        // Nota: Se omiten 'creado_en' por simplicidad

                        val oferta = Oferta(
                            // 💡 Se necesita añadir 'idOferta' a tu data class Oferta para este método
                            idOferta = idOferta,
                            titulo = titulo,
                            empresa = empresa,
                            descripcion = descripcion,
                            salario_min = salarioMin,
                            salario_max = salarioMax,
                            modalidad = modalidad,
                            tipo = tipo,
                            ubicacion = ubicacion,
                            categoria = categoria,
                            vigente = vigente,
                            detalle = detalle,
                            icon_emp = iconEmpresa
                        )
                        ofertasList.add(oferta)
                    } while (it.moveToNext())
                }
            }
        }
        return ofertasList
    }






}