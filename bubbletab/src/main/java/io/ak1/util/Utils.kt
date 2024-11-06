package io.ak1.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.StateListAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat


const val ICON_STATE_ANIMATOR_DURATION: Long = 0

internal fun ImageView.colorAnimator(
    @ColorInt from: Int,
    @ColorInt to: Int,
    durationInMillis: Long
): Animator = ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
    duration = durationInMillis
    addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        run { setColorFilter(color) }
    }
}

internal fun ImageView.setColorStateListAnimator(
    @ColorInt color: Int,
    @ColorInt unselectedColor: Int
) {
    val stateList = StateListAnimator().apply {
        addState(
            intArrayOf(android.R.attr.state_selected),
            colorAnimator(unselectedColor, color, ICON_STATE_ANIMATOR_DURATION)
        )
        addState(
            intArrayOf(),
            colorAnimator(color, unselectedColor, ICON_STATE_ANIMATOR_DURATION)
        )
    }

    stateListAnimator = stateList

    // Refresh the drawable state to avoid the unselected animation on view creation
    refreshDrawableState()
}


var DURATION = 350L
var ALPHA = 0.15f
internal fun TextView.expand( @ColorInt color: Int,title :String) {
    setTextColor(color)
    text = title
}


internal fun TextView.collapse(
    @ColorInt color: Int,
    @ColorInt unselectedColor: Int,
    title: String
) {
    text = title
    setTextColor(unselectedColor)

}

internal fun LinearLayout.setCustomBackground(color: Int, alpha: Float, cornerRadius: Float) {
    val containerBackground = GradientDrawable().apply {
        this.cornerRadius = cornerRadius
        DrawableCompat.setTint(
            DrawableCompat.wrap(this), Color.argb(
                (Color.alpha(color) * alpha).toInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        )
    }
    background = containerBackground
}
