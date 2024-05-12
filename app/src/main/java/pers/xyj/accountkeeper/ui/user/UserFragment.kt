package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
    var nickname: String = ""
    var introduction: String = ""
    var avatar: String = ""
    lateinit var nicknameText: TextView
    lateinit var introductionText: TextView
    lateinit var avatarImageView: ImageView
    override fun initFragment(
        binding: FragmentUserBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        nicknameText = binding.nicknameText
        introductionText = binding.introductionText
        avatarImageView = binding.avatar
        binding.editUserButton.setOnClickListener{
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_editUserFragment)
        }
        binding.editButton.setOnClickListener{
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_editUserFragment)
        }
        binding.joinBookButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_joinBookFragment)
        }
        binding.billReminderButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_mainNavigationFragment_to_billFragment)
        }
    }
    override fun onResume() {
        super.onResume()
        publicViewModel?.apply {
            var userInfo = spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
            nickname = userInfo.nickName
            introduction = userInfo.introduction
            avatar = userInfo.avatar
            nicknameText.setText(nickname)
            introductionText.setText(introduction)
            Picasso.get().load(avatar).into(avatarImageView)
        }
    }
}
