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
    private lateinit var adapter: BookmarkAdapter
    private val bookmarkList = mutableListOf<MaterialModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        rvBookmark = findViewById(R.id.rvbookmark)
        rvBookmark.layoutManager = LinearLayoutManager(this)

        adapter = BookmarkAdapter(this, bookmarkList)
        rvBookmark.adapter = adapter

        loadBookmarks()
    }

    private fun loadBookmarks() {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("bookmarks")
            .get()
            .addOnSuccessListener { snap ->

                bookmarkList.clear()

                for (doc in snap.documents) {
                    val material = doc.toObject(MaterialModel::class.java)
                    if (material != null) {
                        material.fileId = doc.id
                        bookmarkList.add(material)
                    }
                }

                adapter.notifyDataSetChanged()

                if (bookmarkList.isEmpty()) {
                    Toast.makeText(this, "No bookmarks yet", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load bookmarks", Toast.LENGTH_SHORT).show()
            }
    }
}
