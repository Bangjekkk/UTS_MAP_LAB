package com.example.map_lab

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddStoryFragment : Fragment() {

    private lateinit var storyEditText: EditText
    private lateinit var storyImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var submitButton: Button
    private var imageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_story, container, false)

        storyEditText = view.findViewById(R.id.story_text)
        storyImageView = view.findViewById(R.id.story_image)
        selectImageButton = view.findViewById(R.id.select_image_button)
        submitButton = view.findViewById(R.id.submit_button)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        selectImageButton.setOnClickListener {
            selectImage()
        }

        submitButton.setOnClickListener {
            uploadStory()
        }

        return view
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            storyImageView.setImageURI(imageUri)
        }
    }

    private fun uploadStory() {
        val storyText = storyEditText.text.toString()
        if (storyText.isEmpty() || imageUri == null) {
            Toast.makeText(context, "Please write a story and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val storyRef = database.getReference("stories").child(userId).push()
            val imageRef = storage.reference.child("story_images/${storyRef.key}.jpg")

            imageRef.putFile(imageUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val story = Story(userId, storyText, uri.toString(), System.currentTimeMillis())
                    storyRef.setValue(story).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to upload story", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}