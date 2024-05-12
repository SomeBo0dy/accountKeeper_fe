package pers.xyj.accountkeeper.ui.bill

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentAddBillBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BillApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.repository.model.AddBillForm
import pers.xyj.accountkeeper.repository.model.AddRecordForm
import pers.xyj.accountkeeper.repository.model.EditBillForm
import pers.xyj.accountkeeper.repository.model.EditRecordForm
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat

class AddBillFragment  : BaseFragment<FragmentAddBillBinding, AddBillViewModel>(
    FragmentAddBillBinding::inflate,
    AddBillViewModel::class.java,
    true
){
    lateinit var timePicker: TimePicker
    var isEdit: Boolean = false
    var billId: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    lateinit var date: String
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun initFragment(
        binding: FragmentAddBillBinding,
        viewModel: AddBillViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.viewModel = viewModel
        timePicker = binding.billTime
        timePicker.setIs24HourView(true)
        var bundle = arguments
        if (bundle != null){
            isEdit = bundle.getBoolean("isEdit")
            date = bundle.getString("date")!!
            if (isEdit) {
                binding.deleteBillButton.visibility = View.VISIBLE
                binding.appName.text = "编辑账单提醒"
                billId = bundle.getInt("billId")
                hour = bundle.getInt("hour")
                minute = bundle.getInt("minute")
                viewModel!!.billName.value = bundle.getString("billName")
                timePicker.hour = hour
                timePicker.minute = minute
            }
        }
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.saveBillButton.setOnClickListener {
            publicViewModel?.apply {
                if (!isEdit) {
                    request(BillApi::class.java).addBill(
                        AddBillForm(
                            viewModel!!.billName.value!!,
                            format.parse(date + " " + timePicker.hour + ":" + timePicker.minute + ":00")
                            )
                    ).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    LogUtil.e("${it.data.toString()}")
                                    withContext(Dispatchers.Main) {
                                        requireActivity().findNavController(R.id.app_navigation)
                                            .navigateUp()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    request(BillApi::class.java).editBill(
                        EditBillForm(
                            billId,
                            viewModel!!.billName.value!!,
                            format.parse(date + " " + timePicker.hour + ":" + timePicker.minute + ":00")
                        )
                    ).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    LogUtil.e("${it.data.toString()}")
                                    withContext(Dispatchers.Main) {
                                        requireActivity().findNavController(R.id.app_navigation)
                                            .navigateUp()
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        binding.deleteBillButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("确认删除")
            builder.setMessage("你确认要删除这条账单吗？删除后将无法找回")
            builder.setPositiveButton("确定") { dialog, which ->
                publicViewModel?.apply {
                    request(BillApi::class.java).deleteBill(billId).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    dialog.dismiss()
                                    LogUtil.e("${it.data.toString()}")
                                    withContext(Dispatchers.Main) {
                                        requireActivity().findNavController(R.id.app_navigation)
                                            .navigateUp()
                                    }
                                }
                            }
                        }
                    }
                }

            }
            builder.setNegativeButton("取消") { dialog, which ->
                dialog.dismiss() // 关闭对话框
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

}