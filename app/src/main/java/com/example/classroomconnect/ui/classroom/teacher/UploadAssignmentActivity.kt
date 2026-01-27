package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityUploadAssignmentBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth


class UploadAssignmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadAssignmentBinding

    private var fileUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private lateinit var classId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: run {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnselectassign.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 101)
        }

        binding.btnuploadassign.setOnClickListener {

            val name = binding.etAssignmentname.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter assignment name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fileUri == null) {
                Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (FirebaseAuth.getInstance().currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val size = contentResolver.openInputStream(fileUri!!)?.available() ?: 0
            if (size <= 0) {
                Toast.makeText(this, "Invalid file", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            binding.uploadProgress.visibility = View.VISIBLE

            val fileRef = storage.reference
                .child("assignments/$classId/${System.currentTimeMillis()}")

            fileRef.putFile(fileUri!!)
                .addOnSuccessListener {

                    fileRef.downloadUrl.addOnSuccessListener { url ->

                        val assignmentRef = firestore.collection("classes")
                            .document(classId)
                            .collection("assignments")
                            .document()

                        val assignmentData = hashMapOf(
                            "assignmentId" to assignmentRef.id,
                            "name" to name,
                            "fileUrl" to url.toString(),
                            "timestamp" to FieldValue.serverTimestamp()
                        )

                        assignmentRef.set(assignmentData)
                            .addOnSuccessListener {
                                binding.uploadProgress.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    "Assignment uploaded successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                binding.uploadProgress.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    "Failed to save assignment",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                    e.printStackTrace()
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {
            fileUri = data?.data
            binding.fileNameText.text =
                fileUri?.lastPathSegment ?: "File Selected"
        }
    }
}
