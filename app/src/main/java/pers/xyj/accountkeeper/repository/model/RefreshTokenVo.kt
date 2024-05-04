package pers.xyj.accountkeeper.repository.model

data class RefreshTokenVo (
    var newAccessToken: String,
    var newRefreshToken: String,
)