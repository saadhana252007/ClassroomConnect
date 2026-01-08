package com.example.classroomconnect.ui.classroom.student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classroomconnect.databinding.ActivityDiscussionBinding
import com.example.classroomconnect.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DiscussionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscussionBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var classId: String
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter
    private var senderName = "Student"
    private var senderRole = "student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: run {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = MessageAdapter(
            list = messages,
            onDelete = { msg -> deleteMessage(msg.id) },
            onReply = { originalMsg, replyText ->
                sendReply(originalMsg, replyText)
            }
        )

        binding.chatRecycler.layoutManager = LinearLayoutManager(this)
        binding.chatRecycler.adapter = adapter

        detectUserRole()
        loadMessages()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }
    private fun detectUserRole() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                senderRole = doc.getString("role") ?: "student"
                senderName = if (senderRole == "teacher") "Teacher" else "Student"
            }
    }

    private fun loadMessages() {
        firestore.collection("classrooms")
            .document(classId)
            .collection("discussions")
            .orderBy("timestamp")
            .addSnapshotListener { snap, error ->

                if (error != null) return@addSnapshotListener

                messages.clear()

                snap?.forEach { doc ->
                    messages.add(
                        Message(
                            id = doc.id,
                            text = doc.getString("text") ?: "",
                            senderName = doc.getString("senderName") ?: "",
                            senderId = doc.getString("senderId") ?: "",
                            role = doc.getString("role") ?: ""
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val uid = auth.currentUser?.uid ?: return

        val msg = hashMapOf(
            "text" to text,
            "senderId" to uid,
            "senderName" to senderName,
            "role" to senderRole,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("classrooms")
            .document(classId)
            .collection("discussions")
            .add(msg)

        binding.etMessage.setText("")
    }
    private fun sendReply(original: Message, replyText: String) {
        val uid = auth.currentUser?.uid ?: return

        val replyMsg = hashMapOf(
            "text" to "↪ Reply to ${original.senderName}: $replyText",
            "senderId" to uid,
            "senderName" to senderName,
            "role" to senderRole,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("classrooms")
            .document(classId)
            .collection("discussions")
            .add(replyMsg)
    }
    private fun deleteMessage(messageId: String) {
        firestore.collection("classrooms")
            .document(classId)
            .collection("discussions")
            .document(messageId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
            }
    }
}