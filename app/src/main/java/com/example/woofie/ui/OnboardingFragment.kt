package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.model.Profession
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    interface Listener {
        fun onOnboardingCompleted(profession: Profession, level: String)
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
        val buttonContinue: MaterialButton = view.findViewById(R.id.buttonContinue)
        val feedbackText: TextView = view.findViewById(R.id.textSelectionSummary)
        val chipProfessionGroup: ChipGroup = view.findViewById(R.id.chipProfessionGroup)
        val chipLevelGroup: ChipGroup = view.findViewById(R.id.chipLevelGroup)

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

        fun refreshContinueState() {
            val profession = selectedProfession()
            val level = selectedLevel()
            val enabled = profession != null && level != null
            buttonContinue.isEnabled = enabled

            feedbackText.text = if (profession != null && level != null) {
                getString(
                    R.string.onboarding_summary,
                    profession.displayName,
                    level
                )
            } else {
                getString(R.string.onboarding_summary_empty)
            }
        }

        chipProfessionGroup.setOnCheckedStateChangeListener { _, _ -> refreshContinueState() }
        chipLevelGroup.setOnCheckedStateChangeListener { _, _ -> refreshContinueState() }

        buttonContinue.setOnClickListener {
            val profession = selectedProfession() ?: return@setOnClickListener
            val level = selectedLevel() ?: return@setOnClickListener
            listener?.onOnboardingCompleted(profession, level)
        }

        refreshContinueState()
    }
}

