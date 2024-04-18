package pers.xyj.accountkeeper.ui.record

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.repository.entity.Record
import java.util.Date

class AddRecordViewModel : ViewModel() {
    val amount by lazy {
        MutableLiveData("0.0")
    }
    val typeId by lazy {
        MutableLiveData(1)
    }
    val description by lazy {
        MutableLiveData("")
    }
//    var dataBindingVariable =
//        object: ObservableField<String>(amount.value.toString()) {
//            override fun get(): String? {
//                return super.get()
//            }
//            override fun set(value: String?) {
//                super.set(value)
//                amount.value = (value?.toDoubleOrNull() ?: amount.value)
//            }
//        }
}