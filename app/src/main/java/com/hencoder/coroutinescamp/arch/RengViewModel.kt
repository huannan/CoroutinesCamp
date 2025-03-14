package com.hencoder.coroutinescamp.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hencoder.coroutinescamp.Api
import com.hencoder.coroutinescamp.model.Repo
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RengViewModel : ViewModel() {

  /**
   * KTX LiveData对协程的支持
   */
  val repos = liveData {
    emit(loadUsers())

    /**
     * KTX ViewModel对协程的支持
     */
    viewModelScope.launch {

    }
  }

  private suspend fun loadUsers(): List<Repo> {
    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.github.com/")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
      .build()
    val api = retrofit.create(Api::class.java)
    return api.listReposKt("rengwuxian")
  }
}