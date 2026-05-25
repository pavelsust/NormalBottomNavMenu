package io.ak1

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi

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

    // Bar / center FAB dimensions resolved from XML attrs or defaults.
    var barHeight: Int = 0
    var centerItemSize: Int = 0

    // Style params mirrored from SimpleTabBar
    private var disabledIconColorParam: Int = Color.GRAY
    private var horizontalPaddingParam: Float = 0f
    private var iconPaddingParam: Float = 0f
    private var verticalPaddingParam: Float = 0f
    private var iconSizeParam: Float = 0f
    private var titleSizeParam: Float = 0f
    private var cornerRadiusParam: Float = 0f
    private var customFontParam: Int = 0
    private var disableTitleColorParam: Int = Color.GRAY
    private var titleTopPaddingParam: Float = 0f

    // Center FAB style params
    private var centerItemElevationParam: Float = 0f
    private var centerIconColorParam: Int = Color.WHITE
    private var centerBubbleColorParam: Int = Color.WHITE
    private var centerBubbleStrokeColorParam: Int = 0

    init {
        val dp = resources.displayMetrics.density
        barHeight = (56 * dp).toInt()
        centerItemSize = (64 * dp).toInt()
        centerBubbleStrokeColorParam = context.getColor(R.color.bubble_center_stroke_default)

        // Dimension defaults from dimens.xml (same defaults SimpleTabBar uses)
        iconSizeParam = resources.getDimension(R.dimen.bubble_icon_size)
        titleSizeParam = resources.getDimension(R.dimen.bubble_icon_size)
        horizontalPaddingParam = resources.getDimension(R.dimen.bubble_horizontal_padding)
        verticalPaddingParam = resources.getDimension(R.dimen.bubble_vertical_padding)
        iconPaddingParam = resources.getDimension(R.dimen.bubble_icon_padding)
        cornerRadiusParam = resources.getDimension(R.dimen.bubble_corner_radius)
        titleTopPaddingParam = resources.getDimension(R.dimen.bubble_title_top_padding)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleTabBar, defStyleAttr, 0)
            try {
                barHeight = ta.getDimensionPixelSize(
                    R.styleable.BubbleTabBar_bubbletab_bar_height, barHeight
                )
                centerItemSize = ta.getDimensionPixelSize(
                    R.styleable.BubbleTabBar_bubbletab_center_item_size, centerItemSize
                )
                disabledIconColorParam = ta.getColor(
                    R.styleable.BubbleTabBar_bubbletab_disabled_icon_color, Color.GRAY
                )
                customFontParam = ta.getResourceId(
                    R.styleable.BubbleTabBar_bubbletab_custom_font, 0
                )
                iconPaddingParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_icon_padding, iconPaddingParam
                )
                horizontalPaddingParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_horizontal_padding, horizontalPaddingParam
                )
                verticalPaddingParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_vertical_padding, verticalPaddingParam
                )
                iconSizeParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_icon_size, iconSizeParam
                )
                titleSizeParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_title_size, titleSizeParam
                )
                cornerRadiusParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_tab_corner_radius, cornerRadiusParam
                )
                disableTitleColorParam = ta.getColor(
                    R.styleable.BubbleTabBar_bubbletab_title_disable_color, Color.GRAY
                )
                centerItemElevationParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_center_item_elevation, 0f
                )
                centerIconColorParam = ta.getColor(
                    R.styleable.BubbleTabBar_bubbletab_center_icon_color, Color.WHITE
                )
                centerBubbleColorParam = ta.getColor(
                    R.styleable.BubbleTabBar_bubbletab_center_bubble_color, Color.WHITE
                )
                val strokeFromXml = ta.getColor(
                    R.styleable.BubbleTabBar_bubbletab_center_bubble_stroke_color, 0
                )
                if (strokeFromXml != 0) centerBubbleStrokeColorParam = strokeFromXml
                titleTopPaddingParam = ta.getDimension(
                    R.styleable.BubbleTabBar_bubbletab_title_top_padding, titleTopPaddingParam
                )
            } finally {
                ta.recycle()
            }
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

    /**
     * Populate the bar with [items]. The item at [centerIndex] becomes the floating FAB;
     * all other items become side [Bubble] tabs.
     *
     * Bar-level style params (read from XML attrs) are applied as defaults: if the caller
     * has already set a non-zero value on an item field, it is preserved; otherwise the
     * bar-level param fills it in — mirroring how SimpleTabBar.setMenuResource() works.
     */
    fun setMenu(items: List<MenuItem>, centerIndex: Int) {
        tabStrip.removeAllViews()
        centerBubble?.let { removeView(it) }

        Log.d("APP_STATUS" , "icon Size: ${iconSizeParam}")
        items.forEachIndexed { index, item ->
            // Apply bar-level style params as defaults (non-zero caller values take priority)
            if (item.horizontalPadding == 0f) item.horizontalPadding = horizontalPaddingParam
            if (item.verticalPadding == 0f) item.verticalPadding = verticalPaddingParam
            if (item.iconSize == 0f) item.iconSize = iconSizeParam
            if (item.iconPadding == 0f) item.iconPadding = iconPaddingParam
            if (item.customFont == 0) item.customFont = customFontParam
            if (item.titleSize == 0f) item.titleSize = titleSizeParam
            if (item.cornerRadius == 0f) item.cornerRadius = cornerRadiusParam
            // Color fields: only apply bar param when item still holds the MenuItem default
            if (item.disabledIconColor == Color.GRAY) item.disabledIconColor = disabledIconColorParam
            if (item.disableTitleColor == Color.GRAY) item.disableTitleColor = disableTitleColorParam
            if (item.titleTopPadding == 0f) item.titleTopPadding = titleTopPaddingParam

            if (index == centerIndex) {
                val circleSize = if (item.centerItemSize > 0) item.centerItemSize.toInt() else centerItemSize

                // Apply center FAB params as defaults
                if (item.centerItemElevation == 0f) item.centerItemElevation = centerItemElevationParam
                if (item.centerIconColor == Color.WHITE) item.centerIconColor = centerIconColorParam
                if (item.centerBubbleColor == Color.WHITE) item.centerBubbleColor = centerBubbleColorParam
                if (item.centerBubbleStrokeColor == 0) item.centerBubbleStrokeColor = centerBubbleStrokeColorParam

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
        // Install the touch guard after layout so positions are known.
        post { installTouchGuard() }
    }

    /** Alias for [setOnBubbleClickListener], matching SimpleTabBar's API. */
    fun addBubbleListener(listener: OnBubbleClickListener) {
        clickListener = listener
    }

    fun setOnBubbleClickListener(listener: OnBubbleClickListener) {
        clickListener = listener
    }

    /**
     * Select a side tab by its position among Bubble children (skips the center placeholder).
     * Mirrors SimpleTabBar.setSelected(position, callListener).
     */
    fun setSelected(position: Int, callListener: Boolean = true) {
        var bubbleIndex = 0
        for (i in 0 until tabStrip.childCount) {
            val child = tabStrip.getChildAt(i)
            if (child is Bubble) {
                if (bubbleIndex == position) {
                    selectBubble(child)
                    if (callListener) clickListener?.onBubbleClick(child.id)
                    return
                }
                bubbleIndex++
            }
        }
    }

    /**
     * Programmatically update the visual selection state (e.g. from NavController's
     * OnDestinationChangedListener). [callListener] defaults to false to avoid a
     * navigate → destination-changed → navigate loop when wired to navigation.
     */
    fun setSelectedWithId(@IdRes id: Int, callListener: Boolean = false) {
        for (i in 0 until tabStrip.childCount) {
            val child = tabStrip.getChildAt(i)
            if (child is Bubble && child.id == id) {
                selectBubble(child)
                if (callListener) clickListener?.onBubbleClick(child.id)
                return
            }
        }
        // Center tab clicked — deselect the active side tab.
        if (centerBubble?.id == id) {
            oldBubble?.isSelected = false
            oldBubble = null
        }
    }

    private fun selectBubble(bubble: Bubble) {
        if (oldBubble?.id == bubble.id) return
        bubble.isSelected = true
        oldBubble?.isSelected = false
        oldBubble = bubble
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val adjustedHeightSpec = if (heightMode != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(barHeight, MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, adjustedHeightSpec)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post { installTouchGuard() }
    }

    override fun onDetachedFromWindow() {
        removeTouchGuard()
        super.onDetachedFromWindow()
    }

    // ---------------------------------------------------------------------------
    // Touch guard — replaces the broken TouchDelegate approach.
    //
    // TouchDelegate is checked inside View.onTouchEvent on the parent, which is
    // only reached when NO child handles the event. FragmentContainerView always
    // consumes ACTION_DOWN via its RecyclerView/ViewPager2, so the parent's
    // onTouchEvent is never called and the TouchDelegate is never triggered.
    //
    // Instead we add a transparent, clickable View directly into the parent.
    // Because it is added last it has the highest z-order and receives ACTION_DOWN
    // before FragmentContainerView for any touch in the protrusion zone.
    // Its click listener calls bubble.performClick() so the bar's own
    // OnBubbleClickListener fires exactly as if the user tapped within the bar.
    // ---------------------------------------------------------------------------

    private var touchGuard: View? = null

    private fun installTouchGuard() {
        val bubble = centerBubble ?: return
        val parentView = parent as? ViewGroup ?: return

        removeTouchGuard()

        val protrusion = (-bubble.translationY).toInt()
        val tp = touchPadding
        val guardWidth = bubble.width + tp * 2
        val guardHeight = protrusion + tp

        val guard = View(context).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener { bubble.performClick() }
        }

        parentView.addView(guard, ViewGroup.LayoutParams(guardWidth, guardHeight))
        // Position the guard so it sits exactly over the protruding half of the circle.
        guard.x = (left + bubble.left - tp).toFloat()
        guard.y = (top - protrusion - tp).toFloat()

        touchGuard = guard
    }

    private fun removeTouchGuard() {
        touchGuard?.let { (parent as? ViewGroup)?.removeView(it) }
        touchGuard = null
    }

    private val touchPadding: Int
        get() = (8 * resources.displayMetrics.density).toInt()
}

