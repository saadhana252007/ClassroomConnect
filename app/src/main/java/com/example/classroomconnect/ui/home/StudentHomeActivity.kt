package com.example.classroomconnect.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityStudentHomeBinding
import com.example.classroomconnect.model.ClassModel
import com.example.classroomconnect.ui.auth.LoginSignupActivity
import com.example.classroomconnect.ui.classroom.student.ProfileActivity
import com.example.classroomconnect.ui.classroom.student.StudentClassDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class StudentHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentHomeBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStudentClasses.layoutManager = LinearLayoutManager(this)

        binding.btnJoinBlue.setOnClickListener {
            showJoinClassDialog()
        }
        binding.profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


        loadStudentClasses()
    }

    private fun showJoinClassDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Enter Class ID"

        AlertDialog.Builder(this)
            .setTitle("Join Class")
            .setView(input)
            .setPositiveButton("Join") { _, _ ->
                joinClass(input.text.toString().trim())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun joinClass(classId: String) {
        val studentId = auth.currentUser?.uid ?: return

        firestore.collection("classes")
            .document(classId)
            .update("students", FieldValue.arrayUnion(studentId))
            .addOnSuccessListener {
                Toast.makeText(this, "Joined class", Toast.LENGTH_SHORT).show()
                loadStudentClasses()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Invalid Class ID", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadStudentClasses() {
        val studentId = auth.currentUser?.uid ?: return

        firestore.collection("classes")
            .whereArrayContains("students", studentId)
            .get()
            .addOnSuccessListener { result ->
                val classList = mutableListOf<Pair<String, ClassModel>>()

                for (doc in result.documents) {
                    val classData = doc.toObject(ClassModel::class.java)
                    if (classData != null) {
                        classList.add(Pair(doc.id, classData))
                    }
                }

                binding.rvStudentClasses.adapter =
                    StudentClassAdapter(classList) { pair ->

                        val classId = pair.first
                        val classData = pair.second

                        val intent = Intent(this, StudentClassDetailActivity::class.java)
                        intent.putExtra("CLASS_ID", classId)
                        intent.putExtra("CLASS_NAME", classData.className)
                        intent.putExtra("CLASS_DESCRIPTION", classData.description)

                        startActivity(intent)
                    }


            }
    }
}
