package io.ak1

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

class Bubble(context: Context, private val item: MenuItem) : FrameLayout(context) {
    private val icon = ImageView(context)
    private val title = TextView(context)
    private val container = LinearLayout(context)

    private val dpAsPixels = item.horizontalPadding.toInt()
    private val dpAsPixelsVertical = item.verticalPadding.toInt()
    private val dpAsPixelsIcons = item.iconSize.toInt()
    private val dpAsIconPadding = item.iconPadding.toInt()

    init {
        id = item.id
        isClickable = true
        isFocusable = true

        container.apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            setPadding(dpAsPixels, dpAsPixelsVertical, dpAsPixels, dpAsPixelsVertical)
        }

        icon.apply {
            layoutParams = LinearLayout.LayoutParams(dpAsPixelsIcons, dpAsPixelsIcons)
            setImageResource(item.icon)
            isEnabled = item.enabled
            if (isEnabled) {
                setColorStateListAnimator(
                    color = item.iconColor,
                    unselectedColor = item.disabledIconColor
                )
            } else {
                setColorFilter(Color.GRAY)
                this@Bubble.setOnClickListener(null)
            }
        }

        title.apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = item.titleTopPadding.toInt()
            }
            text = item.title
            maxLines = 1
            textSize = item.titleSize / resources.displayMetrics.scaledDensity
            setTextColor(item.disableTitleColor)
            if (item.customFont != 0) {
                try {
                    typeface = ResourcesCompat.getFont(context, item.customFont)
                } catch (e: Exception) {
                    Log.e("Bubble", "Font not loaded: ${e.message}")
                }
            }
        }

        container.addView(icon)
        container.addView(title)
        addView(container)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        icon.jumpDrawablesToCurrentState()
        if (!enabled && isSelected) {
            isSelected = false
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            title.expand(item.iconColor, item.title.toString())
        } else {
            title.collapse(item.iconColor, item.disableTitleColor, item.title.toString())
        }
    }
}
