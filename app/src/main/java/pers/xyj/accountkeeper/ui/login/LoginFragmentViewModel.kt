package pers.xyj.accountkeeper.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LoginFragmentViewModel : ViewModel() {
    val phone by lazy {
        MutableLiveData("")
    }

    val code by lazy {
        MutableLiveData("")
    }
}