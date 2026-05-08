package com.example.woofie.ui

object PlacementTestEngine {
    fun scoreToLevel(score: Int): String = when (score) {
        0, 1 -> "A1"
        2 -> "A2"
        else -> "B1"
    }
}

