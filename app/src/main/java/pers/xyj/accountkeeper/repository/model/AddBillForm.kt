package pers.xyj.accountkeeper.repository.model

import java.util.Date

data class AddBillForm (
    var billName:String,
    var reminderTime:Date
)