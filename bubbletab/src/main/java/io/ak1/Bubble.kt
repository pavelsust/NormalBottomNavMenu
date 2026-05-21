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
                setPadding(
                    item.horizontalPadding.toInt(),
                    item.verticalPadding.toInt(),
                    item.horizontalPadding.toInt(),
                    item.verticalPadding.toInt()
                )
            }
        }

        icon.apply {
            layoutParams = LinearLayout.LayoutParams(item.iconSize.toInt(), item.iconSize.toInt())
            setImageResource(item.icon)
            isEnabled = item.enabled
            if (!isEnabled) setColorFilter(Color.GRAY)
        }

        title.apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
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

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        title.setTextColor(if (selected) item.iconColor else item.disableTitleColor)
    }
}