package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.databinding.ActivityCenterBubbleBinding
import io.ak1.MenuParser

class CenterCircularActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCenterBubbleBinding

    companion object {
        // bubble_menu.xml: Home(0) Search(1) Add(2) Alerts(3) Profile(4)
        private const val CENTER_INDEX = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCenterBubbleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupTabBar()
        setupNavigation()
    }

    /**
     * Handle edge-to-edge insets (mandatory on targetSdk 35 + Material3).
     *
     * Bottom (navigation bar / gesture strip):
     *   - Expand BubbleTabBar's layout height to cover the system-nav-bar area.
     *   - Add paddingBottom so the tabStrip and centre circle stay above it.
     *   FrameLayout Gravity.BOTTOM automatically accounts for paddingBottom,
     *   so the strip content sits flush against the inset boundary.
     *
     * Top (status bar):
     *   - Add a topMargin to the fragment container so content starts below
     *     the status bar rather than drawing behind it.
     */
    private fun applyWindowInsets() {
        val barHeightPx = (56 * resources.displayMetrics.density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val bars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Fragment content: push below the status bar.
            (binding.bubbleNavHost.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = bars.top
                binding.bubbleNavHost.layoutParams = this
            }

            // BubbleTabBar: grow downward to fill the system-nav-bar area,
            // then pad the inner content (tabStrip + circle) up above it.
            (binding.bubbleTabBar.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = barHeightPx + bars.bottom
                binding.bubbleTabBar.layoutParams = this
            }
            binding.bubbleTabBar.setPadding(bars.left, 0, bars.right, bars.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    /**
     * Parse the menu resource and configure each MenuItem before handing
     * the list to BubbleTabBar. Bar and circle sizes come from XML attrs
     * (bubbletab_bar_height / bubbletab_center_item_size).
     */
    private fun setupTabBar() {
        val dp = resources.displayMetrics.density
        val items = MenuParser(this).parse(R.menu.center_menu)

        items.forEach { item ->
            item.iconSize = 24 * dp
            item.titleSize = 10 * dp
            item.horizontalPadding = 8 * dp
            item.verticalPadding = 6 * dp
            item.disabledIconColor = Color.parseColor("#9E9E9E")
            item.disableTitleColor = Color.parseColor("#9E9E9E")
        }

        items.getOrNull(CENTER_INDEX)?.also {
            it.centerItemElevation = 8 * dp
            it.centerIconColor = Color.WHITE
        }

        binding.bubbleTabBar.setMenu(items, CENTER_INDEX)
    }

    /**
     * Wire BubbleTabBar to NavController:
     *  - Tab click  → navigate to that destination (singleTop + save/restore state)
     *  - Destination change → sync visual tab selection (no click listener dispatch
     *    to avoid the navigate → changed → navigate loop)
     *
     * Menu item IDs in bubble_menu.xml match destination IDs in bubble_nav_graph.xml,
     * so navController.navigate(menuItemId) maps directly without a lookup.
     */
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.bubbleNavHost) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bubbleTabBar.setOnBubbleClickListener { id ->
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setPopUpTo(
                    navController.graph.startDestinationId,
                    inclusive = false,
                    saveState = true
                )
                .build()
            navController.navigate(id, null, navOptions)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bubbleTabBar.setSelectedWithId(destination.id)
        }
    }
}
