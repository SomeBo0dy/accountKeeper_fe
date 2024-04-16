package pers.xyj.accountkeeper.ui.book

import android.os.Bundle
import android.os.Looper
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
import pers.xyj.accountkeeper.repository.model.AddBookForm
import pers.xyj.accountkeeper.ui.login.USER_LOGIN_TYPE
import pers.xyj.accountkeeper.util.LogUtil

class AddBookFragment : BaseFragment<FragmentAddBookBinding, AddBookViewModel>(
    FragmentAddBookBinding::inflate,
    AddBookViewModel::class.java,
    true
) {
    override fun initFragment(
        binding: FragmentAddBookBinding,
        viewModel: AddBookViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        viewModel!!.apply {
            binding.viewModel = this
            binding.postBookButton.setOnClickListener {
                if (name.value == "") {
                    Toast.makeText(requireContext(), "账本名称不能为空", Toast.LENGTH_SHORT)
                        .show()
                } else if (description.value == "") {
                    Toast.makeText(requireContext(), "账本描述不能为空", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    publicViewModel?.apply {
                        request(BookApi::class.java).addBook(
                            AddBookForm(
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