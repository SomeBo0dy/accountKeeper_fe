package pers.xyj.accountkeeper.ui.book

import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.repository.entity.BookVo

class BookRecycleViewModel : ViewModel() {
    val data by lazy {
        ArrayList<BookVo>()
    }
}