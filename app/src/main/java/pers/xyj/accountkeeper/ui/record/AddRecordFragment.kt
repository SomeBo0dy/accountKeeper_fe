package pers.xyj.accountkeeper.ui.record

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
    var isSmartAdded: Boolean = false
    val typeList: ArrayList<Type> = ArrayList()
    val incomeTypeList: ArrayList<Type> = ArrayList()
    val outcomeTypeList: ArrayList<Type> = ArrayList()
    val typeAdapter: TypeAdapter = TypeAdapter(typeList)

    var bookId: Int = 0
    val typeId by lazy {
        MutableLiveData(0)
    }
    var timeInMillis: Long = 0
    var recordId: Long = 0L

    lateinit var incomeCheckBox: CheckBox
    lateinit var outcomeCheckBox: CheckBox

    lateinit var amountEditText: TextView
    lateinit var appName: TextView
    lateinit var deleteRecordButton: Button
    val isIncome by lazy {
        MutableLiveData(false)
    }
    lateinit var bundle: Bundle
    override fun initFragment(
        binding: FragmentAddRecordBinding,
        viewModel: AddRecordViewModel?,
        savedInstanceState: Bundle?
    ) {
        initTypeListFromDB()
        bundle = requireArguments()
        appName = binding.appName
        amountEditText = binding.amountEditText
        deleteRecordButton = binding.deleteRecordButton
        incomeCheckBox = binding.incomeCheckBox
        outcomeCheckBox = binding.outcomeCheckBox
        incomeCheckBox.setOnClickListener {
            isIncome.value = true
            typeAdapter.notifyDataSetChanged()

        }
        outcomeCheckBox.setOnClickListener {
            isIncome.value = false
            typeAdapter.notifyDataSetChanged()

        }
        isIncome.observe(requireActivity()) {
            if (isIncome.value!!) {
                incomeCheckBox.isChecked = true
                outcomeCheckBox.isChecked = false
                binding.typeTextLabel.text = "收入金额"
                binding.typeCheckBoxLabel.text = "收入类型"
                binding.descLabel.text = "收入描述"
                binding.descInput.hint = "描述这笔收入..."
                typeList.clear()
                typeList.addAll(incomeTypeList)
            } else {
                outcomeCheckBox.isChecked = true
                incomeCheckBox.isChecked = false
                binding.typeTextLabel.text = "支出金额"
                binding.typeCheckBoxLabel.text = "支出类型"
                binding.descLabel.text = "支出描述"
                binding.descInput.hint = "描述这笔支出..."
                typeList.clear()
                typeList.addAll(outcomeTypeList)
            }
            var flag = true
            if (typeList.size != 0) {
                for (item in typeList){
                    if (item.isChecked){
                        flag = false
                        break
                    }
                }
                if (flag){
                    typeId.value = typeList[0].id
                }
            }
            typeAdapter.notifyDataSetChanged()
        }
        typeId.observe(requireActivity()){
            for (item: Type in typeList) {
                if (item.id == typeId.value) {
                    item.isChecked = true
                }else{
                    item.isChecked = false
                }
            }
            Handler(Looper.getMainLooper()).post{
                typeAdapter.notifyDataSetChanged()
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
                    Toast.makeText(requireContext(), "请输入金额", Toast.LENGTH_SHORT)
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
                                                if (!isSmartAdded) {
                                                    requireActivity().findNavController(R.id.app_navigation)
                                                        .navigateUp()
                                                } else {
                                                    findNavController().popBackStack(
                                                        R.id.smartKeepResultFragment, false
                                                    )
                                                }
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
        if (bundle != null) {
            var isSmart = bundle.getBoolean("isSmartAdded")
            if (isSmart) {
                isIncome.value = false
                isSmartAdded = bundle.getBoolean("isSmartAdded")
                bookId = bundle.getInt("bookId")
                timeInMillis = bundle.getLong("timeInMillis")
                appName.text = "编辑识别记录"
                viewModel!!.amount.value = bundle.getDouble("amount").toString()
                viewModel!!.description.value = bundle.getString("description")
            } else {
                isEdit = bundle.getBoolean("isEdit")
                if (!isEdit) {
                    bookId = bundle.getInt("bookId")
                    timeInMillis = bundle.getLong("timeInMillis")
                    isIncome.value = false
                } else {
                    recordId = bundle.getLong("recordId")
                    typeId.value = bundle.getInt("typeId")
                    var income = bundle.getInt("isIncome")
                    if (income == 1) {
                        isIncome.value = true
                    } else {
                        isIncome.value = false
                    }
                    deleteRecordButton.visibility = View.VISIBLE
                    appName.text = "编辑记录"
                    viewModel!!.amount.value = bundle.getDouble("recordAmount").toString()
                    viewModel!!.description.value = bundle.getString("description")
                }
            }
        }
    }

    fun initTypeListFromDB() {
        typeList.clear()
        publicViewModel?.apply {
            request(TypeApi::class.java).getTypes().getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            var types = it.data?.data as ArrayList<Type>
                            spUtil.toBeanList(types, typeList)
                            classifyTypeList(typeList)
                            withContext(Dispatchers.Main) {
                                if (bundle.getInt("isIncome") == 1){
                                    isIncome.value = true
                                }else{
                                    isIncome.value = false
                                }
                                typeAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    fun classifyTypeList(totalTypeList: ArrayList<Type>) {
        for (item: Type in totalTypeList) {
            if (item.id == typeId.value) {
                item.isChecked = true
            }else{
                item.isChecked = false
            }
            if (item.isIncome == 1) {
                incomeTypeList.add(item)
            } else {
                outcomeTypeList.add(item)
            }
        }
        if (typeId.value == 0) {
            outcomeTypeList[0].isChecked = true
        }
    }

    override fun onItemClick(position: Int) {
        typeAdapter.notifyDataSetChanged()
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