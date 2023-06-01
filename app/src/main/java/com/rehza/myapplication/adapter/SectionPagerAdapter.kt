package com.rehza.myapplication.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rehza.myapplication.ui.DetailUserActivity
import com.rehza.myapplication.ui.FollowersFragment

class SectionPagerAdapter(activity: AppCompatActivity, data: Bundle) : FragmentStateAdapter(activity) {

    private var fragmentBundle: Bundle

    init {
        fragmentBundle = data
    }
    override fun createFragment(position: Int): Fragment {
        val fragment = FollowersFragment()
        fragment.arguments = Bundle().apply {
            putInt(FollowersFragment.ARG_SECTION_NUMBER, position)
            putString(FollowersFragment.EXTRA_USERNAME, fragmentBundle.getString(DetailUserActivity.EXTRA_USERNAME).toString())
        }
        return fragment
    }
    override fun getItemCount(): Int {
        return DetailUserActivity.TAB_TITLES.size
    }
}