package com.example.classroomconnect.ui.classroom.student

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityStudentClassDetailBinding
import com.example.classroomconnect.ui.classroom.student.StudentMaterialsActivity
import com.google.firebase.firestore.FirebaseFirestore

class StudentClassDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentClassDetailBinding
    private var classId: String = ""
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentClassDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        classId = intent.getStringExtra("CLASS_ID") ?: ""

        if (classId.isEmpty()) {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val className = intent.getStringExtra("CLASS_NAME") ?: ""
        val classDescription = intent.getStringExtra("CLASS_DESCRIPTION") ?: ""

        binding.classNameTextView.text = className
        binding.classDescriptionTextView.text = classDescription

        loadClassDetails()
        binding.btnViewMaterialsStu.setOnClickListener {
            val intent = Intent(this, StudentMaterialsActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }



        binding.btnDiscussionStu.setOnClickListener {
            Toast.makeText(this, "Opening discussion…", Toast.LENGTH_SHORT).show()
            val i = Intent(this, DiscussionActivity::class.java)
            i.putExtra("CLASS_ID", classId)
            startActivity(i)
        }

        binding.btnAssignmentsStu.setOnClickListener {
            val intent = Intent(this, StudentAssignmentActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }




        binding.btnbookmark.setOnClickListener {
            Toast.makeText(this, "Opening boohmarks…", Toast.LENGTH_SHORT).show()
            val i = Intent(this, BookmarksActivity::class.java)
            i.putExtra("CLASS_ID", classId)
            startActivity(i)
        }
    }

    private fun loadClassDetails() {
        firestore.collection("classes")
            .document(classId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.classNameTextView.text =
                        document.getString("className") ?: ""

                    binding.classDescriptionTextView.text =
                        document.getString("description") ?: ""
                } else {
                    Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load class", Toast.LENGTH_SHORT).show()
            }
    }
}