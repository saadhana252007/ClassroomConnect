package com.example.classroomconnect.model

data class StudentAssignmentModel(
    val assignmentId: String = "",
    var name: String = "",
    var fileUrl: String = "",
    var timestamp: Long = 0
)
