package com.example.bolsatrabajoapp.entity

data   class Oferta(
    val idOferta: Int,
    val titulo: String,
    val empresa: String,
    val descripcion: String,
    val salario_min : Double,
    val salario_max : Double,
    val modalidad : String,
    val tipo : String,
    val ubicacion : String,
    val categoria: Int,
    val vigente: Int,
    val detalle: String,
    val iconEmpresa: String,
)

