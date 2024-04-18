package pers.xyj.accountkeeper.repository.model

data class EditRecordForm (
    var id: Long,
    var amount: Double,
    var tId: Int,
    var description: String,
)