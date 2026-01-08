package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityCreateClassBinding
import com.example.classroomconnect.model.ClassModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateClassBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateClass.setOnClickListener {
            createClass()
        }
    }
    private fun createClass() {
        val className = binding.etClassName.text.toString().trim()
        val subject = binding.etSubject.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (className.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        val teacherId = auth.currentUser?.uid ?: return
        val classRef = firestore.collection("classes").document()
        val classId = classRef.id
        val classData = ClassModel(
            className = className,
            subject = subject,
            description = description,
            teacherId = teacherId
        )

        classRef.set(classData)
            .addOnSuccessListener {
                val intent = Intent(this, ClassCreatedActivity::class.java)
                intent.putExtra("CLASS_ID", classId)
                startActivity(intent)
                finish()
            }

            .addOnFailureListener {
                Toast.makeText(this, "Failed to create class", Toast.LENGTH_SHORT).show()
            }
    }
}