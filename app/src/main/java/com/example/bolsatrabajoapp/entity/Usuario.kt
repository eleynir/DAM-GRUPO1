package com.example.bolsatrabajoapp.entity

data class Usuario(
    val idUsuario: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val clave: String,
    val edad: Int? = null,
    val dni: String? = null,
    val celular: String? = null,
    val sexo: String? = null,
    val rol: String = "POSTULANTE"
) {
    val nombre: String get() = "$nombres $apellidos"
}