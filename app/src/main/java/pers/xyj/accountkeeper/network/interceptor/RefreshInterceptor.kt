package pers.xyj.accountkeeper.network.interceptor

import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.setruth.doublewillow.utils.SPUtil
import com.setruth.mvvmbaseproject.viewmodel.PublicViewModel
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import pers.xyj.accountkeeper.network.RequestBuilder
import pers.xyj.accountkeeper.network.api.TokenApi
import pers.xyj.accountkeeper.network.response.MyResponse
import pers.xyj.accountkeeper.repository.model.RefreshTokenVo
import pers.xyj.accountkeeper.util.LogUtil
import pers.xyj.accountkeeper.util.appContext
import retrofit2.Call
import java.io.IOException

class RefreshInterceptor : Interceptor {

    private val gson: Gson by lazy { Gson() }
    private val lock = Any()

    val spUtil = SPUtil(appContext)


    private val NEED_REFRESH_TOKEN_CODE = "401002"

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return if (isTokenExpired(response)) {
            synchronized(lock) {
                refreshToken()
            }
            response
        } else {
            response
        }
    }

    fun loginAgain() {

    }

    private fun isTokenExpired(response: Response): Boolean {
        response.body?.let { responseBody ->
            try {
                val mediaType = response.body!!.contentType()
                val string = response.body!!.string()
                val responseBody = ResponseBody.create(mediaType, string)
                val myResponse = gson.fromJson(string, MyResponse::class.java)
//                LogUtil.e(apiResponse.toString())
                if (myResponse.code.equals(NEED_REFRESH_TOKEN_CODE)) {
                    return true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun refreshToken() {
        var oldRefreshToken: String = spUtil.getItem("refreshToken", "") as String
//        val call : Call<MyResponse<RefreshTokenVo>> = publicViewModel!!.request(TokenApi::class.java).refreshToken(oldRefreshToken)
//        val response = call.execute()
//        val result = response.body()?.toString()
//        val newTokenSet: RefreshTokenVo = Gson().fromJson(result, RefreshTokenVo::class.java)
//        LogUtil.e(newTokenSet.toString())
    }
}