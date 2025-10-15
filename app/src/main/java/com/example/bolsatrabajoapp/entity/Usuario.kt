package com.example.bolsatrabajoapp.entity

data class Usuario(
    val idUsuario: Long = 0,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val clave: String,
    val edad: Int? = null,
    val celular: String? = null,
    val sexo: String? = null,
    val rol: String
)