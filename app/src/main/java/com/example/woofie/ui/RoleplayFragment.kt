package com.example.woofie.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woofie.R
import com.example.woofie.data.RoleplayMessage
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Locale

class RoleplayFragment : Fragment(R.layout.fragment_roleplay) {

    private lateinit var tts: TextToSpeech
    private lateinit var rvChat: RecyclerView
    private lateinit var chipGroupOptions: ChipGroup
    private lateinit var btnSend: Button

    private val chatAdapter = RoleplayChatAdapter()

    // Aquí definimos el guion de la reunión
    private val scenario = listOf(
        RoleplayMessage(1, true, "Good morning! What did you do yesterday?", listOf("fixed", "I", "login", "the", "module", "bug"), "I fixed the login module bug"),
        RoleplayMessage(2, true, "Great! Anything blocking you today?", listOf("No,", "blockers.", "working", "perfectly", "Everything", "is"), "No, blockers. Everything is working perfectly")
    )
    private var currentStep = 0
    private var selectedWords = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.applySystemBarsPadding(includeBottom = false)

        // Inicializar vistas
        rvChat = view.findViewById(R.id.rvChat)
        chipGroupOptions = view.findViewById(R.id.chipGroupOptions)
        btnSend = view.findViewById(R.id.btnSend)

        rvChat.layoutManager = LinearLayoutManager(requireContext())
        rvChat.adapter = chatAdapter

        // Inicializar Text to Speech
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                view?.post {
                    startStep() // Empezar la charla en el UI thread
                }
            }
        }

        btnSend.setOnClickListener { checkAnswer() }
    }

    private fun startStep() {
        if (currentStep < scenario.size) {
            val currentMessage = scenario[currentStep]

            chatAdapter.addMessage(currentMessage)
            rvChat.scrollToPosition(chatAdapter.itemCount - 1)

            // Hacer que Woofie hable
            tts.speak(currentMessage.text, TextToSpeech.QUEUE_FLUSH, null, null)

            // Mostrar opciones (palabras desordenadas)
            showChips(currentMessage.options)
            selectedWords.clear()
        } else {
            Toast.makeText(context, "¡Reunión Standup completada!", Toast.LENGTH_LONG).show()
        }
    }

    private fun showChips(options: List<String>) {
        chipGroupOptions.removeAllViews()
        options.shuffled().forEach { word ->
            val chip = Chip(requireContext()).apply {
                text = word
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedWords.add(word)
                    } else {
                        selectedWords.remove(word)
                    }
                }
            }
            chipGroupOptions.addView(chip)
        }
    }

    private fun checkAnswer() {
        if (currentStep >= scenario.size) return

        val currentMessage = scenario[currentStep]
        val userAnswer = selectedWords.joinToString(" ")

        if (userAnswer == currentMessage.expectedAnswer) {
            chatAdapter.addMessage(RoleplayMessage(999, false, userAnswer))
            rvChat.scrollToPosition(chatAdapter.itemCount - 1)

            Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()
            currentStep++

            // Delay next step slightly
            view?.postDelayed({ startStep() }, 1000)

        } else {
            Toast.makeText(context, "Error. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
