package com.example.woofie.ui

import android.annotation.SuppressLint
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.woofie.R
import com.example.woofie.data.WoofieRepository
import com.example.woofie.ui.common.applySystemBarsPadding

class LessonPathFragment : Fragment(R.layout.fragment_lesson_path) {

    interface Listener {
        fun onOpenLessonFromPath()
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.applySystemBarsPadding()
        val profile = WoofieRepository.profile ?: return
        val container: LinearLayout = view.findViewById(R.id.pathContainer)
        val trailContainer: LinearLayout = view.findViewById(R.id.pathTrailContainer)
        val nodeAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.woofie_option_pop)
        container.removeAllViews()
        trailContainer.removeAllViews()

        val stages = WoofieRepository.getLessonStages(profile.profession)
        val totalNodes = WoofieRepository.getTotalLessons(profile.profession)
        val completed = profile.completedLessons.coerceAtMost(totalNodes)
        val nextUnlocked = (completed + 1).coerceAtMost(totalNodes)
        val currentNode = if (nextUnlocked <= totalNodes) nextUnlocked else totalNodes

        fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

        repeat(totalNodes) { index ->
            val number = index + 1
            val isDone = number <= completed
            val isCurrentTrail = number == currentNode

            val chip = TextView(requireContext()).apply {
                text = number.toString()
                textSize = 13f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dp(30), dp(30))
                background = when {
                    isDone -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_completed)
                    isCurrentTrail -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_current)
                    else -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_locked)
                }
                setTextColor(
                    if (isDone) ContextCompat.getColor(requireContext(), R.color.woofie_text_on_dark)
                    else ContextCompat.getColor(requireContext(), R.color.woofie_primary_dark)
                )
            }
            trailContainer.addView(chip)

            if (number < totalNodes) {
                val connector = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(dp(18), dp(4)).also { params ->
                        params.leftMargin = dp(6)
                        params.rightMargin = dp(6)
                    }
                    setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (number < currentNode) R.color.woofie_primary else R.color.woofie_gray_200
                        )
                    )
                }
                trailContainer.addView(connector)
            }
        }

        stages.forEach { stage ->
            val stageTitle = TextView(requireContext()).apply {
                text = "Etapa ${stage.number}: ${stage.title}"
                setTextColor(ContextCompat.getColor(requireContext(), R.color.woofie_primary_dark))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                textSize = 16f
                setPadding(dp(4), dp(10), dp(4), dp(8))
            }
            container.addView(stageTitle)

            stage.lessonRange.forEach { nodeNumber ->
                val index = nodeNumber - 1
                val nodeView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_lesson_path_node, container, false)

                val anchor: FrameLayout = nodeView.findViewById(R.id.nodeAnchor)
                val badge: TextView = nodeView.findViewById(R.id.textNodeBadge)
                val connector: View = nodeView.findViewById(R.id.pathConnector)
                val shadow: View = nodeView.findViewById(R.id.nodeShadow)
                val concept: TextView = nodeView.findViewById(R.id.textNodeConcept)
                val edgeTop: View = nodeView.findViewById(R.id.edgeTop)
                val edgeRight: View = nodeView.findViewById(R.id.edgeRight)
                val edgeBottom: View = nodeView.findViewById(R.id.edgeBottom)
                val edgeLeft: View = nodeView.findViewById(R.id.edgeLeft)

                val isCompleted = nodeNumber <= completed
                val isUnlocked = nodeNumber <= nextUnlocked
                val isCurrent = nodeNumber == currentNode

                badge.text = nodeNumber.toString()
                concept.text = getString(
                    R.string.lesson_path_concept_format,
                    nodeNumber,
                    stage.title
                )
                concept.alpha = if (isUnlocked) 1f else 0.7f
                badge.background = when {
                    isCompleted -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_completed)
                    isCurrent -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_current)
                    isUnlocked -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_unlocked)
                    else -> ContextCompat.getDrawable(requireContext(), R.drawable.lesson_node_locked)
                }
                badge.setTextColor(
                    if (isCompleted) ContextCompat.getColor(requireContext(), R.color.woofie_text_on_dark)
                    else ContextCompat.getColor(requireContext(), R.color.woofie_primary_dark)
                )
                badge.alpha = if (isUnlocked) 1f else 0.65f
                badge.isEnabled = isUnlocked
                shadow.alpha = if (isUnlocked) 1f else 0.45f
                badge.scaleX = if (isCurrent) 1.08f else 1f
                badge.scaleY = if (isCurrent) 1.08f else 1f

                val edgeColor = if (isCompleted) {
                    ContextCompat.getColor(requireContext(), R.color.woofie_primary)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.woofie_gray_200)
                }
                edgeTop.setBackgroundColor(edgeColor)
                edgeRight.setBackgroundColor(edgeColor)
                edgeBottom.setBackgroundColor(edgeColor)
                edgeLeft.setBackgroundColor(edgeColor)

                val anchorParams = anchor.layoutParams as LinearLayout.LayoutParams
                val offsetX = when (index % 6) {
                    0 -> 0f
                    1 -> 24f
                    2 -> 48f
                    3 -> 72f
                    4 -> 32f
                    else -> 8f
                }
                anchor.translationX = offsetX
                anchor.layoutParams = anchorParams

                connector.visibility = if (nodeNumber == totalNodes) View.GONE else View.VISIBLE
                if (nodeNumber < totalNodes) {
                    connector.translationX = offsetX
                    connector.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (nodeNumber < currentNode) R.color.woofie_primary else R.color.woofie_primary_light
                        )
                    )
                }

                if (isUnlocked) {
                    val floatAnim = ObjectAnimator.ofFloat(badge, View.TRANSLATION_Y, 0f, -8f, 0f).apply {
                        duration = 1800
                        startDelay = index * 100L
                        repeatCount = ValueAnimator.INFINITE
                    }
                    floatAnim.start()

                    badge.setOnTouchListener { touchedView, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                touchedView.animate().scaleX(0.92f).scaleY(0.92f).setDuration(80).start()
                                true
                            }

                            MotionEvent.ACTION_UP -> {
                                touchedView.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                                touchedView.performClick()
                                true
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                touchedView.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                                true
                            }

                            else -> false
                        }
                    }

                    badge.setOnClickListener {
                        it.startAnimation(nodeAnim)
                        listener?.onOpenLessonFromPath()
                    }
                }

                badge.elevation = if (isCurrent) 10f else 6f

                nodeView.startAnimation(nodeAnim)
                container.addView(nodeView)
            }
        }
    }
}


















