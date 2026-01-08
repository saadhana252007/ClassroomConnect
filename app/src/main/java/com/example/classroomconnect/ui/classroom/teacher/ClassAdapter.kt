package com.example.classroomconnect.ui.classroom.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.databinding.ItemClassBinding
import com.example.classroomconnect.model.ClassModel

class ClassAdapter(
    private val classList: List<Pair<String, ClassModel>>,
    private val onDeleteRequest: (String) -> Unit,
    private val onClassClick: (String) -> Unit
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(val binding: ItemClassBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val (classId, classData) = classList[position]

        holder.binding.tvClassName.text = classData.className
        holder.binding.tvSubject.text = "Subject: ${classData.subject}"
        holder.binding.tvClassId.text = "Class ID: $classId"

        holder.itemView.setOnClickListener {
            onClassClick(classId)
        }

        holder.binding.root.setOnLongClickListener {
            onDeleteRequest(classId)
            true
        }
    }

    override fun getItemCount(): Int = classList.size
}