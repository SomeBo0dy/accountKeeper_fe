package pers.xyj.accountkeeper.repository.model

import java.util.Date

data class SmartKeepRecordVo(
    var amount: Double,
    var shopName: String,
    var description: String,
    var createDate: Date,
)
