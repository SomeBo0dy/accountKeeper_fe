package pers.xyj.accountkeeper.ui.book

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class AddBookViewModel : ViewModel() {

    val name by lazy {
        MutableLiveData("")
    }

    val description by lazy {
        MutableLiveData("")
    }

}