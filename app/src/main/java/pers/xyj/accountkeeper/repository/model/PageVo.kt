package pers.xyj.accountkeeper.repository.model

data class PageVo(
    var rows: List<Any>,
    var pages: Long,
    var totalNum: Long,
)