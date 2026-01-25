package com.example.myfinalproject.data


data class Question(
    val question: String,
    val options: List<String>,
    val answerIndex: Int,
    val imageResId: Int? = null
)
