package com.example.classroomconnect.ui.classroom.student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityStudentMaterialsBinding
import com.example.classroomconnect.model.MaterialModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentMaterialsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentMaterialsBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val materialList = mutableListOf<MaterialModel>()
    private lateinit var adapter: StudentMaterialsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentMaterialsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("CLASS_ID")
        if (classId.isNullOrEmpty()) {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvMaterials.layoutManager = LinearLayoutManager(this)
        adapter = StudentMaterialsAdapter(this, materialList)
        binding.rvMaterials.adapter = adapter

        loadMaterials(classId)
    }

    private fun loadMaterials(classId: String) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("classes")
            .document(classId)
            .collection("materials")
            .get()
            .addOnSuccessListener { snapshot ->

                materialList.clear()

                for (doc in snapshot.documents) {
                    val material = doc.toObject(MaterialModel::class.java) ?: continue

                    material.fileId = doc.id
                    material.classId = classId   

                    materialList.add(material)

                    firestore.collection("classes")
                        .document(classId)
                        .collection("materials")
                        .document(doc.id)
                        .collection("reactions")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { reactionDoc ->
                            if (reactionDoc.exists()) {
                                material.myReaction =
                                    reactionDoc.getString("emoji") ?: ""
                                adapter.notifyDataSetChanged()
                            }
                        }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load materials", Toast.LENGTH_SHORT).show()
            }
    }

}