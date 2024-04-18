package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentUserBinding

class EditUserFragment  : BaseFragment<FragmentUserBinding, ViewModel>(
    FragmentUserBinding::inflate,
    null,
    true
) {
    override fun initFragment(
        binding: FragmentUserBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {


    }
}