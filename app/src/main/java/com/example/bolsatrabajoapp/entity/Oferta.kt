package com.example.bolsatrabajoapp.entity

data   class Oferta(
    // id_oferta se genera en la BD, no es necesario para insertar
    val idOferta: Int = 0,
    val empresa: String,
    val titulo: String,
    val descripcion: String,
    val salario_min: Double,
    val salario_max: Double,
    val modalidad: String,
    val tipo: String,
    val ubicacion: String,
    val categoria: Int, // Debe ser el ID de la categoría
    val vigente: Int = 1, // Valor por defecto para una nueva oferta
    val detalle: String? = null,
    val icon_emp: String? = null
)
