package com.example.woofie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.woofie.data.WoofieRepository
import com.example.woofie.model.Profession
import com.example.woofie.model.UserProfile
import com.example.woofie.ui.HomeFragment
import com.example.woofie.ui.LessonFragment
import com.example.woofie.ui.LessonPathFragment
import com.example.woofie.ui.LoginFragment
import com.example.woofie.ui.OnboardingFragment
import com.example.woofie.ui.PracticeCenterFragment
import com.example.woofie.ui.ProgressFragment
import com.example.woofie.ui.RoleplayFragment
import com.example.woofie.ui.VocabularyFragment
import com.example.woofie.ui.PlacementTestFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View

class MainActivity : AppCompatActivity(),
    LoginFragment.Listener,
    OnboardingFragment.Listener,
    HomeFragment.Listener,
    LessonPathFragment.Listener,
    LessonFragment.Listener,
    ProgressFragment.Listener,
    PracticeCenterFragment.Listener,
    PlacementTestFragment.Listener {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> { showRoleplay(); true }
                R.id.navigation_path -> { showLessonPath(); true }
                R.id.navigation_practice -> { showPracticeCenter(); true }
                R.id.navigation_vocabulary -> { showVocabulary(); true }
                R.id.navigation_progress -> { showProgress(); true }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            if (WoofieRepository.profile == null) {
                bottomNav.visibility = View.GONE
                showLogin()
            } else {
                bottomNav.visibility = View.VISIBLE
                showRoleplay()
            }
        } else {
            bottomNav.visibility = if (WoofieRepository.profile == null) View.GONE else View.VISIBLE
        }
    }

    override fun onLoginContinue(email: String) {
        showOnboarding()
    }

    override fun onOnboardingCompleted(name: String, profession: Profession, level: String) {
        WoofieRepository.profile = UserProfile(
            name = name,
            profession = profession,
            level = level,
            streak = 1,
            completedLessons = 0,
            xp = 0
        )
        bottomNav.visibility = View.VISIBLE
        bottomNav.selectedItemId = R.id.navigation_home
    }

    override fun onOpenPlacementTest() {
        showPlacementTest()
    }

    override fun onPlacementTestFinished(level: String) {
        // No-op. El resultado se aplica en Onboarding via FragmentResult.
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
        bottomNav.selectedItemId = R.id.navigation_home
    }

    override fun onRestartPlan() {
        WoofieRepository.profile = null
        WoofieRepository.lastResultLabel = null
        WoofieRepository.lastEarnedXp = 0
        WoofieRepository.lastLessonPassed = false
        bottomNav.visibility = View.GONE
        showLogin()
    }

    override fun onOpenMatchPairs() {
        // TODO: Implement Match Pairs Practice
    }

    override fun onOpenSpeakPractice() {
        // TODO: Implement Speak Practice
    }

    override fun onOpenErrorReview() {
        // TODO: Implement Error Review Practice
    }

    private fun showOnboarding() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, OnboardingFragment())
            .commit()
    }

    private fun showLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    private fun showHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }

    private fun showRoleplay() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, RoleplayFragment())
            .commit()
    }

    private fun showPracticeCenter() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PracticeCenterFragment())
            .commit()
    }

    private fun showVocabulary() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, VocabularyFragment())
            .commit()
    }

    private fun showLesson() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.woofie_enter_right,
                R.anim.woofie_exit_left,
                R.anim.woofie_enter_left,
                R.anim.woofie_exit_right
            )
            .replace(R.id.fragmentContainer, LessonFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showLessonPath() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.woofie_enter_right,
                R.anim.woofie_exit_left,
                R.anim.woofie_enter_left,
                R.anim.woofie_exit_right
            )
            .replace(R.id.fragmentContainer, LessonPathFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showPlacementTest() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.woofie_enter_right,
                R.anim.woofie_exit_left,
                R.anim.woofie_enter_left,
                R.anim.woofie_exit_right
            )
            .replace(R.id.fragmentContainer, PlacementTestFragment())
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
