package com.setruth.doublewillow.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * @author  :Setruth
 * time     :2022/5/12 17:36
 * e-mail   :1607908758@qq.com
 * remark   :
 */

class SPUtil(context: Context) {
    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    /**
     * TODO 设置内容
     *
     * @param key
     * @param any 值
     */
    @SuppressLint("CommitPrefEdits")
    fun setItem(key: String, any: Any) {
        val edit = sharedPreferences.edit()
        when (any) {
            is Int -> {
                edit.putInt(key, any)
            }

            is Float -> {
                edit.putFloat(key, any)
            }

            is Boolean -> {
                edit.putBoolean(key, any)
            }

            is Long -> {
                edit.putLong(key, any)
            }

            is String -> {
                edit.putString(key, any)
            }

            is Number -> {
                edit.putString(key, any.toString())
            }

            else -> {
                edit.putString(key, Gson().toJson(any))
            }
        }
        edit.commit()
    }

    inline fun <reified T> toBeanList(list: List<Any>, books: ArrayList<T>) {
        for (item in list) {
            var gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
            val toJson = gson.toJson(item)
            val entity: T = gson.fromJson(toJson, T::class.java)
            books.add(entity)
        }
    }

    inline fun <reified T> getObjectItem(key: String, type: T): T? {
        return Gson().fromJson(this.getItem(key, "") as String, T::class.java)
    }

    /**
     * TODO 获取item项的值
     *
     * @param key
     * @param defaultValue 如果值不存在 返回的默认值
     * @return 返回为null的时候就是没有defaultValue这种类型的get
     */
    fun getItem(key: String, defaultValue: Any?): Any? = when (defaultValue) {
        is Int -> {
            sharedPreferences.getInt(key, defaultValue)
        }

        is Float -> {
            sharedPreferences.getFloat(key, defaultValue)
        }

        is Boolean -> {
            sharedPreferences.getBoolean(key, defaultValue)
        }

        is Long -> {
            sharedPreferences.getLong(key, defaultValue)
        }

        is String -> {
            sharedPreferences.getString(key, defaultValue)
        }

        is MutableSet<*> -> {
            sharedPreferences.getStringSet(key, defaultValue as MutableSet<String>?)
        }

        else -> {
            null
        }
    }

    //SPUtil对应的常量参数
    companion object {
        const val SP_NAME = "account"
        const val PASSWORD_KEY = "pwd"
        const val NICKNAME_KEY = "nickname"
        const val ACCOUNT_KEY = "account"
    }

    fun removeItem(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
