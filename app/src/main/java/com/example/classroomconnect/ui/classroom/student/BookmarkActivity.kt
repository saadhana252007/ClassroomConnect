package com.example.classroomconnect.ui.classroom.student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R
import com.example.classroomconnect.ui.classroom.student.BookmarkAdapter
import com.example.classroomconnect.model.MaterialModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookmarksActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var rvBookmark: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        rvBookmark = findViewById(R.id.rvbookmark)
        rvBookmark.layoutManager = LinearLayoutManager(this)

        loadBookmarks()
    }

    private fun loadBookmarks() {

        val userId = auth.currentUser?.uid ?: return
        val combinedList = mutableListOf<MaterialModel>()

        firestore.collection("users")
            .document(userId)
            .collection("bookmarks")
            .get()
            .addOnSuccessListener { snap ->

                val materialList = snap.toObjects(MaterialModel::class.java)
                combinedList.addAll(materialList)
                firestore.collection("users")
                    .document(userId)
                    .collection("bookmarkedAssignments")
                    .get()
                    .addOnSuccessListener { assignSnap ->

                        for (doc in assignSnap.documents) {
                            val name = doc.getString("name") ?: "Assignment"
                            val fileUrl = doc.getString("fileUrl") ?: ""
                            val material = MaterialModel(
                                 fileName =  name,
                                fileUrl = fileUrl
                            )
                            combinedList.add(material)
                        }
                        rvBookmark.adapter = BookmarkAdapter(this, combinedList)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to load assignments", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load bookmarks", Toast.LENGTH_SHORT).show()
            }
    }
}
