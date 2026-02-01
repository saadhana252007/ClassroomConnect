package com.example.classroomconnect.ui.classroom.student

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R
import com.example.classroomconnect.model.MaterialModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentMaterialsAdapter(
    private val context: Context,
    private val list: MutableList<MaterialModel>
) : RecyclerView.Adapter<StudentMaterialsAdapter.VH>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMaterialName)
        val btnBookmark: ImageView = view.findViewById(R.id.btnbookmark)
        val btnDownload: ImageView = view.findViewById(R.id.btndownload)
        val tvEmoji: TextView = view.findViewById(R.id.tvSelectedEmoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material_student, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.tvName.text =
            if (item.fileName.isNotBlank()) item.fileName else "Material"

        if (item.myReaction.isNotEmpty()) {
            holder.tvEmoji.text = item.myReaction
            holder.tvEmoji.visibility = View.VISIBLE
        } else {
            holder.tvEmoji.visibility = View.GONE
        }

        holder.btnBookmark.setImageResource(
            if (item.isBookmarked)
                R.drawable.bookmark_filled
            else
                R.drawable.bookmark_outline
        )

        holder.btnBookmark.setOnClickListener {

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            val bookmarkRef = firestore.collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(item.fileId)

            if (!item.isBookmarked) {

                val data = hashMapOf(
                    "fileId" to item.fileId,
                    "fileName" to item.fileName,
                    "fileUrl" to item.fileUrl,
                    "classId" to item.classId
                )

                bookmarkRef.set(data)
                    .addOnSuccessListener {
                        item.isBookmarked = true
                        holder.btnBookmark.setImageResource(R.drawable.bookmark_filled)
                        Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show()
                    }

            } else {
                bookmarkRef.delete()
                    .addOnSuccessListener {
                        item.isBookmarked = false
                        holder.btnBookmark.setImageResource(R.drawable.bookmark_outline)
                        Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        holder.itemView.setOnClickListener {
            openPreview(item.fileUrl)
        }

        holder.btnDownload.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Material options")
                .setItems(arrayOf("Preview", "Download")) { _, which ->
                    if (which == 0) openPreview(item.fileUrl)
                    else downloadFile(item.fileUrl, item.fileName)
                }
                .show()
        }
    }

    override fun getItemCount(): Int = list.size

    private fun openPreview(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadFile(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName.ifBlank { "material" }
            )

        val manager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}
