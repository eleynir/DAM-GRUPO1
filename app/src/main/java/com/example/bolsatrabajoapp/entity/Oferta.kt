package com.example.bolsatrabajoapp.entity

/**
 * Data class que representa la tabla 'oferta' de la base de datos.
 */
data class Oferta(
    val idOferta: Int = 0,
    val empresa: String,
    val titulo: String,
    val descripcion: String,
    val salario_min: Double,
    val salario_max: Double,
    val modalidad: String,
    val tipo: String,
    val categoria: Int,
    val ubicacion: String,
    val vigente: Int = 1,
    val detalle: String?,
    val icon_emp: String? = null
)