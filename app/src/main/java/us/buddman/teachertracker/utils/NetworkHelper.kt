package us.buddman.teachertracker.utils


import android.content.Context
import android.net.ConnectivityManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Junseok Oh on 2017-08-02.
 */

object NetworkHelper {
    private val url = "http://yy.whatch.co.kr/"
    private val port = 80

    private var retrofit: Retrofit? = null

    val networkInstance: NetworkAPI
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!.create<NetworkAPI>(NetworkAPI::class.java)
        }

}
