package pers.xyj.accountkeeper.ui.login


import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.base.BaseFragment
import pers.xyj.accountkeeper.databinding.FragmentLoginBinding
import pers.xyj.accountkeeper.network.ApiResponse
import pers.xyj.accountkeeper.network.api.LoginApi
import pers.xyj.accountkeeper.repository.model.LoginFoem
import pers.xyj.accountkeeper.repository.model.LoginUserInfo
import pers.xyj.accountkeeper.util.LogUtil
import java.util.regex.Pattern


const val USER_LOGIN_TYPE = "0"

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginFragmentViewModel>(
    FragmentLoginBinding::inflate,
    LoginFragmentViewModel::class.java,
    true
) {

    override fun initFragment(
        binding: FragmentLoginBinding,
        viewModel: LoginFragmentViewModel?,
        savedInstanceState: Bundle?
    ) {
        viewModel!!.apply {
//            publicViewModel?.spUtil!!.removeItem("token")
            var token = publicViewModel?.spUtil!!.getItem("token", "") as String
            if (token != "") {
                findNavController().navigate(
                    R.id.mainNavigationFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                )
            }
            binding.viewModel = this
            binding.buttonLogin.setOnClickListener {
                if (checkBox(phone.value, code.value)) {
                    loginRequest(phone.value!!, code.value!!)
                }
            }
            binding.buttonCode.setOnClickListener {
                if (phone.value == "") {
                    Toast.makeText(requireContext(), "手机号不能为空", Toast.LENGTH_SHORT)
                        .show()
                } else if (!regexPhone(phone.value!!)) {
                    Toast.makeText(requireContext(), "请输入合法手机号", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    sendCode(phone.value!!)
                }
            }
        }

    }

    private fun checkBox(phone: String?, code: String?): Boolean {
        if (phone == "" || code == "") {
            Toast.makeText(requireContext(), "手机号或验证码不能为空", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (!regexPhone(phone!!)) {
            Toast.makeText(requireContext(), "请输入合法手机号", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true;
    }

    private fun sendCode(phone: String) {
        publicViewModel?.apply {
            request(LoginApi::class.java).getPhoneLoginCode(phone, USER_LOGIN_TYPE).getResponse {
                it.collect {
                    when (it) {
                        is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                        ApiResponse.Loading -> LogUtil.e("Loading")
                        is ApiResponse.Success -> {
                            LogUtil.e("${it.data.toString()}")

                            Looper.prepare();
                            Toast.makeText(
                                requireContext(),
                                "验证码发送成功",
                                Toast.LENGTH_SHORT
                            ).show()
                            Looper.loop();
                        }
                    }
                }
            }
        }
    }

    private fun loginRequest(phone: String, code: String) {
        publicViewModel?.apply {
            request(LoginApi::class.java).login(LoginFoem(phone, code, USER_LOGIN_TYPE))
                .getResponse {
                    it.collect {
                        when (it) {
                            is ApiResponse.Error -> LogUtil.e("${it.errMsg} ${it.errMsg}")
                            ApiResponse.Loading -> LogUtil.e("Loading")
                            is ApiResponse.Success -> {
                                LogUtil.e("${it.data?.data.toString()}")
                                var loginInfo: LoginUserInfo = (it.data?.data) as LoginUserInfo
                                spUtil.setItem("token", loginInfo.token)
                                spUtil.setItem("userInfo", loginInfo.userInfoVo)
                                withContext(Dispatchers.Main) {
                                    findNavController().navigate(
                                        R.id.mainNavigationFragment,
                                        null,
                                        NavOptions.Builder().setPopUpTo(R.id.loginFragment, true)
                                            .build()
                                    )
                                }

                            }
                        }
                    }
                }
        }
    }

    private fun regexPhone(phone: String): Boolean {
        var mainRegex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,3,5-9])|(177))\\d{8}$"
        var p = Pattern.compile(mainRegex)
        val m = p.matcher(phone)
        return m.matches()
    }
}