package pers.xyj.accountkeeper.repository.entity

import java.util.Date

data class Bill (
    var id: Int,
    var billName: String,
    var reminderTime: Date,
    var createBy: Long,
)