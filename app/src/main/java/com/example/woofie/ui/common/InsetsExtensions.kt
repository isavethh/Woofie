package com.example.woofie.ui.common

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun View.applySystemBarsPadding(includeTop: Boolean = true, includeBottom: Boolean = true) {
    val leftPadding = paddingLeft
    val topPadding = paddingTop
    val rightPadding = paddingRight
    val bottomPadding = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = leftPadding,
            top = topPadding + if (includeTop) bars.top else 0,
            right = rightPadding,
            bottom = bottomPadding + if (includeBottom) bars.bottom else 0
        )
        insets
    }
    requestApplyInsets()
}


