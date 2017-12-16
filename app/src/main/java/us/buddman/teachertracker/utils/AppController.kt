package us.buddman.teachertracker.utils


import android.app.Application
import android.content.Context
import android.content.Intent
import us.buddman.teachertracker.service.GPSService

/**
 * Created by Junseok Oh on 2017-07-09.
 */

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        var context: Context? = null
    }

}
