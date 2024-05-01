package pers.xyj.accountkeeper.repository.model

data class BookStatisticsVo(
    var recordCountTotal: Int,
    var recordAmountTotal: Double,
    var typeList: ArrayList<TypeNameAndCountVo>,
)