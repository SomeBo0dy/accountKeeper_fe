package pers.xyj.accountkeeper.ui.book

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.repository.entity.BookVo

class BookViewModel : ViewModel() {
    val date by lazy {
        MutableLiveData("")
    }
    val bookId by lazy {
        MutableLiveData(0)
    }
}