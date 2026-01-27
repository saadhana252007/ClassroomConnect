package com.example.classroomconnect.ui.classroom.student

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.databinding.ItemStudentAssignmentBinding
import com.example.classroomconnect.model.StudentAssignmentModel

class StudentAssignmentsAdapter(
    private val context: Context,
    private val list: List<StudentAssignmentModel>
) : RecyclerView.Adapter<StudentAssignmentsAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemStudentAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvStuAssignmentName.text =
            if (item.name.isNotEmpty()) item.name else "Assignment"

        holder.binding.btnSubmitAssignment.setOnClickListener {

            if (item.assignmentId.isEmpty()) {
                Toast.makeText(context, "Invalid assignment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(context, SubmitAssignmentActivity::class.java)
            intent.putExtra("CLASS_ID", item.classId)
            intent.putExtra("ASSIGNMENT_ID", item.assignmentId)
            intent.putExtra("ASSIGNMENT_NAME", item.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
