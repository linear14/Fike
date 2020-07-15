package com.dongldh.fike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dongldh.fike.adapter.BikeShowingStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    val tabText = arrayOf<String>("검색결과", "즐겨찾기")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        view_pager.adapter = BikeShowingStateAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(tab_layout, view_pager) { tab: TabLayout.Tab, position: Int ->  
            tab.text = tabText[position]
        }.attach()
    }
}