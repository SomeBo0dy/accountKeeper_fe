package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentEditUserBinding
import pers.xyj.accountkeeper.databinding.FragmentUserBinding

class EditUserFragment  : BaseFragment<FragmentEditUserBinding, ViewModel>(
    FragmentEditUserBinding::inflate,
    null,
    true
) {
    override fun initFragment(
        binding: FragmentEditUserBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }

    }
}