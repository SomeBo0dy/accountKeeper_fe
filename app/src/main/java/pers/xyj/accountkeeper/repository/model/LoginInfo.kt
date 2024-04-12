package pers.xyj.accountkeeper.repository.model

data class LoginInfo(
    var phone: String = "",
    var p_code: String = "",
    var type: String = "",
)
