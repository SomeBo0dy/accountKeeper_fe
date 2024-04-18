package pers.xyj.accountkeeper.ui.record

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentAddRecordBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.network.api.TypeApi
import pers.xyj.accountkeeper.repository.entity.Type
import pers.xyj.accountkeeper.repository.model.AddRecordForm
import pers.xyj.accountkeeper.repository.model.EditRecordForm
import pers.xyj.accountkeeper.ui.type.adapter.TypeAdapter
import pers.xyj.accountkeeper.util.LogUtil


class AddRecordFragment : BaseFragment<FragmentAddRecordBinding, AddRecordViewModel>(
    FragmentAddRecordBinding::inflate,
    AddRecordViewModel::class.java,
    true
), TypeAdapter.OnItemClickListener {
    var isEdit: Boolean = false
    var typeList: ArrayList<Type> = ArrayList()
    var typeAdapter: TypeAdapter = TypeAdapter(typeList)
    var bookId: Int = 0
    var typeId: Int = 0
    var timeInMillis: Long = 0
    var recordId: Long = 0L

    override fun initFragment(
        binding: FragmentAddRecordBinding,
        viewModel: AddRecordViewModel?,
        savedInstanceState: Bundle?
    ) {
        val bundle = arguments
        if (bundle != null) {
            isEdit = bundle.getBoolean("isEdit")
            if (!isEdit) {
                bookId = bundle.getInt("bookId")
                timeInMillis = bundle.getLong("timeInMillis")
            } else {
                binding.deleteRecordButton.visibility = View.VISIBLE
                binding.appName.text = "编辑记录"
                recordId = bundle.getLong("recordId")
                viewModel!!.amount.value = bundle.getDouble("recordAmount").toString()
                typeId = bundle.getInt("typeId")
                viewModel!!.description.value = bundle.getString("description")
            }
        }
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.deleteRecordButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("确认删除")
            builder.setMessage("你确认要删除这条记录吗？删除后将无法找回")
            builder.setPositiveButton("确定") { dialog, which ->
                publicViewModel?.apply {
                    request(RecordApi::class.java).deleteRecord(recordId).getResponse {
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
        binding.amountEditText.setOnFocusChangeListener { view, hasFocus ->
            var text = binding.amountEditText.text
            if (hasFocus) {
                var amount = text.toString()
                var toDoubleOrNull = amount.toDoubleOrNull()
                if (toDoubleOrNull == 0.0) {
                    text.clear()
                }
            }
//            else{
//                var amount = text.toString()
//                if (amount == ""){
//                    text = Editable.Factory.getInstance().newEditable("0.0")
//                }
//            }
        }
        binding.clearText.setOnClickListener {
            binding.amountEditText.text.clear()
        }
        var layoutManger: FlexboxLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW)
        binding.typeRecyclerView.layoutManager = layoutManger
        binding.typeRecyclerView.adapter = typeAdapter
        typeAdapter.setOnItemClickListener(this)
        viewModel!!.apply {
            binding.viewModel = this
            binding.saveRecordButton.setOnClickListener {
                var toDoubleOrNull = binding.amountEditText.text.toString().toDoubleOrNull()
                if (toDoubleOrNull == null) {
                    Toast.makeText(requireContext(), "请输入支出金额", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    publicViewModel?.apply {
                        if (!isEdit) {
                            request(RecordApi::class.java).addRecord(
                                AddRecordForm(
                                    amount.value!!.toDouble(),
                                    getSelectedType(typeList),
                                    bookId,
                                    description.value!!,
                                    timeInMillis
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
                            request(RecordApi::class.java).editRecord(
                                EditRecordForm(
                                    recordId,
                                    amount.value!!.toDouble(),
                                    getSelectedType(typeList),
                                    description.value!!,
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
            }
        }
    }

    fun initTypeListFromDB() {
        typeList.removeAll(typeList)
        publicViewModel?.apply {
            request(TypeApi::class.java).getTypes().getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            var types = it.data?.data as ArrayList<Type>
                            spUtil.toBeanList(types, typeList)
//                            LogUtil.e(typeList.toString())
                            //将第一个类型设为默认类型
                            if (typeId == 0) {
                                typeList[0].isChecked = true
                            } else {
                                typeList[typeId - 1].isChecked = true
                            }
                            withContext(Dispatchers.Main) {
                                typeAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int) {

    }

    override fun onResume() {
        super.onResume()
        initTypeListFromDB()
    }

    fun getSelectedType(typeList: ArrayList<Type>): Int {
        for (item in typeList) {
            if (item.isChecked) {
                return item.id
            }
        }
        return 0
    }
}