package com.example.myapplication.utils

import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import io.ak1.BubbleTabBar

/**
 * @author : Akshay Sharma
 * @since : 11/01/21, Mon
 * akshay2211.github.io
 **/

/**
 * Extension method to connect [ViewPager] from [BubbleTabBar]
 */
fun BubbleTabBar.setupViewPager(viewPager: ViewPager) {
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            setSelected(position, false)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    })
}

/**
 * Extension method to connect [NavController] from [BubbleTabBar]
 */
fun onNavDestinationSelected(
    itemId: Int,
    navController: NavController
): Boolean {
    val builder = NavOptions.Builder()
        .setLaunchSingleTop(true)
    //if (itemId == getChildAt(0).id) {
    //builder.setPopUpTo(findStartDestination(navController.graph)!!.id, true)
    // }
    builder.setPopUpTo(itemId, true)
    val options = builder.build()
    return try {
        navController.navigate(itemId, null, options)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}