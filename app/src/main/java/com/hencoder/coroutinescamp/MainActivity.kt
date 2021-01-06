package com.hencoder.coroutinescamp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hencoder.coroutinescamp.databinding.ActivityMainBinding
import com.hencoder.coroutinescamp.model.Repo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity";
    }

    // 主线程Scope
    val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*GlobalScope.launch {
          println("Coroutines Camp 1 ${Thread.currentThread().name}")
        }

        Thread {
          println("Coroutines Camp 2 ${Thread.currentThread().name}")
        }.start()

        thread {
          println("Coroutines Camp 3 ${Thread.currentThread().name}")
        }

        thread {
          ioCode1()
          runOnUiThread {
            uiCode1()
            thread {
              ioCode2()
              runOnUiThread {
                uiCode2()
                thread {
                  ioCode3()
                  runOnUiThread {
                    uiCode3()
                  }
                }
              }
            }
          }
        }*/

        val job = GlobalScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
        }
        // 协程取消
        job.cancel()

        classicIoCode1(false) {
            classicIoCode1 {
                uiCode1()
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

        val api = retrofit.create(Api::class.java)

        /**
         * 协程网络请求
         */
        /*
        GlobalScope.launch(Dispatchers.Main) {
          try {
            val repos = api.listReposKt("rengwuxian") // 后台
            binding.textView.text = repos[0].name // 前台
          } catch (e: Exception) {
            binding.textView.text = e.message // 前台
          }
        }
        */

        /**
         * RxJava网络请求
         */
        /*
        api.listReposRx("rengwuxian")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Repo>> {
                override fun onSuccess(repos: List<Repo>) {
                    binding.textView.text = repos[0].name
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    binding.textView.text = e.message
                }
            })
        */

        /**
         * 通过嵌套回调的形式实现的先后请求
         */
        /*
        api.listRepos("rengwuxian")
            .enqueue(object : Callback<List<Repo>?> {
                override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                    val nameRengwuxian = response.body()?.get(0)?.name
                    api.listRepos("google")
                        .enqueue(object : Callback<List<Repo>?> {
                            override fun onResponse(
                                call: Call<List<Repo>?>,
                                response: Response<List<Repo>?>
                            ) {
                                binding.textView.text =
                                    "${nameRengwuxian}-${response.body()?.get(0)?.name}"
                            }

                            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
                            }
                        })
                }

                override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {

                }
            })
        */


        /**
         * RxJava实现并行请求
         */
        /*
        Single.zip(
            api.listReposRx("rengwuxian"),
            api.listReposRx("google"),
            { list1, list2 -> "${list1[0].name} - ${list2[0].name}" }
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribe { combined -> binding.textView.text = combined }
        */

        /**
         * 协程实现并行请求
         */
        /*
        GlobalScope.launch(Dispatchers.Main) {
          val one = async { api.listReposKt("rengwuxian") }
          val two = async { api.listReposKt("google") }
          binding.textView.text = "${one.await()[0].name} -> ${two.await()[0].name}"
        }
        */

        /**
         * 通过KTX的扩展属性lifecycleScope来管理协程
         */
        /*
        lifecycleScope.launchWhenCreated {
          val one = async { api.listReposKt("rengwuxian") }
          val two = async { api.listReposKt("google") }
          binding.textView.text = "${one.await()[0].name} -> ${two.await()[0].name}"
        }*/
    }

    suspend fun ioCode1() {
        withContext(Dispatchers.IO) {
            println("Coroutines Camp io1 ${Thread.currentThread().name}")
        }
    }

    fun uiCode1() {
        println("Coroutines Camp ui1 ${Thread.currentThread().name}")
    }

    suspend fun ioCode2() {
        withContext(Dispatchers.IO) {
            println("Coroutines Camp io2 ${Thread.currentThread().name}")
        }
    }

    fun uiCode2() {
        println("Coroutines Camp ui2 ${Thread.currentThread().name}")
    }

    suspend fun ioCode3() {
        withContext(Dispatchers.IO) {
            println("Coroutines Camp io3 ${Thread.currentThread().name}")
        }
    }

    fun uiCode3() {
        println("Coroutines Camp ui3 ${Thread.currentThread().name}")
    }

    private val executor = ThreadPoolExecutor(5, 20, 1, TimeUnit.MINUTES, LinkedBlockingDeque())

    private fun classicIoCode1(uiThread: Boolean = true, block: () -> Unit) {
        executor.execute {
            println("Coroutines Camp classic io1 ${Thread.currentThread().name}")
            if (uiThread) {
                runOnUiThread {
                    block()
                }
            } else {
                block()
            }
        }
    }
}