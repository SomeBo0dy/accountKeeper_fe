package pers.xyj.accountkeeper.repository.model

data class BookStatisticsVo(
    var incomeCount: Int,
    var outcomeCount: Int,
    var incomeAmountSum: Double,
    var outcomeAmountSum: Double,
    var incomeStatistics: ArrayList<TypeNameAndCountVo>,
    var outcomeStatistics: ArrayList<TypeNameAndCountVo>,
)