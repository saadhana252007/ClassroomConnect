package com.example.classroomconnect.model

data class MaterialModel(
    var fileId: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val timestamp: Long = 0L,
    var classId: String = "",
    var myReaction: String = "",
    var studentName: String = "",
    var isBookmarked: Boolean = false
)
