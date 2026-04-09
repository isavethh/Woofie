package com.example.woofie.model

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int
)

