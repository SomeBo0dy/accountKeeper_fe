package pers.xyj.accountkeeper.ui.book

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentBookBinding
import pers.xyj.accountkeeper.databinding.FragmentUserBinding

class BookFragment : BaseFragment<FragmentBookBinding, ViewModel>(
    FragmentBookBinding::inflate,
    null
) {
    override fun initFragment(
        binding: FragmentBookBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {

    }
}
