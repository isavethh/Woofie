package com.example.woofie.data

import com.example.woofie.model.Profession
import com.example.woofie.model.QuizQuestion
import com.example.woofie.model.UserProfile

object WoofieRepository {
    data class LessonOutcome(
        val correctAnswers: Int,
        val totalQuestions: Int,
        val earnedXp: Int,
        val passed: Boolean
    ) {
        val scoreLabel: String = "$correctAnswers/$totalQuestions"
    }

    const val LESSON_SIZE = 8
    const val MIN_CORRECT_TO_PASS = 6

    var profile: UserProfile? = null
    var lastResultLabel: String? = null
    var lastEarnedXp: Int = 0
    var lastLessonPassed: Boolean = false

    fun getQuestionsFor(profession: Profession): List<QuizQuestion> {
        return when (profession) {
            Profession.IT -> listOf(
                QuizQuestion(
                    prompt = "How do you say 'ticket resuelto' in a sprint update?",
                    options = listOf("Solved ticket", "Strong ticket", "Ticket weather"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Choose the best phrase for daily standup:",
                    options = listOf("I blocked yesterday", "I am blocked by API", "I was coding table"),
                    correctIndex = 1
                ),
                QuizQuestion(
                    prompt = "What means 'deployment completed successfully'?",
                    options = listOf("La reunion fue cancelada", "El despliegue termino bien", "No hay conexion"),
                    correctIndex = 1
                ),
                QuizQuestion(
                    prompt = "Translate: 'Necesito revisar los logs del servidor'.",
                    options = listOf("I need to review the server logs", "I need to paint the server", "I need to close the office"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best phrase for code review request:",
                    options = listOf("Could you review this pull request?", "Could you remove this keyboard?", "Could you call my manager now?"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "What means 'rollback to previous version'?",
                    options = listOf("Avanzar a la siguiente version", "Volver a la version anterior", "Eliminar el proyecto"),
                    correctIndex = 1
                ),
                QuizQuestion(
                    prompt = "Translate: 'La API responde con timeout'.",
                    options = listOf("The API responds with a timeout", "The API responds with a smile", "The API responds with a shortcut"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best sentence for sprint planning:",
                    options = listOf("Let's estimate these tasks", "Let's cancel all tasks", "Let's ship without tests"),
                    correctIndex = 0
                )
            )

            Profession.HEALTH -> listOf(
                QuizQuestion(
                    prompt = "Translate: 'Tome este medicamento dos veces al dia'",
                    options = listOf(
                        "Take this medicine twice a day",
                        "Drink this medicine all week",
                        "Call this medicine now"
                    ),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best phrase to ask patient comfort:",
                    options = listOf("Are you feeling comfortable?", "You are fine now?", "Can you sleep medicine?"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "What means 'follow-up appointment'?",
                    options = listOf("Cita de seguimiento", "Cita urgente", "Examen de sangre"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Translate: 'Respire profundo, todo esta bajo control'.",
                    options = listOf("Breathe deeply, everything is under control", "Run quickly, nothing is ready", "Drink this later"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best phrase before checking vital signs:",
                    options = listOf("I will check your vital signs", "I will cancel your room", "I will call your office"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Translate: 'Debe guardar reposo por tres dias'.",
                    options = listOf("You should rest for three days", "You should walk for three hours", "You should skip your meal"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "What means 'discharge instructions'?",
                    options = listOf("Instrucciones de alta", "Instrucciones de ingreso", "Resultado de radiografia"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best phrase for pain scale question:",
                    options = listOf("How would you rate your pain from 1 to 10?", "Can you pay now?", "Do you need a new room key?"),
                    correctIndex = 0
                )
            )

            Profession.SALES -> listOf(
                QuizQuestion(
                    prompt = "Translate: 'Podemos agendar una llamada de seguimiento'",
                    options = listOf(
                        "Can we schedule a follow-up call?",
                        "Can we close now?",
                        "Can we buy this later?"
                    ),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Choose the best sentence for objection handling:",
                    options = listOf("I understand your concern", "You are wrong", "This is impossible"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "What means 'pricing proposal attached'?",
                    options = listOf("Propuesta de precios adjunta", "Cliente no responde", "Producto agotado"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Translate: 'Gracias por tu tiempo en la reunion'.",
                    options = listOf("Thank you for your time in the meeting", "You are late for the meeting", "Let's stop this forever"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best opening for discovery call:",
                    options = listOf("Can you tell me about your current process?", "Can you buy now in one minute?", "Can you send money first?"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Translate: 'Te envio un resumen por correo'.",
                    options = listOf("I will send you a summary by email", "I will send you a car by noon", "I will send you a voice note to HR"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "What means 'decision-maker'?",
                    options = listOf("Persona que toma la decision", "Persona que guarda facturas", "Persona que recibe paquetes"),
                    correctIndex = 0
                ),
                QuizQuestion(
                    prompt = "Best phrase to schedule next step:",
                    options = listOf("Does next Tuesday work for a demo?", "Do you want to cancel all meetings?", "Can you transfer now?"),
                    correctIndex = 0
                )
            )
        }
    }

    fun registerLessonResult(correctAnswers: Int, totalAnswers: Int = LESSON_SIZE): LessonOutcome {
        val currentProfile = profile ?: return LessonOutcome(
            correctAnswers = correctAnswers,
            totalQuestions = totalAnswers,
            earnedXp = 0,
            passed = false
        )

        val passedLesson = correctAnswers >= MIN_CORRECT_TO_PASS
        val earnedXp = (correctAnswers * 12) + if (passedLesson) 40 else 10

        currentProfile.completedLessons += 1
        currentProfile.xp += earnedXp
        currentProfile.streak += 1
        lastResultLabel = "$correctAnswers/$totalAnswers"
        lastEarnedXp = earnedXp
        lastLessonPassed = passedLesson

        return LessonOutcome(
            correctAnswers = correctAnswers,
            totalQuestions = totalAnswers,
            earnedXp = earnedXp,
            passed = passedLesson
        )
    }
}

