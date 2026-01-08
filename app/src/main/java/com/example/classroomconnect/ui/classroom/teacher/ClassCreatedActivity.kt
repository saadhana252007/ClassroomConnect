package com.example.classroomconnect.ui.classroom.teacher

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityClassCreatedBinding

class ClassCreatedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassCreatedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClassCreatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val classId = intent.getStringExtra("CLASS_ID") ?: "N/A"

        binding.tvClassId.text = classId

        binding.btnCopy.setOnClickListener {
            val clipboard =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Class ID", classId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Class ID copied", Toast.LENGTH_SHORT).show()
        }

        binding.btnDone.setOnClickListener {
            finish()
        }
    }
}