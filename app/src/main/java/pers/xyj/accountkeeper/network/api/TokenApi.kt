package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.PageVo
import pers.xyj.accountkeeper.repository.model.RefreshTokenVo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TokenApi {
    @GET("/token/refresh")
    fun refreshToken(@Query("oldRefreshToken") oldRefreshToken: String): Call<MyResponse<RefreshTokenVo>>
}