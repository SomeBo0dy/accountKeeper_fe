package pers.xyj.accountkeeper.network.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.EditUserForm
import pers.xyj.accountkeeper.repository.model.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {
    @Multipart
    @PUT("/users/info")
    fun editUserInfo(
        @Part avatarFile: MultipartBody.Part,
        @Part("nickName") nickName: RequestBody,
        @Part("introduction") introduction: RequestBody
    ): Call<MyResponse<UserInfo>>
    @PUT("/users/infoString")
    fun editUserInfoString(
        @Body editUserForm: EditUserForm
    ): Call<MyResponse<UserInfo>>
}