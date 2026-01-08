package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityTeacherClassDetailBinding
import com.example.classroomconnect.ui.classroom.student.DiscussionActivity
import com.google.firebase.firestore.FirebaseFirestore

class TeacherClassDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherClassDetailBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherClassDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val classId = intent.getStringExtra("CLASS_ID")
        if (classId == null) {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        loadClassDetails(classId)
        binding.btnviewStudents.setOnClickListener {

            val classId = intent.getStringExtra("CLASS_ID")

            if (classId == null) {
                Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val viewIntent = Intent(this, ViewStudentsActivity::class.java)
            viewIntent.putExtra("CLASS_ID", classId)
            startActivity(viewIntent)
        }
        binding.btnviewMaterials.setOnClickListener {

            val classId = intent.getStringExtra("CLASS_ID")

            if (classId == null) {
                Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ViewMaterialsActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }
        binding.btnAssignments.setOnClickListener {

            val classId = intent.getStringExtra("CLASS_ID")

            if (classId == null) {
                Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, AssignmentsActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }
        binding.discussion.setOnClickListener {

            val classId = intent.getStringExtra("CLASS_ID")

            if (classId == null) {
                Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, DiscussionActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }




    }

    private fun loadClassDetails(classId: String) {
        firestore.collection("classes")
            .document(classId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val className = document.getString("className") ?: "Class"
                    val description = document.getString("description") ?: "No description"
                    binding.classNameTextView.text = className
                    binding.classDescriptionTextView.text = description
                }
                else {
                    Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load class", Toast.LENGTH_SHORT).show()
            }
    }
}