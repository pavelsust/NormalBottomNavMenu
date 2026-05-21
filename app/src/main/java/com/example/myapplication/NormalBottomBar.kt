package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.databinding.ActivityNormalBottombarBinding

class NormalBottomBar : AppCompatActivity() {

    private lateinit var binding: ActivityNormalBottombarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalBottombarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupNavigation()
    }

    /**
     * Edge-to-edge insets (mandatory on targetSdk 35 + Material3).
     *
     * Bottom: expand SimpleTabBar to cover the system navigation bar area and
     * add paddingBottom so tab content stays above it. LinearLayout respects
     * padding when centering children, so items shift up naturally.
     *
     * Top: push the fragment container below the status bar.
     */
    private fun applyWindowInsets() {
        val barHeightPx = (56 * resources.displayMetrics.density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val bars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            (binding.mainNavHost.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = bars.top
                binding.mainNavHost.layoutParams = this
            }

            (binding.simpleTabBar.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = barHeightPx + bars.bottom
                binding.simpleTabBar.layoutParams = this
            }
            binding.simpleTabBar.setPadding(bars.left, 0, bars.right, bars.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    /**
     * Wire SimpleTabBar to NavController using the same pattern as BubbleActivity:
     *
     *  - Tab click  → navigate to that destination (singleTop + save/restore state)
     *  - Destination change → sync visual selection with callListener=false to avoid
     *    the navigate → changed → navigate loop.
     *
     * Menu item IDs in main_menu.xml match destination IDs in main_nav_graph.xml,
     * so navController.navigate(id) maps directly.
     */
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        val navController = navHostFragment.navController

        binding.simpleTabBar.addBubbleListener { id ->
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
            binding.simpleTabBar.setSelectedWithId(destination.id, callListener = false)
        }
    }
}
