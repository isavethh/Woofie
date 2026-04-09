package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.data.WoofieRepository
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    interface Listener {
        fun onStartLesson()
        fun onOpenProgress()
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
        view.applySystemBarsPadding()
        val profile = WoofieRepository.profile ?: return

        val greeting: TextView = view.findViewById(R.id.textHomeGreeting)
        val profession: TextView = view.findViewById(R.id.textHomeProfession)
        val streak: TextView = view.findViewById(R.id.textHomeStreak)
        val mascot: ImageView = view.findViewById(R.id.imageWolf)
        val buttonStartLesson: MaterialButton = view.findViewById(R.id.buttonStartLesson)
        val buttonProgress: MaterialButton = view.findViewById(R.id.buttonOpenProgress)

        greeting.text = getString(R.string.home_greeting)
        profession.text = getString(
            R.string.home_profession_detail,
            profile.profession.displayName,
            profile.level,
            profile.profession.dailyFocus
        )
        streak.text = getString(
            R.string.home_streak,
            profile.streak,
            profile.completedLessons,
            profile.xp
        )

        mascot.contentDescription = getString(R.string.wolf_content_description)

        buttonStartLesson.setOnClickListener { listener?.onStartLesson() }
        buttonProgress.setOnClickListener { listener?.onOpenProgress() }
    }
}

