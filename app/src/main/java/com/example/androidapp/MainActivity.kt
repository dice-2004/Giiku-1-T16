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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        android.util.Log.d("gemini",BuildConfig.apiKey) OK
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
            val question ="あなたはゲームマスターです。\n"+

            "RPG風の学習クエストを作成してください。\n"+

            "以下のフォーマットに従って、「◯◯の技術（エンジニアに関すること）」に関する1日で完了できるクエストを1つ生成してください。\n"+

            "対象は優秀なエンジニアを目指す初心者〜中級者です。\n"+



            "【出力フォーマット】\n"+
            "タイトル：『◯◯◯◯を手に入れよ！』\n"+
            "難易度：★☆☆☆☆（1〜5段階）\n"+
            "所要時間：◯分（30〜60分）\n"+
            "内容：どのような学習・作業を行うかを具体的に書いてください。\n"+
            "報酬：どんなスキルや知識が身につくかをゲーム風に表現してください。各報酬に説明も入れてください（＋1など）\n"+
            "経験値：+◯◯ EXP（目安：80〜150）\n"+


            "クエストの内容はワクワク感のある冒険風で、学習モチベーションを高めるような言葉遣いにしてください。\n"




            val answer=RunGemini(question)
            Log.d("RETURN",answer)
        }

    }
// i:none o:string(推論結果)
suspend fun  RunGemini(param:String): String{
        return try{
            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.apiKey
            )
            val response = model.generateContent(param)
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