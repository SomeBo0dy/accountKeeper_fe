package pers.xyj.accountkeeper.repository.model

data class AddBookForm(
    var name: String,
    var description: String,
    var priority: Int,
)