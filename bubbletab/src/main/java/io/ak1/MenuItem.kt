package io.ak1

import android.graphics.Color
import androidx.annotation.DrawableRes

data class MenuItem(
    val id: Int,
    val title: CharSequence,
    @DrawableRes val icon: Int,
    val enabled: Boolean = true,
    val checked: Boolean = false,
    var iconColor: Int = Color.WHITE
) {
    var customFont: Int = 0
    var horizontalPadding: Float = 0f
    var verticalPadding: Float = 0f
    var iconSize: Float = 0f
    var cornerRadius: Float = 0f

    var titleSize: Float = 0f
    var iconPadding: Float = 0f
    var disabledIconColor: Int = Color.GRAY
    var disableTitleColor: Int = Color.GRAY
    var titleTopPadding: Float = 0f
    var centerItemSize: Float = 0f
    var centerItemElevation: Float = 0f
    var centerIconColor: Int = Color.WHITE
    var centerBubbleColor: Int = Color.WHITE
    var centerBubbleStrokeColor: Int = 0
}