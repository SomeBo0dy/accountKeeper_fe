package pers.xyj.accountkeeper.ui.book

import android.app.AlertDialog
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentAddBookBinding
import pers.xyj.accountkeeper.databinding.FragmentBookBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.network.api.LoginApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.repository.model.AddBookForm
import pers.xyj.accountkeeper.repository.model.EditBookForm
import pers.xyj.accountkeeper.ui.login.USER_LOGIN_TYPE
import pers.xyj.accountkeeper.util.LogUtil

class AddBookFragment : BaseFragment<FragmentAddBookBinding, AddBookViewModel>(
    FragmentAddBookBinding::inflate,
    AddBookViewModel::class.java,
    true
) {
    var isEdit: Boolean = false
    var bookId: Int = 0
    var bookCount: Int = 0
    override fun initFragment(
        binding: FragmentAddBookBinding,
        viewModel: AddBookViewModel?,
        savedInstanceState: Bundle?
    ) {
        val bundle = arguments
        if (bundle != null) {
            isEdit = bundle.getBoolean("isEdit")
            if (isEdit) {
                binding.appName.text = "编辑账本"
                binding.deleteBookButton.visibility = View.VISIBLE
                bookId = bundle.getInt("bookId")
                viewModel!!.name.value = bundle.getString("bookName")
                viewModel!!.description.value = bundle.getString("bookDescription")
            } else {
                bookCount = bundle.getInt("bookCount")
            }
        }
        binding.deleteBookButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("确认删除")
            builder.setMessage("你确认要删除这个账本吗？删除后将无法找回")
            builder.setPositiveButton("确定") { dialog, which ->
                publicViewModel?.apply {
                    request(BookApi::class.java).deleteBook(bookId)
                        .getResponse {
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
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        viewModel!!.apply {
            binding.viewModel = this
            binding.saveBookButton.setOnClickListener {
                if (name.value == "") {
                    Toast.makeText(requireContext(), "账本名称不能为空", Toast.LENGTH_SHORT)
                        .show()
                } else if (description.value == "") {
                    Toast.makeText(requireContext(), "账本描述不能为空", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    publicViewModel?.apply {
                        if (!isEdit) {
                            request(BookApi::class.java).addBook(
                                AddBookForm(
                                    name.value!!,
                                    description.value!!,
                                    bookCount + 1
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
                            request(BookApi::class.java).editBook(
                                EditBookForm(
                                    bookId,
                                    name.value!!,
                                    description.value!!
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

}