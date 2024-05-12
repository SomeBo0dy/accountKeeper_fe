package pers.xyj.accountkeeper.ui.bill

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.repository.entity.Bill

class AddBillViewModel : ViewModel() {
    val billName by lazy {
        MutableLiveData("")
    }
}