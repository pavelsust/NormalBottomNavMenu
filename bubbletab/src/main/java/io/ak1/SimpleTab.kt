package io.ak1

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

@SuppressLint("ViewConstructor")
class SimpleTab(context: Context, private var item: MenuItem) : FrameLayout(context) {

    private var icon = ImageView(context)
    private var title = TextView(context)
   private var container = LinearLayout(context)

    private val dpAsPixels = item.horizontalPadding.toInt()
    private val dpAsPixelsVertical = item.verticalPadding.toInt()
    private val dpAsPixelsIcons = item.iconSize.toInt()
    private val dpAsIconPadding = item.iconPadding.toInt()

    init {
        id = item.id
        layoutParams = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            gravity = Gravity.CENTER
        }

        container.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    setPadding(dpAsPixels, dpAsPixelsVertical, dpAsPixels, dpAsPixelsVertical)
                    gravity = Gravity.CENTER
                }
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
        }
        icon.apply {
            layoutParams = LayoutParams(dpAsPixelsIcons, dpAsPixelsIcons).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            setImageResource(item.icon)
            isEnabled = item.enabled
            if (isEnabled) {
                setColorStateListAnimator(
                    color = item.iconColor,
                    unselectedColor = item.disabledIconColor
                )
            } else {
                setColorFilter(Color.GRAY)
                this@SimpleTab.setOnClickListener(null)
            }
        }
        title.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    //setPaddingRelative(dpAsIconPadding, 0, 0, 0)
                    gravity = Gravity.CENTER_VERTICAL
                    textAlignment = TEXT_ALIGNMENT_GRAVITY
                }

            maxLines = 1
            textSize = item.titleSize / resources.displayMetrics.scaledDensity

            visibility = VISIBLE
            if (item.customFont != 0) {
                try {
                    typeface = ResourcesCompat.getFont(context, item.customFont)
                } catch (e: Exception) {
                    Log.e("BubbleTabBar", "Could not get typeface: " + e.message)
                }
            }
            text = item.title
            setTextColor(item.disableTitleColor)
        }


        addView(container.apply {
            addView(icon)
            addView(title)
        })
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
        if (selected){
            title.expand(item.iconColor, item.title.toString())
        }else{
            title.collapse(item.iconColor, item.disableTitleColor, item.title.toString())
        }
    }

}
