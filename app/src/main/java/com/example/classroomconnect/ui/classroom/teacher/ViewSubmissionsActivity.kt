package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityViewSubmissionsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.classroomconnect.model.SubmissionModel

class ViewSubmissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewSubmissionsBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val list = mutableListOf<SubmissionModel>()
    private lateinit var adapter: SubmissionsAdapter

    private lateinit var classId: String
    private lateinit var assignmentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewSubmissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: run {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        assignmentId = intent.getStringExtra("ASSIGNMENT_ID") ?: run {
            Toast.makeText(this, "Assignment ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvSubmissions.layoutManager = LinearLayoutManager(this)

        adapter = SubmissionsAdapter(list) { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        binding.rvSubmissions.adapter = adapter

        loadSubmissions()
    }

    private fun loadSubmissions() {

        firestore.collection("classes")
            .document(classId)
            .collection("assignments")
            .document(assignmentId)
            .collection("submissions")
            .get()
            .addOnSuccessListener { snap ->

                list.clear()
                list.addAll(snap.toObjects(SubmissionModel::class.java))
                adapter.notifyDataSetChanged()

                if (list.isEmpty()) {
                    Toast.makeText(this, "No submissions yet", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load submissions: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
