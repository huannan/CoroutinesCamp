package com.hencoder.coroutinescamp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_practice1.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class PracticeActivity3 : AppCompatActivity() {
    companion object {
        private const val TAG = "PracticeActivity3";
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice1)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

        val api = retrofit.create(Api::class.java)

        /**
         * lifecycleScope是KTX的扩展属性
         */
        lifecycleScope.launch {
            try {
                val one = async { api.listReposKt("rengwuxian") }
                val two = async { api.listReposKt("google") }
                textView.text = "${one.await()[0].name}_${two.await()[0].name}"
            } catch (e: Exception) {
                textView.text = e.message
            }
        }
    }
}