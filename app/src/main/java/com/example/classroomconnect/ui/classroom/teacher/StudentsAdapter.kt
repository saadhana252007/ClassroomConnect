package com.example.classroomconnect.ui.classroom.teacher

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.databinding.ActivityItemViewStudentsBinding
import com.example.classroomconnect.model.Student

class StudentsAdapter(
    private val classId: String,
    private val students: MutableList<Student>,
    private val onDeleteStudent: (Student, Int) -> Unit
) : RecyclerView.Adapter<StudentsAdapter.VH>() {

    inner class VH(val binding: ActivityItemViewStudentsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ActivityItemViewStudentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val student = students[position]

        holder.binding.tvStudentName.text = student.name

        holder.binding.tvStudentEmail.text = student.email

        holder.binding.btnDel.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Remove Student")
                .setMessage("Are you sure you want to remove ${student.name}?")
                .setPositiveButton("Remove") { _, _ ->
                    onDeleteStudent(student, position)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount() = students.size
}
