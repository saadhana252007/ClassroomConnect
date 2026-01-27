package com.example.classroomconnect.ui.auth

import android.content.Intent
import com.example.classroomconnect.ui.home.TeacherHomeActivity
import com.example.classroomconnect.ui.home.StudentHomeActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivitySignupBinding
import com.example.classroomconnect.viewmodel.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val role = if (binding.rbTeacher.isChecked) {
                "teacher"
            } else {
                "student"
            }

            vm.signUp(email, password, name, role)
        }
        vm.signupResult.observe(this) { result ->
            Log.d("SignupActivity", "Signup result: $result")
            if (result.first) {
                val role = if (binding.rbTeacher.isChecked) "teacher" else "student"
                val intent = if (role == "teacher") {
                    Intent(this, TeacherHomeActivity::class.java)
                } else {
                    Intent(this, StudentHomeActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            else {
                binding.tvError.text = result.second
            }
        }

    }
}
