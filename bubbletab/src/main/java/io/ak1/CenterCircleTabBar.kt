package io.ak1

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.rock.library.CenterBubble

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CenterCircleTabBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var tabStrip: LinearLayout
    private var centerBubble: CenterBubble? = null
    private var oldBubble: Bubble? = null
    private var clickListener: OnBubbleClickListener? = null

    // Both are resolved from XML attrs (bubbletab_bar_height / bubbletab_center_item_size)
    // or default to 56 dp / 64 dp respectively.
    var barHeight: Int = 0
    var centerItemSize: Int = 0

    init {
        val dp = resources.displayMetrics.density
        barHeight = (56 * dp).toInt()
        centerItemSize = (64 * dp).toInt()

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleTabBar, defStyleAttr, 0)
            barHeight = ta.getDimensionPixelSize(R.styleable.BubbleTabBar_bubbletab_bar_height, barHeight)
            centerItemSize = ta.getDimensionPixelSize(R.styleable.BubbleTabBar_bubbletab_center_item_size, centerItemSize)
            ta.recycle()
        }

        tabStrip = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            clipChildren = false
        }

        clipChildren = false
        clipToPadding = false
        addView(tabStrip, LayoutParams(LayoutParams.MATCH_PARENT, barHeight).apply {
            gravity = Gravity.BOTTOM
        })
    }

    fun setMenu(items: List<MenuItem>, centerIndex: Int) {
        tabStrip.removeAllViews()
        centerBubble?.let { removeView(it) }

        items.forEachIndexed { index, item ->
            if (index == centerIndex) {
                val circleSize = if (item.centerItemSize > 0) item.centerItemSize.toInt() else centerItemSize

                // Empty placeholder so side tabs divide remaining width equally.
                tabStrip.addView(View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(circleSize, LayoutParams.MATCH_PARENT)
                })

                // Center circle: gravity=TOP places top at y=0 (top of this FrameLayout).
                // translationY = -(circleSize/2) shifts the circle up so its centre sits on the
                // top edge of the bar — half protruding above, half inside.
                val bubble = CenterBubble(context, item, circleSize)
                bubble.layoutParams = LayoutParams(circleSize, circleSize).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                }
                bubble.translationY = -circleSize / 2f
                bubble.setOnClickListener { clickListener?.onBubbleClick(item.id) }
                centerBubble = bubble
                addView(centerBubble)
            } else {
                val bubble = Bubble(context, item).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                    setOnClickListener {
                        selectBubble(this)
                        clickListener?.onBubbleClick(item.id)
                    }
                    if (item.checked) {
                        isSelected = true
                        oldBubble = this
                    }
                }
                tabStrip.addView(bubble)
            }
        }
    }

    fun setOnBubbleClickListener(listener: OnBubbleClickListener) {
        clickListener = listener
    }

    private fun selectBubble(bubble: Bubble) {
        if (oldBubble?.id == bubble.id) return
        bubble.isSelected = true
        oldBubble?.isSelected = false
        oldBubble = bubble
    }

    /**
     * Programmatically update the visual selection state (e.g. from NavController's
     * OnDestinationChangedListener). Does NOT dispatch to [OnBubbleClickListener] — that
     * would create an infinite loop when wired to navigation.
     */
    fun setSelectedWithId(id: Int) {
        for (i in 0 until tabStrip.childCount) {
            val child = tabStrip.getChildAt(i)
            if (child is Bubble && child.id == id) {
                selectBubble(child)
                return
            }
        }
        // Center bubble is a floating action button — no persistent selected state.
    }
}
