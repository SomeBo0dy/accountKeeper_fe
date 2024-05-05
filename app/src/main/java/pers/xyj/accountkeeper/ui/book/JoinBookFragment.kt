package pers.xyj.accountkeeper.ui.book

import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentJoinBookBinding
import pers.xyj.accountkeeper.databinding.FragmentShareCodeBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.ShareCodeApi
import pers.xyj.accountkeeper.repository.entity.ShareCode
import pers.xyj.accountkeeper.repository.model.CodeForm
import pers.xyj.accountkeeper.util.LogUtil

class JoinBookFragment : BaseFragment<FragmentJoinBookBinding, ViewModel>(
    FragmentJoinBookBinding::inflate,
    null,
    true
) {
    lateinit var codeTextView: TextView
    override fun initFragment(
        binding: FragmentJoinBookBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        codeTextView = binding.joinCodeText
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.buttonJoin.setOnClickListener {
            var code: String = codeTextView.text.toString()
            if (code == "") {
                Toast.makeText(requireContext(), "邀请码不得为空", Toast.LENGTH_SHORT)
                    .show()
            } else {
                publicViewModel?.apply {
                    request(ShareCodeApi::class.java).joinBook(CodeForm(code)).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    var message: String = it.data?.data as String
                                    Looper.prepare()
                                    Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Looper.loop()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}