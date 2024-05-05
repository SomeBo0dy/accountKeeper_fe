package pers.xyj.accountkeeper.repository.model

import pers.xyj.accountkeeper.repository.entity.BookVo

data class BookAndRecordVo(
    var bId: Int,
    var name: String,
    var createBy: Long,
    var description: String,
    var recordPage: PageVo,

    )