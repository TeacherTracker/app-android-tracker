package us.buddman.teachertracker.utils


/**
 * Created by Junseok Oh on 2017-07-18.
 */


import android.content.Context
import android.content.SharedPreferences
import android.support.v4.util.Pair

import com.google.gson.Gson
import us.buddman.teachertracker.models.User


class CredentialsManager {
    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val context: Context = AppController.context!!


    init {
        preferences = context.getSharedPreferences("TeacherTracker", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    fun save(key: String, data: String) {
        editor.putString(key, data)
        editor.apply()
    }


    fun saveUserInfo(user: User) {
        editor.putString(USER_SCHEMA, Gson().toJson(user))
        editor.putBoolean(HAS_ACTIVE_USER, true)
        editor.apply()
    }

    val activeUser: Pair<Boolean, User>
        get() {
            if (preferences.getBoolean(HAS_ACTIVE_USER, false)) {
                val user = Gson().fromJson(preferences.getString(USER_SCHEMA, ""), User::class.java)
                return Pair.create(true, user)
            } else
                return Pair.create<Boolean, User>(false, null)
        }

    var pushEnabled: Boolean
        get() = preferences.getBoolean(PUSH, true)
        set(value) {
            editor.putBoolean(PUSH, value)
            editor.apply()
        }

    var schoolName: String
        get() = preferences.getString(SCHOOL_NAME, "")
        set(value) {
            editor.putString(SCHOOL_NAME, value)
            editor.apply()
        }
    var lastTime : Long
        get() = preferences.getLong(LAST_TIME, 0)
        set(value) {
            editor.putLong(LAST_TIME, value)
            editor.apply()
        }
    fun removeAllData() {
        editor.clear()
        editor.apply()
    }

    fun getString(key: String): String {
        return preferences.getString(key, "")
    }

    fun getInt(key: String): Int {
        return preferences.getInt(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    val isFirst: Boolean
        get() = preferences.getBoolean("IS_FIRST", true)

    fun notFirst() {
        editor.putBoolean("IS_FIRST", false)
        editor.apply()
    }

    fun getLong(key: String): Long {
        return preferences.getLong(key, 0)
    }

    companion object {

        /* Login Type
        * 0 Facebook
        * 1: Native
        * */

        /* Data Keys */
        private val USER_SCHEMA = "user_schema"
        private val HAS_ACTIVE_USER = "hasactive"
        private val PUSH = "push_alert"
        private val SCHOOL_NAME = "school_name"
        private val LAST_TIME = "time"
        internal var manager: CredentialsManager? = null

        val instance: CredentialsManager
            get() {
                if (manager == null) manager = CredentialsManager()
                return manager as CredentialsManager
            }
    }

}