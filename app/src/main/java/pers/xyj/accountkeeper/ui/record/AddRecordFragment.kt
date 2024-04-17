package pers.xyj.accountkeeper.ui.record

import android.os.Bundle
import android.text.Editable
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
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.network.api.TypeApi
import pers.xyj.accountkeeper.repository.entity.Type
import pers.xyj.accountkeeper.repository.model.AddBookForm
import pers.xyj.accountkeeper.repository.model.AddRecordForm
import pers.xyj.accountkeeper.repository.model.BookAndRecordVo
import pers.xyj.accountkeeper.ui.type.adapter.TypeAdapter
import pers.xyj.accountkeeper.util.LogUtil
import java.util.Date


class AddRecordFragment : BaseFragment<FragmentAddRecordBinding, AddRecordViewModel>(
    FragmentAddRecordBinding::inflate,
    AddRecordViewModel::class.java,
    true
), TypeAdapter.OnItemClickListener {
    var typeList: ArrayList<Type> = ArrayList()
    var typeAdapter: TypeAdapter = TypeAdapter(typeList)
    var bookId: Int = 0
    var timeInMillis: Long = 0
    override fun initFragment(
        binding: FragmentAddRecordBinding,
        viewModel: AddRecordViewModel?,
        savedInstanceState: Bundle?
    ) {
        val bundle = arguments
        if (bundle != null){
            bookId = bundle.getInt("bookId")
            timeInMillis = bundle.getLong("timeInMillis")
        }
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.amountEditText.setOnFocusChangeListener { view, hasFocus ->
            var text = binding.amountEditText.text
            
            if (hasFocus){
                var amount = text.toString()
                if (amount.equals("0.0")){
                    text.clear()
                }
            }
//            else{
//                var amount = text.toString()
//                if (amount.equals("")){
//                    text.set
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
                if (binding.amountEditText.text.toString() == "") {
                    Toast.makeText(requireContext(), "请输入支出金额", Toast.LENGTH_SHORT)
                        .show()
                }else{
                    publicViewModel?.apply {
                        request(RecordApi::class.java).addRecord(
                            AddRecordForm(
                                amount,
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
                            typeList[0].isChecked = true
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
    fun getSelectedType(typeList: ArrayList<Type>):Int{
        for (item in typeList){
            if (item.isChecked){
                return item.id
            }
        }
        return 0
    }
}