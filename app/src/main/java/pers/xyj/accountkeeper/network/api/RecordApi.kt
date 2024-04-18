package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.AddBookForm
import pers.xyj.accountkeeper.repository.model.AddRecordForm
import pers.xyj.accountkeeper.repository.model.BookAndRecordVo
import pers.xyj.accountkeeper.repository.model.EditRecordForm
import pers.xyj.accountkeeper.repository.model.PageVo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date

interface RecordApi {
    @GET("/records")
    fun getRecords(
        @Query("pageNum") pageNum: Number,
        @Query("pageSize") pageSize: Number
    ): Call<MyResponse<PageVo>>

    @GET("/records/top")
    fun getTopBookRecords(
        @Query("date") date: Date,
        @Query("pageNum") pageNum: Number,
        @Query("pageSize") pageSize: Number
    ): Call<MyResponse<BookAndRecordVo>>
    @POST("/records")
    fun addRecord(@Body addRecordForm: AddRecordForm): Call<MyResponse<Nothing>>
    @PUT("/records")
    fun editRecord(@Body editRecordForm: EditRecordForm): Call<MyResponse<Nothing>>
    @DELETE("/records/{recordId}")
    fun deleteRecord(@Path("recordId") recordId: Long): Call<MyResponse<Nothing>>
}