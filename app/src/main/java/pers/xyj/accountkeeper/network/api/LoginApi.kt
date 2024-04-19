package pers.xyj.accountkeeper.network.api


import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.LoginFoem
import pers.xyj.accountkeeper.repository.model.LoginUserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    //    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json")
    @GET("/phone/send/code/login")
    fun getPhoneLoginCode(
        @Query("phone") phone: String,
        @Query("type") type: String
    ): Call<MyResponse<Nothing>>

    //    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json")
    @POST("/login/phone")
    fun login(@Body loginInfo: LoginFoem): Call<MyResponse<LoginUserInfo>>
    @POST("/logout")
    fun logout(): Call<MyResponse<LoginUserInfo>>
}