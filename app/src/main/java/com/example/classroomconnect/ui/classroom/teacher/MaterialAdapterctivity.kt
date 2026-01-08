package com.example.classroomconnect.ui.classroom.teacher

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.content.Intent
import android.net.Uri

class MaterialsAdapter(
    private val classId: String,
    private var list: MutableList<MaterialItem>,
    private val refresh: () -> Unit
) : RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvMaterialName)
        val delete: ImageView = itemView.findViewById(R.id.btnDel)
        val btnViewReactions: Button = itemView.findViewById(R.id.btnViewReactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        holder.name.text = item.fileName

        holder.name.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
            context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete material")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Delete") { _, _ ->

                    firestore.collection("classes")
                        .document(classId)
                        .collection("materials")
                        .document(item.fileId)
                        .delete()
                        .addOnSuccessListener {

                            if (item.fileUrl.isNotEmpty()) {
                                storage.getReferenceFromUrl(item.fileUrl).delete()
                            }

                            list.removeAt(position)
                            notifyDataSetChanged()
                            refresh()

                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        holder.btnViewReactions.setOnClickListener {

            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_reactions, null)

            val rv = dialogView.findViewById<RecyclerView>(R.id.rvReactions)
            rv.layoutManager = LinearLayoutManager(context)

            val reactionsList = mutableListOf<ReactionItem>()
            val reactionsAdapter = ReactionsAdapter(reactionsList)
            rv.adapter = reactionsAdapter

            val dialog = AlertDialog.Builder(context)
                .setTitle("Student Reactions")
                .setView(dialogView)
                .setNegativeButton("Close", null)
                .create()

            firestore.collection("materials")
                .document(item.fileId)
                .collection("reactions")
                .get()
                .addOnSuccessListener { reactionDocs ->

                    if (reactionDocs.isEmpty) {
                        Toast.makeText(context, "No reactions yet", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    reactionsList.clear()

                    for (doc in reactionDocs.documents) {
                        val userId = doc.id
                        val emoji = doc.getString("emoji") ?: ""

                        firestore.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val studentName =
                                    userDoc.getString("name") ?: "Student"

                                reactionsList.add(
                                    ReactionItem(studentName, emoji)
                                )
                                reactionsAdapter.notifyDataSetChanged()
                            }
                    }

                    dialog.show()
                }
        }
    }

    override fun getItemCount(): Int = list.size
}
