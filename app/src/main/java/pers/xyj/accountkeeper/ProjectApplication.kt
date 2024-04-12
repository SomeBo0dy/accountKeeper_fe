package pers.xyj.accountkeeper

import android.app.Application
import android.content.Context

class ProjectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: Context
    }
}
