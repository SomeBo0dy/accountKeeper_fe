package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import pers.xyj.accountkeeper.R
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
            binding.nicknameText.setText(userInfo.nickName)
            binding.introductionText.setText(userInfo.introduction)
            Picasso.get().load(userInfo.avatar).into(binding.avatar)
            binding.editUserButton.setOnClickListener{
                requireActivity().findNavController(R.id.app_navigation)
                    .navigate(R.id.action_mainNavigationFragment_to_editUserFragment)
            }
            binding.editButton.setOnClickListener{
                requireActivity().findNavController(R.id.app_navigation)
                    .navigate(R.id.action_mainNavigationFragment_to_editUserFragment)
            }
        }
    }
}
