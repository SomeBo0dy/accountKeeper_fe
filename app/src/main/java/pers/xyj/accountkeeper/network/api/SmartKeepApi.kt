package pers.xyj.accountkeeper.network.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.SmartKeepVo
import pers.xyj.accountkeeper.repository.model.UserInfo
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface SmartKeepApi {
    @Multipart
    @POST("/smartKeep/shoppingReceipt")
    fun smartKeepByShoppingReceipt(
        @Part receipt: MultipartBody.Part,
    ): Call<MyResponse<SmartKeepVo>>
}