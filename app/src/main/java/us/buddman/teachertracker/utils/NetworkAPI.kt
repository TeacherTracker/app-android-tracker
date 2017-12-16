package us.buddman.teachertracker.utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import us.buddman.teachertracker.models.Teacher
import us.buddman.teachertracker.models.User

/**
 * Created by Junseok Oh on 2017-08-02.
 */
interface NetworkAPI {

    @POST("/api/insetTeacher")
    @FormUrlEncoded
    fun insertTeacher(@Field("name") name: String,
                      @Field("school") school: String,
                      @Field("phone") phone: String): Call<ResponseBody>

    @POST("/api/insertLocation")
    @FormUrlEncoded
    fun insetLocation(@Field("lat") lat: Float,
                      @Field("lon") lon: Float,
                      @Field("teacher_phone") phone: Int): Call<ResponseBody>

    @POST("/api/selectTeacherList")
    @FormUrlEncoded
    fun getTeacherInfoList(@Field("school") school: String): Call<ArrayList<Teacher>>

    @POST("/api/selectTeacher")
    @FormUrlEncoded
    fun getTeacherInfo(@Field("teacher_phone") teacherPhoneNumber: String): Call<Teacher>
}

