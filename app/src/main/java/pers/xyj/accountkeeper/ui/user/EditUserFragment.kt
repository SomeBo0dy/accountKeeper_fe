package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentEditUserBinding
import pers.xyj.accountkeeper.databinding.FragmentUserBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.LoginApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.util.LogUtil

class EditUserFragment  : BaseFragment<FragmentEditUserBinding, ViewModel>(
    FragmentEditUserBinding::inflate,
    null,
    true
) {
    var nickname: String = ""
    var introduction: String = ""
    var avatar: String = ""
    override fun initFragment(
        binding: FragmentEditUserBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.logoutButton.setOnClickListener {
            publicViewModel?.apply {
                request(LoginApi::class.java).logout().getResponse {
                    it.collect {
                        when (it) {
                            is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                            ApiResponse.Loading -> LogUtil.e("Loading")
                            is ApiResponse.Success -> {
                                LogUtil.e("${it.data.toString()}")
                                spUtil!!.removeItem("token")
                                withContext(Dispatchers.Main) {
                                    findNavController().navigate(
                                        R.id.loginFragment,
                                        null,
                                        NavOptions.Builder().setPopUpTo(R.id.mainNavigationFragment, true)
                                            .build()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        var bundle = arguments
        if (bundle != null){
            nickname = bundle.getString("nickname").toString()
            introduction = bundle.getString("introduction").toString()
            avatar = bundle.getString("avatar").toString()
            binding.nameText.setText(nickname)
            binding.introduction.setText(introduction)
            Picasso.get().load(avatar).into(binding.avatarImage)
        }

    }
}