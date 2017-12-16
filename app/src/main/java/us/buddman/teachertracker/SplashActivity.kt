package us.buddman.teachertracker

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import us.buddman.teachertracker.service.GPSService
import us.buddman.teachertracker.utils.CredentialsManager


class SplashActivity : AppCompatActivity() {
    var schoolName = arrayListOf(
            "선린인터넷고등학교",
            "한국디지털미디어고등학교",
            "대덕소프트웨어마이스터고등학교",
            "양영디지털고등학교",
            "미림여자정보과학고등학교",
            "한세사이버보안고등학교"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermission()
    }
    fun checkPermission(){
        TedPermission(this@SplashActivity)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        startService(Intent(applicationContext, GPSService::class.java))
                        if (CredentialsManager.instance.schoolName == "")
                            MaterialDialog.Builder(this@SplashActivity)
                                    .title("학교를 선택해주세요.")
                                    .items(schoolName)
                                    .cancelable(false)
                                    .itemsCallback { _, _, _, text ->
                                        run {
                                            CredentialsManager.instance.schoolName = text.toString()
                                            startActivity(Intent(this@SplashActivity, MainMapActivity::class.java))
                                            finish()
                                        }
                                    }
                                    .show()
                        else Handler().postDelayed({
                            startActivity(Intent(this@SplashActivity, MainMapActivity::class.java))
                        }, 500)
                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                        MaterialDialog.Builder(this@SplashActivity)
                                .title("권한 허용")
                                .content("위치 기반 서비스를 사용하기 위해 권한이 필요합니다.\n거부 시 어플리케이션을 종료합니다.")
                                .negativeText("거부")
                                .positiveText("확인")
                                .onNegative { dialog, which -> finish() }
                                .onPositive { dialog, which -> checkPermission() }
                                .show()
                    }
                })
                .check()
    }
}
