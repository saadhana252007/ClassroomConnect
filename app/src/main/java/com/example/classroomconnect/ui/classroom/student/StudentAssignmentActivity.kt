package com.example.classroomconnect.ui.classroom.student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityStudentAssignmentBinding
import com.example.classroomconnect.model.StudentAssignmentModel
import com.google.firebase.auth.FirebaseAuth
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

        val studentId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("classes")
            .document(classId)
            .collection("assignments")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snap ->

                assignmentList.clear()

                if (snap.isEmpty) {
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                var processed = 0
                val total = snap.size()

                for (doc in snap.documents) {

                    val assignmentId = doc.id
                    val name = doc.getString("name") ?: "Assignment"
                    val fileUrl = doc.getString("fileUrl") ?: ""

                    val model = StudentAssignmentModel(
                        assignmentId = assignmentId,
                        name = name,
                        fileUrl = fileUrl,
                        classId = classId,
                        isSubmitted = false,
                        submittedFileUrl = ""
                    )

                    assignmentList.add(model)

                    firestore.collection("classes")
                        .document(classId)
                        .collection("assignments")
                        .document(assignmentId)
                        .collection("submissions")
                        .whereEqualTo("studentId", studentId)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { subSnap ->

                            if (!subSnap.isEmpty) {
                                val subDoc = subSnap.documents[0]
                                model.isSubmitted = true
                                model.submittedFileUrl =
                                    subDoc.getString("fileUrl") ?: ""
                            }

                            processed++
                            if (processed == total) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener {
                            processed++
                            if (processed == total) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
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