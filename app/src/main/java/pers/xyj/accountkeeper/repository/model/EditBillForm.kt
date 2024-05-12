package pers.xyj.accountkeeper.repository.model

import java.util.Date

data class EditBillForm (
    var id: Int,
    var billName: String,
    var reminderTime: Date
)