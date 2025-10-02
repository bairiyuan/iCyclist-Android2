package com.zjgsu.cyclelist_android2

data class Post(
    val id: Int = 0,
    val title: String,
    val author: String,
    val time: String,
    val content: String,
    val category: String = "装备讨论",
    val likes: Int = 0,
    val comments: Int = 0
)