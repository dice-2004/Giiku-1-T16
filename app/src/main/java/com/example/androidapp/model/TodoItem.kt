package com.example.androidapp.model

data class TodoItem(
    val id: Long,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false
)
