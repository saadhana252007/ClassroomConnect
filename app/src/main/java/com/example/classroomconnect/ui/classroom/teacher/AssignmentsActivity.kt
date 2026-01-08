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
    private val db = FirebaseFirestore.getInstance()
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

    private fun showDeleteDialog(item: AssignmentModel, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete assignment")
            .setMessage("Are you sure you want to delete this?")
            .setPositiveButton("Delete") { _, _ ->

                db.collection("assignments")
                    .document(item.fileId)
                    .delete()
                    .addOnSuccessListener {

                        if (item.fileUrl.isNotEmpty()) {
                            storage.getReferenceFromUrl(item.fileUrl).delete()
                        }

                        assignmentList.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadAssignments() {
        db.collection("assignments")
            .orderBy("timestamp")
            .addSnapshotListener { value, _ ->

                assignmentList.clear()

                value?.forEach { doc ->
                    assignmentList.add(
                        AssignmentModel(
                            fileId = doc.id,
                            name = doc.getString("name") ?: "",
                            fileUrl = doc.getString("fileUrl") ?: ""
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }
}
