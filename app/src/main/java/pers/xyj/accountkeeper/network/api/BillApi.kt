package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.AddBillForm
import pers.xyj.accountkeeper.repository.model.AddBookForm
import pers.xyj.accountkeeper.repository.model.EditBillForm
import pers.xyj.accountkeeper.repository.model.EditBookForm
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

interface BillApi {
    @GET("/bills")
    fun getBills(
        @Query("date") date: Date,
        @Query("pageNum") pageNum: Number,
        @Query("pageSize") pageSize: Number
    ): Call<MyResponse<PageVo>>

    @POST("/bills")
    fun addBill(@Body addBillForm: AddBillForm): Call<MyResponse<Nothing>>
    @PUT("/bills")
    fun editBill(@Body editBillForm: EditBillForm): Call<MyResponse<Nothing>>

    @DELETE("/bills/{billId}")
    fun deleteBill(@Path("billId") billId: Int): Call<MyResponse<Nothing>>
}