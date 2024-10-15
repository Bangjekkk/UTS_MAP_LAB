package com.example.map_lab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.map_lab.R
import com.example.map_lab.Story
import com.example.map_lab.StoryAdapter
import com.google.firebase.database.*

class HomePageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var storiesRef: DatabaseReference
    private val stories = mutableListOf<Story>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        database = FirebaseDatabase.getInstance()
        storiesRef = database.getReference("stories")
        storyAdapter = StoryAdapter(stories, storiesRef)
        recyclerView.adapter = storyAdapter

        loadStories()

        return view
    }

    private fun loadStories() {
        storiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stories.clear()
                for (storySnapshot in snapshot.children) {
                    for (childSnapshot in storySnapshot.children) {
                        val story = childSnapshot.getValue(Story::class.java)
                        if (story != null) {
                            stories.add(story)
                        }
                    }
                }
                stories.sortByDescending { it.timestamp }
                storyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}