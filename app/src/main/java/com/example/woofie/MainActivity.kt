package com.example.woofie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.woofie.data.WoofieRepository
import com.example.woofie.model.Profession
import com.example.woofie.model.UserProfile
import com.example.woofie.ui.HomeFragment
import com.example.woofie.ui.LessonFragment
import com.example.woofie.ui.LessonPathFragment
import com.example.woofie.ui.OnboardingFragment
import com.example.woofie.ui.ProgressFragment

class MainActivity : AppCompatActivity(),
    OnboardingFragment.Listener,
    HomeFragment.Listener,
    LessonPathFragment.Listener,
    LessonFragment.Listener,
    ProgressFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (WoofieRepository.profile == null) {
                showOnboarding()
            } else {
                showHome()
            }
        }
    }

    override fun onOnboardingCompleted(profession: Profession, level: String) {
        WoofieRepository.profile = UserProfile(
            profession = profession,
            level = level,
            streak = 1,
            completedLessons = 0,
            xp = 0
        )
        showHome()
    }

    override fun onStartLesson() {
        showLessonPath()
    }

    override fun onOpenLessonFromPath() {
        showLesson()
    }

    override fun onOpenProgress() {
        showProgress()
    }

    override fun onLessonFinished(correctAnswers: Int, totalAnswers: Int) {
        val outcome = WoofieRepository.registerLessonResult(
            correctAnswers = correctAnswers,
            totalAnswers = totalAnswers
        )
        showProgress(
            resultLabel = outcome.scoreLabel,
            earnedXp = outcome.earnedXp,
            passedLesson = outcome.passed
        )
    }

    override fun onBackToHome() {
        showHome()
    }

    override fun onRestartPlan() {
        WoofieRepository.profile = null
        WoofieRepository.lastResultLabel = null
        WoofieRepository.lastEarnedXp = 0
        WoofieRepository.lastLessonPassed = false
        showOnboarding()
    }

    private fun showOnboarding() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, OnboardingFragment())
            .commit()
    }

    private fun showHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }

    private fun showLesson() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LessonFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showLessonPath() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LessonPathFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showProgress(
        resultLabel: String? = null,
        earnedXp: Int = WoofieRepository.lastEarnedXp,
        passedLesson: Boolean = WoofieRepository.lastLessonPassed
    ) {
        val fragment = ProgressFragment.newInstance(
            resultLabel = resultLabel ?: WoofieRepository.lastResultLabel.orEmpty(),
            earnedXp = earnedXp,
            passedLesson = passedLesson
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
