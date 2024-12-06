package com.example.myflightloud

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Redirect to LoginActivity if the user is not authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        val fragment1: Fragment = HomeFragment()
        val fragment2: Fragment = SearchFragment()
        val fragment3: Fragment = FlightListFragment()

        val bottomNavigationItemView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle navigation selection
        bottomNavigationItemView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.menu_home -> fragment = fragment1
                R.id.menu_search -> fragment = fragment2
                R.id.menu_list -> fragment = fragment3
            }

            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }

        // Default fragment
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment1)
            .commit()
    }
}
