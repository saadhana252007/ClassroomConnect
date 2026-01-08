package com.example.classroomconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.classroomconnect.ui.home.StudentHomeActivity
import com.example.classroomconnect.ui.home.TeacherHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        else {
            Log.d("StartActivity", "Current user email: ${user.email}, UID: ${user.uid}")
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val role = doc.getString("role")?.lowercase()
                        Log.d("StartActivity", "Fetched role: $role")  // debug role
                        when(role) {
                            "teacher" -> startActivity(Intent(this, TeacherHomeActivity::class.java))
                            "student" -> startActivity(Intent(this, StudentHomeActivity::class.java))
                            else -> {
                                Log.d("StartActivity", "User role missing or invalid")
                                startActivity(Intent(this, SignupActivity::class.java))
                            }
                        }
                        finish()
                    }
                    else {
                        Log.d("StartActivity", "User document not found")
                        startActivity(Intent(this, SignupActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {
                    Log.d("StartActivity", "Failed to load user data: ${it.message}")
                    startActivity(Intent(this, SignupActivity::class.java))
                    finish()
                }
        }
    }
}
