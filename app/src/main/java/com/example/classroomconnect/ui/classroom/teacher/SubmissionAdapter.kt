package com.example.classroomconnect.ui.classroom.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R
import com.example.classroomconnect.model.SubmissionModel

class SubmissionsAdapter(
    private val list: List<SubmissionModel>,
    private val onOpen: (String) -> Unit
) : RecyclerView.Adapter<SubmissionsAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val studentName: TextView = view.findViewById(R.id.tvStudentName)
        val openFileBtn: Button = view.findViewById(R.id.btnOpenFile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_submission, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.studentName.text =
            if (item.studentName.isNotBlank())
                item.studentName
            else
                "Unknown Student"

        holder.openFileBtn.setOnClickListener {
            onOpen(item.fileUrl)
        }
    }


    override fun getItemCount(): Int = list.size
}
