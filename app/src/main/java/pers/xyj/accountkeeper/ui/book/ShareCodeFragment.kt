package pers.xyj.accountkeeper.ui.book

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentBookBinding
import pers.xyj.accountkeeper.databinding.FragmentShareCodeBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.network.api.ShareCodeApi
import pers.xyj.accountkeeper.repository.entity.ShareCode
import pers.xyj.accountkeeper.repository.model.PageVo
import pers.xyj.accountkeeper.util.LogUtil

class ShareCodeFragment : BaseFragment<FragmentShareCodeBinding, ViewModel>(
    FragmentShareCodeBinding::inflate,
    null,
    true
) {
    var bookId = 0
    var isOwner = false
    lateinit var shareCodeText: TextView
    lateinit var shareCountText: TextView
    override fun initFragment(
        binding: FragmentShareCodeBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        var bundle = arguments
        shareCodeText = binding.shareCodeText
        shareCountText = binding.shareCountText

        if (bundle != null) {
            isOwner = bundle.getBoolean("isOwner")
            bookId = bundle.getInt("bookId")
        }
        if (!isOwner) {
            binding.buttonQuitBook.visibility = View.VISIBLE
        }
        binding.buttonQuitBook.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("确认删除")
            builder.setMessage("你确认要删除这个账本吗？删除后将无法找回")
            builder.setPositiveButton("确定") { dialog, which ->
                publicViewModel?.apply {
                    request(ShareCodeApi::class.java).quitBook(bookId)
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
        binding.buttonCopy.setOnClickListener {
            copyToClipboard(requireActivity(), shareCodeText.text.toString())
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("shareCode", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        publicViewModel?.apply {
            request(ShareCodeApi::class.java).getShareCode(bookId).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            var shareCode: ShareCode = it.data?.data as ShareCode
                            var code = shareCode.shareCode
                            var count = shareCode.sharedCount

                            withContext(Dispatchers.Main) {
                                shareCodeText.text = code
                                shareCountText.text = count.toString()
                            }
                        }
                    }
                }
            }
        }
    }
}