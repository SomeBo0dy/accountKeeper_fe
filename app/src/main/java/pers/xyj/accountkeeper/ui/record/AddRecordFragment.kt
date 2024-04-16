package pers.xyj.accountkeeper.ui.record

import android.os.Bundle
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
import pers.xyj.accountkeeper.repository.model.BookAndRecordVo
import pers.xyj.accountkeeper.ui.type.adapter.TypeAdapter
import pers.xyj.accountkeeper.util.LogUtil


class AddRecordFragment : BaseFragment<FragmentAddRecordBinding, AddRecordViewModel>(
    FragmentAddRecordBinding::inflate,
    AddRecordViewModel::class.java,
    true
), TypeAdapter.OnItemClickListener {
    var typeList: ArrayList<Type> = ArrayList()
    var typeAdapter: TypeAdapter = TypeAdapter(typeList)
    override fun initFragment(
        binding: FragmentAddRecordBinding,
        viewModel: AddRecordViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        var layoutManger: FlexboxLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW)
        binding.typeRecyclerView.layoutManager = layoutManger
        binding.typeRecyclerView.adapter = typeAdapter
        typeAdapter.setOnItemClickListener(this)
        viewModel!!.apply {
            binding.viewModel = this
            binding.postRecordButton.setOnClickListener {
//                if (name.value == "") {
//                    Toast.makeText(requireContext(), "账本名称不能为空", Toast.LENGTH_SHORT)
//                        .show()
//                } else if (description.value == "") {
//                    Toast.makeText(requireContext(), "账本描述不能为空", Toast.LENGTH_SHORT)
//                        .show()
//                } else {
//                    publicViewModel?.apply {
//                        request(BookApi::class.java).addBook(AddBookForm(name.value!!, description.value!!)).getResponse {
//                            it.collect {
//                                when (it) {
//                                    is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
//                                    ApiResponse.Loading -> LogUtil.e("Loading")
//                                    is ApiResponse.Success -> {
//                                        LogUtil.e("${it.data.toString()}")
//                                        withContext(Dispatchers.Main){
//                                            requireActivity().findNavController(R.id.app_navigation).navigateUp()
//                                        }
//                                    }
//                                }
//                            }
//                        }}
//                }
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
                            LogUtil.e(typeList.toString())
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
}