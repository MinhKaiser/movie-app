package com.example.thuctapcosochuyennganh.DataClass

data class CommentPage(
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val lastPage: Boolean,
    val firstPage: Boolean,
    val commentResponseDtoList: List<Comment>
)