package pers.xyj.accountkeeper.repository.model

data class LoginUserInfo(
    var token: String,
    var userInfoVo: UserInfo,
)
