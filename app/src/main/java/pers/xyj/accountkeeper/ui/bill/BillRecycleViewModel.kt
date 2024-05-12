package pers.xyj.accountkeeper.ui.bill

import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.repository.entity.Bill
import pers.xyj.accountkeeper.repository.entity.BookVo

class BillRecycleViewModel: ViewModel() {
    val data by lazy {
        ArrayList<Bill>()
    }
}