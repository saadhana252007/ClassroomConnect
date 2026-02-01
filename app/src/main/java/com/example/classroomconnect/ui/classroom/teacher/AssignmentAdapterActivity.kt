package com.example.classroomconnect.ui.classroom.teacher

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.databinding.ItemAssignBinding
import com.example.classroomconnect.model.AssignmentModel

class AssignmentAdapter(
    private val list: MutableList<AssignmentModel>,
    private val onDelete: (AssignmentModel, Int) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentVH>() {

    inner class AssignmentVH(
        val binding: ItemAssignBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentVH {
        val binding = ItemAssignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssignmentVH(binding)
    }

    override fun onBindViewHolder(holder: AssignmentVH, position: Int) {
        val item = list[position]
        val context = holder.binding.root.context
        
        holder.binding.tvAssignmentName.text = item.name

        holder.binding.root.setOnClickListener {
            if (item.fileUrl.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
                context.startActivity(intent)
            }
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(item, position)
        }

        holder.binding.btnView.setOnClickListener {
            val intent = Intent(context, ViewSubmissionsActivity::class.java)
            intent.putExtra("CLASS_ID", item.classId)
            intent.putExtra("ASSIGNMENT_ID", item.fileId)
            intent.putExtra("ASSIGNMENT_NAME", item.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
