package com.example.classroomconnect.model

data class StudentAssignmentModel(
    val assignmentId: String = "",
    val name: String = "",
    val fileUrl: String = "",
    var classId: String = "",
    val timestamp: Long = 0L,
    var isSubmitted: Boolean = false,
    var submittedFileUrl: String = ""
)
