package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityUploadMaterialBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UploadMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadMaterialBinding

    private var fileUri: Uri? = null
    private var selectedClassName: String? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadClassesFromFirestore()

        binding.selectFileBtn.setOnClickListener {
            selectFile()
        }

        binding.uploadFileBtn.setOnClickListener {
            uploadFile()
        }
    }

    private fun loadClassesFromFirestore() {
        firestore.collection("classes")
            .get()
            .addOnSuccessListener { result ->

                val classList = mutableListOf("Select Class")

                for (doc in result) {
                    doc.getString("className")?.let { classList.add(it) }
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    classList
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                binding.classSpinner.adapter = adapter

                binding.classSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedClassName = classList[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load classes", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {
            fileUri = data?.data
            binding.fileNameText.text =
                fileUri?.lastPathSegment ?: "File Selected"
        }
    }

    private fun uploadFile() {

        if (selectedClassName == null || selectedClassName == "Select Class") {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show()
            return
        }

        if (fileUri == null) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        val typedName = binding.etfilename.text.toString().trim()
        if (typedName.isEmpty()) {
            Toast.makeText(this, "Please enter file name", Toast.LENGTH_SHORT).show()
            return
        }

        binding.uploadProgress.visibility = View.VISIBLE

        val fileRef =
            storage.reference.child("materials/${System.currentTimeMillis()}")

        fileRef.putFile(fileUri!!)
            .addOnProgressListener { taskSnapshot ->
                val progress =
                    (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                binding.uploadProgress.progress = progress.toInt()
            }
            .addOnSuccessListener {

                fileRef.downloadUrl.addOnSuccessListener { url ->

                    firestore.collection("classes")
                        .whereEqualTo("className", selectedClassName)
                        .get()
                        .addOnSuccessListener { docs ->

                            for (doc in docs) {

                                val materialRef = firestore.collection("classes")
                                    .document(doc.id)
                                    .collection("materials")
                                    .document()

                                val materialData = hashMapOf(
                                    "fileId" to materialRef.id,
                                    "fileName" to typedName,
                                    "fileUrl" to url.toString(),
                                    "timestamp" to System.currentTimeMillis()
                                )

                                materialRef.set(materialData)
                            }

                            binding.uploadProgress.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "File uploaded successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                }
            }
            .addOnFailureListener {
                binding.uploadProgress.visibility = View.GONE
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
    }
}
