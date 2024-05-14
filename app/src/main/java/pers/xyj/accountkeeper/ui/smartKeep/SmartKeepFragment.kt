package pers.xyj.accountkeeper.ui.smartKeep

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
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentSmartKeepBinding
import pers.xyj.accountkeeper.databinding.FragmentUserBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.SmartKeepApi
import pers.xyj.accountkeeper.network.api.UserApi
import pers.xyj.accountkeeper.repository.model.SmartKeepRecordVo
import pers.xyj.accountkeeper.repository.model.SmartKeepVo
import pers.xyj.accountkeeper.repository.model.UserInfo
import pers.xyj.accountkeeper.util.LogUtil
import java.io.File

class SmartKeepFragment : BaseFragment<FragmentSmartKeepBinding, ViewModel>(
    FragmentSmartKeepBinding::inflate,
    null,
    true
) {
    val PERMISSION_REQUEST_CODE: Int = 3415
    val OPEN_GALLERY_REQUEST_CODE: Int = 330382
    lateinit var photoImageView: ImageView
    lateinit var selectedImage: Uri
    lateinit var imageFile: File
    lateinit var requestFile: RequestBody
    lateinit var imageBody: MultipartBody.Part
    lateinit var recordList: ArrayList<SmartKeepRecordVo>
    var count: Int = -1;
    var isChanged: Boolean = false;
    override fun initFragment(
        binding: FragmentSmartKeepBinding,
        viewModel: ViewModel?,
        savedInstanceState: Bundle?
    ) {
        photoImageView = binding.photoImage
        binding.backButton.setOnClickListener {
            requireActivity().findNavController(R.id.app_navigation).navigateUp()
        }
        binding.photoImage.setOnClickListener {
            applyPermission()
            openGallery()
        }
        binding.buttonSmartKeep.setOnClickListener {
            if (!isChanged) {
                Toast.makeText(requireContext(), "请上传小票图片", Toast.LENGTH_SHORT)
                    .show()
            } else {
                publicViewModel?.apply {
                    request(SmartKeepApi::class.java).smartKeepByShoppingReceipt(imageBody)
                        .getResponse {
                            it.collect {
                                when (it) {
                                    is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                                    ApiResponse.Loading -> LogUtil.e("Loading")
                                    is ApiResponse.Success -> {
                                        LogUtil.e("${it.data.toString()}")
                                        var smartKeepVo: SmartKeepVo = it.data?.data as SmartKeepVo
                                        count = smartKeepVo.count
                                        recordList = smartKeepVo.recordList
                                        var bundle: Bundle = Bundle()
                                        bundle.putInt("recordCount", count)
                                        bundle.putSerializable("recordList", recordList)
                                        withContext(Dispatchers.Main){
                                            requireActivity().findNavController(R.id.app_navigation)
                                                .navigate(
                                                    R.id.action_smartKeepFragment_to_smartKeepResultFragment,
                                                    bundle
                                                )
                                        }
                                    }
                                }
                            }
                        }
                }
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
            photoImageView.setImageURI(selectedImage)
            imageFile = uriToFile(requireActivity(), selectedImage)
            requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData(
                "receipt", imageFile.name,
                requestFile!!
            )
            isChanged = true
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