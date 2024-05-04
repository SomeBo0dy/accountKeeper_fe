package pers.xyj.accountkeeper.ui.user

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentEditUserBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.LoginApi
import pers.xyj.accountkeeper.network.api.UserApi
import pers.xyj.accountkeeper.repository.model.BookAndRecordVo
import pers.xyj.accountkeeper.repository.model.EditUserForm
import pers.xyj.accountkeeper.repository.model.UserInfo
import pers.xyj.accountkeeper.util.LogUtil
import java.io.File

class EditUserFragment : BaseFragment<FragmentEditUserBinding, EditUserViewModel>(
    FragmentEditUserBinding::inflate,
    EditUserViewModel::class.java,
    true
) {
    var avatarChanged: Boolean = false
    val PERMISSION_REQUEST_CODE: Int = 3413
    val OPEN_GALLERY_REQUEST_CODE: Int = 330381
    lateinit var avatarImageView: ImageView
    lateinit var selectedImage: Uri
    lateinit var imageFile: File
    lateinit var requestFile: RequestBody
    lateinit var imageBody: MultipartBody.Part
    override fun initFragment(
        binding: FragmentEditUserBinding,
        viewModel: EditUserViewModel?,
        savedInstanceState: Bundle?
    ) {
        binding.viewModel = viewModel
        avatarImageView = binding.avatarImage
        var userInfo = publicViewModel!!.spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
        viewModel!!.nickname.value = userInfo.nickName
        viewModel!!.introduction.value = userInfo.introduction
        viewModel!!.avatar.value = userInfo.avatar
        Picasso.get().load(viewModel!!.avatar.value).into(binding.avatarImage)
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.editAvatarButton.setOnClickListener {
            applyPermission()
            openGallery()
        }
        binding.editNameButton.setOnClickListener {
            var nameBundle: Bundle = Bundle()
            publicViewModel!!.publicEditStringType.value = "name"
            nameBundle.putString("name", viewModel!!.nickname.value)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_editUserFragment_to_editStringFragment, nameBundle)
        }
        binding.editIntroductionButton.setOnClickListener {
            var introductionBundle: Bundle = Bundle()
            publicViewModel!!.publicEditStringType.value = "introduction"
            introductionBundle.putString("introduction", viewModel!!.introduction.value)
            requireActivity().findNavController(R.id.app_navigation)
                .navigate(R.id.action_editUserFragment_to_editStringFragment, introductionBundle)
        }
        binding.saveUserButton.setOnClickListener {
            publicViewModel?.apply {
                if (avatarChanged){
                    imageFile = uriToFile(requireActivity(), selectedImage)
                    requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    imageBody = MultipartBody.Part.createFormData("avatarFile", imageFile.name,
                        requestFile!!
                    )
                }
                val nickName = binding.nameText.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val introduction = binding.introduction.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                if (avatarChanged){
                    request(UserApi::class.java).editUserInfo(
                        imageBody,
                        nickName,
                        introduction
                    ).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    LogUtil.e("${it.data.toString()}")
                                    var newUserInfo: UserInfo = it.data?.data as UserInfo
                                    LogUtil.e(newUserInfo.toString())
                                    var userInfo = spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
                                    userInfo.nickName = newUserInfo.nickName
                                    userInfo.introduction = newUserInfo.introduction
                                    userInfo.avatar = newUserInfo.avatar
                                    spUtil.setItem("userInfo", userInfo)
                                    Looper.prepare()
                                    Toast.makeText(
                                        requireContext(),
                                        "保存成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Looper.loop()
                                }
                            }
                        }
                    }
                }else{
                    request(UserApi::class.java).editUserInfoString(
                        EditUserForm(binding.nameText.text.toString(),
                            binding.introduction.text.toString())
                    ).getResponse {
                        it.collect {
                            when (it) {
                                is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                ApiResponse.Loading -> LogUtil.e("Loading")
                                is ApiResponse.Success -> {
                                    LogUtil.e("${it.data.toString()}")
                                    var newUserInfo: UserInfo = it.data?.data as UserInfo
                                    LogUtil.e(newUserInfo.toString())
                                    var userInfo = spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
                                    userInfo.nickName = newUserInfo.nickName
                                    userInfo.introduction = newUserInfo.introduction
                                    userInfo.avatar = newUserInfo.avatar
                                    spUtil.setItem("userInfo", userInfo)
                                    Looper.prepare()
                                    Toast.makeText(
                                        requireContext(),
                                        "保存成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Looper.loop()
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
                                spUtil!!.removeItem("accessToken")
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

        publicViewModel!!.publicEditString.observe(requireActivity()) {
            var publicEditStringType = publicViewModel!!.publicEditStringType.value
            if (publicEditStringType.equals("name")) {
//                viewModel!!.nickname.value = it
                binding.nameText.text = it
            } else if (publicEditStringType.equals("introduction")) {
//                viewModel!!.introduction.value = it
                binding.introduction.text = it
            }
        }

    }

    fun applyPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data!!
            avatarImageView.setImageURI(selectedImage)
            avatarChanged = true
        }
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return File(it.getString(columnIndex))
            }
        }
        return File("")
    }
}