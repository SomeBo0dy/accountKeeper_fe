package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.entity.BookVo
import pers.xyj.accountkeeper.repository.model.AddBookForm
import pers.xyj.accountkeeper.repository.model.BookPriority
import pers.xyj.accountkeeper.repository.model.PageVo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface BookApi {
    //    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json")
    @GET("/books")
    fun getBooks(
        @Query("pageNum") pageNum: Number,
        @Query("pageSize") pageSize: Number
    ): Call<MyResponse<PageVo>>

    @POST("/books")
    fun addBook(@Body addBookForm: AddBookForm): Call<MyResponse<Nothing>>

    @PUT("/bookUsers/priority")
    fun updateBooksPriority(@Body bookList: ArrayList<BookVo>?): Call<MyResponse<Nothing>>
}