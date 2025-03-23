package com.example.androidapp.data.repository

import android.content.Context
import com.example.androidapp.data.model.UserData

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
        private const val KEY_USERNAME = "username"
        private const val KEY_GOAL = "goal"
        private const val KEY_PROGRESS = "progress"
        private const val KEY_AGE = "age"
        private const val KEY_IS_MALE = "isMale"
        private const val KEY_SETUP_COMPLETED = "setup_completed"
    }

    fun saveUserData(userData: UserData) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(KEY_USERNAME, userData.username)
            putString(KEY_GOAL, userData.goal)
            putInt(KEY_PROGRESS, userData.progress)
            putInt(KEY_AGE, userData.age)
            putBoolean(KEY_IS_MALE, userData.isMale)
            putBoolean(KEY_SETUP_COMPLETED, true)
            apply()
        }
    }

    fun loadUserData(): UserData? {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean(KEY_SETUP_COMPLETED, false)) {
            return null
        }

        return UserData(
            username = sharedPreferences.getString(KEY_USERNAME, "") ?: "",
            goal = sharedPreferences.getString(KEY_GOAL, "") ?: "",
            age = sharedPreferences.getInt(KEY_AGE, 0),
            progress = sharedPreferences.getInt(KEY_PROGRESS, 0),
            isMale = sharedPreferences.getBoolean(KEY_IS_MALE, true)
        )
    }

    /**
     * 初期設定が完了しているかをチェック
     * @return 設定が完了している場合はtrue
     */
    fun isSetupCompleted(): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_SETUP_COMPLETED, false)
    }
}