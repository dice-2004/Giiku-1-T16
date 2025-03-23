package com.example.androidapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.data.model.UserData
import com.example.androidapp.data.repository.UserPreferencesRepository

@Composable
fun InitialSetupScreen(navController: NavController, onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository(context) }

    var username by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }

    var usernameError by remember { mutableStateOf(false) }
    var goalError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "初期設定",
            style = MaterialTheme.typography.headlineMedium
        )

        // ユーザー名入力
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = it.length > 10
            },
            label = { Text("ユーザー名 (10文字以内)") },
            isError = usernameError,
            supportingText = {
                if (usernameError) {
                    Text("ユーザー名は10文字以内にしてください")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 目標入力
        OutlinedTextField(
            value = goal,
            onValueChange = {
                goal = it
                goalError = it.length > 100
            },
            label = { Text("現在の目標 (100文字以内)") },
            isError = goalError,
            supportingText = {
                if (goalError) {
                    Text("目標は100文字以内にしてください")
                } else {
                    Text("${goal.length}/100")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        // 年齢入力
        OutlinedTextField(
            value = age,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    age = it
                    ageError = it.isNotEmpty() && (it.toIntOrNull() ?: 0) !in 9..100
                }
            },
            label = { Text("年齢 (9〜100)") },
            isError = ageError,
            supportingText = {
                if (ageError) {
                    Text("年齢は9〜100の範囲で入力してください")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // 性別選択
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("性別:", modifier = Modifier.padding(end = 16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isMale,
                    onClick = { isMale = true }
                )
                Text("男性", modifier = Modifier.padding(start = 8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isMale,
                    onClick = { isMale = false }
                )
                Text("女性", modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (validateInput(username, goal, age) && !usernameError && !goalError && !ageError) {
                    val userData = UserData(
                        username = username,
                        goal = goal,
                        progress = 0,
                        age = age.toInt(),
                        isMale = isMale
                    )
                    repository.saveUserData(userData)
                    onSetupComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("設定を保存")
        }
    }
}

private fun validateInput(username: String, goal: String, age: String): Boolean {
    if (username.isBlank() || username.length > 10) return false
    if (goal.length > 100) return false

    val ageNum = age.toIntOrNull() ?: return false
    if (ageNum !in 9..100) return false

    return true
}