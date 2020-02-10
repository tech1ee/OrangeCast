package com.example.orangecast.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.orangecast.R
import com.example.orangecast.view.discover.DiscoverFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val discoverFragment = DiscoverFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun initBottomNavigationBar() {
        home_navigation_bar?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_item_discover -> {
                    true
                }
                R.id.navigation_item_library -> {
                    true
                }
                R.id.navigation_item_new_episodes -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()

    }
}