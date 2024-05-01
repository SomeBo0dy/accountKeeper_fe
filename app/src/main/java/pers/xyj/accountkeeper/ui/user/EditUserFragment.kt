package pers.xyj.accountkeeper.ui.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.Observable
import androidx.lifecycle.Observer
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
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.BookApi
import pers.xyj.accountkeeper.network.api.LoginApi
import pers.xyj.accountkeeper.network.api.RecordApi
import pers.xyj.accountkeeper.network.api.UserApi
import pers.xyj.accountkeeper.repository.model.EditBookForm
import pers.xyj.accountkeeper.repository.model.EditUserForm
import pers.xyj.accountkeeper.util.LogUtil

class EditUserFragment : BaseFragment<FragmentEditUserBinding, EditUserViewModel>(
    FragmentEditUserBinding::inflate,
    EditUserViewModel::class.java,
    true
) {
//    var nickname: String = ""
//    var introduction: String = ""
//    var avatar: String = ""
    val PERMISSION_REQUEST_CODE: Int = 3413
    val OPEN_GALLERY_REQUEST_CODE: Int = 330381
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                // 在这里处理选择的图像 URI，例如显示图像
            }
        }

    override fun initFragment(
        binding: FragmentEditUserBinding,
        viewModel: EditUserViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.viewModel = viewModel
        var bundle = arguments
        if (bundle != null) {
            viewModel!!.nickname.value = bundle.getString("nickname")
            viewModel!!.introduction.value = bundle.getString("introduction")
            viewModel!!.avatar.value = bundle.getString("avatar")
//            binding.nameText.setText(viewModel!!.nickname.value)
//            binding.introduction.setText(viewModel!!.introduction.value )
            Picasso.get().load(viewModel!!.avatar.value).into(binding.avatarImage)
        }
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.editAvatarButton.setOnClickListener {
            applyPermission()
        }
        binding.editNameButton.setOnClickListener {
            var nameBundle: Bundle = Bundle()
//            nameBundle.putString("editString", "name")
            publicViewModel!!.publicEditStringType.value = "name"
            nameBundle.putString("name", viewModel!!.nickname.value)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_editUserFragment_to_editStringFragment, nameBundle)
        }
        binding.editIntroductionButton.setOnClickListener {
            var introductionBundle: Bundle = Bundle()
//            introductionBundle.putString("editString", "introduction")
            publicViewModel!!.publicEditStringType.value = "introduction"
            introductionBundle.putString("introduction", viewModel!!.introduction.value)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_editUserFragment_to_editStringFragment, introductionBundle)
        }
        binding.saveUserButton.setOnClickListener {
            publicViewModel?.apply {
                request(UserApi::class.java).editUser(
                    EditUserForm(
                        //todo
                        viewModel!!.avatar.value.toString(), binding.nameText.text.toString(), binding.introduction.text.toString()
                    )
                ).getResponse {
                    it.collect {
                        when (it) {
                            is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                            ApiResponse.Loading -> LogUtil.e("Loading")
                            is ApiResponse.Success -> {
                                LogUtil.e("${it.data.toString()}")
                                withContext(Dispatchers.Main) {
                                    requireActivity().findNavController(R.id.app_navigation)
                                        .navigateUp()
                                }
                            }
                        }
                    }
                }
            }
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
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.mainNavigationFragment, true)
                                            .build()
                                        //TODO
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        publicViewModel!!.publicEditString.observe(requireActivity()){
            var publicEditStringType = publicViewModel!!.publicEditStringType.value
            if (publicEditStringType.equals("name")){
//                viewModel!!.nickname.value = it
                binding.nameText.text = it
            }else if (publicEditStringType.equals("introduction")){
//                viewModel!!.introduction.value = it
                binding.introduction.text = it
            }
        }

    }

    fun applyPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            openGallery()
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
//        requireActivity().startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同样授权
                openGallery();
            } else {
                // 用户拒绝授权
                Toast.makeText(requireContext(), "你拒绝使用存储权限！", Toast.LENGTH_SHORT).show();
                LogUtil.e("你拒绝使用存储权限！");
            }
        }
    }
}