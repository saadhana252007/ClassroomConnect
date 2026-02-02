package com.example.classroomconnect.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityTeacherHomeBinding
import com.example.classroomconnect.ui.auth.LoginSignupActivity
import com.example.classroomconnect.ui.classroom.teacher.CreateClassActivity
import com.example.classroomconnect.ui.classroom.teacher.MyClassesActivity
import com.example.classroomconnect.ui.classroom.teacher.UploadMaterialActivity
import com.google.firebase.auth.FirebaseAuth

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherHomeBinding
    private var lastBackTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCreateClass.setOnClickListener {
            startActivity(Intent(this, CreateClassActivity::class.java))
        }
        binding.btnMyClasses.setOnClickListener {
            startActivity(Intent(this, MyClassesActivity::class.java))
        }
        binding.btnUpload.setOnClickListener {
            startActivity(Intent(this, UploadMaterialActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginSignupActivity::class.java))
            finish()
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - lastBackTime < 2000) {
                        finishAffinity() // exits app
                    } else {
                        Toast.makeText(
                            this@TeacherHomeActivity,
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                        lastBackTime = System.currentTimeMillis()
                    }
                }
            }
        )

    }



}
