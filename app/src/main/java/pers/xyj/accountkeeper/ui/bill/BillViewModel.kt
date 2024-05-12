package pers.xyj.accountkeeper.ui.bill

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BillViewModel  : ViewModel() {
    val date by lazy {
        MutableLiveData("")
    }
}