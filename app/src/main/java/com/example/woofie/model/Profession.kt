package com.example.woofie.model

enum class Profession(
    val key: String,
    val displayName: String,
    val dailyFocus: String
) {
    IT(
        key = "it",
        displayName = "Tecnologia e IT",
        dailyFocus = "Standups, documentacion y entrevistas tecnicas"
    ),
    HEALTH(
        key = "health",
        displayName = "Salud",
        dailyFocus = "Atencion al paciente e instrucciones claras"
    ),
    SALES(
        key = "sales",
        displayName = "Ventas y atencion",
        dailyFocus = "Llamadas, objeciones y seguimiento comercial"
    );

    companion object {
        fun fromKey(key: String): Profession {
            return entries.firstOrNull { it.key == key } ?: IT
        }
    }
}

