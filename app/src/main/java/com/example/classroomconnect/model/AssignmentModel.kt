package com.example.classroomconnect.model

data class StudentAssignmentModel(
    val assignmentId: String = "",
    val name: String = "",
    val fileUrl: String = "",
    val classId: String = "",
    val timestamp: Long = 0L
)
