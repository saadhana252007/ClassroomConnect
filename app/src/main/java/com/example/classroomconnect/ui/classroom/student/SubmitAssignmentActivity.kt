package com.example.classroomconnect.ui.classroom.student

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivitySubmitAssignmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SubmitAssignmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubmitAssignmentBinding
    private var fileUri: Uri? = null
    private lateinit var classId: String

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var assignmentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitAssignmentBinding.inflate(layoutInflater)
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

        val assignmentName = intent.getStringExtra("ASSIGNMENT_NAME") ?: ""
        binding.tvAssignmentName.text = assignmentName

        binding.btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 101)
        }

        binding.btnSubmitAssignment.setOnClickListener {
            submitAssignment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            fileUri = data?.data
            binding.tvSelectedFile.text =
                fileUri?.lastPathSegment ?: "File selected"
        }
    }

    private fun submitAssignment() {

        val uri = fileUri ?: run {
            Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show()
            return
        }

        val studentId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        binding.uploadProgress.visibility = View.VISIBLE
        binding.uploadProgress.progress = 0

        firestore.collection("users")
            .document(studentId)
            .get()
            .addOnSuccessListener { userDoc ->

                val studentName = userDoc.getString("name") ?: "Unknown Student"

                val ref = storage.reference.child(
                    "submissions/$assignmentId/$studentId-${System.currentTimeMillis()}"
                )

                ref.putFile(uri)
                    .addOnProgressListener { task ->
                        val progress = (100.0 * task.bytesTransferred / task.totalByteCount).toInt()
                        binding.uploadProgress.progress = progress
                    }
                    .addOnSuccessListener {

                        ref.downloadUrl.addOnSuccessListener { url ->

                            val data = hashMapOf(
                                "studentId" to studentId,
                                "studentName" to studentName,
                                "fileUrl" to url.toString(),
                                "submittedAt" to System.currentTimeMillis()
                            )

                            firestore.collection("classes")
                                .document(classId)
                                .collection("assignments")
                                .document(assignmentId)
                                .collection("submissions")
                                .add(data)
                                .addOnSuccessListener {
                                    binding.uploadProgress.visibility = View.GONE
                                    Toast.makeText(
                                        this,
                                        "Assignment submitted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    binding.uploadProgress.visibility = View.GONE
                                    Toast.makeText(this, "Failed to save submission", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        binding.uploadProgress.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Upload failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {
                binding.uploadProgress.visibility = View.GONE
                Toast.makeText(this, "Failed to get student details", Toast.LENGTH_SHORT).show()
            }
    }

}
