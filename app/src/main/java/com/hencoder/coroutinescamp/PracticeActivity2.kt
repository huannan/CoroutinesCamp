package com.hencoder.coroutinescamp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PracticeActivity2 : AppCompatActivity() {
    companion object {
        private const val TAG = "PracticeActivity2";
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice1)

        GlobalScope.launch(Dispatchers.Main) {
            val data = getData()
            val processedData = processData(data)
            Log.d(TAG, "setText thread= ${Thread.currentThread().name}")
            findViewById<TextView>(R.id.textView).text = processedData
        }
    }

    // 耗时函数 1
    private suspend fun getData() = withContext(Dispatchers.IO) {
        Log.d(TAG, "getData thread= ${Thread.currentThread().name}")
        // 假设这个函数比较耗时，需要放在后台
        return@withContext "hen_coder"
    }

    // 耗时函数 2
    private suspend fun processData(data: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "processData thread= ${Thread.currentThread().name}")
        // 假设这个函数也比较耗时，需要放在后台
        return@withContext data.split("_") // 把 "hen_coder" 拆成 ["hen", "coder"]
            .map { it.capitalize() } // 把 ["hen", "coder"] 改成 ["Hen", "Coder"]
            .reduce { acc, s -> acc + s } // 把 ["Hen", "Coder"] 改成 "HenCoder"
    }
}