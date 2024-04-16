package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.entity.Type
import pers.xyj.accountkeeper.repository.model.PageVo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TypeApi {
    @GET("/types")
    fun getTypes(): Call<MyResponse<ArrayList<Type>>>
}