package pers.xyj.accountkeeper.repository.entity

import java.io.Serializable
import java.util.Date

data class BookVo(
    var id: Long,

    var name: String,

    var description: String,

    var priority: Int,

    var createBy: Long,

    var createTime: Date,

    var memberCount: Int,

    var amount: Double,
) : Serializable