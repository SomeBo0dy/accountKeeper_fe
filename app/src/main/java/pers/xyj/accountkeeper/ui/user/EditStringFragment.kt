package pers.xyj.accountkeeper.ui.user

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentEditStringBinding
import pers.xyj.accountkeeper.databinding.FragmentEditUserBinding

class EditStringFragment  : BaseFragment<FragmentEditStringBinding, EditStringViewModel>(
    FragmentEditStringBinding::inflate,
    EditStringViewModel::class.java,
    true
) {
    override fun initFragment(
        binding: FragmentEditStringBinding,
        viewModel: EditStringViewModel?,
        savedInstanceState: Bundle?
    ) {
        var bundle = arguments
        if (bundle != null){
            var editString = publicViewModel!!.publicEditStringType.value
            if (editString.equals("name")){
                binding.appName.text = "编辑用户名"
                viewModel!!.string.value = bundle.getString("name")
            }else if (editString.equals("introduction")){
                binding.appName.text = "编辑简介"
                viewModel!!.string.value = bundle.getString("introduction")
            }
        }
        binding.backButton.setOnClickListener {
            publicViewModel!!.publicEditStringType.value = ""
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.buttonConform.setOnClickListener {
            val string = binding.stringText.text.toString()
            publicViewModel!!.publicEditString.value = string
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.viewModel = viewModel
    }
}
