package com.example.thuctapcosochuyennganh.DataClass

import java.time.LocalDateTime

data class Comment(
    val commentid: String,
    val username: String,
    val episodeid: String,
    val content: String,
    val canCancel: Boolean,
    val createAt: String
)
