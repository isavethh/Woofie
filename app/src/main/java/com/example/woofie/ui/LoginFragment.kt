package com.example.woofie.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.ui.common.applySystemBarsPadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment(R.layout.fragment_login) {

    interface Listener {
        fun onLoginContinue(email: String)
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

        val emailInput: TextInputEditText = view.findViewById(R.id.editEmail)
        val buttonContinue: MaterialButton = view.findViewById(R.id.buttonLoginContinue)
        val buttonCreate: MaterialButton = view.findViewById(R.id.buttonCreateAccount)

        fun updateButtonState() {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            buttonContinue.isEnabled = email.isNotBlank()
        }

        emailInput.doOnTextChanged { _, _, _, _ -> updateButtonState() }

        buttonContinue.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            if (email.isNotBlank()) {
                listener?.onLoginContinue(email)
            }
        }

        buttonCreate.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            listener?.onLoginContinue(email)
        }

        updateButtonState()
    }
}

