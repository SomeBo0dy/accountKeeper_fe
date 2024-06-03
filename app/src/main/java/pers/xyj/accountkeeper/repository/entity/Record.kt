package pers.xyj.accountkeeper.repository.entity

data class Record(
    var id: Long = -1L,
    var amount: Double = -0.0,
    var tId: Int = -1,
    var isIncome: Int = 1,
    var typeName: String = "",
    var imgUrl: String = "",
    var description: String = "",
    var nickName: String = "",
)