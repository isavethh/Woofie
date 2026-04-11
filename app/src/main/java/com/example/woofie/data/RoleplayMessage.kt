package com.example.woofie.data

data class RoleplayMessage(
    val id: Int,
    val isFromWoofie: Boolean,
    val text: String,
    val options: List<String> = emptyList(), // Palabras desordenadas para que el usuario elija
    val expectedAnswer: String = "" // La respuesta correcta esperada
)

