package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityViewMaterialsBinding
import com.google.firebase.firestore.FirebaseFirestore

data class MaterialItem(
    val fileId: String = "",
    val fileName: String = "",
    val fileUrl: String = ""
)


class ViewMaterialsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMaterialsBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMaterialsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("CLASS_ID")

        if (classId == null) {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvMaterials.layoutManager = LinearLayoutManager(this)

        loadMaterials(classId)
    }

    private fun loadMaterials(classId: String) {

        firestore.collection("classes")
            .document(classId)
            .collection("materials")
            .get()
            .addOnSuccessListener { result ->

                val list = result.documents.mapNotNull { doc ->
                    MaterialItem(
                        fileId = doc.id,
                        fileName = doc.getString("fileName") ?: "File",
                        fileUrl = doc.getString("fileUrl") ?: ""
                    )
                }.toMutableList()

                binding.rvMaterials.adapter =
                    MaterialsAdapter(
                        classId = classId,
                        list = list,
                        refresh = { loadMaterials(classId) }
                    )
            }
    }


    private fun openFile(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
