package com.example.thuctapcosochuyennganh.ChatScreen

import android.graphics.Bitmap
import com.example.thuctapcosochuyennganh.DataClass.Chat

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)