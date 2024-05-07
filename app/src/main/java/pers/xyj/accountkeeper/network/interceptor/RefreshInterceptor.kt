package pers.xyj.accountkeeper.network.interceptor

import android.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.setruth.doublewillow.utils.SPUtil
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.network.RequestConfiguration
import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.RefreshTokenDto
import pers.xyj.accountkeeper.repository.model.RefreshTokenVo
import pers.xyj.accountkeeper.util.LogUtil
import pers.xyj.accountkeeper.util.appContext

class RefreshInterceptor : Interceptor {

    private val gson: Gson by lazy { Gson() }
    private val lock = Any()

    val spUtil = SPUtil(appContext)

    private val NEED_LOGIN_CODE = "401001"
    private val NEED_REFRESH_TOKEN_CODE = "401002"
    private val SUCCESS_CODE = "200"
    private val REFRESH_EXPIRED_NEED_LOGIN_CODE = "401003"
    private val REFRESH_TOKEN_URL = "/token/refresh"

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return if (response.body != null && response.body!!.contentType() != null){
            val mediaType = response.body!!.contentType()
            val string = response.body!!.string()
            val responseBody = ResponseBody.create(mediaType, string)
            try {
                val myResponse = gson.fromJson(string, MyResponse::class.java)
                if (myResponse.code == NEED_REFRESH_TOKEN_CODE) {
                    //同步防止并发 请求刷新token
                    synchronized(lock) {
                        refreshToken()
                    }
                    //使用新的Token，创建新的请求
                    val newRequest = chain.request()
                        .newBuilder()
                        .header("token", spUtil.getItem("accessToken", "") as String)
                        .build()
                    chain.proceed(newRequest)
                } else {
                    response.newBuilder().body(responseBody).build()
                }

            } catch (ex: Exception) {
                response
            }
        }else{
            response
        }
    }

    fun loginAgain() {

    }

    private fun refreshToken() {
        var oldRefreshToken: String = spUtil.getItem("refreshToken", "") as String
        val jsonStr = gson.toJson(RefreshTokenDto(oldRefreshToken)).toString()
        val requestBody = jsonStr.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(RequestConfiguration.URL + REFRESH_TOKEN_URL)
            .post(requestBody)
            .build()
        val call = OkHttpClient.Builder().build().newCall(request)
        val response = call.execute()
        if (response.body != null && response.body!!.contentType() != null){
            val result = response.body!!.string()
            LogUtil.e(result)
            val myResponse: MyResponse<RefreshTokenVo> = gson.fromJson(result, MyResponse::class.java) as MyResponse<RefreshTokenVo>
            var code = myResponse.code
            if (code == SUCCESS_CODE){
                val dataMap = myResponse.data as? LinkedTreeMap<*, *>
                val newTokenSet = gson.fromJson(gson.toJson(dataMap), RefreshTokenVo::class.java)
                spUtil.setItem("accessToken", newTokenSet.newAccessToken)
                spUtil.setItem("refreshToken", newTokenSet.newRefreshToken)
            }else if (code == REFRESH_EXPIRED_NEED_LOGIN_CODE || code == NEED_LOGIN_CODE){
                //todo 会报错
                spUtil.removeItem("accessToken")
                spUtil.removeItem("refreshToken")
                val builder = AlertDialog.Builder(appContext)
                builder.setTitle("重新登录")
                builder.setMessage("您的会话已过期，请重新登录。")
                builder.setPositiveButton("确定") { dialog, _ ->
                    navigateToLogin()
                    dialog.dismiss()
                }
                builder.setCancelable(false) // 禁止用户点击对话框外部或返回键关闭对话框
                builder.show()

            }
        }
    }
    private fun navigateToLogin(){
        appContext.findNavController(R.id.app_navigation).navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.mainNavigationFragment, true)
                .build()
        )
    }
}