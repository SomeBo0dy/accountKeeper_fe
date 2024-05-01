package pers.xyj.accountkeeper.network.api

import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.EditUserForm
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface UserApi {
    @PUT("/users")
    fun editUser(@Body editUserForm: EditUserForm): Call<MyResponse<Nothing>>
}