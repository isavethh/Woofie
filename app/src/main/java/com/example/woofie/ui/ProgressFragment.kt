package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.data.WoofieRepository
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.button.MaterialButton

class ProgressFragment : Fragment(R.layout.fragment_progress) {

    interface Listener {
        fun onBackToHome()
        fun onRestartPlan()
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

        val resultLabel = requireArguments().getString(ARG_RESULT_LABEL).orEmpty()
        val earnedXp = requireArguments().getInt(ARG_EARNED_XP)
        val passedLesson = requireArguments().getBoolean(ARG_PASSED_LESSON)

        val textHeadline: TextView = view.findViewById(R.id.textProgressHeadline)
        val textSummary: TextView = view.findViewById(R.id.textProgressSummary)
        val textResult: TextView = view.findViewById(R.id.textProgressResult)
        val textPoints: TextView = view.findViewById(R.id.textProgressPoints)
        val textStatus: TextView = view.findViewById(R.id.textProgressStatus)
        val buttonBackHome: MaterialButton = view.findViewById(R.id.buttonBackHome)
        val buttonRestartPlan: MaterialButton = view.findViewById(R.id.buttonRestartPlan)
        val resultAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_result_fade_in)

        textHeadline.text = getString(R.string.progress_title, profile.profession.displayName)
        textSummary.text = getString(
            R.string.progress_summary,
            profile.completedLessons,
            profile.streak,
            profile.xp,
            profile.level
        )
        textResult.text = getString(R.string.progress_last_result, resultLabel)
        textPoints.text = getString(R.string.progress_points_earned, earnedXp)
        val statusText = if (passedLesson) {
            getString(R.string.progress_passed)
        } else {
            getString(R.string.progress_not_passed)
        }
        textStatus.text = getString(R.string.progress_pass_status, statusText)

        textResult.startAnimation(resultAnim)
        textPoints.startAnimation(resultAnim)
        textStatus.startAnimation(resultAnim)

        buttonBackHome.setOnClickListener { listener?.onBackToHome() }
        buttonRestartPlan.setOnClickListener { listener?.onRestartPlan() }
    }

    companion object {
        private const val ARG_RESULT_LABEL = "arg_result_label"
        private const val ARG_EARNED_XP = "arg_earned_xp"
        private const val ARG_PASSED_LESSON = "arg_passed_lesson"

        fun newInstance(resultLabel: String, earnedXp: Int, passedLesson: Boolean): ProgressFragment {
            return ProgressFragment().apply {
                arguments = bundleOf(
                    ARG_RESULT_LABEL to resultLabel,
                    ARG_EARNED_XP to earnedXp,
                    ARG_PASSED_LESSON to passedLesson
                )
            }
        }
    }
}

