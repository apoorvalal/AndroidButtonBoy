package com.example.buttonboy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Link BottomNav with ViewPager
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_log -> viewPager.currentItem = 0
                R.id.nav_history -> viewPager.currentItem = 1
            }
            true
        }

        // Link ViewPager with BottomNav (for swipe gestures)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNav.menu.getItem(position).isChecked = true
            }
        })
    }
}