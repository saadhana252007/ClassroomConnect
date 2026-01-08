package com.example.classroomconnect.ui.classroom.student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityStudentAssignmentBinding
import com.example.classroomconnect.model.StudentAssignmentModel
import com.google.firebase.firestore.FirebaseFirestore

class StudentAssignmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentAssignmentBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val list = mutableListOf<StudentAssignmentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("CLASS_ID")
        if (classId.isNullOrEmpty()) {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvStudentAssignment.layoutManager =
            LinearLayoutManager(this)

        loadAssignments()
    }

    private fun loadAssignments() {

        firestore.collection("assignments")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snap ->

                list.clear()

                for (doc in snap.documents) {
                    val item = doc.toObject(StudentAssignmentModel::class.java)

                    if (item != null && item.fileUrl.isNotBlank()) {
                        val fixedItem = item.copy(
                            assignmentId = doc.id
                        )
                        list.add(fixedItem)
                    }
                }
                binding.rvStudentAssignment.adapter =
                    StudentAssignmentsAdapter(this, list)
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to load assignments",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
