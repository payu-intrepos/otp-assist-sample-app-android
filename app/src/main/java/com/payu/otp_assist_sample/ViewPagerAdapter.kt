package com.payu.otp_assist_sample

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: ViewPagerActivity, var intent: Intent) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return NewCardFragment.newInstance(
                intent.getStringExtra("key"),
                intent.getStringExtra("email"),
                intent.getStringExtra("salt")
            )
//            0 -> return CardFragment.newInstance(intent.getStringExtra("key")!!,intent.getStringExtra("salt")!!)
            1 -> return SaveCardFragment.newInstance(
                intent.getStringExtra("key"),
                intent.getStringExtra("email"),
                intent.getStringExtra("salt")
            )
        }
        return NewCardFragment()
    }
}