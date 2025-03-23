package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _currentEnemy = MutableStateFlow<Enemy?>(null)
    val currentEnemy: StateFlow<Enemy?> = _currentEnemy.asStateFlow()

    private val _storyText = MutableStateFlow("")
    val storyText: StateFlow<String> = _storyText.asStateFlow()

    private val _enemyHealth = MutableStateFlow(0)
    val enemyHealth: StateFlow<Int> = _enemyHealth.asStateFlow()

    private val _battleResult = MutableStateFlow(BattleResult.NONE)
    val battleResult: StateFlow<BattleResult> = _battleResult.asStateFlow()

    private val repository = StoryRepository()

    fun loadUserData() {
        viewModelScope.launch {
            val user = repository.getUserData()
            _userData.value = user
        }
    }

    fun loadCurrentEnemy() {
        viewModelScope.launch {
            val enemy = repository.getCurrentEnemy()
            _currentEnemy.value = enemy
            _enemyHealth.value = enemy.currentHealth
            _storyText.value = enemy.introText
        }
    }

    fun loadNextEnemy() {
        viewModelScope.launch {
            delay(1500) // 演出のための遅延
            val nextEnemy = repository.getNextEnemy()
            _currentEnemy.value = nextEnemy
            _enemyHealth.value = nextEnemy.currentHealth
            _storyText.value = nextEnemy.introText
            _battleResult.value = BattleResult.NONE
        }
    }

    fun attackEnemy() {
        viewModelScope.launch {
            val currentUserLevel = _userData.value?.level ?: 0
            val enemy = _currentEnemy.value ?: return@launch

            if (currentUserLevel < enemy.requiredLevel) {
                _battleResult.value = BattleResult.DEFEAT
                _storyText.value = "敵には力が足りないようです。もっとタスクをこなしてレベルアップしましょう！"
                return@launch
            }

            // 攻撃の計算 (レベル差に応じてダメージ量を調整)
            val damage = 10 + (currentUserLevel - enemy.requiredLevel) * 5
            val newHealth = maxOf(0, enemy.currentHealth - damage)

            _enemyHealth.value = newHealth
            _storyText.value = "敵に${damage}のダメージを与えた！"

            // 敵の状態を更新
            val updatedEnemy = enemy.copy(currentHealth = newHealth)
            _currentEnemy.value = updatedEnemy

            // 敵を倒したかチェック
            if (newHealth <= 0) {
                delay(1000) // 演出のための遅延
                _storyText.value = enemy.defeatText
                _battleResult.value = BattleResult.VICTORY

                // 経験値は獲得しない（タスク完了のみで経験値を得る仕様）
                delay(500)
                _storyText.value = "${enemy.defeatText}\n次の敵が現れるのを待ちましょう..."
            }
        }
    }
}