package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.LinearProgressIndicator

class PlacementTestFragment : Fragment(R.layout.fragment_placement_test) {

    companion object {
        const val RESULT_KEY = "placement_result"
        const val RESULT_LEVEL = "placement_level"
    }

    private data class PlacementQuestion(
        val promptRes: Int,
        val optionsRes: List<Int>,
        val correctIndex: Int
    )

    private val questions = listOf(
        PlacementQuestion(
            R.string.placement_q1_prompt,
            listOf(R.string.placement_q1_o1, R.string.placement_q1_o2, R.string.placement_q1_o3, R.string.placement_q1_o4),
            0
        ),
        PlacementQuestion(
            R.string.placement_q2_prompt,
            listOf(R.string.placement_q2_o1, R.string.placement_q2_o2, R.string.placement_q2_o3),
            1
        ),
        PlacementQuestion(
            R.string.placement_q3_prompt,
            listOf(R.string.placement_q3_o1, R.string.placement_q3_o2, R.string.placement_q3_o3),
            1
        ),
        PlacementQuestion(
            R.string.placement_q4_prompt,
            listOf(R.string.placement_q4_o1, R.string.placement_q4_o2, R.string.placement_q4_o3),
            2
        )
    )

    private var listener: Listener? = null
    private var stepIndex = 0
    private val answers = IntArray(questions.size) { -1 }

    interface Listener {
        fun onPlacementTestFinished(level: String)
    }

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

        val buttonBack: ImageButton = view.findViewById(R.id.buttonPlacementBack)
        val textTitle: TextView = view.findViewById(R.id.textPlacementTitle)
        val textProgress: TextView = view.findViewById(R.id.textPlacementProgress)
        val progressBar: LinearProgressIndicator = view.findViewById(R.id.placementProgressBar)
        val heroImage: ImageView = view.findViewById(R.id.placementHero)
        val headline: TextView = view.findViewById(R.id.textPlacementHeadline)
        val subhead: TextView = view.findViewById(R.id.textPlacementSubhead)
        val questionText: TextView = view.findViewById(R.id.textPlacementQuestion)
        val optionsContainer: LinearLayout = view.findViewById(R.id.placementOptionsContainer)
        val feedbackText: TextView = view.findViewById(R.id.textPlacementFeedback)
        val badgesGroup: ChipGroup = view.findViewById(R.id.placementBadges)
        val primaryButton: MaterialButton = view.findViewById(R.id.buttonPlacementPrimary)

        val optionButtons = listOf(
            view.findViewById<MaterialButton>(R.id.buttonOption1),
            view.findViewById<MaterialButton>(R.id.buttonOption2),
            view.findViewById<MaterialButton>(R.id.buttonOption3),
            view.findViewById<MaterialButton>(R.id.buttonOption4)
        )

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_result_fade_in)
        val optionAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_option_pop)

        val textPrimary = ContextCompat.getColor(requireContext(), R.color.woofie_text_primary)
        val textSuccess = ContextCompat.getColor(requireContext(), R.color.woofie_success)
        val textError = ContextCompat.getColor(requireContext(), R.color.woofie_error)
        val optionDefaultBg = ContextCompat.getColor(requireContext(), R.color.woofie_surface)
        val optionSelectedBg = ContextCompat.getColor(requireContext(), R.color.woofie_primary_light)
        val optionCorrectBg = ContextCompat.getColor(requireContext(), R.color.woofie_success_light)
        val optionIncorrectBg = ContextCompat.getColor(requireContext(), R.color.woofie_error_light)

        fun scoreToLevel(score: Int): String = PlacementTestEngine.scoreToLevel(score)

        fun resetOptionStyles() {
            optionButtons.forEach { button ->
                button.isEnabled = true
                button.setTextColor(textPrimary)
                button.setBackgroundColor(optionDefaultBg)
                button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.woofie_gray_200)
                button.scaleX = 1f
                button.scaleY = 1f
            }
        }

        fun highlightOptions(selectedIndex: Int, correctIndex: Int) {
            optionButtons.forEachIndexed { index, button ->
                when {
                    index == correctIndex -> {
                        button.setBackgroundColor(optionCorrectBg)
                        button.setTextColor(textSuccess)
                    }
                    index == selectedIndex -> {
                        button.setBackgroundColor(optionIncorrectBg)
                        button.setTextColor(textError)
                    }
                }
                button.isEnabled = false
            }
        }

        fun renderIntro() {
            textTitle.text = getString(R.string.placement_test_title)
            textProgress.text = ""
            progressBar.progress = 0
            heroImage.visibility = View.VISIBLE
            headline.text = getString(R.string.placement_intro_headline)
            subhead.text = getString(R.string.placement_intro_body)
            questionText.visibility = View.GONE
            optionsContainer.visibility = View.GONE
            feedbackText.visibility = View.GONE
            badgesGroup.visibility = View.GONE
            primaryButton.isEnabled = true
            primaryButton.text = getString(R.string.placement_test_start)
            headline.startAnimation(fadeIn)
        }

        fun renderQuestion(index: Int) {
            val question = questions[index]
            val total = questions.size
            val step = index + 1
            val selectedAnswer = answers[index]

            textTitle.text = getString(R.string.placement_test_title_short)
            textProgress.text = getString(R.string.placement_test_progress, step, total)
            progressBar.max = total
            progressBar.setProgressCompat(step, true)
            heroImage.visibility = View.GONE
            headline.text = getString(R.string.placement_question_headline)
            subhead.text = getString(R.string.placement_question_subhead)
            questionText.visibility = View.VISIBLE
            questionText.text = getString(question.promptRes)
            optionsContainer.visibility = View.VISIBLE
            badgesGroup.visibility = View.GONE
            feedbackText.visibility = View.GONE
            primaryButton.text = getString(R.string.placement_test_next)
            primaryButton.isEnabled = selectedAnswer != -1

            optionButtons.forEachIndexed { buttonIndex, button ->
                val hasOption = buttonIndex < question.optionsRes.size
                button.visibility = if (hasOption) View.VISIBLE else View.GONE
                if (hasOption) {
                    button.text = getString(question.optionsRes[buttonIndex])
                }
            }

            resetOptionStyles()

            if (selectedAnswer != -1) {
                highlightOptions(selectedAnswer, question.correctIndex)
                feedbackText.text = if (selectedAnswer == question.correctIndex) {
                    feedbackText.setTextColor(textSuccess)
                    getString(R.string.placement_feedback_correct)
                } else {
                    feedbackText.setTextColor(textError)
                    getString(
                        R.string.placement_feedback_incorrect,
                        getString(question.optionsRes[question.correctIndex])
                    )
                }
                feedbackText.visibility = View.VISIBLE
            }
        }

        fun renderResult() {
            val score = questions.indices.count { index -> answers[index] == questions[index].correctIndex }
            val level = scoreToLevel(score)
            val accuracy = (score * 100f / questions.size).toInt()

            textTitle.text = getString(R.string.placement_test_title_short)
            textProgress.text = ""
            progressBar.progress = questions.size
            heroImage.visibility = View.VISIBLE
            headline.text = getString(R.string.placement_result_title)
            subhead.text = getString(R.string.placement_result_body, level)
            questionText.visibility = View.GONE
            optionsContainer.visibility = View.GONE
            feedbackText.visibility = View.GONE
            badgesGroup.visibility = View.VISIBLE
            badgesGroup.removeAllViews()

            fun addBadge(text: String) {
                val chip = layoutInflater.inflate(R.layout.item_placement_badge, badgesGroup, false) as Chip
                chip.text = text
                badgesGroup.addView(chip)
            }

            addBadge(getString(R.string.placement_badge_accuracy, accuracy))
            addBadge(
                when {
                    score >= 3 -> getString(R.string.placement_badge_confidence_high)
                    score == 2 -> getString(R.string.placement_badge_confidence_mid)
                    else -> getString(R.string.placement_badge_confidence_low)
                }
            )
            addBadge(getString(R.string.placement_badge_speed))

            primaryButton.text = getString(R.string.placement_test_finish)
            primaryButton.isEnabled = true
            headline.startAnimation(fadeIn)

            primaryButton.setOnClickListener {
                parentFragmentManager.setFragmentResult(
                    RESULT_KEY,
                    bundleOf(RESULT_LEVEL to level)
                )
                listener?.onPlacementTestFinished(level)
                parentFragmentManager.popBackStack()
            }
        }

        fun renderStep() {
            when (stepIndex) {
                0 -> renderIntro()
                in 1..questions.size -> renderQuestion(stepIndex - 1)
                else -> renderResult()
            }
        }

        buttonBack.setOnClickListener {
            if (stepIndex == 0) {
                parentFragmentManager.popBackStack()
            } else {
                stepIndex = (stepIndex - 1).coerceAtLeast(0)
                renderStep()
            }
        }

        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val questionIndex = stepIndex - 1
                if (questionIndex !in questions.indices) return@setOnClickListener
                val question = questions[questionIndex]
                val correctIndex = question.correctIndex
                answers[questionIndex] = index

                resetOptionStyles()
                highlightOptions(index, correctIndex)
                feedbackText.text = if (index == correctIndex) {
                    feedbackText.setTextColor(textSuccess)
                    getString(R.string.placement_feedback_correct)
                } else {
                    feedbackText.setTextColor(textError)
                    getString(
                        R.string.placement_feedback_incorrect,
                        getString(question.optionsRes[correctIndex])
                    )
                }
                feedbackText.visibility = View.VISIBLE
                feedbackText.startAnimation(fadeIn)
                button.startAnimation(optionAnim)
                primaryButton.isEnabled = true
            }
        }

        primaryButton.setOnClickListener {
            when {
                stepIndex == 0 -> {
                    stepIndex = 1
                    renderStep()
                }
                stepIndex in 1..questions.size -> {
                    val currentAnswer = answers[stepIndex - 1]
                    if (currentAnswer == -1) return@setOnClickListener
                    stepIndex += 1
                    renderStep()
                }
                else -> renderResult()
            }
        }

        renderStep()
    }
}

