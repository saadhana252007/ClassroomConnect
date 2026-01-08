package com.example.classroomconnect.ui.classroom.teacher

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityViewStudentsBinding
import com.example.classroomconnect.model.Student
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ViewStudentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewStudentsBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var classId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: return

        binding.rvStudents.layoutManager = LinearLayoutManager(this)

        loadStudents()
    }

    private fun loadStudents() {

        firestore.collection("classes")
            .document(classId)
            .get()
            .addOnSuccessListener { doc ->

                val studentIds =
                    doc.get("students") as? List<String> ?: emptyList()

                if (studentIds.isEmpty()) {
                    binding.tvStudentCount.text = "Total Students: 0"
                    return@addOnSuccessListener
                }

                val students = mutableListOf<Student>()
                var loaded = 0

                studentIds.forEach { id ->
                    firestore.collection("users")
                        .document(id)
                        .get()
                        .addOnSuccessListener { userDoc ->

                            students.add(
                                Student(
                                    id = id,
                                    name = userDoc.getString("name") ?: "Unknown",
                                    email = userDoc.getString("email") ?: "No email"
                                )
                            )

                            loaded++
                            if (loaded == studentIds.size) {
                                setupAdapter(students)
                            }
                        }
                }
            }
    }

    private fun setupAdapter(students: MutableList<Student>) {

        binding.tvStudentCount.text = "Total Students: ${students.size}"

        binding.rvStudents.adapter =
            StudentsAdapter(classId, students) { student, position ->

                firestore.collection("classes")
                    .document(classId)
                    .update(
                        "students",
                        FieldValue.arrayRemove(student.id)
                    )
                    .addOnSuccessListener {
                        students.removeAt(position)
                        binding.rvStudents.adapter?.notifyItemRemoved(position)
                        binding.tvStudentCount.text =
                            "Total Students: ${students.size}"

                        Toast.makeText(
                            this,
                            "Student removed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failed to remove student",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }
}
