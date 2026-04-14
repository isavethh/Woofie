package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.RadioGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.model.Profession
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    interface Listener {
        fun onOnboardingCompleted(name: String, profession: Profession, level: String)
    }

    private var listener: Listener? = null
    private var recommendedLevel: String? = null
    private var isSettingLevelProgrammatically = false

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

        val buttonContinue: MaterialButton = view.findViewById(R.id.buttonContinue)
        val feedbackText: TextView = view.findViewById(R.id.textSelectionSummary)
        val chipProfessionGroup: ChipGroup = view.findViewById(R.id.chipProfessionGroup)
        val chipLevelGroup: ChipGroup = view.findViewById(R.id.chipLevelGroup)
        val nameInput: TextInputEditText = view.findViewById(R.id.editUserName)
        val buttonAssessment: MaterialButton = view.findViewById(R.id.buttonLevelAssessment)

        val chipsByProfession = mapOf(
            R.id.chipProfessionIt to Profession.IT,
            R.id.chipProfessionHealth to Profession.HEALTH,
            R.id.chipProfessionSales to Profession.SALES
        )

        fun selectedProfession(): Profession? = chipsByProfession[chipProfessionGroup.checkedChipId]

        fun selectedLevel(): String? {
            val selectedChipId = chipLevelGroup.checkedChipId
            if (selectedChipId == View.NO_ID) return null
            val chip = view.findViewById<Chip>(selectedChipId)
            return chip.text.toString()
        }

        fun selectedName(): String = nameInput.text?.toString()?.trim().orEmpty()

        fun refreshContinueState() {
            val profession = selectedProfession()
            val level = selectedLevel()
            val name = selectedName()
            val enabled = name.isNotBlank() && profession != null && level != null
            buttonContinue.isEnabled = enabled

            feedbackText.text = if (profession != null && level != null) {
                if (recommendedLevel != null) {
                    getString(
                        R.string.onboarding_summary_recommended,
                        profession.displayName,
                        level,
                        recommendedLevel
                    )
                } else {
                    getString(
                        R.string.onboarding_summary,
                        profession.displayName,
                        level
                    )
                }
            } else {
                getString(R.string.onboarding_summary_empty)
            }
        }

        fun showAssessmentDialog() {
            val dialogView = layoutInflater.inflate(R.layout.dialog_level_assessment, null)
            val q1Group: RadioGroup = dialogView.findViewById(R.id.assessmentQ1Group)
            val q2Group: RadioGroup = dialogView.findViewById(R.id.assessmentQ2Group)
            val q3Group: RadioGroup = dialogView.findViewById(R.id.assessmentQ3Group)

            val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.onboarding_level_assessment_title)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.onboarding_level_assessment_apply, null)
                .create()

            dialog.setOnShowListener {
                val positive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)

                fun updatePositiveState() {
                    positive.isEnabled = q1Group.checkedRadioButtonId != View.NO_ID &&
                        q2Group.checkedRadioButtonId != View.NO_ID &&
                        q3Group.checkedRadioButtonId != View.NO_ID
                }

                q1Group.setOnCheckedChangeListener { _, _ -> updatePositiveState() }
                q2Group.setOnCheckedChangeListener { _, _ -> updatePositiveState() }
                q3Group.setOnCheckedChangeListener { _, _ -> updatePositiveState() }
                updatePositiveState()

                positive.setOnClickListener {
                    var score = 0
                    if (q1Group.checkedRadioButtonId == R.id.assessmentQ1Option2) score += 1
                    if (q2Group.checkedRadioButtonId == R.id.assessmentQ2Option2) score += 1
                    if (q3Group.checkedRadioButtonId == R.id.assessmentQ3Option2) score += 1

                    val level = when (score) {
                        0, 1 -> "A1"
                        2 -> "A2"
                        else -> "B1"
                    }

                    val chipId = when (level) {
                        "A1" -> R.id.chipLevelA1
                        "A2" -> R.id.chipLevelA2
                        else -> R.id.chipLevelB1
                    }

                    recommendedLevel = level
                    isSettingLevelProgrammatically = true
                    chipLevelGroup.check(chipId)
                    isSettingLevelProgrammatically = false
                    refreshContinueState()
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

        chipProfessionGroup.setOnCheckedStateChangeListener { _, _ -> refreshContinueState() }
        chipLevelGroup.setOnCheckedStateChangeListener { _, _ ->
            if (!isSettingLevelProgrammatically) {
                recommendedLevel = null
            }
            refreshContinueState()
        }
        nameInput.doOnTextChanged { _, _, _, _ -> refreshContinueState() }
        buttonAssessment.setOnClickListener { showAssessmentDialog() }

        buttonContinue.setOnClickListener {
            val name = selectedName()
            val profession = selectedProfession() ?: return@setOnClickListener
            val level = selectedLevel() ?: return@setOnClickListener
            if (name.isBlank()) return@setOnClickListener
            listener?.onOnboardingCompleted(name, profession, level)
        }

        refreshContinueState()
    }
}
