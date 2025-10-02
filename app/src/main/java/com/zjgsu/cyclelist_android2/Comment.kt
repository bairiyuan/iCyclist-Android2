package com.zjgsu.cyclelist_android2

data class Comment(
    val id: Int = 0,
    val postId: Int,
    val author: String,
    val content: String,
    val time: String
)