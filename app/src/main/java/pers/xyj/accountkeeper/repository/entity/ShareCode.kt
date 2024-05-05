package pers.xyj.accountkeeper.repository.entity

data class ShareCode (
    var id: Int,
    var bId: Int,
    var shareCode: String,
    var sharedCount: Int,

)