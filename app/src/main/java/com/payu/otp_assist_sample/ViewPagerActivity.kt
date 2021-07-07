package com.payu.otp_assist_sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy


class ViewPagerActivity : AppCompatActivity() {
    var viewpager: ViewPager2? = null
    var tabLayout: TabLayout? = null
    val names = arrayOf("New Card","Saved Cards")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        tabLayout = findViewById(R.id.tabs)
        viewpager = findViewById(R.id.view_pager)
        setUpViewPager(intent)
    }

    private fun setUpViewPager(intent: Intent) {
        val adapter = ViewPagerAdapter(this, intent)
        viewpager?.setAdapter(adapter)
        TabLayoutMediator(tabLayout!!, viewpager!!,
            TabConfigurationStrategy { tab, position -> tab.setText(names.get(position)) }).attach()
    }
}