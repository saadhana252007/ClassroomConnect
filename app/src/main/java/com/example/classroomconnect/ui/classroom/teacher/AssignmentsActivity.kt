package com.example.classroomconnect.ui.classroom.teacher

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityAssignmentsBinding
import com.example.classroomconnect.model.AssignmentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AssignmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignmentsBinding
    private lateinit var adapter: AssignmentAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val assignmentList = mutableListOf<AssignmentModel>()
    private lateinit var classId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAssignmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: run {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val test = AssignmentModel(classId = "TEST_OK")

        binding.rvMaterials.layoutManager = LinearLayoutManager(this)

        adapter = AssignmentAdapter(assignmentList) { item, position ->
            showDeleteDialog(item, position)
        }

        binding.rvMaterials.adapter = adapter

        binding.btnuploadassign.setOnClickListener {
            val intent = Intent(this, UploadAssignmentActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }

        loadAssignments()
    }

    private fun loadAssignments() {
        firestore.collection("classes")
            .document(classId)
            .collection("assignments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Toast.makeText(this, "Failed to load assignments", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                assignmentList.clear()

                snapshot?.forEach { doc ->
                    assignmentList.add(
                        AssignmentModel(
                            fileId = doc.id,
                            name = doc.getString("name") ?: "",
                            fileUrl = doc.getString("fileUrl") ?: "",
                            classId = classId
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun showDeleteDialog(item: AssignmentModel, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete assignment")
            .setMessage("Are you sure you want to delete this assignment?")
            .setPositiveButton("Delete") { _, _ ->

                firestore.collection("classes")
                    .document(classId)
                    .collection("assignments")
                    .document(item.fileId)
                    .delete()
                    .addOnSuccessListener {

                        if (item.fileUrl.isNotBlank()) {
                            storage.getReferenceFromUrl(item.fileUrl).delete()
                        }

                        assignmentList.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        Toast.makeText(this, "Assignment deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
