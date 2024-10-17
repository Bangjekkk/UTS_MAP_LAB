// ProfilePageFragment.kt
package com.example.map_lab.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.map_lab.LoginActivity
import com.example.map_lab.R
import com.example.map_lab.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfilePageFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var profileImage: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var nimEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            userRef = database.getReference("users").child(currentUser.uid)
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return view
        }

        profileImage = view.findViewById(R.id.profile_image)
        nameEditText = view.findViewById(R.id.et_name)
        nimEditText = view.findViewById(R.id.et_nim)
        updateButton = view.findViewById(R.id.btn_update)
        logoutButton = view.findViewById(R.id.btn_logout)

        loadUserProfile()

        updateButton.setOnClickListener {
            updateUserProfile()
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun loadUserProfile() {
        Log.d("ProfilePageFragment", "Loading user profile")
        userRef.get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            if (user != null) {
                Log.d("ProfilePageFragment", "User found: $user")
                nameEditText.setText(user.name)
                nimEditText.setText(user.nim)
                // Load profile image if available
            } else {
                Log.d("ProfilePageFragment", "No user data found")
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
            Log.e("ProfilePageFragment", "Error loading profile", it)
        }
    }

    private fun updateUserProfile() {
        val name = nameEditText.text.toString()
        val nim = nimEditText.text.toString()

        if (name.isEmpty() || nim.isEmpty()) {
            Toast.makeText(context, "Name and NIM cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = User(currentUser.email!!, "", name, nim)
            userRef.setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("ProfilePageFragment", "Error updating profile", e)
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}