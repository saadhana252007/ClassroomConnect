package com.example.classroomconnect.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.databinding.ItemClassStudentBinding
import com.example.classroomconnect.model.ClassModel

class StudentClassAdapter(
    private val classList: List<Pair<String, ClassModel>>,
    private val onClassClick: (Pair<String, ClassModel>) -> Unit
) : RecyclerView.Adapter<StudentClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(
        val binding: ItemClassStudentBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemClassStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val pair = classList[position]
        val classData = pair.second

        holder.binding.tvClassName.text = classData.className
        holder.binding.tvSubject.text = "Subject: ${classData.subject}"
        holder.binding.tvClassId.text = ""

        holder.itemView.setOnClickListener {
            onClassClick(pair)   // ✅ SEND FULL PAIR
        }
    }

    override fun getItemCount(): Int = classList.size
}
