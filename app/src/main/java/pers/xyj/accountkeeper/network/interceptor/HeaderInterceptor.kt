package pers.xyj.accountkeeper.network.interceptor

import android.app.Activity
import com.setruth.doublewillow.utils.SPUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import pers.xyj.accountkeeper.ProjectApplication.Companion.context
import kotlin.coroutines.coroutineContext

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var token: String = SPUtil(context).getItem("accessToken", "") as String
        var originalRequest: Request = chain.request()
        var request: Request =
            originalRequest.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Accept", "application/json")
                .build()
        if (token == "") {
            return chain.proceed(originalRequest)
        } else {
            var updateRequest: Request = request.newBuilder().header("token", token).build()
            return chain.proceed(updateRequest)
        }
    }
}