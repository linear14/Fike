package com.dongldh.fike.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dongldh.fike.fragment.ResultFragment
import com.dongldh.fike.fragment.StarFragment

class BikeShowingStateAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ResultFragment()
            else -> StarFragment()
        }
    }

}