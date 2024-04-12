package pers.xyj.accountkeeper.repository.model

data class UserInfo(
    var id: Long = -1L,
    var nickName: String = "",
    var avatar: String = "",
    var sex: String = "",
    var type: String = "",
    var typeName: String = "",
    //账号状态（0正常 1停用）
    var state: String = "",
    //手机号
    var phoneNumber: String = "",
    //个人简介
    var introduction: String = "",
)
