package com.example.classroomconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    startActivity(Intent(this, StartActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvForgot.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {

        val emailInput = android.widget.EditText(this)
        emailInput.hint = "Enter your registered email"

        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("We will send a reset link to your email")
            .setView(emailInput)
            .setPositiveButton("Send") { _, _ ->

                val email = emailInput.text.toString().trim()

                if (email.isEmpty()) {
                    Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Password reset email sent",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failed to send reset email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
