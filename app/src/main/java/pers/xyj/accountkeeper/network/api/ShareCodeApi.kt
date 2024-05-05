package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.entity.ShareCode
import pers.xyj.accountkeeper.repository.model.CodeForm
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShareCodeApi {

    @GET("/shareCode/{bookId}")
    fun getShareCode(@Path("bookId") bookId: Int): Call<MyResponse<ShareCode>>

    @POST("/shareCode/cooperation")
    fun joinBook(@Body codeForm: CodeForm): Call<MyResponse<String>>

    @DELETE("/shareCode/cooperation/{bookId}")
    fun quitBook(@Path("bookId") bookId: Int): Call<MyResponse<Nothing>>
}