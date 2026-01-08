package com.example.classroomconnect.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.classroomconnect.model.User

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        role: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (password.length < 6) {
            onComplete(false, "Password must be at least 6 characters")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val user = User(uid, name, email, role)

                firestore.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, e.message ?: "Failed to save user data")
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                val errorMessage = when {
                    e.message?.contains("email address is already in use", true) == true ->
                        "This email is already registered"

                    e.message?.contains("badly formatted", true) == true ->
                        "Invalid email format"

                    e.message?.contains("network error", true) == true ->
                        "Network error. Check your internet connection"

                    else ->
                        e.message ?: "Signup failed. Try again"
                }
                onComplete(false, errorMessage)
            }
    }
    fun signIn(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }
    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
