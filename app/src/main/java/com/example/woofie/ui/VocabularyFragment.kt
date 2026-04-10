package com.example.woofie.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.data.WoofieRepository
import com.example.woofie.model.Profession
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.card.MaterialCardView

class VocabularyFragment : Fragment(R.layout.fragment_vocabulary) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.applySystemBarsPadding(includeBottom = false)
        val profile = WoofieRepository.profile ?: return

        val title: TextView = view.findViewById(R.id.textVocabularyTitle)
        val subtitle: TextView = view.findViewById(R.id.textVocabularySubtitle)
        val container: LinearLayout = view.findViewById(R.id.vocabularyContainer)

        title.text = "Vocabulario tecnico"
        subtitle.text = "Terminos clave para ${profile.profession.displayName}"
        container.removeAllViews()

        val terms: List<Triple<String, String, String>> = when (profile.profession) {
            Profession.IT -> listOf(
                Triple("Pull request", "solicitud de revision", "Please review my pull request before noon."),
                Triple("Bug fix", "correccion de error", "We pushed a bug fix to production."),
                Triple("Deploy", "desplegar", "We deploy every Friday."),
                Triple("API", "interfaz de servicios", "The API returns a timeout error.")
            )

            Profession.HEALTH -> listOf(
                Triple("Vitals", "signos vitales", "I will check your vitals now."),
                Triple("Diagnosis", "diagnostico", "The diagnosis is clear."),
                Triple("Dosage", "dosis", "Take the correct dosage every day."),
                Triple("Discharge", "alta medica", "The patient received discharge instructions.")
            )

            Profession.SALES -> listOf(
                Triple("Lead", "cliente potencial", "This lead is ready for a demo."),
                Triple("Pitch", "presentacion comercial", "Our sales pitch is concise."),
                Triple("Proposal", "propuesta", "I sent the pricing proposal yesterday."),
                Triple("Closing", "cierre de venta", "We are close to closing this deal.")
            )
        }

        terms.forEach { term ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 20f
                strokeWidth = 2
                setStrokeColor(ContextCompat.getColor(requireContext(), R.color.woofie_primary))
                setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.woofie_surface))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val body = TextView(requireContext()).apply {
                text = "${term.first}\n${term.second}\nEjemplo: ${term.third}"
                setPadding(
                    14,
                    14,
                    14,
                    14
                )
                setTextColor(ContextCompat.getColor(requireContext(), R.color.woofie_text_primary))
            }

            card.addView(body)
            container.addView(card)
        }
    }
}



