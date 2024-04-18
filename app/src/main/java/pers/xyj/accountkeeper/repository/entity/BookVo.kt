package pers.xyj.accountkeeper.repository.entity

import java.io.Serializable
import java.util.Date

data class BookVo(
    var id: Int,

    var name: String,

    var description: String,

    var priority: Int,

    var createBy: Long,

    var createTime: Date,

    var memberCount: Int,

    var amount: Double,
) : Serializable