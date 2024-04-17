package pers.xyj.accountkeeper.repository.model

import java.util.Date

data class AddRecordForm (
    var amount: Double,
    var tId: Int,
    var bId: Int,
    var description: String,
    var timeInMillis: Long,
)