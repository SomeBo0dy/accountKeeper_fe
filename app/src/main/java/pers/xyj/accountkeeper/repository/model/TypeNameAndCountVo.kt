package pers.xyj.accountkeeper.repository.model

data class TypeNameAndCountVo (
    var typeId: Int,
    var typeName: String,
    var isIncome: Int,
    var count: Int,
    var amount: Double,
)