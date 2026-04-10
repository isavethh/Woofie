package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.data.WoofieRepository
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.card.MaterialCardView

class PracticeCenterFragment : Fragment(R.layout.fragment_practice_center) {

    interface Listener {
        fun onOpenMatchPairs()
        fun onOpenSpeakPractice()
        fun onOpenErrorReview()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.applySystemBarsPadding(includeBottom = false)

        val cardMatchPairs: MaterialCardView = view.findViewById(R.id.cardMatchPairs)
        val cardSpeak: MaterialCardView = view.findViewById(R.id.cardSpeak)
        val cardReviewErrors: MaterialCardView = view.findViewById(R.id.cardReviewErrors)
        val textReviewErrorsDesc: TextView = view.findViewById(R.id.textPracticeReviewErrorsDesc)

        val mistakesCount = WoofieRepository.recentMistakes.size
        textReviewErrorsDesc.text = getString(R.string.practice_review_errors_desc, mistakesCount)

        cardMatchPairs.setOnClickListener { listener?.onOpenMatchPairs() }
        cardSpeak.setOnClickListener { listener?.onOpenSpeakPractice() }
        cardReviewErrors.setOnClickListener { listener?.onOpenErrorReview() }
    }
}

