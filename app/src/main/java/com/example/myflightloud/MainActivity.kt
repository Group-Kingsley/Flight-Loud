package com.example.myflightloud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myflightloud.ui.theme.MyflightloudTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        val fragment1: Fragment = HomeFragment()
        val fragment2: Fragment = SearchFragment()
        val fragment3: Fragment = FlightListFragment()

        val bottomNavigationItemView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        //handle navigation selection
        bottomNavigationItemView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when(item.itemId) {
                R.id.menu_home -> fragment = fragment1
                R.id.menu_search -> fragment = fragment2
                R.id.menu_list -> fragment = fragment3
            }
        }



    }
}

