package us.buddman.teachertracker.models

/**
 * Created by User on 2017-12-17.
 */
data class Teacher(
        var id: String,
        var name: String,
        var school: String,
        var lat: Float,
        var lon: Float,
        var phone: String,
        var distance: Double
) {
    var infoString: String = ""
        get() = phone + " / " + distance + "m"
}