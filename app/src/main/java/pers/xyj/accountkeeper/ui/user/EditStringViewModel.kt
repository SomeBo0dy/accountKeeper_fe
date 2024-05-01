package pers.xyj.accountkeeper.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditStringViewModel:ViewModel() {
    val string by lazy {
        MutableLiveData("")
    }
}