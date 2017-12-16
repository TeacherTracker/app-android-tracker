package us.buddman.teachertracker.listener

/**
 * Created by User on 2017-12-16.
 */
interface OnMessageListener {
    fun onMessageReceived(lat : Float, lon : Float)
}