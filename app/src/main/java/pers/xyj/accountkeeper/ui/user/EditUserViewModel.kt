package pers.xyj.accountkeeper.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditUserViewModel: ViewModel() {
    val avatar by lazy {
        MutableLiveData("")
    }
    val nickname by lazy {
        MutableLiveData("")
    }
    val introduction by lazy {
        MutableLiveData("111")
    }
}