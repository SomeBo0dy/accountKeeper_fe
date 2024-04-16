package pers.xyj.accountkeeper.ui.bill

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentBillBinding
import pers.xyj.accountkeeper.databinding.FragmentBookBinding

class BillFragment : BaseFragment<FragmentBillBinding, ViewModel>(
    FragmentBillBinding::inflate,
    null
) {
    override fun initFragment(
        binding: FragmentBillBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {

    }
}