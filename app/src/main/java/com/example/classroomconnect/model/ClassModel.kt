package com.example.classroomconnect.model

data class ClassModel(
    val classId: String = "",
    val className: String = "",
    val subject: String = "",
    val description: String = "",
    val teacherId: String = "",
    val students: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
