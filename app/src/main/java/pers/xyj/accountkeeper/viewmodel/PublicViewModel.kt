package com.setruth.mvvmbaseproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.setruth.doublewillow.utils.SPUtil
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import pers.xyj.accountkeeper.network.RequestBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pers.xyj.accountkeeper.network.ApiResponse

class PublicViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedString = MutableLiveData<String>()
    val publicEditStringType by lazy {
        MutableLiveData("")
    }
    val publicEditString by lazy {
        MutableLiveData("")
    }
    val requestBuilder = RequestBuilder(application.applicationContext)
    val spUtil = SPUtil(application.applicationContext)
    fun <T> request(APIType: Class<T>): T = requestBuilder.getAPI(APIType)
    fun <T> Call<T>.getResponse(process: suspend (flow: Flow<ApiResponse<T>>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            requestBuilder.apply {
                process(requestBuilder.getResponse {
                    this@getResponse.execute()
                })
            }
        }
    }

}