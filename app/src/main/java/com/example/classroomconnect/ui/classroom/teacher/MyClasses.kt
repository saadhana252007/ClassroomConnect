package com.example.classroomconnect.ui.classroom.teacher

import android.os.Bundle
import android.content.Intent
import com.example.classroomconnect.ui.classroom.teacher.TeacherClassDetailActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityMyClassesBinding
import com.example.classroomconnect.model.ClassModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyClassesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassesBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyClassesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMyClasses.layoutManager = LinearLayoutManager(this)
        loadMyClasses()
    }
    private fun loadMyClasses() {
        val teacherId = auth.currentUser?.uid ?: return

        firestore.collection("classes")
            .whereEqualTo("teacherId", teacherId)
            .get()
            .addOnSuccessListener { result ->
                val classList = mutableListOf<Pair<String, ClassModel>>()
                for (doc in result.documents) {
                    val classData = doc.toObject(ClassModel::class.java)
                    if (classData != null) {
                        classList.add(Pair(doc.id, classData))
                    }
                }

                binding.rvMyClasses.adapter = ClassAdapter(
                    classList,
                    { classId ->
                        confirmDelete(classId)
                    },
                    { classId ->
                        val intent = Intent(this, TeacherClassDetailActivity::class.java)
                        intent.putExtra("CLASS_ID", classId)
                        startActivity(intent)
                    }
                )
            }
    }
    private fun confirmDelete(classId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete this class?")
            .setPositiveButton("Delete") { _, _ ->
                deleteClass(classId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun deleteClass(classId: String) {
        firestore.collection("classes")
            .document(classId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Class deleted", Toast.LENGTH_SHORT).show()
                loadMyClasses()
            }

            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete class", Toast.LENGTH_SHORT).show()
            }
    }
}
