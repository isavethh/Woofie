package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
        fun onOpenPlacementTest()
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

        parentFragmentManager.setFragmentResultListener(
            PlacementTestFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val level = bundle.getString(PlacementTestFragment.RESULT_LEVEL).orEmpty()
            if (level.isBlank()) return@setFragmentResultListener

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
        }

        chipProfessionGroup.setOnCheckedStateChangeListener { _, _ -> refreshContinueState() }
        chipLevelGroup.setOnCheckedStateChangeListener { _, _ ->
            if (!isSettingLevelProgrammatically) {
                recommendedLevel = null
            }
            refreshContinueState()
        }
        nameInput.doOnTextChanged { _, _, _, _ -> refreshContinueState() }
        buttonAssessment.setOnClickListener { listener?.onOpenPlacementTest() }

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
