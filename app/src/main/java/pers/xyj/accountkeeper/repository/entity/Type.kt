package pers.xyj.accountkeeper.repository.entity

data class Type(
    var id: Int,
    var name: String,
    var isIncome: Int,
    var imgUrl: String,
    var isChecked: Boolean = false,
)