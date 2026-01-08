package com.example.classroomconnect.model

data class MaterialModel(
    var fileId: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val timestamp: Long = 0L,
    val classId: String = "",
    var myReaction: String = ""
)
