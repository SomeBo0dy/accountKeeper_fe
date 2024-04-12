package pers.xyj.accountkeeper.network.response

data class MyResponse<T>(
    val code: String,
    val data: T?,
    val msg: String
)