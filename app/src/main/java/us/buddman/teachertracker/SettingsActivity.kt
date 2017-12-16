package us.buddman.teachertracker

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_settings.*
import us.buddman.teachertracker.databinding.SettingsContentBinding
import us.buddman.teachertracker.utils.CredentialsManager

class SettingsActivity : AppCompatActivity() {

    lateinit var adapter: LastAdapter
    private var settingsArr = arrayListOf(
            SettingsContent("푸쉬알림 설정", true, true),
            SettingsContent("학교 변경하기", false, false)
    )
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
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "설정"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.WHITE)
        updateSchoolInfo()
        settingsRV.layoutManager = LinearLayoutManager(applicationContext)
        settingsRV.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        adapter = LastAdapter(settingsArr, BR.content)
                .map<SettingsContent, SettingsContentBinding>(R.layout.settings_content) {
                    onBind {
                        it.binding.pushSwitch.visibility = if (settingsArr[it.layoutPosition].hasSwitch) View.VISIBLE else GONE
                        it.binding.pushSwitch.isChecked = CredentialsManager.instance.pushEnabled
                    }
                    onClick {
                        when (it.layoutPosition) {
                            0 -> {
                                settingsArr[0].switch = !settingsArr[0].switch
                                CredentialsManager.instance.pushEnabled = settingsArr[0].switch
                                adapter.notifyDataSetChanged()
                            }
                            1 -> {
                                MaterialDialog.Builder(this@SettingsActivity)
                                        .title(settingsArr[1].title)
                                        .items(schoolName)
                                        .itemsCallback { _, _, _, text ->
                                            run {
                                                CredentialsManager.instance.schoolName = text.toString()
                                                updateSchoolInfo()
                                            }
                                        }
                                        .show()
                            }
                        }
                    }
                }
                .into(settingsRV)
    }

    fun updateSchoolInfo() {
        schoolInfoText.text = CredentialsManager.instance.schoolName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

data class SettingsContent(var title: String, var hasSwitch: Boolean, var switch: Boolean)
