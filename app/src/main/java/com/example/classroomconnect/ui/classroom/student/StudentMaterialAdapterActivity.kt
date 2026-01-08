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
    private val list: List<MaterialModel>
) : RecyclerView.Adapter<StudentMaterialsAdapter.MyViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvMaterialName)
        val btnBookmark: ImageView = itemView.findViewById(R.id.btnbookmark)
        val btnDownload: ImageView = itemView.findViewById(R.id.btndownload)
        val tvSelectedEmoji: TextView = itemView.findViewById(R.id.tvSelectedEmoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material_student, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        holder.tvName.text =
            if (item.fileName.isNotBlank()) item.fileName else "Material"

        if (item.myReaction.isNotEmpty()) {
            holder.tvSelectedEmoji.text = item.myReaction
            holder.tvSelectedEmoji.visibility = View.VISIBLE
        } else {
            holder.tvSelectedEmoji.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            openPreview(item.fileUrl)
        }

        holder.btnDownload.setOnClickListener {

            if (item.fileUrl.isBlank()) {
                Toast.makeText(context, "File not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(context)
                .setTitle("Material options")
                .setMessage("What do you want to do?")
                .setPositiveButton("Preview") { _, _ ->
                    openPreview(item.fileUrl)
                }
                .setNegativeButton("Download") { _, _ ->
                    downloadFile(item.fileUrl, item.fileName)
                }
                .setNeutralButton("Cancel", null)
                .show()
        }

        holder.itemView.setOnLongClickListener {

            val userId = auth.currentUser?.uid ?: return@setOnLongClickListener true

            val emojis = arrayOf("👍", "❤️", "🔥", "😊", "👏", "🙌")

            AlertDialog.Builder(context)
                .setTitle("React to material")
                .setItems(emojis) { _, which ->

                    val selectedEmoji = emojis[which]

                    firestore.collection("materials")
                        .document(item.fileId)
                        .collection("reactions")
                        .document(userId)
                        .set(mapOf("emoji" to selectedEmoji))
                        .addOnSuccessListener {

                            item.myReaction = selectedEmoji
                            holder.tvSelectedEmoji.text = selectedEmoji
                            holder.tvSelectedEmoji.visibility = View.VISIBLE

                            Toast.makeText(
                                context,
                                "Reacted $selectedEmoji",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .show()

            true
        }

        holder.btnBookmark.setOnClickListener {

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            firestore.collection("users")
                .document(userId)
                .collection("bookmarks")
                .add(item)
                .addOnSuccessListener {
                    Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int = list.size
    private fun openPreview(url: String) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open preview", Toast.LENGTH_SHORT).show()
        }
    }
    private fun downloadFile(url: String, fileName: String) {

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading material...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName.ifBlank { "material_file" }
            )

        val manager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        manager.enqueue(request)

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }
}
