package pers.xyj.accountkeeper.repository.model

data class LoginUserInfo(
    var accessToken: String,
    var refreshToken: String,
    var userInfoVo: UserInfo,
)
