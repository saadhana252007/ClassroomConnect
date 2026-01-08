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

        // ✅ CLASS ID CHECK
        classId = intent.getStringExtra("CLASS_ID") ?: run {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 📂 Select file
        binding.btnselectassign.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 101)
        }

        // ⬆️ Upload assignment
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

            binding.uploadProgress.visibility = View.VISIBLE

            val fileRef =
                storage.reference.child("assignments/$classId/${System.currentTimeMillis()}")

            fileRef.putFile(fileUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { url ->

                        val assignmentData = hashMapOf(
                            "name" to name,
                            "fileUrl" to url.toString(),
                            "timestamp" to FieldValue.serverTimestamp()
                        )

                        firestore.collection("classes")
                            .document(classId)
                            .collection("assignments")
                            .add(assignmentData)
                            .addOnSuccessListener {
                                binding.uploadProgress.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    "Assignment uploaded",
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
                .addOnFailureListener {
                    binding.uploadProgress.visibility = View.GONE
                    Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show()
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
