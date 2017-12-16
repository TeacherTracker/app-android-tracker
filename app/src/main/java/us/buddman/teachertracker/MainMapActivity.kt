package us.buddman.teachertracker

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.lastadapter.LastAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main_map.*
import kotlinx.android.synthetic.main.activity_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import us.buddman.teachertracker.databinding.TeacherContentBinding
import us.buddman.teachertracker.listener.OnMessageListener
import us.buddman.teachertracker.models.Teacher
import us.buddman.teachertracker.service.GPSService
import us.buddman.teachertracker.utils.CredentialsManager
import us.buddman.teachertracker.utils.LocationUtils
import us.buddman.teachertracker.utils.NetworkHelper
import java.util.*

class MainMapActivity : AppCompatActivity(), OnMapReadyCallback, OnMessageListener {


    private lateinit var mMap: GoogleMap
    lateinit var service: GPSService
    lateinit var adapter: LastAdapter
    var myMarker: Marker? = null
    var markerArray = ArrayList<Marker>()
    var teacherArray = ArrayList<Teacher>()
    lateinit var loadingDialog: MaterialDialog
    var loaded = false
    var displayed = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_map)
        init()
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadTeacher(true)
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f), 1000, null)
    }

    fun init() {
        supportActionBar!!.title = "TeacherTracker"
        service = GPSService()
        GPSService.listener = this

        loadingDialog = MaterialDialog.Builder(this@MainMapActivity)
                .title("현재 위치를 로드중입니다.")
                .progress(true, 0)
                .cancelable(false
                )
                .show()
        teacherRV.layoutManager = LinearLayoutManager(this@MainMapActivity)
        teacherRV.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))

    }

    fun loadTeacher(isFirst: Boolean) {
        Handler().postDelayed({
            NetworkHelper.networkInstance.getTeacherInfoList(CredentialsManager.instance.schoolName).enqueue(object : Callback<ArrayList<Teacher>> {
                override fun onFailure(call: Call<ArrayList<Teacher>>, t: Throwable) {
                    println(t.message)
                }

                override fun onResponse(call: Call<ArrayList<Teacher>>, response: Response<ArrayList<Teacher>>) {
                    when (response.code()) {
                        200 -> {
                            teacherArray = response.body()!!
                            calculateDistance()
                            updateList()
                            if (isFirst) {
                                for (t in response.body()!!) {
                                    addMarker(t)
                                }
                            } else updateTeacher(response.body()!!)
                        }
                    }
                }
            })
            loadTeacher(false)
        }, 500)
    }

    fun calculateDistance() {
        if (loaded) {
            for (i in teacherArray) {
                i.distance = LocationUtils.distance(i.lat.toDouble(), i.lon.toDouble(), myMarker!!.position.latitude, myMarker!!.position.longitude, "meter")
                if (i.distance < 50 && (System.currentTimeMillis() - CredentialsManager.instance.lastTime) > 10000 && CredentialsManager.instance.pushEnabled) {
                    setNotification(i.name)
                    CredentialsManager.instance.lastTime = System.currentTimeMillis()
                }
            }
        }
        updateList()
        if (displayed != -1) contentText.text = teacherArray[displayed].infoString
    }

    fun updateTeacher(items: ArrayList<Teacher>) {
        for (marker in markerArray) {
            for (teacher in items) {
                if (marker.tag == teacher.phone) {
                    marker.position = LatLng(teacher.lat.toDouble(), teacher.lon.toDouble())
                    continue
                }
            }
        }
    }

    fun addMarker(item: Teacher) {
        val position = LatLng(item.lon.toDouble(), item.lat.toDouble())
        val markerOption = MarkerOptions().position(position).title(item.name).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
        val marker = mMap.addMarker(markerOption)
        marker.tag = item.phone
        markerArray.add(marker)
    }

    fun updateList() {
        adapter = LastAdapter(teacherArray, BR.content)
                .map<Teacher, TeacherContentBinding>(R.layout.teacher_content) {
                    onBind { }
                    onClick {
                        displayed = it.layoutPosition
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerArray[it.layoutPosition].position))
                        sheetw.collapse()
                        titleText.text = teacherArray[it.layoutPosition].name
                        contentText.text = teacherArray[it.layoutPosition].infoString
                        markerArray[it.layoutPosition].showInfoWindow()
                    }
                }
                .into(teacherRV)
    }

    override fun onMessageReceived(lat: Float, lon: Float) {
        if (myMarker == null) {
            loaded = true
            val position = LatLng(lat.toDouble(), lon.toDouble())
            val markerOption = MarkerOptions().position(position).title("현재 위치")
            val marker = mMap.addMarker(markerOption)
            myMarker = marker
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myMarker!!.position))

        } else {
            myMarker!!.position = LatLng(lat.toDouble(), lon.toDouble())
        }
        loadingDialog.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivityForResult(Intent(applicationContext, SettingsActivity::class.java), 1111)
                displayed = -1
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1111) {
            recreate()
        }
    }

    fun setNotification(teacherName: String) {
        var nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder = Notification.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(Notification.DEFAULT_SOUND)
        builder.setAutoCancel(true)
        builder.setContentTitle(teacherName + " 선생님이 50m 내에 있습니다.");
        builder.setPriority(Notification.PRIORITY_MAX)
        nm.notify(6974, builder.build());
    }
}
