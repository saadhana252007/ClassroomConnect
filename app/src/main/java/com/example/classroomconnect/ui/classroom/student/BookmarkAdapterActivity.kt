package com.example.classroomconnect.ui.classroom.student

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R
import com.example.classroomconnect.model.MaterialModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookmarkAdapter(
    private val context: Context,
    private val list: MutableList<MaterialModel>
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvBookmarkName)
        val btnRemove: ImageView = view.findViewById(R.id.btnRemoveBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_bookmark, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvName.text = item.fileName.ifBlank { "Material" }

        holder.itemView.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to open file", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnRemove.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Remove bookmark")
                .setMessage("Do you really want to delete this bookmark?")
                .setPositiveButton("Yes") { _, _ ->
                    val userId = auth.currentUser?.uid ?: return@setPositiveButton
                    firestore.collection("users")
                        .document(userId)
                        .collection("bookmarks")
                        .document(item.fileId)
                        .delete()
                        .addOnSuccessListener {
                            list.removeAt(position)
                            notifyItemRemoved(position)
                            Toast.makeText(context, "Removed from bookmarks", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
    override fun getItemCount(): Int = list.size
}
