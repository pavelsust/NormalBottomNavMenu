package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.utils.isDarkThemeOn
import com.example.myapplication.utils.setUpStatusNavigationBarColors

/**
 * [MainActivity] is used as the main container to hold all child
 * fragments for listing examples.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setUpStatusNavigationBarColors(
            isDarkThemeOn(),
            ContextCompat.getColor(this, R.color.background)
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
    }
}
