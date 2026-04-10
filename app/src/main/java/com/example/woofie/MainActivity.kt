package com.example.woofie

import android.os.Bundle
import android.view.View
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.woofie.data.WoofieRepository
import com.example.woofie.model.Profession
import com.example.woofie.model.UserProfile
import com.example.woofie.ui.HomeFragment
import com.example.woofie.ui.LessonFragment
import com.example.woofie.ui.LessonPathFragment
import com.example.woofie.ui.OnboardingFragment
import com.example.woofie.ui.PracticeCenterFragment
import com.example.woofie.ui.ProgressFragment
import com.example.woofie.ui.VocabularyFragment

class MainActivity : AppCompatActivity(),
    OnboardingFragment.Listener,
    HomeFragment.Listener,
    LessonPathFragment.Listener,
    PracticeCenterFragment.Listener,
    LessonFragment.Listener,
    ProgressFragment.Listener {

    private lateinit var bottomNavigation: BottomNavigationView
    private var suppressNavSelectionCallback: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        setupBottomNavigationMenu()

        bottomNavigation.setOnItemSelectedListener { item ->
            if (suppressNavSelectionCallback) return@setOnItemSelectedListener true
            when (item.itemId) {
                R.id.navigation_home -> showHome()
                R.id.navigation_path -> showLessonPath()
                R.id.navigation_practice -> showPracticeCenter()
                R.id.navigation_vocabulary -> showVocabulary()
                R.id.navigation_progress -> showProgress()
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        if (savedInstanceState == null) {
            if (WoofieRepository.profile == null) {
                showOnboarding()
            } else {
                showMainNavigation()
                selectBottomTab(R.id.navigation_home)
            }
        } else {
            bottomNavigation.visibility = if (WoofieRepository.profile == null) View.GONE else View.VISIBLE
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
        showMainNavigation()
        selectBottomTab(R.id.navigation_home)
    }

    override fun onStartLesson() {
        selectBottomTab(R.id.navigation_path)
    }

    override fun onOpenLessonFromPath() {
        showLesson()
    }

    override fun onOpenMatchPairs() {
        showLesson()
    }

    override fun onOpenSpeakPractice() {
        Toast.makeText(this, R.string.practice_speak_coming_soon, Toast.LENGTH_SHORT).show()
    }

    override fun onOpenErrorReview() {
        if (WoofieRepository.recentMistakes.isEmpty()) {
            Toast.makeText(this, R.string.practice_no_errors_yet, Toast.LENGTH_SHORT).show()
            return
        }
        showLesson()
    }

    override fun onOpenProgress() {
        selectBottomTab(R.id.navigation_progress)
    }

    override fun onLessonFinished(correctAnswers: Int, totalAnswers: Int) {
        WoofieRepository.registerLessonResult(
            correctAnswers = correctAnswers,
            totalAnswers = totalAnswers
        )
        selectBottomTab(R.id.navigation_progress)
    }

    override fun onBackToHome() {
        selectBottomTab(R.id.navigation_home)
    }

    override fun onRestartPlan() {
        WoofieRepository.profile = null
        WoofieRepository.lastResultLabel = null
        WoofieRepository.lastEarnedXp = 0
        WoofieRepository.lastLessonPassed = false
        showOnboarding()
    }

    private fun showMainNavigation() {
        bottomNavigation.visibility = View.VISIBLE
    }

    private fun setupBottomNavigationMenu() {
        val menu = bottomNavigation.menu
        menu.clear()
        menu.add(Menu.NONE, R.id.navigation_home, 0, getString(R.string.nav_home))
            .setIcon(android.R.drawable.ic_menu_view)
        menu.add(Menu.NONE, R.id.navigation_path, 1, getString(R.string.nav_path))
            .setIcon(android.R.drawable.ic_menu_directions)
        menu.add(Menu.NONE, R.id.navigation_practice, 2, getString(R.string.nav_practice))
            .setIcon(android.R.drawable.ic_menu_edit)
        menu.add(Menu.NONE, R.id.navigation_vocabulary, 3, getString(R.string.nav_vocabulary))
            .setIcon(android.R.drawable.ic_menu_agenda)
        menu.add(Menu.NONE, R.id.navigation_progress, 4, getString(R.string.nav_progress))
            .setIcon(android.R.drawable.ic_menu_myplaces)
    }

    private fun hideMainNavigation() {
        bottomNavigation.visibility = View.GONE
    }

    private fun selectBottomTab(itemId: Int) {
        if (bottomNavigation.selectedItemId == itemId) {
            when (itemId) {
                R.id.navigation_home -> showHome()
                R.id.navigation_path -> showLessonPath()
                R.id.navigation_practice -> showPracticeCenter()
                R.id.navigation_vocabulary -> showVocabulary()
                R.id.navigation_progress -> showProgress()
            }
            return
        }
        suppressNavSelectionCallback = true
        bottomNavigation.selectedItemId = itemId
        suppressNavSelectionCallback = false
        when (itemId) {
            R.id.navigation_home -> showHome()
            R.id.navigation_path -> showLessonPath()
            R.id.navigation_practice -> showPracticeCenter()
            R.id.navigation_vocabulary -> showVocabulary()
            R.id.navigation_progress -> showProgress()
        }
    }

    private fun showOnboarding() {
        hideMainNavigation()
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
            .setCustomAnimations(
                R.anim.woofie_enter_right,
                R.anim.woofie_exit_left,
                R.anim.woofie_enter_left,
                R.anim.woofie_exit_right
            )
            .replace(R.id.fragmentContainer, LessonFragment())
            .commit()
    }

    private fun showPracticeCenter() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PracticeCenterFragment())
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
            .commit()
    }

    private fun showVocabulary() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, VocabularyFragment())
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
            .commit()
    }
}
