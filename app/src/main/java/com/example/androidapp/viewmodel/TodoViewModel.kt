package com.example.androidapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.androidapp.model.TodoItem

class TodoViewModel : ViewModel() {
    private val _todos = mutableStateListOf<TodoItem>()
    val todos: List<TodoItem> = _todos

    private var nextId = 1L

    fun addTodo(title: String, description: String = "") {
        if (title.isNotEmpty()) {
            _todos.add(TodoItem(nextId++, title, description))
        }
    }

    fun toggleTodoStatus(id: Long) {
        val index = _todos.indexOfFirst { it.id == id }
        if (index != -1) {
            _todos[index] = _todos[index].copy(isCompleted = !_todos[index].isCompleted)
        }
    }

    fun deleteTodo(id: Long) {
        _todos.removeIf { it.id == id }
    }
}