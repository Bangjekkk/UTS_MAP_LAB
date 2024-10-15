package com.example.map_lab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.map_lab.AddStoryFragment
import com.example.map_lab.ui.HomePageFragment
import com.example.map_lab.ui.ProfilePageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("users")

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomePageFragment())
                    true
                }
                R.id.navigation_add_story -> {
                    loadFragment(AddStoryFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfilePageFragment())
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    // Example method to write data to the database
    private fun writeUserData(userId: String, name: String, email: String) {
        val user = User(email, name)
        userRef.child(userId).setValue(user)
    }

    // Example method to read data from the database
    private fun readUserData(userId: String) {
        userRef.child(userId).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            // Handle the retrieved user data
        }.addOnFailureListener {
            // Handle the error
        }
    }
}