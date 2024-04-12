package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentMainNavigationBinding
import pers.xyj.accountkeeper.databinding.FragmentUserBinding
import pers.xyj.accountkeeper.repository.model.UserInfo
import pers.xyj.accountkeeper.util.LogUtil

class UserFragment : BaseFragment<FragmentUserBinding, ViewModel>(
    FragmentUserBinding::inflate,
    null,
    true
) {
    override fun initFragment(
        binding: FragmentUserBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        publicViewModel?.apply {
            LogUtil.e((spUtil.getItem("token", "")) as String)
            var userInfo = spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
//            LogUtil.e(userInfo.toString())
//            LogUtil.e(userInfo.nickName)
//            LogUtil.e(userInfo.introduction)
//            LogUtil.e(userInfo.avatar)
            binding.nicknameText.setText(userInfo.nickName)
            binding.introductionText.setText(userInfo.introduction)
            Picasso.get().load(userInfo.avatar).into(binding.avatar)
        }
    }
}
