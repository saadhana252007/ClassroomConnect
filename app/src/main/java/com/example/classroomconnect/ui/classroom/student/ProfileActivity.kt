package com.example.classroomconnect.ui.classroom.student

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.classroomconnect.databinding.ActivityProfileBinding
import com.example.classroomconnect.ui.auth.LoginSignupActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var isEditing = false
    private var selectedImageUri: Uri? = null
    private lateinit var imagePicker: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgProfile.isClickable = false
        imagePicker = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imgProfile.setImageURI(uri)
            }
        }

        binding.imgProfile.setOnClickListener {
            if (isEditing) {
                imagePicker.launch("image/*")
            }
        }

        loadUserProfile()

        binding.btnEditProfile.setOnClickListener {
            if (!isEditing) {
                enableEditing(true)
                binding.btnEditProfile.text = "Save"
                isEditing = true
            } else {
                saveProfile()
            }
        }
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginSignupActivity::class.java))
            finish()
        }
        binding.tvChangePassword.setOnClickListener {
            val email = auth.currentUser?.email

            if (email.isNullOrEmpty()) {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Password reset link sent to your email",
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

    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                binding.tvName.setText(doc.getString("name") ?: "")
                binding.tvEmail.setText(doc.getString("email") ?: "")
                binding.tvdob.setText(doc.getString("dob") ?: "")
                binding.tvInterest.setText(doc.getString("interest") ?: "")

                val imageUrl = doc.getString("profileImage")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.imgProfile)
                }

                enableEditing(false)
            }
    }

    private fun enableEditing(enable: Boolean) {
        binding.tvName.isEnabled = enable
        binding.tvName.isFocusableInTouchMode = enable
        binding.tvdob.isEnabled = enable
        binding.tvdob.isFocusableInTouchMode = enable
        binding.tvInterest.isEnabled = enable
        binding.tvInterest.isFocusableInTouchMode = enable
        binding.tvEmail.isEnabled = false
        binding.imgProfile.isClickable = enable
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)
        val updatedData = hashMapOf(
            "name" to binding.tvName.text.toString(),
            "dob" to binding.tvdob.text.toString(),
            "interest" to binding.tvInterest.text.toString()
        )

        if (selectedImageUri != null) {
            val imgRef = storage.reference.child("profile_images/$uid.jpg")

            imgRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imgRef.downloadUrl.addOnSuccessListener { url ->
                        updatedData["profileImage"] = url.toString()
                        updateProfileData(userRef, updatedData)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            updateProfileData(userRef, updatedData)
        }
    }

    private fun updateProfileData(userRef: com.google.firebase.firestore.DocumentReference,
                                  data: HashMap<String, String>) {

        userRef.update(data as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                enableEditing(false)
                binding.btnEditProfile.text = "Edit Profile"
                isEditing = false
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
}
