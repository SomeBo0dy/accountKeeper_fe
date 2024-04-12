package pers.xyj.accountkeeper.ui.mainnavigation

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentMainNavigationBinding

class MainNavigationFragment : BaseFragment<FragmentMainNavigationBinding, ViewModel>(
    FragmentMainNavigationBinding::inflate,
    null
) {
    override fun initFragment(
        binding: FragmentMainNavigationBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        (childFragmentManager.findFragmentById(R.id.main_view_nav) as NavHostFragment).apply {
            binding.bottomNav.setupWithNavController(this.navController)
        }
    }
}