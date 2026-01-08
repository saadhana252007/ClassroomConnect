package com.example.classroomconnect.ui.classroom.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R

data class ReactionItem(
    val studentName: String,
    val emoji: String
)

class ReactionsAdapter(
    private val list: List<ReactionItem>
) : RecyclerView.Adapter<ReactionsAdapter.ReactionViewHolder>() {

    class ReactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reaction, parent, false)
        return ReactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        val item = list[position]
        holder.tvStudentName.text = item.studentName
        holder.tvEmoji.text = item.emoji
    }

    override fun getItemCount(): Int = list.size
}
