package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.data.WoofieRepository
import com.example.woofie.model.QuizQuestion
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

class LessonFragment : Fragment(R.layout.fragment_lesson) {

    interface Listener {
        fun onLessonFinished(correctAnswers: Int, totalAnswers: Int)
    }

    private var listener: Listener? = null
    private var questions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0
    private var correctAnswers = 0
    private var hasAnsweredCurrent = false

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
        questions = WoofieRepository.getQuestionsFor(profile.profession)

        val progressText: TextView = view.findViewById(R.id.textLessonProgress)
        val progressBar: LinearProgressIndicator = view.findViewById(R.id.lessonProgressBar)
        val questionText: TextView = view.findViewById(R.id.textLessonQuestion)
        val resultText: TextView = view.findViewById(R.id.textLessonResult)
        val wrongFeedbackCard: MaterialCardView = view.findViewById(R.id.cardWrongFeedback)
        val wrongFeedbackBody: TextView = view.findViewById(R.id.textWrongFeedbackBody)
        val wrongFeedbackHint: TextView = view.findViewById(R.id.textWrongFeedbackHint)
        val optionsGroup: RadioGroup = view.findViewById(R.id.optionsGroup)
        val optionOne: RadioButton = view.findViewById(R.id.optionOne)
        val optionTwo: RadioButton = view.findViewById(R.id.optionTwo)
        val optionThree: RadioButton = view.findViewById(R.id.optionThree)
        val buttonCheck: MaterialButton = view.findViewById(R.id.buttonCheck)
        val options = listOf(optionOne, optionTwo, optionThree)

        val optionAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_option_pop)
        val resultAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_result_fade_in)
        val questionAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_slide_up)
        val shakeAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_shake)
        var isFeedbackExpanded = false

        progressBar.max = questions.size

        fun animateOptionsEntry() {
            options.forEachIndexed { index, option ->
                option.alpha = 0f
                option.translationY = 16f
                option.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay((index * 70).toLong())
                    .setDuration(180)
                    .start()
            }
        }

        fun collapseFeedback() {
            isFeedbackExpanded = false
            wrongFeedbackBody.maxLines = 2
            wrongFeedbackHint.text = getString(R.string.lesson_feedback_expand)
        }

        fun hideFeedback() {
            wrongFeedbackCard.visibility = View.GONE
            collapseFeedback()
        }

        fun showWrongFeedback(selectedOption: String, correctOption: String) {
            wrongFeedbackBody.text = getString(
                R.string.lesson_feedback_body,
                selectedOption,
                correctOption
            )
            collapseFeedback()
            wrongFeedbackCard.visibility = View.VISIBLE
            wrongFeedbackCard.startAnimation(resultAnim)
        }

        fun bindQuestion() {
            val question = questions[currentIndex]
            progressText.text = getString(R.string.lesson_progress, currentIndex + 1, questions.size)
            progressBar.progress = currentIndex + 1
            questionText.text = question.prompt
            questionText.startAnimation(questionAnim)
            optionOne.text = question.options[0]
            optionTwo.text = question.options[1]
            optionThree.text = question.options[2]
            optionsGroup.clearCheck()
            options.forEach { option ->
                option.isEnabled = true
                option.setBackgroundResource(R.drawable.lesson_option_selector)
            }
            resultText.text = ""
            hideFeedback()
            buttonCheck.text = getString(R.string.lesson_check)
            hasAnsweredCurrent = false
            animateOptionsEntry()
        }

        fun showAnswerState(selectedIndex: Int, correctIndex: Int) {
            options.forEachIndexed { index, radioButton ->
                val background = when {
                    index == correctIndex -> R.drawable.lesson_option_correct
                    index == selectedIndex -> R.drawable.lesson_option_incorrect
                    else -> R.drawable.lesson_option_idle
                }
                radioButton.setBackgroundResource(background)
                radioButton.isEnabled = false
            }
        }

        optionsGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == View.NO_ID || hasAnsweredCurrent) return@setOnCheckedChangeListener
            group.findViewById<RadioButton>(checkedId)?.startAnimation(optionAnim)
        }

        wrongFeedbackCard.setOnClickListener {
            isFeedbackExpanded = !isFeedbackExpanded
            wrongFeedbackBody.maxLines = if (isFeedbackExpanded) Int.MAX_VALUE else 2
            wrongFeedbackHint.text = getString(
                if (isFeedbackExpanded) R.string.lesson_feedback_collapse
                else R.string.lesson_feedback_expand
            )
        }

        buttonCheck.setOnClickListener {
            it.animate().scaleX(0.96f).scaleY(0.96f).setDuration(70)
                .withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
                }.start()

            if (buttonCheck.text == getString(R.string.lesson_next)) {
                currentIndex += 1
                if (currentIndex >= questions.size) {
                    listener?.onLessonFinished(correctAnswers, questions.size)
                } else {
                    bindQuestion()
                }
                return@setOnClickListener
            }

            val selectedId = optionsGroup.checkedRadioButtonId
            if (selectedId == View.NO_ID) {
                resultText.text = getString(R.string.lesson_select_option)
                resultText.startAnimation(resultAnim)
                optionsGroup.startAnimation(shakeAnim)
                return@setOnClickListener
            }

            val selectedIndex = when (selectedId) {
                R.id.optionOne -> 0
                R.id.optionTwo -> 1
                else -> 2
            }
            val currentQuestion = questions[currentIndex]
            val isCorrect = selectedIndex == currentQuestion.correctIndex
            if (isCorrect) correctAnswers += 1
            hasAnsweredCurrent = true
            showAnswerState(selectedIndex = selectedIndex, correctIndex = currentQuestion.correctIndex)

            resultText.text = if (isCorrect) {
                hideFeedback()
                getString(R.string.lesson_correct)
            } else {
                showWrongFeedback(
                    selectedOption = currentQuestion.options[selectedIndex],
                    correctOption = currentQuestion.options[currentQuestion.correctIndex]
                )
                getString(
                    R.string.lesson_incorrect,
                    currentQuestion.options[currentQuestion.correctIndex]
                )
            }
            resultText.startAnimation(resultAnim)
            if (!isCorrect) {
                view.findViewById<RadioButton>(selectedId)?.startAnimation(shakeAnim)
            }
            buttonCheck.text = getString(R.string.lesson_next)
        }

        bindQuestion()
    }
}

