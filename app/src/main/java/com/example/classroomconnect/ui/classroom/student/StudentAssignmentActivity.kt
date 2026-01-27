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
    private val assignmentList = mutableListOf<StudentAssignmentModel>()
    private lateinit var classId: String
    private lateinit var adapter: StudentAssignmentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: run {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvStudentAssignment.layoutManager = LinearLayoutManager(this)
        adapter = StudentAssignmentsAdapter(this, assignmentList)
        binding.rvStudentAssignment.adapter = adapter

        loadAssignments()
    }

    private fun loadAssignments() {

        firestore.collection("classes")
            .document(classId)
            .collection("assignments")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snap ->

                assignmentList.clear()

                for (doc in snap.documents) {

                    val name = doc.getString("name") ?: "Assignment"
                    val fileUrl = doc.getString("fileUrl") ?: ""

                    assignmentList.add(
                        StudentAssignmentModel(
                            assignmentId = doc.id,
                            name = name,
                            fileUrl = fileUrl
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load assignments: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
