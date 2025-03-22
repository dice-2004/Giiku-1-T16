package com.example.androidapp.userinterface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.userinterface.AddTodoDialog
import com.example.androidapp.userinterface.TodoItem
import com.example.androidapp.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen() {
    val viewModel: TodoViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddTodoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, description ->
                viewModel.addTodo(title, description)
            }
        )
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("本日のクエスト") }
            )
        },

/*
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "タスクを追加")
            }
        }


 */
    ) { paddingValues ->
        if (viewModel.todos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "タスクがありません\n明日までお待ちください",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(viewModel.todos) { todo ->
                    TodoItem(
                        todo = todo,
                        onToggle = { viewModel.toggleTodoStatus(todo.id) },
                        onDelete = { viewModel.deleteTodo(todo.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}