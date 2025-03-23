package com.example.androidapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidapp.ui.theme.AndroidAppTheme
import com.google.ai.client.generativeai.GenerativeModel
import android.util.Log
import androidx.collection.emptyLongSet
import androidx.lifecycle.lifecycleScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db=Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "profile"
        ).build()
        val userDao=db.userDoa()
        enableEdgeToEdge()
        setContent {
            AndroidAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                    )
                }
            }
        }


        lifecycleScope.launch{
            var userData = User(UserName = "XXX", goal = "XXX", goal_raito = 0,age=20, gender = true)
//            userDao.insertUser(userData)
            var R_userData:User = userDao.getUser()
            Log.d("DATADBASE","UserName: ${R_userData.UserName}, goal: ${R_userData.goal}, goal_ratio: ${R_userData.goal_raito}, age: ${R_userData.age}, gender: ${R_userData.gender}")
            userData = User(UserName = userData.UserName,goal=userData.goal, goal_raito = 3,age=userData.age, gender = userData.gender)
            userDao.updateUser(userData)
            R_userData=userDao.getUser()
            Log.d("DATADBASE","UserName: ${R_userData.UserName}, goal: ${R_userData.goal}, goal_ratio: ${R_userData.goal_raito}, age: ${R_userData.age}, gender: ${R_userData.gender}")

            val answer=RunGemini(R_userData)
            Log.d("RETURN",answer)
        }

    }
// i:User o:string(推論結果)
suspend fun  RunGemini(user: User): String{
    var gender:String =""
    if (user.gender == true){
        gender="男性"
    }else{
        gender="女性"
    }

    val prompt="あなたはゲームマスターです。\n"+

            "RPG風の学習クエストを作成してください。\n"+

            "以下のフォーマットに従って、達成したい目標に関連することに関する1日で完了できるクエストを1つ生成してください。\n"+
            "達成すべき目標は"+user.goal+"、その目標の達成度は現在"+user.goal_raito+"です。"+


            "対象は優秀なエンジニアを目指す"+user.age+"歳の初心者〜中級者の"+gender+"です。\n"+



            "【出力フォーマット】\n"+
            "タイトル：『◯◯◯◯を手に入れよ！』\n"+
            "難易度：★☆☆☆☆（1〜5段階）\n"+
            "所要時間：◯分（30〜60分）\n"+
            "内容：どのような学習・作業を行うかを具体的に書いてください。\n"+
            "報酬：どんなスキルや知識が身につくかをゲーム風に表現してください。各報酬に説明も入れてください（＋1など）\n"+
            "経験値：+◯◯ EXP（目安：80〜150）\n"+


            "クエストの内容はワクワク感のある冒険風で、学習モチベーションを高めるような言葉遣いにしてください。\n"
    Log.d("Q",prompt)
    return try{
            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.apiKey
            )
            val response = model.generateContent(prompt)
//            Log.d("R",response.text.toString())
            response.text.toString()
        } catch (e :Exception) {
            "Error: ${e.message}"
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidAppTheme {
        Greeting("Android")
    }
}

@Entity
data class User(
    @PrimaryKey val UserName:String,
    @ColumnInfo(name="goal") val goal:String,
    @ColumnInfo(name="goal_ratio") val goal_raito:Int,
    @ColumnInfo(name="age") val age:Int,
    @ColumnInfo(name="gender") val gender:Boolean
)

@Dao
interface UserDao{
//    データ取得
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser():User

//    データ挿入
//    ユーザデータの定義
//    val userData = User(UserName = "XXX", goal = "XXX", goal_raito = 0,age=20, gender = true)
    @Insert
    suspend fun insertUser(user: User)

//    データ更新

    @Update
    suspend fun updateUser(user: User)

}

@Database(entities = [User::class],version=1)
abstract  class AppDatabase:RoomDatabase(){
    abstract  fun userDoa():UserDao
}
