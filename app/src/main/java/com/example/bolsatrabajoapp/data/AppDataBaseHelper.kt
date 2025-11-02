package com.example.bolsatrabajoapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "emplea.db"
private const val DB_VERSION = 2

class AppDataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {

        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1) Usuarios (base para postulante/empresa)
        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario       INTEGER PRIMARY KEY AUTOINCREMENT,
                correo           TEXT    NOT NULL UNIQUE,
                clave            TEXT    NOT NULL,
                nombres          TEXT    NOT NULL,
                apellidos        TEXT    NOT NULL,
                dni              TEXT,
                celular          TEXT,
                sexo             TEXT,
                edad             INTEGER,
                rol              TEXT    NOT NULL CHECK (rol IN ('POSTULANTE','EMPRESA')),
                creado_en        INTEGER DEFAULT (strftime('%s','now'))
            );
        """.trimIndent())

        // 2) Empresa (datos adicionales del usuario empresa)
        db.execSQL("""
            CREATE TABLE empresa (
                id_empresa   INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario   INTEGER NOT NULL UNIQUE,
                ruc          TEXT,
                razon_social TEXT NOT NULL,
                rubro        TEXT,
                sitio_web    TEXT,
                descripcion  TEXT,
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
            );
        """.trimIndent())

        // 3) Postulante (datos de CV del usuario postulante)
        db.execSQL("""
            CREATE TABLE postulante (
                id_postulante INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario    INTEGER NOT NULL UNIQUE,
                cv_url        TEXT,      -- ruta/URL PDF opcional
                resumen       TEXT,      
                experiencia   TEXT,     
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
            );
        """.trimIndent())

        // 4) Categorías
        db.execSQL("""
            CREATE TABLE categoria (
                id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre       TEXT NOT NULL UNIQUE
            );
        """.trimIndent())

        db.execSQL("INSERT INTO categoria(nombre) VALUES ('General'),('Tecnología'),('Administración'),('Ventas');")


        // 5) Oferta Guardamos FK a empresa y además el nombre visible 'empresa_nombre' para evitar
        db.execSQL("""
            CREATE TABLE oferta (
                id_oferta       INTEGER PRIMARY KEY AUTOINCREMENT,
                id_empresa      INTEGER NOT NULL,
                empresa_nombre  TEXT    NOT NULL,  -- coincide con tu 'empresa'
                titulo          TEXT    NOT NULL,
                descripcion     TEXT    NOT NULL,
                salario_min     REAL,
                salario_max     REAL,
                modalidad       TEXT    CHECK (modalidad IN ('PRESENCIAL','REMOTO','HIBRIDO')),
                tipo            TEXT    CHECK (tipo IN ('TIEMPO_COMPLETO','MEDIO_TIEMPO','PRACTICAS')),
                ubicacion       TEXT,
                categoria       INTEGER NOT NULL,  -- FK a categoria (coincide con tu data class)
                vigente         INTEGER NOT NULL DEFAULT 1,
                detalle         TEXT,
                icon_empresa    TEXT,              -- coincide con tu iconEmpresa (URI/asset)
                creado_en       INTEGER DEFAULT (strftime('%s','now')),
                FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE,
                FOREIGN KEY (categoria)  REFERENCES categoria(id_categoria) ON DELETE RESTRICT
            );
        """.trimIndent())

        // 6) Postulaciones
        db.execSQL("""
            CREATE TABLE postulacion (
                id_postulacion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_oferta      INTEGER NOT NULL,
                id_postulante  INTEGER NOT NULL,
                estado         TEXT    NOT NULL DEFAULT 'ENVIADA'
                               CHECK (estado IN ('ENVIADA','VISTA','DESCARTADA','ACEPTADA')),
                fecha          INTEGER DEFAULT (strftime('%s','now')),
                UNIQUE (id_oferta, id_postulante),
                FOREIGN KEY (id_oferta)     REFERENCES oferta(id_oferta) ON DELETE CASCADE,
                FOREIGN KEY (id_postulante) REFERENCES postulante(id_postulante) ON DELETE CASCADE
            );
        """.trimIndent())

        // 7) Favoritos
        db.execSQL("""
            CREATE TABLE favorito (
                id_usuario  INTEGER NOT NULL,
                id_oferta   INTEGER NOT NULL,
                PRIMARY KEY (id_usuario, id_oferta),
                FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                FOREIGN KEY (id_oferta)  REFERENCES oferta(id_oferta)  ON DELETE CASCADE
            );
        """.trimIndent())

        // Índices útiles
        db.execSQL("""CREATE INDEX idx_oferta_categoria  ON oferta(categoria);""")
        db.execSQL("""CREATE INDEX idx_oferta_vigente    ON oferta(vigente);""")
        db.execSQL("""CREATE INDEX idx_oferta_empresa    ON oferta(id_empresa);""")
        db.execSQL("""CREATE INDEX idx_postulacion_oferta  ON postulacion(id_oferta);""")
        db.execSQL("""CREATE INDEX idx_postulacion_postulante ON postulacion(id_postulante);""")
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
}
