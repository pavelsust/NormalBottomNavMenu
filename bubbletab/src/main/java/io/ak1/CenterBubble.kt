package io.ak1

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi

/**
 * Floating center circle button.
 *
 * Visual: solid fill circle with a subtle stroke ring, matching OneBottomNavigationBar's
 * drawFloating() which draws a stroke circle then fills the inner area.
 *
 * @param circleSize resolved pixel size (caller reconciles item.centerItemSize vs bar default)
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CenterBubble(
    context: Context,
    private val item: MenuItem,
    val circleSize: Int
) : FrameLayout(context) {

    private val icon = ImageView(context)

    init {
        id = item.id
        isClickable = true
        isFocusable = true

        // Stroke width: ~6 % of circle size, at least 2 px  — mirrors the 1 dp linePaint stroke
        // that OneBottomNavigationBar uses to draw the border ring around the floating circle.
        val strokeWidth = (circleSize * 0.06f).toInt().coerceAtLeast(2)

        val fillDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(item.centerBubbleColor)
            setStroke(strokeWidth, item.centerBubbleStrokeColor)
        }

        // Ripple over the fill: learned from OneBottomNavigationBar's touch feedback on the
        // floating circle (ACTION_DOWN returns true so the item gets visual press state).
        background = RippleDrawable(
            ColorStateList.valueOf(Color.argb(60, 255, 255, 255)),
            fillDrawable,
            null
        )

        elevation = item.centerItemElevation
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }
        clipToOutline = true

        val iconSize = (circleSize * 0.45f).toInt()
        icon.apply {
            layoutParams = LayoutParams(iconSize, iconSize).apply { gravity = Gravity.CENTER }
            setImageResource(item.icon)
            setColorFilter(item.centerIconColor)
        }
        addView(icon)

        if (!item.enabled) {
            alpha = 0.4f
            isEnabled = false
        }
    }

}
