package com.example.classroomconnect.ui.classroom.student

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classroomconnect.R
import com.example.classroomconnect.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(
    private val list: MutableList<Message>,
    private val onDelete: (Message) -> Unit,
    private val onReply: (Message, String) -> Unit
) : RecyclerView.Adapter<MessageAdapter.MsgVH>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    companion object {
        const val VIEW_RIGHT = 1
        const val VIEW_LEFT = 2
    }

    inner class MsgVH(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == currentUserId)
            VIEW_RIGHT
        else
            VIEW_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgVH {
        val layout = if (viewType == VIEW_RIGHT)
            R.layout.item_message_right
        else
            R.layout.item_message_left

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return MsgVH(view)
    }

    override fun onBindViewHolder(holder: MsgVH, position: Int) {
        val msg = list[position]

        holder.message.text = msg.text

        holder.itemView.setOnLongClickListener {

            if (msg.senderId == currentUserId) {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete message?")
                    .setMessage("Delete for everyone?")
                    .setPositiveButton("Delete") { _, _ -> onDelete(msg) }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                val input = EditText(holder.itemView.context)
                input.hint = "Reply..."

                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Reply")
                    .setView(input)
                    .setPositiveButton("Send") { _, _ ->
                        val reply = input.text.toString()
                        if (reply.isNotBlank()) onReply(msg, reply)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            true
        }
    }

    override fun getItemCount() = list.size
}
