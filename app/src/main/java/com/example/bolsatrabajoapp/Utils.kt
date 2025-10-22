package com.example.bolsatrabajoapp

// Función para normalizar el correo electrónico
fun normalizarCorreo(input: String): String {
    val raw = input.trim().lowercase()
    return if (raw.contains("@")) raw else "$raw@cibertec.edu.pe"
}